package main;

import main.parser.Expr;
import main.parser.Parser;
import main.scanner.Scanner;
import main.scanner.Token;
import main.scanner.TokenType;
import main.util.AstPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Zbornik {

    static boolean hadError = false;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Uporaba: zbornik <datoteka>");
            System.exit(64);
        } else if (args.length == 1) {
            try {
                runFile(args[0]);
            } catch (IOException e) {
                System.out.printf("Datoteka '%s' ne obstaja.", args[0]);
                System.exit(1);
            }
        } else {
            try {
                runPrompt();
            } catch (IOException e) {
                System.out.println();
            }
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        while (true) {
            System.out.println("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();

        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        if (hadError) return;

        System.out.println(new AstPrinter().print(expression));
    }

    public static void error(int line, String message) {
        report(line, "", message);
    }
     public static void error(Token token, String message) {
        if (token.type == TokenType.EOF) report(token.line, " na koncu", message);
        else report(token.line, " pri " + token.lexeme + "'", message);
     }

    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}