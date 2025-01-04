package framework.utilities;

import java.util.HashMap;
import java.util.Map;

public class ErrorWrapper {

    Map<String, String> messages;

    public ErrorWrapper() {
        this.messages = new HashMap<>();
    }

    public ErrorWrapper(Map<String, String> messages) {
        this.messages = messages;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

    public void addMessage(String key, String message) {
        if (!key.startsWith("error")) {
            key = "error" + key.substring(0, 1).toUpperCase() + key.substring(1).toLowerCase();
        }

        this.messages.put(key, message);
    }

    public boolean hasErrors() {
        return this.messages.size() > 0;
    }
}
