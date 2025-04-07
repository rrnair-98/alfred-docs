package com.rohan.ast.nodes

open class BaseAstNode(protected val lineNumber: UInt = 0u, protected val filePath: String) {
    protected val children = mutableListOf<BaseAstNode>()
    val allChildren get() = this.children.toList()
    protected val colNumber: UInt = 0u

    protected fun addChild(node: BaseAstNode) {
        this.children.addLast(node)
    }

}