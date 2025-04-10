package com.rohan.ast.nodes

data class PackageNode(val packageLineNumber: UInt, val packageColNumber: UInt, val packageName: String) :
    BaseAstNode(packageLineNumber, packageColNumber) {
}