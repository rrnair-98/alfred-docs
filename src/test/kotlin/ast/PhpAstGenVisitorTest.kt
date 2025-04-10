package ast

import com.rohan.ast.PhpAstGenVisitor
import com.rohan.ast.nodes.FileNode
import com.rohan.ast.nodes.PackageNode
import com.rohan.grammars.php.PhpLexer
import com.rohan.grammars.php.PhpParser
import org.antlr.v4.kotlinruntime.CharStreams
import org.antlr.v4.kotlinruntime.CommonTokenStream
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import kotlin.test.assertEquals

class PhpAstGenVisitorTest {

    companion object {
        val log: Logger = LoggerFactory.getLogger(PhpAstGenVisitorTest::class.java)
    }

    @Test
    fun tesHelloWorld() {
        val filePath = Path.of("""C:\Users\rohan\Projects\alfred-docs-cli\src\test\resources\fixtures\namespace.php""")
        val phpLexer = PhpLexer(CharStreams.fromPath(filePath))
        val phpParser = PhpParser(CommonTokenStream(phpLexer))
        val parseTree = phpParser.htmlDocument()
        val parsedFile = PhpAstGenVisitor(filePath = filePath.toString()).visit(parseTree) as FileNode
        Assertions.assertEquals("Hello\\World", parsedFile.packageNode?.packageName)
        Assertions.assertEquals(16, parsedFile.imports?.size)

    }
}