import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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

    public void print() {
        System.out.println("Raw: " + this.raw);
        System.out.println("Value " + value.day + "/" + value.month + "/" + value.year);
    }
    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    public List<DateTime> normalize(RuleSet ruleset) {

        if (this.raw == null) {
            System.out.println(ruleset.regex.get(4).pattern);
            return Collections.singletonList(this);
        }
        Date today = Date.now("Asia/Bangkok");
        List<DateTime> output = new ArrayList<>();

        String[] plusOne = new String[]{"sau", "tới", "kế"};
        String[] remain = new String[]{"này", "nay"};
        List<String> plusOne_ = Arrays.asList(plusOne);
        List<String> remain_ = Arrays.asList(remain);

        if (Pattern.compile(ruleset.regex.get(0).pattern).matcher(this.raw).find()) {
            String[] components_ = raw.split("[\\/.-]", -1);
            List<String> components = Arrays.asList(components_);
            if (components.size() == 3) {
                Date dateOutput1 = new Date(Integer.parseInt(components.get(0)), Integer.parseInt(components.get(1)), Integer.parseInt(components.get(2)));
                DateTime output1 = new DateTime(this.raw, dateOutput1);
                output.add(output1);
                output1.print();
            }
        }
        if (Pattern.compile(ruleset.regex.get(4).pattern).matcher(this.raw).find()) {
            List<String> days = Arrays.asList(this.raw.split("-", -1));
            List<String> secondComponents = Arrays.asList(days.get(days.size() - 1).split("/", -1));
            String month_tmp = secondComponents.get(1);
            String year_tmp;
            if (secondComponents.size() > 2) {
                year_tmp = secondComponents.get(2);
            }
            else {
                year_tmp = String.valueOf(today.year);
            }
            String day2 = secondComponents.get(0);
//            System.out.println(secondComponents);
            Date dateOutput2 = new Date(Integer.parseInt(day2), Integer.parseInt(month_tmp), Integer.parseInt(year_tmp));
            DateTime output2 = new DateTime(this.raw, dateOutput2);
            output.add(output2);
            String day1 = days.get(0);
            Date dateOutput1 = new Date(Integer.parseInt(day1), Integer.parseInt(month_tmp), Integer.parseInt(year_tmp));
            DateTime output1 = new DateTime(this.raw, dateOutput1);
            output.add(output1);

            System.out.println("Found value: " + this.raw);
            output1.print();
            output2.print();
        }
        if (Pattern.compile(ruleset.regex.get(5).pattern).matcher(this.raw).find()) {
            Matcher m = Pattern.compile(ruleset.regex.get(5).pattern).matcher(this.raw);
            if (m.find()) {
                String d_f = m.group(1);
                String m_f = m.group(2);
                String y_f = m.group(3);
                String d_t = m.group(4);
                String m_t = m.group(5);
                String y_t = m.group(6);

                m_f = (m_f != null) ? m_f : m_t;
                y_f = (y_f != null) ? y_f : y_t;
                y_f = (y_f != null) ? y_f : String.valueOf(today.year);
                y_t = (y_t != null) ? y_t : String.valueOf(today.year);

                Date dateOutput2 = new Date(Integer.parseInt(d_f), Integer.parseInt(m_f), Integer.parseInt(y_f));
                DateTime output2 = new DateTime(this.raw, dateOutput2);
                output.add(output2);

                Date dateOutput1 = new Date(Integer.parseInt(d_t), Integer.parseInt(m_t), Integer.parseInt(y_t));
                DateTime output1 = new DateTime(this.raw, dateOutput1);
                output.add(output1);

                output1.print();
                output2.print();
            }
        }
        if (Pattern.compile("(?:[Nn]gày\\s+|[Ss]áng\\s+|[Tt]rưa\\s+|[Cc]hiều\\s+|[Tt]ối\\s+|[Kk]huya\\s+|[Đđ]êm\\s+|[Hh]ôm\\s+)nay").matcher(this.raw).find()) {
            output.add(new DateTime(this.raw, today));
        }
        else if (Pattern.compile("(?:[Nn]gày\\s+|[Ss]áng\\s+|[Tt]rưa\\s+|[Cc]hiều\\s+|[Tt]ối\\s+|[Kk]huya\\s+|[Đđ]êm\\s+|[Hh]ôm\\s+)(ngày\\s+)?(mai|mơi|sau)").matcher(this.raw).find()) {
            Date dateOutput1 = Date.now("Asia/Bangkok");
            dateOutput1.plusDays(1);
            DateTime output1 = new DateTime(this.raw, dateOutput1);
            output.add(output1);
        }
        else if (Pattern.compile("(?:[Nn]gày\\s+|[Ss]áng\\s+|[Tt]rưa\\s+|[Cc]hiều\\s+|[Tt]ối\\s+|[Kk]huya\\s+|[Đđ]êm\\s+|[Hh]ôm\\s+)(ngày\\s+)?mốt").matcher(this.raw).find()) {
            Date dateOutput1 = Date.now("Asia/Bangkok");
            dateOutput1.plusDays(2);
            DateTime output1 = new DateTime(this.raw, dateOutput1);
            output.add(output1);
        }
        else if (Pattern.compile("(?:[Nn]gày\\s+|[Ss]áng\\s+|[Tt]rưa\\s+|[Cc]hiều\\s+|[Tt]ối\\s+|[Kk]huya\\s+|[Đđ]êm\\s+|[Hh]ôm\\s+)(ngày\\s+)?kia").matcher(this.raw).find()) {
            Date dateOutput1 = Date.now("Asia/Bangkok");
            dateOutput1.plusDays(3);
            DateTime output1 = new DateTime(this.raw, dateOutput1);
            output.add(output1);
        }
        else if ((this.raw.contains("thứ") || this.raw.contains("chủ nhật")) && (this.raw.contains("tuần này") || !this.raw.contains("tuần"))) {
            Date dateOutput1 = Date.now("Asia/Bangkok");
            if (this.raw.contains("2") || this.raw.contains("hai")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                dateOutput1.updateDate();
            }
            else if (this.raw.contains("3") || this.raw.contains("ba")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("4") || this.raw.contains("tư")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("5") || this.raw.contains("năm")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("6") || this.raw.contains("sáu")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("7") || this.raw.contains("bảy") || this.raw.contains("bẩy")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("chủ nhật")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
                dateOutput1.updateDate();
            }
            DateTime output1 = new DateTime(this.raw, dateOutput1);
            output.add(output1);
            output1.print();
        }
        else if ((this.raw.contains("thứ") || this.raw.contains("chủ nhật")) && this.raw.contains("tuần")) {
            Date dateOutput1 = Date.now("Asia/Bangkok");
            if (dateOutput1.weekday < 7) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("2") || this.raw.contains("hai")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                dateOutput1.updateDate();
            }
            else if (this.raw.contains("3") || this.raw.contains("ba")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("4") || this.raw.contains("tư")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("5") || this.raw.contains("năm")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("6") || this.raw.contains("sáu")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("7") || this.raw.contains("bảy") || this.raw.contains("bẩy")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
                dateOutput1.updateDate();
            }
            if (this.raw.contains("chủ nhật")) {
                dateOutput1.date = dateOutput1.date.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
                dateOutput1.updateDate();
            }
            DateTime output1 = new DateTime(this.raw, dateOutput1);
            output.add(output1);
            output1.print();
        }
        else if (Pattern.compile(ruleset.regex.get(1).pattern).matcher(this.raw).find()) {
            Matcher m = Pattern.compile(ruleset.regex.get(1).pattern).matcher(this.raw);
            int numMatches = m.groupCount();
            String[] notIn = new String[]{"/", ".", "-"};
            List<String> notIn_ = Arrays.asList(notIn);
            List<String> allMatches = new ArrayList<>();
            if (m.find()){
                for (int i = 1; i <= numMatches; i++) {
                    if (!notIn_.contains(m.group(i))) {
                        allMatches.add(m.group(i));
                    }
                }

                if (allMatches.size() == 8 && allMatches.get(1) == null) {
                    allMatches.remove(1);
                }
                if (allMatches.size() == 7 && allMatches.get(2) == null) {
                    allMatches.remove(2);
                }
                //System.out.println(allMatches);
                String day = allMatches.get(0);
                day = (allMatches.get(5) != null) ? allMatches.get(5) : day;
                day = (day != null) ? day : String.valueOf(today.day);
                String month = (allMatches.get(1) != null) ? allMatches.get(1) : String.valueOf(today.month);
                String year = (allMatches.get(2) != null) ? allMatches.get(2) : String.valueOf(today.year);

                day = (allMatches.get(3) != null) ? allMatches.get(3) : day;
                year = (allMatches.get(4) != null) ? allMatches.get(4) : year;
                if (plusOne_.contains(month)) {
                    month = (today.month < 12) ? String.valueOf(today.month + 1) : "1";
                    year = (isNumeric(year) && month.equals("1")) ? String.valueOf(Integer.parseInt(year) + 1) : year;
                }
                else if (remain_.contains(month)) {
                    month = String.valueOf(today.month);
                }
                if (plusOne_.contains(year)) {
                    year = String.valueOf(today.year + 1);
                }
                else if (remain_.contains(year)) {
                    year = String.valueOf(today.year);
                }
                Date dateOutput1 = new Date(Integer.parseInt(day), Integer.parseInt(month), Integer.parseInt(year));
                DateTime output1 = new DateTime(this.raw, dateOutput1);
                output.add(output1);
                output1.print();
            }
        }
        else if (Pattern.compile(ruleset.regex.get(6).pattern).matcher(this.raw).find()) {
            Matcher m = Pattern.compile(ruleset.regex.get(6).pattern).matcher(this.raw);
            if (m.find()) {
                String month = m.group(1);
                String year = m.group(2);
                if (plusOne_.contains(year)) {
                    year = String.valueOf(today.year + 1);
                }
                if (plusOne_.contains(month)) {
                    month = (today.month < 12) ? String.valueOf(today.month + 1) : "1";
                    year = (isNumeric(year) && month.equals("1")) ? String.valueOf(Integer.parseInt(year) + 1) : year;
                    year = (year == null) ? String.valueOf(today.year) : year;
                }
                Map<String, Integer> mappingMonth = new HashMap<>();
                mappingMonth.put("một", 1);
                mappingMonth.put("hai", 2);
                mappingMonth.put("ba", 3);
                mappingMonth.put("bốn", 4);
                mappingMonth.put("năm", 5);
                mappingMonth.put("sáu", 6);
                mappingMonth.put("bảy", 7);
                mappingMonth.put("bẩy", 7);
                mappingMonth.put("tám", 8);
                mappingMonth.put("chín", 9);
                mappingMonth.put("mười", 10);
                mappingMonth.put("mười một", 11);
                mappingMonth.put("mười hai", 12);
                if (mappingMonth.containsKey(month)) {
                    month = String.valueOf(mappingMonth.get(month));
                }
                Date dateOutput1 = new Date(today.day, Integer.parseInt(month), Integer.parseInt(year));
                DateTime output1 = new DateTime(this.raw, dateOutput1);
                output.add(output1);
                output1.print();
            }
        }
        return output;
    }

}

