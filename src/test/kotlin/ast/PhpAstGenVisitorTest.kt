package ast

import com.rohan.ast.PhpAstGenVisitor
import com.rohan.ast.nodes.FileNode
import com.rohan.ast.nodes.PackageNode
import com.rohan.ast.nodes.enums.ImportKind
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

        Assertions.assertEquals("""App\Features\ContentPortalApi\Exceptions\InvalidCredentialsException""", parsedFile.imports!![0].packageString)
        Assertions.assertEquals(ImportKind.SIMPLE, parsedFile.imports!![0].importKind)
        Assertions.assertEquals(4u, parsedFile.imports!![0].importLineNumber)

        Assertions.assertEquals("""App\Helpers\Services\ResponseHelper""", parsedFile.imports!![1].packageString)
        Assertions.assertEquals(ImportKind.SIMPLE, parsedFile.imports!![1].importKind)
        Assertions.assertEquals(5u, parsedFile.imports!![1].importLineNumber)


        Assertions.assertEquals("""Illuminate\Foundation\Exceptions\Handler""", parsedFile.imports!![5].packageString)
        Assertions.assertEquals(ImportKind.ALIAS, parsedFile.imports!![5].importKind)
        Assertions.assertEquals(9u, parsedFile.imports!![5].importLineNumber)
        Assertions.assertEquals("ExceptionHandler", parsedFile.imports!![5].alias)

        Assertions.assertEquals("""Symfony\Component\HttpFoundation\Response""", parsedFile.imports!![10].packageString)
        Assertions.assertEquals(ImportKind.ALIAS, parsedFile.imports!![10].importKind)
        Assertions.assertEquals(14u, parsedFile.imports!![10].importLineNumber)
        Assertions.assertEquals("ResponseAlias", parsedFile.imports!![10].alias)

        Assertions.assertEquals("""TypeError""", parsedFile.imports!![14].packageString)
        Assertions.assertEquals(ImportKind.SIMPLE, parsedFile.imports!![14].importKind)
        Assertions.assertEquals(18u, parsedFile.imports!![14].importLineNumber)
        Assertions.assertNull(parsedFile.imports!![14].alias)

        Assertions.assertEquals("""redirect""", parsedFile.imports!![15].packageString)
        Assertions.assertEquals(ImportKind.FUNCTION, parsedFile.imports!![15].importKind)
        Assertions.assertEquals(19u, parsedFile.imports!![15].importLineNumber)
        Assertions.assertNull(parsedFile.imports!![15].alias)

        Assertions.assertEquals(2, parsedFile.klasses?.size)

        // asserting parsed classes
        Assertions.assertEquals("DummyEmptyHandler", parsedFile.klasses?.get(0)?.name)
        Assertions.assertEquals("Something", parsedFile.klasses?.get(0)?.inheritsFrom)
        Assertions.assertEquals("OtherInterfaces,SomeOtherInterface", parsedFile.klasses?.get(0)?.
            implementedInterfaces?.joinToString(","))

        Assertions.assertEquals("Handler", parsedFile.klasses?.get(1)?.name)
        Assertions.assertEquals("ExceptionHandler", parsedFile.klasses?.get(1)?.inheritsFrom)
        Assertions.assertEquals("", parsedFile.klasses?.get(1)?.
            implementedInterfaces?.joinToString(","))
    }
}