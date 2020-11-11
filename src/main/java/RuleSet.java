import java.util.List;

public class RuleSet {
    public List<DictionaryEntry> dictionary;
    public List<RegexEntry> regex;

    public void regexToJava() {
        for (int i = 0; i < regex.size(); i++) {
            RegexEntry rg = regex.get(i);
            rg.pattern = rg.pattern.replaceAll("\\?P<", "?<");
            rg.pattern = rg.pattern.replaceAll("\\?P=([\\w]+)", "\\\\k<$1>");
//            rg.pattern = rg.pattern.replace("\\s", "\\\\s");

//            System.out.println(rg.pattern);
            regex.set(i, rg);
        }
    }
}

