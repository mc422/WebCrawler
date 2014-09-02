import java.util.regex.*;

public class REtest {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
    String txt="src=\"images/bestjobs.png\"";

    String re1="(s)";	// Any Single Character 1
    String re2="(r)";	// Any Single Character 2
    String re3="(c)";	// Any Single Character 3
    String re4="(=)";	// Any Single Character 4
    String re5="(\".*?\")";	// Double Quote String 1

    Pattern p = Pattern.compile(re1+re2+re3+re4+re5,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    Matcher m = p.matcher(txt);
    if (m.find())
    {
        String c1=m.group(1);
        String c2=m.group(2);
        String c3=m.group(3);
        String c4=m.group(4);
        String string1=m.group(5);
        System.out.print("("+c1.toString()+")"+"("+c2.toString()+")"+"("+c3.toString()+")"+"("+c4.toString()+")"+"("+string1.toString()+")"+"\n");
    }


}
