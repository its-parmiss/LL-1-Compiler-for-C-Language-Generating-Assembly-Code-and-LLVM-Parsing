

import com.sun.org.apache.xerces.internal.xni.grammars.Grammar;

import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

/**
 * Created by saeed on 10/9/2018 AD.
 */

public class Scanner1 {
    static char ch;
    static boolean b = false;
    static Map<String, Integer> keyWords = new HashMap<>();
    static File file = new File("Input.txt");
    static File fileGrammer = new File("Grammer.txt");

    static Scanner scanner;
    static FileInputStream fis;
    static BufferedReader fisGrammer;
    static Stack<Token> parseStack;
    public static ArrayList<String> terminals;
    public static Set<String> nullables = new HashSet<>();
    public static Map<String, Boolean> variables = new HashMap<>();
    public static ArrayList<Rule> grammer;
    public static ArrayList<ArrayList<Token>> rhst;
    public static Token startingVar;
    public static Map<String, Integer> parsetable = new HashMap<>();
    public static Map<String, Set<String>> firsts = new HashMap<>();
    public static Map<String, Set<String>> follows = new HashMap<>();


    public static void main(String[] args) {

        init();
        parsetableGenerator();

        parse();
    }

    public static void getGrammer() {


        parsetableGenerator();
    }

    public static void parsetableGenerator() {
        System.out.println("nullables:");
        for (String t : nullables) {
            System.out.println(t);
        }
        System.out.println("end of nullables");

        for (String var : variables.keySet()) {
            first(new Token("Variable", var));
        }

        for (String t : firsts.keySet()) {
            System.out.println(t + " 's firsts:");
            Set<String> set = firsts.get(t);
            for (String string : set) {
                System.out.println(string);
            }
        }



        for (String var : variables.keySet()) {
            follow(new Token("Variable", var));
        }
        for (String t : follows.keySet()) {
            System.out.println(t + " 's follows:");
            Set<String> set2 = follows.get(t);
            for (String string : set2) {
                System.out.println(string);
            }
        }

        boolean flag1 = true;
        for (Rule r : grammer) {
            Set<String> predictTemp = new HashSet<>();

            for (Token t : r.gosTo) {
                if (!nullables.contains(t.value)) {
                    flag1 = false;
                }
            }
            if (flag1 || r.gosTo.get(0).value.equals("landa")) {
                Set<String> temp = new HashSet<>();
                temp = follows.get(r.key.value);
                for (String s : temp) {
                    predictTemp.add(s);
                }
            }
            if (r.gosTo.get(0).typeString.equals("Terminal")) {
                predictTemp.add(r.gosTo.get(0).value);
            } else {
                int index = 0;
                do {
                    if (r.gosTo.get(index).typeString.equals("Terminal")) {
                        predictTemp.add(r.gosTo.get(index).value);
                        break;
                    }

                    Set<String> firsttemp;
                    firsttemp = firsts.get(r.gosTo.get(index).value);
                    for (String s : firsttemp) {
                        predictTemp.add(s);
                    }
                    index++;
                } while ((variables.get(r.gosTo.get(index - 1).value)) && index < r.gosTo.size());
            }
            for (String terminal : predictTemp) {
                parsetable.put(r.key.value + " " + terminal, r.ruleNumberOfthisrule);
            }
        }
        for (String p : parsetable.keySet()) {
            String[] pair = p.split(" ");
            System.out.println("p.var = " + pair[0]);
            System.out.println("p.ter = " + pair[1]);
            System.out.println("parsetable.get(p) = " + parsetable.get(p));
        }
    }

