package katnote.utils;

import java.util.Arrays;
import java.util.List;

public class StringUtils {

    /*
     * get the last word of the string
     */
    public static String getLastWord(String input) {
        String[] parts = input.trim().split("\\s+");
        String lastWord = parts[parts.length - 1];
        return lastWord;
    }

    /*
     * Trim the last word from string
     */
    public static String trimLastWord(String input) {
        List<String> parts = Arrays.asList(input.split("\\s+"));
        parts.remove(parts.size() - 1);
        return join(parts, " ");
    }

    /*
     * Remove the first word from input string For example: if input string is
     * "hello I am a programmer", the returned output is "I am a programmer"
     * 
     * @param input the input string
     * 
     * @return The resulting string
     */
    public static String removeFirstWord(String input) {
        return input.replaceFirst(getFirstWord(input), "").trim();
    }

    /*
     * Return the first word of input string For example: if input string is
     * "hello I am a programmer", the returned output is "hello"
     * 
     * @param input the input string
     * 
     * @return The resulting string
     */
    public static String getFirstWord(String input) {
        String firstWord = input.trim().split("\\s+")[0];
        return firstWord;
    }

    /*
     * Join string array into a string
     */
    public static String join(List<String> parts, String delim) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String s : parts) {
            if (!first) {
                builder.append(delim);
                first = true;
            }
            builder.append(s);
        }
        return builder.toString();
    }
    
    /*
     * Concatenates string list
     */
    public static String concat(String ...strings){
        StringBuilder result = new StringBuilder();
        for (String str : strings){
            if (str != null){
                result.append(str);
            }
        }
        return result.toString();
    }
}
