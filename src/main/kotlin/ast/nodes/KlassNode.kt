package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.KlassKind

data class KlassNode(
    val klassLineNumber: UInt,
    val klassKind: KlassKind,
    val packageName: String,
    val inheritsFrom: String,
    val implementedInterfaces: List<String>,
    val methods: List<MethodNode>,
    val name: String):BaseAstNode(klassLineNumber) {
// TODO: add properties, static non static etc, figure out inherited props
}