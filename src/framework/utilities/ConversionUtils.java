package framework.utilities;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ConversionUtils {
    public static Object convert(String value, Class<?> goalClass) {
        if(value == null || (goalClass != String.class && value.isBlank())) {
            return null;
        } else if(goalClass == int.class || goalClass == Integer.class) {
            return Integer.parseInt(value);
        } else if(goalClass == float.class || goalClass == Float.class) {
            return Float.parseFloat(value);
        } else if(goalClass == double.class || goalClass == Double.class) {
            return Double.parseDouble(value);
        } else if(goalClass == long.class || goalClass == Long.class) {
            return Long.parseLong(value);
        } else if(goalClass == short.class || goalClass == Short.class) {
            return Short.parseShort(value);
        } else if(goalClass == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if(goalClass == char.class) {
            return value.charAt(0);
        } else if(goalClass == LocalDate.class) {
            return LocalDate.parse(value);
        } else if(goalClass == LocalDateTime.class) {
            return LocalDateTime.parse(value);
        } else if(goalClass == Time.class) {
            return Time.valueOf(value);
        } else {
            return value;
        }

    }
}
