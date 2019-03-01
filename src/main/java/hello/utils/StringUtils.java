package hello.utils;

public class StringUtils {

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }
    
    public static boolean isNullOrWhitespace(String s) {
        return s == null || isWhitespace(s);

    }

    private static boolean isWhitespace(String s) {
        int length = s.length();

        if (length <= 0) {
            return false;
        }
        
        for (int i = 0; i < length; i++) {
            if (s.charAt(i) > ' ') return false;
        }

        return true;
    }
}
