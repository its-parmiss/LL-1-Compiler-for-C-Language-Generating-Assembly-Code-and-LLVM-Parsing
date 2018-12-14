import java.util.Collection;
import java.util.Map;

/**
 * Created by saeed on 10/9/2018 AD.
 */
enum Type{
    id,number,ST,string,comment,KW,EOF,Terminal,Variable;
}
public class Token  implements Comparable<Token>{
    Type type;
    String value;
    String typeString;
    public Token(String type,String value)
    {
        typeString=type;
        switch (type) {
            case "EOF":
                this.type = Type.EOF;
                break;
            case "id":
                this.type = Type.id;
                break;
            case "number":
                this.type = Type.number;
                break;
                case "ST":
                this.type = Type.ST;
                break;
                case "string":
                this.type = Type.string;
                break;
                case "comment":

                this.type = Type.comment;
                break;
                case "KW":
                this.type = Type.KW;
                break;
            case "Terminal":
                this.type=Type.Terminal;
                break;
            case "Variable":
                this.type=Type.Variable;
                break;
        }
        this.value = value;

    }

    @Override
    public int compareTo(Token o) {
        if(o.value.equals(this.value) && o.typeString.equals(this.typeString)){
            return 0;
        }
        return -1;
    }
    public void getToken(){
        System.out.print(" <" + type + "," +"'"+ value +"'"+ "> ");
    }
}