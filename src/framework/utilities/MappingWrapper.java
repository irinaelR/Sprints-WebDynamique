package framework.utilities;

import java.util.HashMap;

public class MappingWrapper {

    HashMap<String, Mapping> verbMethods;

    public MappingWrapper() {
        this.verbMethods = new HashMap<>();
    }

    public MappingWrapper(HashMap<String, Mapping> verbMethods) {
        this.verbMethods = verbMethods;
    }

    public HashMap<String, Mapping> getVerbMethods() {
        return verbMethods;
    }

    public void setVerbMethods(HashMap<String, Mapping> verbMethods) {
        this.verbMethods = verbMethods;
    }

    public void addMapping(String verb, Mapping m) throws IllegalArgumentException {
        if(verbMethods.containsKey(verb)) {
            throw new IllegalArgumentException("Verb method " + verb + " already exists in this wrapper");
        } 
        if (verbMethods.containsValue(m)) {
            throw new IllegalArgumentException("Class method of the mapping object already exist in this wrapper");
        }

        this.verbMethods.put(verb, m);
    }

    public boolean supportsVerb(String verb) {
        return verbMethods.containsKey(verb);
    }

    public Mapping getMapping(String verb) {
        return verbMethods.get(verb);
    }
}
