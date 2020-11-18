import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Entity {
    String type;
    public String raw;
    public int startOffset;
    public int endOffset;

    public Entity() {
    }

    public void print() {
        System.out.println("Raw: " + this.raw);
    }
    public List<Integer> getValues() {
        List<Integer> result = new ArrayList<>();
        return result;
    }
    
    void setValues() { }

    public boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public void findOffset(String sentence) {
        if (raw != null) {
            int startOffset = sentence.indexOf(raw);
            int endOffset = -1;
            if (startOffset > 0) {
                endOffset = startOffset + raw.length();
            }
            this.startOffset = startOffset;
            this.endOffset = endOffset;
        }
    }

    public int getStartOffset() {
        return startOffset;
    }

    public List<String> findAll(String pattern, String raw) {
        Matcher m = Pattern.compile(pattern).matcher(raw);
        int numMatches = m.groupCount();
        List<String> allMatches = new ArrayList<>();
        if (m.find()) {
            for (int i = 1; i <= numMatches; i++) {
                allMatches.add(m.group(i));
            }
        }
        return allMatches;
    }
    public String getString() {
        return "";
    }

    void setValues(Integer integer, Integer integer1, Integer integer2, Integer integer3, Integer integer4, Integer integer5) {}
}
