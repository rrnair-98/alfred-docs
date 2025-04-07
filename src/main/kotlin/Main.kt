package com.rohan

import com.rohan.ast.nodes.BaseAstNode
import com.rohan.grammars.php.PhpLexer
import com.rohan.grammars.php.PhpParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import java.nio.file.Path

fun main() {
    println("Hello World!")
    val phpLexer = PhpLexer(CharStreams.fromPath(Path.of("")))
    val phpParser = PhpParser(CommonTokenStream(phpLexer))
    val parseTree = phpParser.htmlDocument()
    var node = BaseAstNode(lineNumber = 1u, filePath = Path.of("").toAbsolutePath().toString())
    println("parseTree generated")
}