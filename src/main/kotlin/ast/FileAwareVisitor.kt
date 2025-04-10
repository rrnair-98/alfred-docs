package com.rohan.ast

import com.rohan.ast.nodes.BaseAstNode
import com.rohan.grammars.php.PhpParserBaseVisitor

import org.antlr.v4.kotlinruntime.tree.ParseTree
import org.slf4j.LoggerFactory
import java.nio.file.InvalidPathException
import java.nio.file.Path
import kotlin.io.path.notExists

class FileAwareVisitor(private val filePath: String) {

    private val phpVisitor: PhpAstGenVisitor

    companion object{
        private final val LOGGER = LoggerFactory.getLogger(FileAwareVisitor::class.java) ?: throw IllegalStateException("Logger could not be initialized")
    }

    init {
        if (Path.of(filePath).notExists()) {
            throw InvalidPathException(filePath, "File '$filePath' does not exist")
        }
        this.phpVisitor = PhpAstGenVisitor(filePath)
    }

    fun run(parseTree: ParseTree) {
        val baseNode = this.phpVisitor.visit(parseTree)
    }


    private fun visitAndPrintNode(node: BaseAstNode) {

    }

}