package framework.utilities;

import java.lang.reflect.Method;

public class Mapping {
    String className;
    String methodName;
    
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public String getMethodName() {
        return methodName;
    }
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Mapping(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }
    public Mapping() {
    }

    public Object invoke() throws Exception {
        Object o = null;
        
        Class<?> clazz = Class.forName(this.getClassName());
        Object ins = clazz.getConstructor().newInstance();

        Method m = clazz.getMethod(this.getMethodName(), null);

        o = m.invoke(ins, null);

        return o;
    }
}
