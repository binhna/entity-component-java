import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTime {
    public String type = "_DATETIME";
    public String raw;
    public Date value;

    public DateTime(String entity_raw, Date entity_value) {
        this.raw = entity_raw;
        this.value = entity_value;
    }
    public DateTime() { }

    public List<DateTime> normalize(RuleSet ruleset) {

        if (this.raw == null) {
            return Collections.singletonList(this);
        }
        Date today = Date.now("Asia/Bangkok");
        List<DateTime> output = Collections.emptyList();

        if (Pattern.compile(ruleset.regex.get(4).pattern).matcher(this.raw).find()) {
            List<String> days = Arrays.asList(this.raw.split("-", -1));
            List<String> components = Arrays.asList(days.get(days.size() - 1).split("/", -1));
            String month_tmp = components.get(1);
            String year_tmp;
            if (components.size() > 2) {
                year_tmp = components.get(2);
            }
            else {
                year_tmp = String.valueOf(today.year);
            }
            String day2 = Arrays.asList(components.get(1).split("/", -1)).get(0);
            Date dateOutput2 = new Date(Integer.parseInt(day2), Integer.parseInt(month_tmp), Integer.parseInt(year_tmp));
            DateTime output2 = new DateTime(this.raw, dateOutput2);
            output.add(output2);
            String day1 = components.get(0);
            Date dateOutput1 = new Date(Integer.parseInt(day1), Integer.parseInt(month_tmp), Integer.parseInt(year_tmp));
            DateTime output1 = new DateTime(this.raw, dateOutput1);
            output.add(output1);

            System.out.println("Found value: " + this.raw);
        }


        return output;
    }

}

