package main.parser;

import main.Zbornik;
import main.scanner.Token;
import main.scanner.TokenType;
import main.scanner.TokenType.*;

import java.util.List;

import static main.scanner.TokenType.*;

public class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {
        return equality();
    }

    private Expr equality() {
        Expr expr = comparison();

        while (match(ENAK, NI_ENAK)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = term();

        while (match(VECJI, VECJI_ALI_ENAK, MANJSI, MANJSI_ALI_ENAK)) {
            Token operator = previous();
            Expr rigth = term();
            expr = new Expr.Binary(expr, operator, rigth);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr rigth = factor();
            expr = new Expr.Binary(expr, operator, rigth);
        }

        return expr;
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr rigth = unary();
            expr = new Expr.Binary(expr, operator, rigth);
        }

        return expr;
    }

    private Expr unary() {
        Expr expr;
        if (match(NI, MINUS)) {
            expr = unary();
            Token operator = previous();
            expr = new Expr.Unary(operator, expr);
        } else {
            expr = primary();
        }

        return expr;
    }

    private Expr primary() {
        if (match(NERESNICNO)) return new Expr.Literal(false);
        if (match(RESNICNO)) return new Expr.Literal(true);
        if (match(PRAZNO)) return new Expr.Literal(null);

        if (match(NUMBER, STRING)) return new Expr.Literal(previous().literal);

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Po izrazu pričakujem ')'");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Pričakovan izraz.");
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Zbornik.error(token, message);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case RAZRED:
                case PRESLIKAVA:
                case NAJ_IMA:
                case ZA_VSAK:
                case CE:
                case DOKLER:
                case IZPISI:
                case VRNI:
                    return;
            }

            advance();
        }
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
