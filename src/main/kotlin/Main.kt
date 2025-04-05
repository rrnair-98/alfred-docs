package com.rohan

import com.rohan.grammars.php.PhpLexer
import com.rohan.grammars.php.PhpParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.antlr.v4.kotlinruntime.TokenStream
import java.nio.file.Path

fun main() {
    println("Hello World!")
    val phpLexer = PhpLexer(CharStreams.fromPath(Path.of("")))
    val phpParser = PhpParser(CommonTokenStream(phpLexer))
    val parseTree = phpParser.htmlDocument()

    println("parseTree generated")
}