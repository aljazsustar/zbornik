package main.scanner;

import main.Zbornik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static main.scanner.TokenType.*;

public class Scanner {
    private int start = 0;
    private int current = 0;
    private int line = 1;
    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("in", IN);
        keywords.put("razred", RAZRED);
        keywords.put("drugace", DRUGACE);
        keywords.put("neresnicno", NERESNICNO);
        keywords.put("za vsak", ZA_VSAK);
        keywords.put("preslikava", PRESLIKAVA);
        keywords.put("ce", CE);
        keywords.put("prazno", PRAZNO);
        keywords.put("ali", ALI);
        keywords.put("izpisi", IZPISI);
        keywords.put("vrni", VRNI);
        keywords.put("stars", STARS);
        keywords.put("tukaj", TUKAJ);
        keywords.put("resnicno", RESNICNO);
        keywords.put("naj ima", NAJ_IMA);
        keywords.put("vrednost", VREDNOST);
        keywords.put("dokler", DOKLER);
        keywords.put("ni", NI);
        keywords.put("vecji od", VECJI);
        keywords.put("vecji ali enak", VECJI_ALI_ENAK);
        keywords.put("manjsi od", MANJSI);
        keywords.put("manjsi ali enak", MANJSI_ALI_ENAK);
        keywords.put("ni enak", NI_ENAK);
        keywords.put("enak", ENAK);
    }

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            // operators
            // special case for division
            case '/':
                if (match('/')) {
                    while (peek() != '\n' || !isAtEnd()) advance();
            } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                }
                else {
                    Zbornik.error(this.line, "Nepričakovan znak.");
                    break;
                }
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);

        switch (text) {
            case "naj" -> {
                handleMultiword(text, " ima");
                return;
            }
            case "za" -> {
                handleMultiword(text, " vsak");
                return;
            }
            case "vecji", "manjsi" -> {
                if (peekNext() == 'o') {
                    handleMultiword(text, " od");
                } else {
                    handleMultiword(text, " ali enak");
                }
                return;
            }
            case "ni" -> {
                handleMultiword(text, " enak");
                return;
            }
        }

        TokenType type = keywords.get(text);

        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private void handleMultiword(String text, String expected) {
        boolean valid = matchMultiple(expected);

        if (valid) {
            addToken(keywords.get(source.substring(start, current)));
        } else {
            Zbornik.error(line, "Nepričakovan niz.");
        }
    }

    private boolean matchMultiple(String expected) {
        for (char c : expected.toCharArray()) {
            if (!match(c)) return false;
        }

        return true;
    }
    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek())) advance();

        if (peek() == '.' && isDigit(peekNext())) {
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 > source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }

        if (isAtEnd()) {
            Zbornik.error(line, "Nezaključen niz.");
            return;
        }

        advance();

        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;

        current++;
        return true;
    }
    private char advance() {
        return this.source.charAt(this.current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }
}
