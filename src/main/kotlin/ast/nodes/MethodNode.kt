package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.InbuiltTypes
import com.rohan.ast.nodes.enums.MethodKind

data class MethodNode(
    val methodLineNumber: UInt,
    val methodKind: MethodKind,
    val className: String,
    val packageName: String,
    val methodBody: String,
    val name: String,
    val docComment: String = "",
    val returnType: String, // TODO: ensure this is a package name
): BaseAstNode(methodLineNumber) {
    private val argsList = this.methodBody.substringAfter('(').substringBefore(')')

    public fun hasComment(): Boolean {
        return this.docComment.isEmpty()
    }
}