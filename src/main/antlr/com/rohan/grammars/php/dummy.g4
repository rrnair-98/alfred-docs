parser grammar dummy;

options {
    tokenVocab = dummy_lexer;
}

parse: expression EOF;

interpolatedString: Backtick (Grapheme | interpolatedStringExpression)* Backtick;

interpolatedStringExpression: LeftBrace expression RightBrace;

expression
    : expression Plus expression
    | atom
    ;

atom: Integer | interpolatedString;
