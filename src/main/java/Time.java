import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time extends Entity{
    public LocalDateTime value;
    public ZoneId zone = ZoneId.of("Asia/Bangkok");
    public Time(String entity_raw, LocalDateTime entity_value) {
        this.raw = entity_raw;
        this.value = entity_value;
        type = "_TIME";
    }
    public Time() {
        type = "_TIME";
    }
    public void print() {
        System.out.println(type + " | " + "Raw: " + this.raw + " | " + "Value " + value.getDayOfMonth() + "/" + value.getMonthValue() + "/" + value.getYear() + " " + value.getHour() + ":" + value.getMinute() + ":" + value.getSecond());
    }

    public List<Integer> getValues() {
        List<Integer> result = new ArrayList<>();
        result.add(value.getDayOfMonth());
        result.add(value.getMonthValue());
        result.add(value.getYear());
        result.add(value.getHour());
        result.add(value.getMinute());
        result.add(value.getSecond());
        return result;
    }

    void setValues(int day, int month, int year, int hour, int minute, int second) {
        System.out.println("Works");
        value = LocalDateTime.of(year, month, day, hour, minute, second);
    }
    public String getString() {
        return type + " | " + "Raw: " + this.raw + " | " + "Value " + value.getDayOfMonth() + "/" + value.getMonthValue() + "/" + value.getYear() + " " + value.getHour() + ":" + value.getMinute() + ":" + value.getSecond();
    }

    public List<Time> normalize(RuleSet ruleset, String input) {

        if (this.raw == null) {
            return Collections.singletonList(this);
        }
        LocalDate nowDate = LocalDate.now(zone);
        LocalTime nowTime = LocalTime.now(zone);
        LocalDateTime now = LocalDateTime.now(zone);
        List<Time> output = new ArrayList<>();

        if (Pattern.compile(ruleset.regex.get(0).pattern).matcher(this.raw).find()) {
            List<String> allMatches = findAll(ruleset.regex.get(0).pattern, raw);
            int hour = allMatches.get(0) != null ? Integer.parseInt(allMatches.get(0)) : 0;
            hour = hour != 24 ? hour : 0;
            int minute = allMatches.get(1) != null ? Integer.parseInt(allMatches.get(1)) : 0;
            Matcher pm = Pattern.compile("[Cc]hiều|[Tt]ối|[Kk]huya|[đĐ]êm").matcher(input);
            if (pm.find() && hour <= 12) {
                hour += 12;
                hour = hour == 24 ? 0 : hour;
            }

            output.add(new Time(raw, LocalDateTime.of(nowDate, LocalTime.of(hour, minute, 0, 0))));
        }
        else if (Pattern.compile(ruleset.regex.get(4).pattern).matcher(this.raw).find()) {
            System.out.println(raw);
            List<String> allMatches = findAll(ruleset.regex.get(4).pattern, raw);
            int num = allMatches.get(0) != null ? Integer.parseInt(allMatches.get(0)) : 0;
            String _case = allMatches.get(1);
            switch (_case) {
                case "giây":
                    now = now.plusSeconds(num);
                    break;
                case "phút":
                    now = now.plusMinutes(num);
                    break;
                case "tiếng":
                case "giờ nữa":
                    now = now.plusHours(num);
                    break;
            }
            String pattern = "[đĐ]ếm ngược|[nN]ữa|[sS]au|[nN]hắc|[gG]ọi|[Bb]ấm giờ|[đĐ]ổ chuông";
            if (Pattern.compile(pattern).matcher(input).find()) {
                output.add(new Time(raw, now));
            }
        }
        if (Pattern.compile(ruleset.regex.get(5).pattern).matcher(raw).find()) {
            List<String> allMatches = findAll(ruleset.regex.get(5).pattern, raw);
            //System.out.println(allMatches);
            int hour = Integer.parseInt(allMatches.get(0));
            LocalDateTime target = LocalDateTime.now(zone);

            if (allMatches.get(1) != null && allMatches.get(2) != null) {
                target = LocalDateTime.of(nowDate, LocalTime.of(hour, 0, 0));
                Long minute = Long.parseLong(allMatches.get(2));
                target = target.minusMinutes(minute);

            }
            else {
                int minute = allMatches.get(2) != null ? Integer.parseInt(allMatches.get(2)) : 0;
                target = LocalDateTime.of(nowDate, LocalTime.of(hour, minute, 0));
            }
            output.add(new Time(raw, target));
        }

        return output;
    }
}
