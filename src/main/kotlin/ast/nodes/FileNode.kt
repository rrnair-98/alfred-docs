package com.rohan.ast.nodes

data class FileNode(val filePath: String, val imports: List<ImportNode>? = null, val packageNode: PackageNode? = null,
                    val klasses: List<KlassNode>? = null, val functions: List<FunctionNode>
): BaseAstNode(0u, 0u) {
}