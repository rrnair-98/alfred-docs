package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.MethodKind

data class KlassNode(val klassLineNumber: UInt,
                     val klassKind: MethodKind,
                     val packageName: String,
                     val methods: List<MethodNode>,
                     val name: String):BaseAstNode(klassLineNumber) {
}