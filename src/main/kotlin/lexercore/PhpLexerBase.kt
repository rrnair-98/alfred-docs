/**
 * PHP grammar.
 * The MIT License (MIT).
 * Copyright (c) 2015-2019, Ivan Kochurkin (kvanttt@gmail.com), Positive Technologies.
 * Copyright (c) 2019, Thierry Marianne (thierry.marianne@weaving-the-web.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
*/

package com.rohan.lexercore

import org.antlr.v4.kotlinruntime.CharStream
import org.antlr.v4.kotlinruntime.CommonToken
import org.antlr.v4.kotlinruntime.Lexer
import com.rohan.grammars.php.*;
import org.antlr.v4.kotlinruntime.Token

abstract class PhpLexerBase(input: CharStream) : Lexer(input) {
    protected var AspTags = true
    protected var _scriptTag = false
    protected var _styleTag = false
    protected var _heredocIdentifier: String? = null
    protected var _prevTokenType = 0
    protected var _htmlNameText: String? = null
    protected var _phpScript = false
    protected var _insideString = false

    override fun nextToken(): Token {
        val token = super.nextToken() as CommonToken

        when (token.type) {
            PhpLexer.Tokens.PHPEnd, PhpLexer.Tokens.PHPEndSingleLineComment -> {
                if (_mode == PhpLexer.Modes.SINGLELINECOMMENTMODE) {
                    // SingleLineCommentMode for such allowed syntax:
                    // <?php echo "Hello world"; // comment ?>
                    popMode() // exit from SingleLineComment mode.
                }
                popMode() // exit from PHP mode.

                if (token.text == "</script>") {
                    _phpScript = false
                    token.type = PhpLexer.Tokens.HtmlScriptClose
                } else {
                    // Add semicolon to the end of statement if it is absent.
                    // For example: <?php echo "Hello world" ?>
                    if (_prevTokenType == PhpLexer.Tokens.SemiColon || _prevTokenType == PhpLexer.Tokens.Colon
                            || _prevTokenType == PhpLexer.Tokens.OpenCurlyBracket || _prevTokenType == PhpLexer.Tokens.CloseCurlyBracket) {
                        token.channel = PhpLexer.Channels.SkipChannel
                    } else {
                        token.type = PhpLexer.Tokens.SemiColon
                    }
                }
            }
            PhpLexer.Tokens.HtmlName -> {
                _htmlNameText = token.text
            }
            PhpLexer.Tokens.HtmlDoubleQuoteString -> {
                if (token.text == "php" && _htmlNameText == "language") {
                    _phpScript = true
                }
            }
        }

        if (_mode == PhpLexer.Modes.HEREDOC) {
            // Heredoc and Nowdoc syntax support: http://php.net/manual/en/language.types.string.php#language.types.string.syntax.heredoc
            when (token.type) {
                PhpLexer.Tokens.StartHereDoc, PhpLexer.Tokens.StartNowDoc -> {
                    _heredocIdentifier = token.text?.substring(3)?.trim()?.replace("'", "")
                }
                PhpLexer.Tokens.HereDocText -> {
                    if (checkHeredocEnd(token.text.toString())) {
                        popMode()

                        val heredocIdentifier = getHeredocIdentifier(token.text.toString())
                        if (token.text?.trim()?.endsWith(";") == true) {
                            return CommonToken(PhpLexer.Tokens.SemiColon, "$heredocIdentifier;\n")
                        } else {
                            val nextToken = super.nextToken() as CommonToken
                            nextToken.text = "$heredocIdentifier\n;"
                            return nextToken
                        }
                    }
                }
            }
        } else if (_mode == PhpLexer.Modes.PHP) {
            if (this.channel != PhpLexer.Channels.HIDDEN) {
                _prevTokenType = token.type
            }
        }

        return token
    }

    private fun getHeredocIdentifier(text: String): String {
        val trimmedText = text.trim()
        val semi = trimmedText.isNotEmpty() && trimmedText.last() == ';'
        return if (semi) trimmedText.substring(0, trimmedText.length - 1) else trimmedText
    }

    private fun checkHeredocEnd(text: String): Boolean {
        return getHeredocIdentifier(text) == _heredocIdentifier
    }

    protected fun isNewLineOrStart(pos: Int): Boolean {
        val ch = _input.LA(pos)
        return ch <= 0 || ch.toChar() == '\r' || ch.toChar() == '\n'
    }

    protected fun pushModeOnHtmlClose() {
        popMode()
        when {
            _scriptTag -> {
                if (!_phpScript) {
                    pushMode(PhpLexer.Modes.SCRIPT)
                } else {
                    pushMode(PhpLexer.Modes.PHP)
                }
                _scriptTag = false
            }
            _styleTag -> {
                pushMode(PhpLexer.Modes.STYLE)
                _styleTag = false
            }
        }
    }

    protected fun hasAspTags(): Boolean {
        return AspTags
    }

    protected fun hasPhpScriptTag(): Boolean {
        return _phpScript
    }

    protected fun popModeOnCurlyBracketClose() {
        if (_insideString) {
            _insideString = false
            this.channel = PhpLexer.Channels.SkipChannel
            popMode()
        }
    }

    protected fun shouldPushHereDocMode(pos: Int): Boolean {
        val ch = _input.LA(pos)
        return ch.toChar() == '\r' || ch.toChar() == '\n'
    }

    protected fun isCurlyDollar(pos: Int): Boolean {
        return _input.LA(pos).toChar() == '$'
    }

    protected fun setInsideString() {
        _insideString = true
    }
}