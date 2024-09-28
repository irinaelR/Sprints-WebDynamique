package mg.itu.prom16;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;

import jakarta.servlet.http.HttpSession;

public class CustomSession {
    private HashMap<String, Object> values;

    public CustomSession() {
    }
    public CustomSession(HttpSession session) {
        HashMap<String, Object> sessionKeyValues = getSessionAttributes(session);
        this.setValues(sessionKeyValues);
    }

    private void setValues(HashMap<String, Object> values) {
        this.values = values;
    }

    public static HashMap<String, Object> getSessionAttributes(HttpSession session) {
        HashMap<String, Object> attributesMap = new HashMap<>();

        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object attributeValue = session.getAttribute(attributeName);

            attributesMap.put(attributeName, attributeValue);
        }

        return attributesMap;
    }

    public void replaceSession(HttpSession session) {
        // Get the attribute names from the session
        Enumeration<String> attributeNames = session.getAttributeNames();

        // Iterate over the attribute names and remove each one
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            session.removeAttribute(attributeName);
        }

        // replace by the contents of this class' values
        for(Entry<String, Object> element : this.values.entrySet()) {
            session.setAttribute(element.getKey(), element.getValue());
        }
    }

    public void add(String key, Object value) {
        this.values.put(key, value);
    }

    public void remove(String key) {
        this.values.remove(key);
    }

    public Object get(String key) {
        return this.values.get(key);
    }

    public void update(String key, Object newValue) {
        this.values.replace(key, newValue);
    }
}
