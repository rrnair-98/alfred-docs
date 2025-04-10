package com.rohan.ast.nodes

data class CommentNode(val commentLineNumber: UInt, val commentColNumber: UInt, val commentContent: String) :
    BaseAstNode(commentLineNumber, commentColNumber) {

}