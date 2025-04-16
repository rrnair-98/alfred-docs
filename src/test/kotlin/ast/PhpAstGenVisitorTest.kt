package ast

import com.rohan.ast.PhpAstGenVisitor
import com.rohan.ast.nodes.FileNode
import com.rohan.ast.nodes.PackageNode
import com.rohan.ast.nodes.enums.ImportKind
import com.rohan.ast.nodes.enums.MethodKind
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

        val methods = parsedFile.klasses?.get(1)?.methods
        Assertions.assertEquals(2, methods?.size)

        Assertions.assertEquals("register", methods?.get(0)?.name)
        Assertions.assertEquals("Handler", methods?.get(0)?.className)
        Assertions.assertEquals("Hello\\World", methods?.get(0)?.packageName)
        // Assertions.assertEquals(METHOD_BODY, methods?.get(0)?.methodBody)
        Assertions.assertEquals(MethodKind.METHOD, methods?.get(0)?.methodKind)
        Assertions.assertFalse(methods?.get(0)?.docComment!!.isEmpty())
        Assertions.assertEquals(REGISTER_MULTLINE_COMMENT.trim(), methods?.get(0)?.docComment?.trim()?.replace("\r\n", "\n"))

        Assertions.assertEquals("theOtherFunction", methods[1].name)
        Assertions.assertEquals("Handler", methods[1].className)
        Assertions.assertEquals("Hello\\World", methods[1].packageName)
        Assertions.assertEquals(MethodKind.METHOD, methods[1].methodKind)
        Assertions.assertTrue(methods[1].docComment.isEmpty())

    }

    companion object {
        val log: Logger = LoggerFactory.getLogger(PhpAstGenVisitorTest::class.java)
        // TODO: move to fixture

        private final const val REGISTER_MULTLINE_COMMENT = """    /**
     * Register the exception handling callbacks for the application.
     */"""
        private final const val METHOD_BODY = """{
        \${'$'}this->reportable(function (Throwable \${'$'}e) {
            if (!is_null(env('SENTRY_DSN'))) {
                Integration::captureUnhandledException(\${'$'}e);
            }
        });

        \${'$'}this->renderable(function (ValidationException \${'$'}exception, Request \${'$'}request) {
            Log::error(\${'$'}exception);
            if (ResponseHelper::isApiCall(\${'$'}request)) {
                return ResponseHelper::unprocessableEntity(\${'$'}exception->errors());
            }
        });

        \${'$'}this->renderable(function (NotFoundHttpException \${'$'}exception, Request \${'$'}request) {
            Log::error(\${'$'}exception);
            if (ResponseHelper::isApiCall(\${'$'}request)) {
                if (\${'$'}exception->getPrevious() instanceof ModelNotFoundException) {
                    \${'$'}modelName = class_basename(\${'$'}exception->getPrevious()->getModel());
                    return ResponseHelper::notFound("\${'$'}modelName does not exist with the specified key!");
                }
                return ResponseHelper::notFound(\${'$'}exception->getMessage());
            }
        });

        \${'$'}this->renderable(function (InvalidCredentialsException \${'$'}exception, Request \${'$'}request) {
            Log::error(\${'$'}exception);
            if (ResponseHelper::isApiCall(\${'$'}request)) {
                return ResponseHelper::errorResponse(ResponseAlias::HTTP_UNAUTHORIZED, \${'$'}exception->getMessage());
            }
        });

        \${'$'}this->renderable(function (AuthorizationException \${'$'}exception, Request \${'$'}request) {
            Log::error(\${'$'}exception);
            if (ResponseHelper::isApiCall(\${'$'}request)) {
                return ResponseHelper::errorResponse(ResponseAlias::HTTP_FORBIDDEN, \${'$'}exception->getMessage());
            }
        });

        \${'$'}this->renderable(function (MethodNotAllowedHttpException \${'$'}exception, Request \${'$'}request) {
            Log::error(\${'$'}exception);
            if (ResponseHelper::isApiCall(\${'$'}request)) {
                return ResponseHelper::methodNotAllowed('The specified method for the request is invalid');
            }
            return redirect()->back();
        });

        \${'$'}this->renderable(function (TypeError \${'$'}exception, Request \${'$'}request) {
            Log::error(\${'$'}exception);
            if (ResponseHelper::isApiCall(\${'$'}request)) {
                return ResponseHelper::badRequest();
            }
        });

        \${'$'}this->renderable(function (QueryException \${'$'}exception, Request \${'$'}request) {
            Log::error(\${'$'}exception);
            if (ResponseHelper::isApiCall(\${'$'}request)) {
                \${'$'}errorCode = \${'$'}exception->errorInfo[1];

                if (\${'$'}errorCode == self::FOREIGN_KEY_VIOLATION_CODE) {
                    return ResponseHelper::errorResponse('Cannot remove this resource permanently,
                as it is related with any other resource', 409);
                }
                return ResponseHelper::internalError();
            }
        });
    }"""
    }
}