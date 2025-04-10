package com.rohan.ast.nodes

open class BaseAstNode(protected val lineNumber: UInt = 0u, protected val colNumber: UInt=0u) {
    protected val children = mutableListOf<BaseAstNode>()
    val allChildren get() = this.children.toList()

    fun addChild(node: BaseAstNode) {
        this.children.addLast(node)
    }

}