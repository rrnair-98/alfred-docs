package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.MethodKind

data class MethodNode(
    private val methodLineNumber: UInt,
    private val methodFilePath: String,
    private val methodKind: MethodKind,
    private val className: String,
    private val methodBody: String,
    private val name: String
): BaseNode(methodLineNumber, methodFilePath) {
    private val argsList = this.methodBody.substringAfter('(').substringBefore(')')

}