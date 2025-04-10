package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.MethodKind

data class MethodNode(
    private val methodLineNumber: UInt,
    private val methodKind: MethodKind,
    private val className: String,
    private val packageName: String,
    private val methodBody: String,
    private val name: String
): BaseAstNode(methodLineNumber) {
    private val argsList = this.methodBody.substringAfter('(').substringBefore(')')

}