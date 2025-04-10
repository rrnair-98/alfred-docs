package com.rohan.ast

import com.rohan.ast.nodes.*
import com.rohan.ast.nodes.enums.ImportKind
import com.rohan.grammars.php.PhpLexer
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
        this.fileBuilder.packageNode(packageNode)
        return null
    }

    override fun defaultResult(): BaseAstNode? {
        LOGGER.info("defaultResult")
        return null
    }


    override fun visitUseDeclaration(ctx: PhpParser.UseDeclarationContext): BaseAstNode? {
        val importedPackageName = StringBuilder()
        ctx.useDeclarationContentList().useDeclarationContent()[0].namespaceNameList().identifier()
            .joinTo(importedPackageName, NAMESPACE_SEPARATOR) { it.text }
        // although the default is the first defined constant, its better this way in case someone changes it in the future
        var importKind = ImportKind.SIMPLE
        ctx.Function_().let { importKind = ImportKind.FUNCTION; }
        var aliasPackageName: String? = null

        ctx.useDeclarationContentList().useDeclarationContent()[0].namespaceNameList().namespaceNameTail()?.let {
            importKind = ImportKind.ALIAS
            if (!it.isEmpty) {
                aliasPackageName = it.identifier().last().text
            }
        }

        val importNode = ImportNode(
            importLineNumber = ctx.start?.line?.toUInt() ?: 0u,
            packageString = importedPackageName.toString(), importColumnNumber = 0u, importKind = importKind,
            alias = aliasPackageName
        )
        importedPackageName.clear()
        this.fileBuilder.addImportNode(importNode)
        LOGGER.info("in visitUseDeclaration")
        return null
    }

}