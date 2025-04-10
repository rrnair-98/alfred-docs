package com.rohan

import com.rohan.ast.FileAwareVisitor
import com.rohan.ast.PhpAstGenVisitor
import com.rohan.ast.nodes.BaseAstNode
import com.rohan.grammars.php.PhpLexer
import com.rohan.grammars.php.PhpParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import java.nio.file.Path

fun main() {
    println("Hello World!")

    val filePath = Path.of("""C:\Users\rohan\Projects\aqp-content-portal\app\Exceptions\Handler.php""")
    val phpLexer = PhpLexer(CharStreams.fromPath(filePath))
    val phpParser = PhpParser(CommonTokenStream(phpLexer))
    val parseTree = phpParser.htmlDocument()

    FileAwareVisitor(filePath = filePath.toString()).run(parseTree)

    println("parseTree generated")
}