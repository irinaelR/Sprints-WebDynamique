package framework.utilities;

import java.lang.reflect.*;

public class ReflectUtils {
    public static Method setter(Field f, Class<?> clazz) throws NoSuchMethodException {
        Method m = null;

        String setterName = getSetterName(f);
        Class<?> paramType = f.getType();

        m = clazz.getDeclaredMethod(setterName, paramType);

        return m;
    }

    public static String getSetterName(Field f) {
        String setterName = "set";

        // capitalizing the field name to match the camelCase convention
        String fieldName = f.getName();
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1).toLowerCase();

        setterName += fieldName;

        return setterName;
    }
}
