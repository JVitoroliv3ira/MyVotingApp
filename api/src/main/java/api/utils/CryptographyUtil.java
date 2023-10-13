package api.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CryptographyUtil {
    private static BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public static String encodeValue(String value) {
        return encoder.encode(value);
    }

    public static Boolean valuesMatches(String rawValue, String encodedValue) {
        return encoder.matches(rawValue, encodedValue);
    }
}
