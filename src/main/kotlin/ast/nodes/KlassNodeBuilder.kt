package com.rohan.ast.nodes

import com.rohan.ast.nodes.enums.KlassKind

open class KlassNodeBuilder {
    // TODO make it idiomatic
    private var klassLineNumber: UInt = 0u
    private var klassKind: KlassKind = KlassKind.SIMPLE
    private lateinit var packageName: String
    private val methods: MutableList<MethodNode> = mutableListOf()
    private val methodBuilders: MutableList<MethodNodeBuilder> = mutableListOf()
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

    fun currentMethodBuilder() : MethodNodeBuilder? {
        if (this.methods.isEmpty()) {
            return null
        }
        return this.methodBuilders.last()
    }

    fun createMethodBuilder(): MethodNodeBuilder {
        var current = MethodNodeBuilder()
        this.methodBuilders.addLast(current)
        return current
    }

    // Setter for methods
    protected fun addMethod(method: MethodNode) = apply {
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
        this.flushMethodsToList()
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

    private fun flushMethodsToList() {
        this.methodBuilders.forEach {
            it.packageName(this.packageName)
            it.className(this.name)
            this.addMethod(it.build())
        }
    }
}
