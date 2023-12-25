package main.scanner;

public enum TokenType {
    // single character
    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    COMMA,
    DOT,
    PLUS,
    MINUS,
    SEMICOLON,
    SLASH,
    STAR,

    // one or two character
    NI,
    NI_ENAK,
    JE,
    ENAK,
    VECJI,
    VECJI_ALI_ENAK,
    MANJSI,
    MANJSI_ALI_ENAK,

    // literals
    IDENTIFIER,
    STRING,
    NUMBER,

    // keywords
    IN,
    RAZRED,
    DRUGACE,
    NERESNICNO,
    PRESLIKAVA,
    ZA_VSAK,
    CE,
    PRAZNO,
    ALI,
    IZPISI,
    VRNI,
    STARS,
    TUKAJ,
    RESNICNO,
    NAJ_IMA,
    DOKLER,
    VREDNOST,

    EOF
}
