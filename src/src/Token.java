/**
 * Created by saeed on 10/9/2018 AD.
 */
enum Type{
    id,number,ST,string,comment,KW,EOF;
}
public class Token {
    Type type;
    String value;
    public Token(String type,String value)
    {
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
        }
        this.value = value;
    }


    public void getToken(){
        System.out.println("<" + type + "," +"'"+ value +"'"+ ">");
    }
}
