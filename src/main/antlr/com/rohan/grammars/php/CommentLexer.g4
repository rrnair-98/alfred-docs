lexer grammar CommentLexer;

//MultiLineStart: '/**';
//MultiLineEnd: '*/';
//CommentContent: .*?;
DocComment: '/**' .*? '*/';
fragment NEWLINE : '\r'? '\n';

Whitespace: (' '|'\t'|NEWLINE)+ -> skip;

// Strings: ~('/' '*'? | '*' '/'?);
