package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.KlassKind

class KlassNodeBuilder {
    // TODO make it idiomatic
    private var klassLineNumber: UInt = 0u
    private var klassKind: KlassKind = KlassKind.SIMPLE
    private lateinit var packageName: String
    private val methods: MutableList<MethodNode> = mutableListOf()
    private lateinit var name: String
    private lateinit var extendsFrom: String
    private val implementsFrom: MutableList<String> = mutableListOf()

    fun setKlassLineNumber(lineNumber: UInt) = apply {
        this.klassLineNumber = lineNumber
    }

    // Setter for klassKind
    fun setKlassKind(klassKind: KlassKind) = apply {
        this.klassKind = klassKind
    }

    // Setter for packageName
    fun setPackageName(packageName: String) = apply {
        this.packageName = packageName
    }

    // Setter for methods
    fun addMethod(method: MethodNode) = apply {
        this.methods.addLast(method)
    }

    // Setter for name
    fun setName(name: String) = apply {
        this.name = name
    }

    fun addImplementedInterface(implementedInterface: String) = apply {
        this.implementsFrom.addLast(implementedInterface)
    }

    fun setExtendsFrom(extendsFrom: String) = apply {
        this.extendsFrom = extendsFrom
    }

    // Build method to create KlassNode
    fun build(): KlassNode {

        require(this::packageName.isInitialized) { "packageName must be initialized" }

        return KlassNode(
            klassLineNumber = this.klassLineNumber,
            klassKind = this.klassKind,
            packageName = this.packageName,
            methods = this.methods,
            inheritsFrom = this.extendsFrom,
            implementedInterfaces = this.implementsFrom,
            name = this.name
        )
    }
}
