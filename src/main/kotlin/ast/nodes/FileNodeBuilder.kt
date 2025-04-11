package com.rohan.ast.nodes

class FileNodeBuilder(private val filePath: String) {

    private var importList = mutableListOf<ImportNode>()
    private lateinit var packageNode: PackageNode

    private var klasses: MutableList<KlassNode> = mutableListOf()
    private var functions: MutableList<FunctionNode> = mutableListOf()
    private var klassBuilder: KlassNodeBuilder? = null

    fun addImportNode(importNode: ImportNode) = apply { this.importList.addLast(importNode) }
    fun setPackageNode(packageNode: PackageNode) = apply { this.packageNode = packageNode }

    fun createNewClassBuilder(): KlassNodeBuilder {
        this.pushOldKlassToList()
        this.klassBuilder = KlassNodeBuilder()
        return this.klassBuilder!!
    }

    fun currentClassBuilder(): KlassNodeBuilder? {
        return this.klassBuilder
    }

    fun addKlass(klass: KlassNode) = apply { this.klasses.addLast(klass) }

    fun addFunction(function: FunctionNode) = apply { this.functions.addLast(function) }

    fun build(): FileNode {
        this.pushOldKlassToList()
        return FileNode(filePath, importList, packageNode, klasses, functions)
    }

    private fun pushOldKlassToList() {
        this.klassBuilder?.run {
            this.setPackageName(this@FileNodeBuilder.packageNode.packageName)
            this@FileNodeBuilder.addKlass(this.build())
        }
    }
}