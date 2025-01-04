package framework.exceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import framework.utilities.ErrorWrapper;

public class FieldValidationException extends Exception {
    List<ErrorWrapper> errors;
    Map<String, String> allMessages;

    public FieldValidationException(List<ErrorWrapper> errors) {
        this.errors = errors;
    }

    public List<ErrorWrapper> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorWrapper> errors) {
        this.errors = errors;
    }

    public Map<String, String> getAllMessages() {
        Map<String, String> messages = new HashMap<>();

        for (ErrorWrapper errorWrapper : errors) {
            Map<String, String> errorMessages = errorWrapper.getMessages();
            messages.putAll(errorMessages);
        }

        return messages;
    }
}
