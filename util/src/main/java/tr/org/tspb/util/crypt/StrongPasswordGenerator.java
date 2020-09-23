package tr.org.tspb.util.crypt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Cihan Coşgun
 */
public class StrongPasswordGenerator {

    private static final Pattern strongRegex = Pattern.compile("[a-z]{2,3}[A-Z]{2,3}[0-9]{2,3}[!@#%\\*\\?\\+\\-]{2,3}");


    public static String generateStrongPassword() {
        Xeger generator = new Xeger(strongRegex.pattern());
        String result = generator.generate();

        // shuffle
        List<Character> chars = new ArrayList<>();
        for (char c : result.toCharArray()) {
            chars.add(c);
        }
        Collections.shuffle(chars);

        // join
        StringBuilder sb = new StringBuilder();
        for (Character c : chars) {
            sb.append(c);
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        StrongPasswordGenerator.generateStrongPassword();
    }
    public StrongPasswordGenerator() {
    }
}
