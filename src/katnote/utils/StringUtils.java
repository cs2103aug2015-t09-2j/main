package katnote.utils;

public class StringUtils {
    
    /*
     * get the last word of the string
     */
    public static String getLastWord(String input){
        String[] parts = input.split("\\s+");
        String lastWord = parts[parts.length - 1];
        return lastWord;
    }
    
    /*
     * Trim the last word from string
     */  
    public static String trimLastWord(String input){
        String[] parts = input.split("\\s+");
        return join(parts, " ");
    }
    
    
    /*
     * Join string array into a string
     */
    public static String join(String[] parts, String delim){
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String s : parts){
            if (!first){
                builder.append(delim);
                first = true;
            }
            builder.append(s);
        }
        return builder.toString();
    }
}
