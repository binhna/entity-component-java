import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static void main(String[] args) {

        Map<String, RuleSet> typeRule = new HashMap<>();

        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("/home/binhna/Downloads/vinbdi/entity-component-system/resources/list_entity.json"));
            List<String> name_entities = gson.fromJson(reader, new TypeToken<List<String>>(){}.getType());

            // regex['_DATETIME'] = [{}, {}, ...]


            for (String name_entity: name_entities) {
                String path = "/home/binhna/Downloads/vinbdi/entity-component-system" + "/resources/regex/vi/" + name_entity + ".json";
                String name_entity_upper = "_" + name_entity.toUpperCase();
                Reader reader_2 = Files.newBufferedReader(Paths.get(path));
                RuleSet ruleSet = gson.fromJson(reader_2, RuleSet.class);
                typeRule.put(name_entity_upper, ruleSet);
            }
            reader.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        DateTime entity = new DateTime("23-25/9", new Date());
        entity.normalize(typeRule.get("_DATETIME"));
//        convert_duration_vi(entity);
//        convert_datetime_vi(entity);
    }

    public static DateTime convert_duration_vi(DateTime entity) {
        if (entity.raw == null) {
            return entity;
        }
        Pattern r = Pattern.compile("(\\d)+(?:\\s?ngay|\\s?ngày|[nN])\\s?(\\d)+(?:\\s?đem|\\s?đêm|\\s?dem|[dĐDđ])");
        Matcher m = r.matcher(entity.raw);
        if (m.find()) {
            System.out.println("Found value: " + m.group());
        }
        return null;
    }

    public static DateTime convert_datetime_vi(DateTime entity) {
        if (entity.raw == null) {
            return entity;
        }
        Date today = Date.now("Asia/Bangkok");

        Date output = new Date();
        System.out.println(output);

//        Pattern r = Pattern.compile();
//        Matcher m = r.matcher(entity_raw_str);
//        if (m.find()) {
//            System.out.println("Found value: " + m.group());
//        }


        return entity;
    }
}