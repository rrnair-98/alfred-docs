package com.rohan.ast.nodes

open class BaseNode(protected val lineNumber: UInt = 0u, protected val filePath: String) {
    protected val _children = mutableListOf<BaseNode>()
    val children get() = _children.toList()
    protected val colNumber: UInt = 0u

    public fun addChild(node: BaseNode) {
        this._children.addLast(node)
    }

}