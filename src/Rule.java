import java.util.ArrayList;

/**
 * Created by Asus on 12/8/2018.
 */

    public class Rule{
        Token key;
        static int ruleNumber=1;
        int ruleNumberOfthisrule=ruleNumber;
        ArrayList<Token> gosTo=new ArrayList<>();
        public Rule(String rule){
            ruleNumber++;
            String[] strs=rule.split("->");
            key=new Token("Variable",strs[0]);
            String[] strss=strs[1].split(" ");
            for(String s:strss){
                if(Scanner1.terminals.contains(s)){
                    gosTo.add(new Token("Terminal",s));
                }
                else{

                    gosTo.add(new Token("Variable",s));
                }
            }
        }
        }

