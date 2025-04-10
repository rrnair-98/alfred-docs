package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.ImportKind

data class ImportNode(val importLineNumber: UInt, val importColumnNumber: UInt, val packageString: String,
                      val alias: String? = null, val importKind: ImportKind = ImportKind.SIMPLE) :
    BaseAstNode(importLineNumber, importColumnNumber) {
}
