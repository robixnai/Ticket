package br.com.cast.ticket.util;

/**
 * @author
 */
public class StringUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty() || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !StringUtils.isEmpty(str);
    }

    public static boolean equals(String ... values) {
        if (values == null || values.length == 0) {
            throw new RuntimeException("StringUtils.equals, parameters cannot be null");
        }

        String old = values[0];
        for (String s : values) {
            if (!old.equals(s)) {
                return false;
            }
        }
        return true;
    }
}