    public static void makeGrammar(){
        String temp = "";

        try {
            while ((temp = fisGrammer.readLine()) != null) {
                Rule temprule=new Rule(temp);
                grammer.add(temprule);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void findnullable() {
        boolean changed;
        for (Rule rule : grammer) {
            if (rule.gosTo.get(0).value.equals("landa")) {
                nullables.add(rule.key.value);
                variables.put(rule.key.value, true);
            }
        }
        do {
            changed = false;
            for (Rule rule : grammer) {
                boolean temp = true;
                if (variables.get(rule.key.value)) {
                    continue;
                }
                for (Token t : rule.gosTo) {
                    if (t.typeString.equals("Terminal")) {
                        temp = false;
                        break;
                    } else {
                        if (variables.get(t.value) == false) {
                            temp = false;
                            break;
                        }
                    }
                }
                if (temp == true) {
                    changed = true;
                    nullables.add(rule.key.value);
                    variables.put(rule.key.value, true);
                }
            }
        } while (changed);
    }

    public static Set<String> follow(Token t) {
        Set<String> followTemp = new HashSet<>();
        if (follows.containsKey(t.value)) {
            followTemp = follows.get(t.value);
            follows.remove(t.value);
        }
        for (Rule r : grammer) {
            for (int k = 0; k < r.gosTo.size(); k++) {
                if (r.gosTo.get(k).value.equals(t.value)) {
                    if (k == r.gosTo.size() - 1) {
                        if (!r.key.value.equals(t.value)) {
                            Set<String> followTemp2 = follow(r.key);
                            for (String string : followTemp2) {
                                followTemp.add(string);
                            }
                        }
                    } else {
                        boolean followingExpressionIsnullable = true;
                        int index = k + 1;
                        do {
                            if (r.gosTo.get(index).typeString.equals("Terminal")) {
                                followTemp.add(r.gosTo.get(index).value);
                                followingExpressionIsnullable = false;
                            } else {
                                if (!variables.get(r.gosTo.get(index).value))
                                    followingExpressionIsnullable = false;
                                Set<String> firstofthisvar = first(r.gosTo.get(index));
                                for (String string : firstofthisvar) {
                                    followTemp.add(string);
                                }
                                index++;
                            }
                        } while (followingExpressionIsnullable && index < r.gosTo.size());
                        if (followingExpressionIsnullable) {
                            if (!r.key.value.equals(t.value)) {
                                Set<String> followTemp2 = follow(r.key);
                                for (String string : followTemp2) {
                                    followTemp.add(string);
                                }
                            }

                        }
                    }
                }
            }
        }
        follows.put(t.value, followTemp);
        return followTemp;
    }

    public static Set<String> first(Token t) {
        Set<String> tempFirst = new HashSet<>();
        Token keyToken;
        for (Rule r : grammer) {
            keyToken = r.key;
            if (keyToken.value.equals(t.value)) {
                if (r.gosTo.get(0).typeString.equals("Terminal")) {
                    if (firsts.containsKey(t)) {
                        tempFirst = firsts.get(t);
                        tempFirst.add(r.gosTo.get(0).value);
                        firsts.remove(t);
                        firsts.put(t.value, tempFirst);
                    } else {
                        tempFirst.add(r.gosTo.get(0).value);
                        firsts.put(t.value, tempFirst);
                    }
                } else {
                    if (t.type.equals(r.gosTo.get(0).type) && t.value.equals(r.gosTo.get(0).value) && !(variables.get(r.gosTo.get(0)))) {
                        continue;
                    }
                    if (firsts.containsKey(t)) {
                        tempFirst = firsts.get(t);
                        firsts.remove(t);
                    }
                    int i = 0;
                    do {
                        if (t.type.equals(r.gosTo.get(i).type) && t.value.equals(r.gosTo.get(i).value)) {
                            i++;
                            continue;
                        }
                        if (r.gosTo.get(i).typeString.equals("Terminal")) {
                            tempFirst.add(r.gosTo.get(i).value);
                            break;
                        }
                        Set<String> tempFirst2 = new HashSet<>();
                        tempFirst2 = first(r.gosTo.get(i));
                        for (String string : tempFirst2) {
                            tempFirst.add(string);
                        }
                        i++;

                    } while ((variables.get(r.gosTo.get(i - 1).value)) && i < r.gosTo.size());
                    firsts.put(t.value, tempFirst);
                }
            }
        }
        return tempFirst;
    }

    //
    public static void parse() {
        Token token;
        token = nextToken();
        System.out.println("parsing starts");
        while (true) {
            Token topofstack;
            //age top e stack variable bud : age token emun string ya number bud production e topstack+ id ya topstack+ number dar gheire in soorat production e topstack + tokenvalue
            if ((topofstack = parseStack.peek()).typeString.equals("Variable")) {
                System.out.println("topofstack.value = " + topofstack.value);
                System.out.println("token.value = " + token.value);
                System.out.println("token.typeString = " + token.typeString);
                int prod;
                if (token.typeString.equals("id")) {
                    if(!parsetable.keySet().contains(parseStack.peek().value + " " + "id")){
                        Error();
                        System.out.println("error1");
                        break;
                    }
                    prod = parsetable.get((parseStack.pop().value + " " + "id"));
                }
                else if (token.typeString.equals("number")) {
                    if(!parsetable.keySet().contains(parseStack.peek().value + " " + "number")){
                        Error();
                        System.out.println("error2");
                        break;
                    }
                    prod = parsetable.get((parseStack.pop().value + " " + "number"));
                }
                else {
                    if(!parsetable.keySet().contains(parseStack.peek().value + " " + token.value)){
                        Error();
                        System.out.println("error3");
                        break;
                    }
                    prod = parsetable.get(parseStack.pop().value + " " + token.value);
                }
                System.out.println("prod = " + prod);
                for (Token tokenInRhst : rhst.get(prod)) {
                    parseStack.push(tokenInRhst);
                }
            } else if ((topofstack = parseStack.peek()).typeString.equals("Terminal")) {
                if (topofstack.value.equals("landa")) {
                    parseStack.pop();
                } else {
                    System.out.println("topofstack = " + topofstack.typeString);
                    System.out.println("topofstack.value = " + topofstack.value);
                    if (token.typeString.equals("id")) {
                        parseStack.pop();
                        token = nextToken();
                    }
                    else if(token.typeString.equals("number")){
                        parseStack.pop();
                        token=nextToken();
                    }else {
                        if (token.value.equals(topofstack.value)) {
                            parseStack.pop();
                            token = nextToken();
                        } else {
                            Error();
                            System.out.println("error4");
                            break;
                        }
                    }
                }
            } else if (topofstack.typeString.equals("EOF") && token.typeString.equals("EOF")) {
                Accept();
                break;
            } else {
                Error();
                System.out.println("error5");
                break;
            }
        }
    }

    public static void Accept() {
        System.out.println("no syntax errors");
    }

    public static void Error() {
        System.out.println("syntax error");
    }

    public static Token nextToken() {
        //System.out.println("called with ch = "+ch); //debug

        String temp = "";
        Token token;
        while (ch != '$') {
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                do {
                    temp += ch;
                    ch = read();
                } while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));

                //System.out.println("out of while"); //debug
                if (findKeyWords(temp)) {
                    //System.out.println("returned kw");
                    return (new Token("KW", temp));
                } else {
                    // System.out.println("returned id");
                    return (new Token("id", temp));
                }
            }
            switch (ch) {
//                case'.':
//                {
//                    ch=read();
//
//                    return new Token("ST",".");
//                }
                case '.':
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
                        if (ch == '.') {
                            if (!b) {
                                b = true;
                            } else {
                                b = false;
                                return new Token("number", temp);
                            }
                        }
                        temp += ch;
                        ch = read();
                    } while ((ch >= '0' && ch <= '9') || ch == '.');
                    b = false;
                    return new Token("number", temp);
                }

                case '"': {
                    temp = "";
                    while ((ch = read()) != '"') {
                        temp += ch;
                    }
                    ch = read();
                    //System.out.println("temp = " + temp);
                    return new Token("string", temp);
                }

                case '+': {
                    ch = read();
                    if (ch == '+') {
                        token = new Token("ST", "++");
                        ch = read();

                    } else if (ch == '=') {
                        token = new Token("ST", "+=");
                        ch = read();

                    } else {
                        token = new Token("ST", "+");

                    }
                    return token;
                }
                case '-': {
                    ch = read();
                    if (ch == '-') {
                        ch = read();
                        token = new Token("ST", "--");

                    } else if (ch == '=') {
                        ch = read();
                        token = new Token("ST", "-=");

                    } else {
                        token = new Token("ST", "-");

                    }
                    return token;
                }
                case '*': {
                    ch = read();
                    token = new Token("ST", "*");
                    return token;
                }
                case '=': {
                    ch = read();
                    //System.out.println("ch = " + ch);
                    if (ch == '=') {
                        ch = read();
                        //System.out.println("ch = " + ch);

                        token = new Token("ST", "==");
                    } else {
                        token = new Token("ST", "=");
                    }
                    return token;
                }
                case '<': {
                    ch = read();
                    if (ch == '<') {
                        ch = read();
                        token = new Token("ST", "<<");
                    } else if (ch == '=') {
                        ch = read();
                        token = new Token("ST", "<=");

                    } else {
                        token = new Token("ST", "<");
                    }
                    return token;
                }
                case '>': {
                    ch = read();
                    if (ch == '>') {
                        ch = read();
                        token = new Token("ST", ">>");
                    } else if (ch == '=') {
                        ch = read();
                        token = new Token("ST", ">=");

                    } else {

                        token = new Token("ST", "<");
                    }
                    return token;
                }
                case ',': {
                    ch = read();
                    token = new Token("ST", ",");
                    return token;
                }
                case ' ':
                case '\t':
                case 13: {
                    //skip
                    do {
                        ch = read();
                    }
                    while (ch == ' ' || ch == '\n' || ch == '\t');
                    //System.out.println("skipped"); //debug
                    token = nextToken(); // bug bud shadid : age faghat next token o seda koni return et o rikhti door kolan va haminjur read mikone ta $ she az while bere birun pashmam
                    return token;
                }
                case ':': {
                    ch = read();
                    token = new Token("ST", ":");
                    return token;
                }

                case ';': {
                    ch = read();
                    token = new Token("ST", ";");
                    return token;
                }
                case '(': {
                    ch = read();
                    token = new Token("ST", "(");
                    return token;
                }
                case ')': {
                    ch = read();
                    token = new Token("ST", ")");
                    return token;
                }
                case '{': {
                    ch = read();
                    token = new Token("ST", "{");
                    return token;
                }
                case '}': {
                    ch = read();
                    token = new Token("ST", "}");
                    return token;
                }
                case '/': {
                    ch = read();
                    String str = "";
                    if (ch == '/') {
                        while (ch != 13) {
                            ch = read();
                            str += ch;
                        }
                    }
                    if (ch != '*') {
//                        token =new Token("ST","/");
//                        return token;
                    } else {
                        ch = read();
                        do {
                            while (ch != '*') {
                                ch = read();
                            }
                            ch = read();
                        } while (ch != '/');
                    }

                }
            }

        }
        //  System.out.println("returned EOF");


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
        Set<String> tempset = new HashSet<>();
        tempset.add("$");
        startingVar = new Token("Variable", "P");
        follows.put(startingVar.value, tempset);
        terminals = new ArrayList<String>();
        grammer = new ArrayList<Rule>();
        rhst = new ArrayList<ArrayList<Token>>();
        parseStack = new Stack<>();
        parseStack.push(new Token("EOF", "$"));
        parseStack.push(startingVar);
        terminals.add("id");
        terminals.add("+");
        terminals.add("*");
        terminals.add("landa");
        terminals.add(")");
        terminals.add("(");
        terminals.add("int");
        terminals.add("float");
        terminals.add("main");
        terminals.add(")");
        terminals.add("(");
        terminals.add("{");
        terminals.add("}");
        terminals.add(";");
        terminals.add(",");
        terminals.add("char");
        terminals.add("bool");
        terminals.add("id");
        terminals.add("=");
        terminals.add(">");
        terminals.add("<");
        terminals.add(">=");
        terminals.add("<=");
        terminals.add("==");
        terminals.add("for");
        terminals.add("while");
        terminals.add("do");
        terminals.add("--");
        terminals.add("++");
        terminals.add("if");
        terminals.add("while");
        terminals.add("+");
        terminals.add("*");
        terminals.add("number");
        terminals.add("||");
        terminals.add("&&");
        variables.put("E", false);
        variables.put("T", false);
        variables.put("T'", false);
        variables.put("E'", false);
        variables.put("F", false);
        variables.put("P", false);
        variables.put("STL", false);
        variables.put("STL'", false);
        variables.put("ST'", false);
        variables.put("ST", false);
        variables.put("DCL", false);
        variables.put("IFST", false);
        variables.put("WST", false);
        variables.put("DWST", false);
        variables.put("FST", false);
        variables.put("TY", false);
        variables.put("DCL'", false);
        variables.put("AST", false);
        variables.put("AST'", false);
        variables.put("FAST", false);
        variables.put("BE", false);
        variables.put("IDST", false);
        variables.put("IDST'", false);
        variables.put("BE'", false);
        variables.put("BT", false);
        variables.put("BF", false);
        variables.put("BT'", false);

        startingVar = new Token("Variable", "E");
