package com.rohan.ast

import com.rohan.ast.nodes.*
import com.rohan.ast.nodes.enums.ImportKind
import com.rohan.grammars.php.PhpParser
import com.rohan.grammars.php.PhpParserBaseVisitor
import org.slf4j.LoggerFactory

/**
 * To be used to gen ast for one file only.
 */
class PhpAstGenVisitor(private val filePath: String) : PhpParserBaseVisitor<BaseAstNode?>() {

    private lateinit var rootNode: BaseAstNode
    private lateinit var fileBuilder: FileNodeBuilder

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PhpAstGenVisitor::class.java)
            ?: throw IllegalStateException("Logger could not be initialized")
        private const val NO_CHILDREN_ERROR_MESSAGE = "No children found while parsing php AST for file: %s"
        private const val NAMESPACE_SEPARATOR = "\\"
    }

    override fun visitHtmlDocument(ctx: PhpParser.HtmlDocumentContext): BaseAstNode {
        LOGGER.info("in visitHtmlDocument")
        this.fileBuilder = FileNodeBuilder(this.filePath)
        if (ctx.childCount == 0) {
            LOGGER.warn("No children found for $filePath")
            throw IllegalStateException(NO_CHILDREN_ERROR_MESSAGE.format(this.filePath))
        }
        ctx.phpBlock().forEach { phpBlockContext: PhpParser.PhpBlockContext ->
            LOGGER.info("found phpBlockContext")
            this.visitPhpBlock(phpBlockContext)
        }
        return this.fileBuilder.build()
    }

    override fun visitNamespaceDeclaration(ctx: PhpParser.NamespaceDeclarationContext): BaseAstNode? {
        val packageName = StringBuilder()
        ctx.namespaceNameList()?.identifier()?.map { it.text }!!.joinTo(packageName, NAMESPACE_SEPARATOR)
        val packageNode = PackageNode(
            packageLineNumber = ctx.start?.line?.toUInt() ?: 0u,
            packageName = packageName.toString(),
            packageColNumber = 0u
        )
        packageName.clear()
        this.fileBuilder.setPackageNode(packageNode)
        return null
    }

    override fun defaultResult(): BaseAstNode? {
        return null
    }


    override fun visitClassDeclaration(ctx: PhpParser.ClassDeclarationContext): BaseAstNode? {
        LOGGER.info("in visitClassDeclaration")
        val klassBuilder = this.fileBuilder.createNewClassBuilder()
        ctx.identifier()?.let {
            klassBuilder.setName(it.text)
        } ?: { LOGGER.warn("either class name was not defined or could not be parsed") }
        // setting class inherited from
        ctx.extendsFrom()?.let {
            it.qualifiedStaticTypeRef().qualifiedNamespaceName()?.let { innerIt ->
                klassBuilder.setExtendsFrom(innerIt.text)
            }
        }
        // setting implemented interfaces
        ctx.interfaceList()?.qualifiedStaticTypeRef()?.forEach {
            it.qualifiedNamespaceName()?.let { qn ->
                klassBuilder.addImplementedInterface(qn.text)
            }
        }
        return super.visitClassDeclaration(ctx)
    }

    override fun visitClassStatement(ctx: PhpParser.ClassStatementContext): BaseAstNode? {
        LOGGER.info("in visitClassStatement")
        ctx.Function_()?.let {
            // method definition found,
            LOGGER.info("method definition foubd")
            val methodBuilder = this.fileBuilder.currentClassBuilder()?.createMethodBuilder()
            methodBuilder?.methodBody(ctx.text) ?: LOGGER.info("method Body not found")
            ctx.identifier()?.text?.let { methodName ->  methodBuilder?.name(methodName) } ?: LOGGER.info("method name was null")
            ctx.returnTypeDecl()?.text?.let { returnTypeVal -> methodBuilder?.methodReturnType(returnTypeVal) }
            if(ctx.MultiLineComment().isNotEmpty())  {
                ctx.MultiLineComment()[0].let {
                    methodBuilder?.docComment(it.text)
                }
            }
        }

        return null
    }

    override fun visitUseDeclaration(ctx: PhpParser.UseDeclarationContext): BaseAstNode? {
        val importedPackageName = StringBuilder()
        ctx.useDeclarationContentList().useDeclarationContent()[0].namespaceNameList().identifier()
            .joinTo(importedPackageName, NAMESPACE_SEPARATOR) { it.text }
        // although the default is the first defined constant, its better this way in case someone changes it in the future
        var importKind = ImportKind.SIMPLE
        ctx.Function_()?.let { importKind = ImportKind.FUNCTION; }
        var aliasPackageName: String? = null

        ctx.useDeclarationContentList().useDeclarationContent()[0].namespaceNameList().namespaceNameTail()?.let {
            importKind = ImportKind.ALIAS
            if (!it.isEmpty) {
                importedPackageName.append(NAMESPACE_SEPARATOR).append(it.identifier().first().text)
                aliasPackageName = it.identifier().last().text
            } else {
                LOGGER.warn(
                    "namespaceNameTail was empty, generated package name will be wrong for lineNumber: %d, content: %s".format(
                        ctx.start?.line,
                        ctx.text
                    )
                )
            }
        }

        val importNode = ImportNode(
            importLineNumber = ctx.start?.line?.toUInt() ?: 0u,
            packageString = importedPackageName.toString(), importColumnNumber = 0u, importKind = importKind,
            alias = aliasPackageName
        )
        importedPackageName.clear()
        this.fileBuilder.addImportNode(importNode)
        return null
    }

}