parser grammar CommentParser;

options {
    tokenVocab = CommentLexer;
}

parse: DocComment+ EOF;