//        Rule rule1 = new Rule("E->T E'");
//        Rule rule2 = new Rule("E'->+ T E'");
//        Rule rule3 = new Rule("E'->landa");
//        Rule rule4 = new Rule("T->F T'");
//        Rule rule5 = new Rule("T'->* F T'");
//        Rule rule6 = new Rule("T'->landa");
//        Rule rule7 = new Rule("F->id");
//        Rule rule8 = new Rule("F->( E )");
//        Rule rule9 = new Rule("F->number");
//        grammer.add(rule1);
//        grammer.add(rule2);
//        grammer.add(rule3);
//        grammer.add(rule4);
//        grammer.add(rule5);
//        grammer.add(rule6);
//        grammer.add(rule7);
//        grammer.add(rule8);
//        grammer.add(rule9);



        try {
            fisGrammer=new BufferedReader(new FileReader(fileGrammer));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
        makeGrammar();
        rhst.add(new ArrayList<Token>());
        for (Rule r : grammer) {
            ArrayList<Token> tempArraylist = new ArrayList<>();
            for (int i = r.gosTo.size() - 1; i >= 0; i--) {
                tempArraylist.add(r.gosTo.get(i));
            }
            rhst.add(tempArraylist);
        }
        findnullable();
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
        //keyWords.put("scanf", 11);
        //keyWords.put("printf", 12);
        keyWords.put("bool", 13);
        keyWords.put("include", 14);
        keyWords.put("void", 15);
        keyWords.put("long", 16);
        keyWords.put("auto", 17);
        keyWords.put("const", 18);
        keyWords.put("case", 19);
        keyWords.put("break", 20);
        keyWords.put("sizeof", 21);
        keyWords.put("default", 22);
        keyWords.put("continue", 23);
        keyWords.put("extern", 24);
        keyWords.put("enum", 25);
        keyWords.put("goto", 26);
        keyWords.put("register", 27);
        keyWords.put("short", 28);
        keyWords.put("signed", 29);
        keyWords.put("static", 30);
        keyWords.put("switch", 31);
        keyWords.put("typedef", 32);
        keyWords.put("union", 33);
        keyWords.put("struct", 34);
        keyWords.put("unsigned", 35);
        keyWords.put("volatile", 36);
        keyWords.put("printf", 37);
        keyWords.put("scanf", 38);

    }
}
