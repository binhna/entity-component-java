import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Comparator.comparing;


public class Main {
    public static void main(String[] args) throws IOException {

        String listEntitiesPath = "resources/list_entity.json";
        String rulePath = "resources/regex/";
        Map<String, RuleSet> mapRuleSet = getRuleSet(listEntitiesPath, rulePath, "vi");
        entityDetection("Tôi muốn cài báo thức lúc 12h đêm và 5:20", mapRuleSet);
        inferTest("Test_NER_offline - binhna.csv", "Test_NER_offline_out - binhna.csv", mapRuleSet);
    }

    public static String entityDetection(String input, Map<String, RuleSet> ruleset) {

        List<Entity> outputResult = new ArrayList<>();
        List<String> outputString = new ArrayList<>();
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
                        //System.out.println(entityType + ": " + raw);
                        if (entityType.equals("_DATETIME")) {
                            DateTime entity = new DateTime(raw, new Date());
                            List<DateTime> output = entity.normalize(ruleSet);
                            for (DateTime dt: output) {
                                dt.findOffset(input);
                                outputResult.add(dt);
                            }
                        }
                        else if (entityType.equals("_TIME")) {
                            Time entity = new Time(raw, LocalDateTime.now());
                            List<Time> output = entity.normalize(ruleSet, input);
                            for (Time dt: output) {
                                dt.findOffset(input);
                                outputResult.add(dt);
                            }
                        }
                    }
                }
            }
        }
        outputResult = removeNested(outputResult);
        outputResult = alignTime(outputResult);
        for (Entity e: outputResult) {
            e.print();
            outputString.add(e.getString());
        }
        return String.join(" <> ", outputString);
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

    public static List<Entity> removeNested(List<Entity> entities) {
        List<Entity> result = new ArrayList<>();
        Collections.sort(result, comparing(Entity::getStartOffset));
        List<Integer> removeIds = new ArrayList<>();
        for (int i = 0; i < entities.size(); i++) {
            for (int j = 0; j < entities.size(); j++) {
                if (entities.get(i).startOffset >= entities.get(j).startOffset &&
                        entities.get(i).endOffset <= entities.get(j).endOffset && i < j &&
                            entities.get(i).type.equals(entities.get(j).type)) {
                    removeIds.add(i);
                }
            }
        }
        for (int i = 0; i < entities.size(); i++) {
            if (!removeIds.contains(i)) {
                result.add(entities.get(i));
            }
        }
        return result;
    }

    public static void inferTest(String csvPathIn, String csvPathOut, Map<String, RuleSet> mapRuleSet) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPathIn))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter csvWriter = new FileWriter(csvPathOut);
        for (int i=0; i < records.size(); i++) {
            if (i > 0) {
                List<String> rowData = new ArrayList<>();
                rowData.add(records.get(i).get(0));
                rowData.add(records.get(i).get(1));
                String input = records.get(i).get(1);
                String outputString = entityDetection(input, mapRuleSet);
                rowData.add(outputString);
                csvWriter.append(String.join(",", rowData));
                csvWriter.append("\n");
            }
        }

        csvWriter.flush();
        csvWriter.close();
    }

    public static List<Entity> alignTime(List<Entity> entities) {
        List<Entity> newEntities = new ArrayList<>();
        for (int i=0; i < entities.size(); i++) {
            if (entities.get(i).type.equals("_TIME")) {
                for (int j = 0; j < entities.size(); j++) {
                    if (entities.get(j).type.equals("_DATETIME")) {
                        Entity eDT = entities.get(j);
                        List<Integer> valuesDT = eDT.getValues();
                        LocalDate dDT = LocalDate.of(valuesDT.get(2), valuesDT.get(1), valuesDT.get(0));
                        Entity eT = entities.get(i);
                        List<Integer> valuesT = eT.getValues();
                        LocalDate dT = LocalDate.of(valuesT.get(2), valuesT.get(1), valuesT.get(0));
                        if (dDT.isAfter(dT)) {
                            Time newEntity = new Time(eT.raw, LocalDateTime.of(valuesDT.get(2), valuesDT.get(1), valuesDT.get(0), valuesT.get(3), valuesT.get(4), valuesT.get(5)));
                            entities.set(i, newEntity);
                        }
                    }
                }
            }
            newEntities.add(entities.get(i));
        }
        return newEntities;
    }
}