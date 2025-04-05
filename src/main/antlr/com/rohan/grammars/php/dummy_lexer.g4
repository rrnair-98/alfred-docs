lexer grammar dummy_lexer;
LeftBrace: '{';
RightBrace: '}' -> popMode;

Backtick: '`' -> pushMode(InterpolatedString);
Integer: [0-9]+;
Plus: '+';

mode InterpolatedString;

EscapedLeftBrace: '\\{' -> type(Grapheme);
EscapedBacktick: '\\`' -> type(Grapheme);
ExprStart: '{' -> type(LeftBrace), pushMode(DEFAULT_MODE);
End: '`' -> type(Backtick), popMode;
Grapheme: ~('{' | '`');
