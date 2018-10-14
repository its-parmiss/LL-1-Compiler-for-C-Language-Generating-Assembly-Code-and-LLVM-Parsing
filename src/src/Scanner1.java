import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.io.*;

/**
 * Created by saeed on 10/9/2018 AD.
 */

public class Scanner1 {
    static char ch;
    static Map<String, Integer> keyWords = new HashMap<>();
    static File file = new File("E:/university/5th/compiler/Compiler/Input.txt");
    static Scanner scanner;
    static FileInputStream fis;

    public static void main(String[] args) {

        init();
        parse();
    }

    public static void parse() {
        Token token;
        do {
            token = nextToken();
           // System.out.println("token.value = " + token.value);
            token.getToken(); //prints the token as wanted
        } while (!(token.type.equals(Type.EOF) && token.value.equals("$")));
    }

    public static Token nextToken() {
        System.out.println("called with ch = "+ch);
        String temp = "";
        Token token;
        while (ch != '$') {
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                do {
                    temp += ch;
                    ch = read();
                } while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));

                System.out.println("out of while");
                if (findKeyWords(temp)) {
                    //System.out.println("returned kw");
                    return (new Token("KW", temp));
                } else {
                   // System.out.println("returned id");
                    return (new Token("id", temp));
                }
            }
            switch (ch) {
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0': {
                    do {
                        temp += ch;
                        ch = read();
                    } while (ch >= '0' && ch <= '9');
                    return new Token("number", temp);
                }

                case '"': {
                    temp = null;
                    while ((ch = read()) != '"') {
                        temp += ch;
                    }
                    return new Token("string", temp);
                }

                case '+': {
                    ch = read();
                    if (ch == '+') {
                        token = new Token("ST", "++");

                    } else if (ch == '=') {
                        token = new Token("ST", "+=");

                    } else {
                        token = new Token("ST", "+");

                    }
                    return token;
                }
                case '-': {
                }
                case '=':{

                }
                case '<': {
                    //giting
                }case '>': {
                }
                case ' ':
                case '\t':
                case 13: {
                    //skip
                    do {
                        ch = read();;
                    }
                    while (ch == ' ' || ch == '\n' || ch == '\t');
                    System.out.println("skipped");
                    token=nextToken(); // bug bud shadid : age faghat next token o seda koni return et o rikhti door kolan va haminjur read mikone ta $ she az while bere birun pashmam
                    return token;
                }
                case ':': {
                }
                case ';': {
                }
                case '(': {
                }
                case ')': {
                }
                case '{': {
                }
                case '}': {
                }


            }

        }
      //  System.out.println("returned EOF");

        // System.out.println("here");
        return new Token("EOF", "$");

    }


    public static char read() {
        try {
            if (fis.available() > 0) {
                ch = (char) fis.read();
                return ch;
            } else {
                return '$';
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return '`'; //?

    }

    public static boolean findKeyWords(String sr) {
        if (keyWords.containsKey(sr))
            return true;
        return false;
    }

    public static void init() {

        try {
            // scanner = new Scanner(file);
            fis = new FileInputStream(file);

            if (fis.available() > 0) {
                ch = (char) fis.read();
            } else {
                System.out.println("no input");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        keyWords.put("if", 0);
        keyWords.put("while", 1);
        keyWords.put("do", 2);
        keyWords.put("for", 3);
        keyWords.put("main", 4);
        keyWords.put("return", 5);
        keyWords.put("int", 6);
        keyWords.put("double", 7);
        keyWords.put("float", 8);
        keyWords.put("char", 9);
        keyWords.put("else", 10);
        keyWords.put("scanf", 11);
        keyWords.put("printf", 12);
        keyWords.put("bool", 13);
        keyWords.put("include", 14);
        keyWords.put("void", 15);
        keyWords.put("long", 16);
    }
}