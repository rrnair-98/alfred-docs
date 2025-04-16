package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.InbuiltTypes
import com.rohan.ast.nodes.enums.MethodKind
import org.slf4j.LoggerFactory

class MethodNodeBuilder {


    companion object {
        private val LOGGER = LoggerFactory.getLogger(MethodNodeBuilder::class.java)
        private const val DEFAULT_CLASS_NAME: String = "123DUMMY123"
        private const val DEFAULT_METHOD_NAME: String = "123FOO123"
        private const val DEFAULT_PACKAGE = "123com.dummy"
        private const val DEFAULT_METHOD_BODY = "";
    }

    private var methodLineNumber: UInt = 0u
    private var methodKind: MethodKind = MethodKind.METHOD
    private var className: String = DEFAULT_CLASS_NAME
    private var packageName = DEFAULT_PACKAGE
    private var methodBody = DEFAULT_METHOD_BODY
    private var name = DEFAULT_METHOD_NAME
    private var methodReturnType = InbuiltTypes.VOID.toString().lowercase()
    private var docComment = ""

    fun methodLineNumber(methodLineNumber: UInt) = apply { this.methodLineNumber = methodLineNumber }
    fun methodKind(methodKind: MethodKind) = apply { this.methodKind = methodKind }
    fun className(className: String) = apply { this.className = className }
    fun packageName(packageName: String) = apply { this.packageName = packageName }
    fun methodBody(methodBody: String) = apply { this.methodBody = methodBody }
    fun name(name: String) = apply { this.name = name }
    fun methodReturnType(methodReturnType: String) = apply { this.methodReturnType = methodReturnType }
    fun docComment(docComment: String) = apply { this.docComment = docComment }


    fun build(): MethodNode {
        return MethodNode(
            methodLineNumber = methodLineNumber,
            methodKind = methodKind,
            className = className,
            packageName = packageName,
            methodBody = methodBody,
            returnType = methodReturnType,
            docComment = docComment,
            name = name
        )
    }
}