import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {
    public static void main(String[] args) {

        String listEntitiesPath = "resources/list_entity.json";
        String rulePath = "resources/regex/";
        Map<String, RuleSet> mapRuleSet = getRuleSet(listEntitiesPath, rulePath, "vi");
        entityDetection("đặt phòng từ 01/10/2020_ 03/10/2020", mapRuleSet);
    }

    public static void entityDetection(String input, Map<String, RuleSet> ruleset) {

        for (String entityType: ruleset.keySet()) {
            RuleSet ruleSet = ruleset.get(entityType);
            for (RegexEntry regex: ruleSet.regex) {
                Matcher m = Pattern.compile(regex.pattern).matcher(input);
                if (m.find()) {
                    String[] matches_ = Pattern.compile(regex.pattern)
                            .matcher(input)
                            .results()
                            .map(MatchResult::group)
                            .toArray(String[]::new);
                    List<String> matches = Arrays.asList(matches_);
                    for (String raw: matches){
                        if (entityType.equals("_DATETIME")) {
                            DateTime entity = new DateTime(raw, new Date());
                            List<DateTime> output = entity.normalize(ruleSet);
                        }
                    }
                }
            }
        }
    }

    public static Map<String, RuleSet> getRuleSet(String listEntitiesPath, String rulePath, String lang) {
        Map<String, RuleSet> typeRule = new HashMap<>();

        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get(listEntitiesPath));
            List<String> name_entities = gson.fromJson(reader, new TypeToken<List<String>>(){}.getType());

            // regex['_DATETIME'] = [{}, {}, ...]

            for (String name_entity: name_entities) {
                String path =  rulePath + lang + "/" + name_entity + ".json";
                String name_entity_upper = "_" + name_entity.toUpperCase();
                Reader reader_2 = Files.newBufferedReader(Paths.get(path));
                RuleSet ruleSet = gson.fromJson(reader_2, RuleSet.class);
                ruleSet.regexToJava();
                typeRule.put(name_entity_upper, ruleSet);
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return typeRule;
    }
}