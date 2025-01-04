package framework.utilities;

import java.lang.reflect.Field;

import framework.annotations.*;

public class Validator {
    Field field;
    Object valueTested;
    String paramName;

    private final String REQUIRED_MESSAGE = " is required";
    private final String NUMBER_MESSAGE = " must be a numeric value";
    private final String STRICT_MIN_MESSAGE = " must be superior to ";
    private final String STRICT_MAX_MESSAGE = " must be inferior to ";
    private final String MIN_MESSAGE = " must be superior or equal to ";
    private final String MAX_MESSAGE = " must be inferior or equal to ";
    private final String LENGTH_MESSAGE = " must have a length inferior to ";
    private final String TEXT_MESSAGE = " must be a text value";

    public Validator() {
    }

    public Validator(Field field, Object valueTested, String paramName) {
        this.field = field;
        this.valueTested = valueTested;
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Object getValueTested() {
        return valueTested;
    }

    public void setValueTested(Object valueTested) {
        this.valueTested = valueTested;
    }

    public ErrorWrapper performFullCheck() {
        ErrorWrapper ew = new ErrorWrapper();

        if (!checkRequired()) {
            ew.addMessage("error_" + paramName + "Required", paramName + REQUIRED_MESSAGE);
        }
        if (checkLength() != null) {
            ew.addMessage("error_" + paramName + "Length", checkLength());
        }
        if (!checkNumeric()) {
            ew.addMessage("error_" + paramName + "Number", paramName + NUMBER_MESSAGE);
        }
        if (checkMin() != null) {
            ew.addMessage("error_" + paramName + "Min", checkMin());
        }
        if (checkMax() != null) {
            ew.addMessage("error_" + paramName + "Max", checkMax());
        }

        return ew;
    }

    public boolean checkRequired() {
        if (field.isAnnotationPresent(Required.class) && valueTested == null) {
            return false;
        }

        return true;
    }

    public String checkLength() {
        if (field.isAnnotationPresent(Length.class)) {
            if (!CharSequence.class.isAssignableFrom(field.getType())) {
                return paramName + TEXT_MESSAGE;
            } else {
                Length l = field.getAnnotation(Length.class);
                int value = l.value();

                CharSequence text = (CharSequence) valueTested;
                if (text.length() > value) {
                    return paramName + LENGTH_MESSAGE + value;
                }
                return null;
            }
        }
        return null;
    }

    private boolean checkNumeric(Field f) {
        Class<?> clazz = f.getType();
        return Number.class.isAssignableFrom(clazz) || clazz == int.class || clazz == double.class
                || clazz == float.class ||
                clazz == long.class || clazz == short.class || clazz == byte.class;
    }

    private boolean checkNumeric(Object o) {
        Class<?> clazz = o.getClass();
        return Number.class.isAssignableFrom(clazz) || clazz == int.class || clazz == double.class
                || clazz == float.class ||
                clazz == long.class || clazz == short.class || clazz == byte.class;
    }

    public boolean checkNumeric() {
        if (field.isAnnotationPresent(Numeric.class)
                && (!checkNumeric(field) || !checkNumeric(valueTested))) {
            return false;
        }

        return true;
    }

    public String checkMin() {
        if (field.isAnnotationPresent(Min.class)) {
            Min min = field.getAnnotation(Min.class);
            double minVal = min.value();
            boolean isStrict = min.isStrict();

            Number n = (Number) valueTested;

            if (isStrict && !(Double.compare(minVal, n.doubleValue()) < 0)) {
                return paramName + STRICT_MIN_MESSAGE + minVal;
            } else if(Double.compare(minVal, n.doubleValue()) >= 0) {
                return paramName + MIN_MESSAGE + minVal;
            }
        }

        return null;
    }

    public String checkMax() {
        if (field.isAnnotationPresent(Max.class)) {
            Max min = field.getAnnotation(Max.class);
            double maxVal = min.value();
            boolean isStrict = min.isStrict();

            Number n = (Number) valueTested;

            if (isStrict && (Double.compare(maxVal, n.doubleValue()) < 0)) {
                return paramName + STRICT_MAX_MESSAGE + maxVal;
            } else if(Double.compare(maxVal, n.doubleValue()) <= 0) {
                return paramName + MAX_MESSAGE + maxVal;
            }
        }

        return null;
    }
}
