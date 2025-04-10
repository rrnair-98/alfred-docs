package com.rohan.ast.nodes

class FileNodeBuilder(private val filePath: String) {

    private var importList = mutableListOf<ImportNode>()
    private var packageNode: PackageNode? = null
    private var klasses: List<KlassNode>? = null
    private var functions: List<FunctionNode> = emptyList()

    fun addImportNode(importNode: ImportNode) = apply { this.importList.addLast(importNode) }
    fun packageNode(packageNode: PackageNode?) = apply { this.packageNode = packageNode }
    fun klasses(klasses: List<KlassNode>?) = apply { this.klasses = klasses }
    fun functions(functions: List<FunctionNode>) = apply { this.functions = functions }

    fun build(): FileNode {
        return FileNode(filePath, importList, packageNode, klasses, functions)
    }
}