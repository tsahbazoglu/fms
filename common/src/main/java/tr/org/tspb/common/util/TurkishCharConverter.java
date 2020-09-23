package tr.org.tspb.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Telman Şahbazoğlu
 */
public class TurkishCharConverter {

    private static Map<String, String> mapTurkishToEnglish = new HashMap<String, String>();
    public final static TurkishCharConverter INSTANCE = new TurkishCharConverter();

    private static boolean checkIfThisMatchShouldBeReplaced() {
        return true;
    }

    private static String computeReplacementString(String key) {
        return mapTurkishToEnglish.get(key);
    }
    public static void main(String[] args) {
        
        //Pattern.compile("şŞ", Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE)
        Pattern pattern = Pattern.compile("(ö|Ö|ü|Ü|ı|İ)");
        StringBuffer myStringBuffer = new StringBuffer();
        Matcher matcher = pattern.matcher("öÖüÜğĞçÇ");
        while (matcher.find()) {
            if (checkIfThisMatchShouldBeReplaced()) {
                String replacement = computeReplacementString(matcher.group());
                if (replacement != null) {
                    matcher.appendReplacement(myStringBuffer, replacement);
                }
            }
        }
        matcher.appendTail(myStringBuffer);
        
    }
    private Map<String, String> map = new HashMap<String, String>();

    private TurkishCharConverter() {
        map.put("º", "Ğ");
        map.put("Ì", "Ş");
        map.put("¦", "İ");
        map.put("▄", "Ü");
        map.put("Ã", "Ç");
        map.put("Í", "Ö");
        map.put("ı", "I");


        mapTurkishToEnglish.put("İ", "I");//veya i, nasil olsa toLowerCase yapılcak
        mapTurkishToEnglish.put("ı", "I");
        mapTurkishToEnglish.put("ö", "O");
        mapTurkishToEnglish.put("Ö", "O");
        mapTurkishToEnglish.put("ü", "Ü");
        mapTurkishToEnglish.put("ğ", "G");
    }

    public String toTurkish(String value) {
        for (String key : map.keySet()) {
            value = value.replace(key, map.get(key));
        }
        return value;
    }

}
