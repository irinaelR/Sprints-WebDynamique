package framework.utilities;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

import framework.annotations.Param;
import jakarta.servlet.http.HttpServletRequest;

public class Mapping {
    String className;
    String methodName;
    Parameter[] params;
    
    public Parameter[] getParams() {
        return params;
    }
    public void setParams(Parameter[] params) {
        this.params = params;
    }
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

    public Mapping(String className, String methodName, Parameter[] params) {
        this.className = className;
        this.methodName = methodName;
        this.params = params;
    }
    public Mapping() {
    }

    public Object invoke(Object[] args) throws Exception {
        Object o = null;
        
        Class<?> clazz = Class.forName(this.getClassName());
        Object ins = clazz.getConstructor().newInstance();

        Method m = clazz.getMethod(this.getMethodName(), this.extractParamTypes());

        o = m.invoke(ins, args);

        return o;
    }

    public Class<?> getReturnType() throws Exception {
        Class<?> clazz = null;

        try {
            Method m = Class.forName(this.getClassName()).getDeclaredMethod(this.getMethodName(), this.extractParamTypes());
            clazz = m.getReturnType();
        } catch (ClassNotFoundException cnfe) {
            throw new Exception("La classe " + this.getClassName() + " dans votre object Mapping n'existe pas", cnfe);
        } catch (NoSuchMethodException nsme) {
            throw new Exception("La fonction " + this.getMethodName() + " dans La classe " + this.getClassName() + " n'existe pas", nsme);
        }

        return clazz;
    }

    public Class[] extractParamTypes() {
        Class[] classArr = null;

        List<Class<?>> tempList = new ArrayList<>();
        for (Parameter p : this.params) {
            tempList.add(p.getType());
        }

        if(tempList.size() > 0) {
            classArr = new Class[tempList.size()];
            for (int i = 0; i < classArr.length; i++) {
                classArr[i] = tempList.get(i);
            }
        }

        return classArr;
    }

    public Object[] findParamsInRequest(HttpServletRequest req) throws Exception {
        List<Object> args = new ArrayList<>();

        Class<?> clazz = Class.forName(this.getClassName());
        Method m = clazz.getMethod(this.getMethodName(), this.extractParamTypes());

        try {
            // Paranamer paranamer = new AdaptiveParanamer();
            // String[] paramNames = paranamer.lookupParameterNames(m);
    
            for (int i = 0; i < this.params.length; i++) {
                Parameter p = this.params[i];
                Object o = null;
                String key = "";
    
                // getting the inline parameter name
                if(p.isAnnotationPresent(Param.class)) {
                    Param annotationParam = (Param) p.getAnnotation(Param.class);
                    key = annotationParam.name();
                } else {
                    // key = paramNames[i];
                    key = p.getName();
                }
    
                Class<?> paramType = p.getType();
                if(!paramType.isPrimitive() && paramType != String.class) {
                    // creating the object to pass in argument
                    Constructor c = paramType.getDeclaredConstructor();
                    o = c.newInstance();
    
                    // setting each of its attributes or fields
                    Field[] attributes = paramType.getDeclaredFields();
                    for (Field attr : attributes) {
                        try {
                            String attrKey = key + ".";
                            if(attr.isAnnotationPresent(framework.annotations.Field.class)) {
                                // the request parameters would be of the format 'paramName.name' (name got from the Field annotation)
                                framework.annotations.Field f = attr.getAnnotation(framework.annotations.Field.class);
                                attrKey += f.name();
                            } else {
                                // the request parameters would be of the format 'paramName.attrName'
                                attrKey += attr.getName();
                            }

                            String attrValStr = req.getParameter(attrKey);
                                
                            // setting the attribute of the object o
                            Method setter = ReflectUtils.setter(attr, paramType);
                            setter.invoke(o, ConversionUtils.convert(attrValStr, attr.getType()));
                        } catch (Exception e) {
                            throw e;
                        }
                    }
                } else {
                    String valueStr = req.getParameter(key);
                    o = ConversionUtils.convert(valueStr, paramType);
                }
                
                args.add(o);
            }
        } catch (ParameterNamesNotFoundException e) {
            throw new Exception(String.format("Parameter names not found (parameter array size:%d)", this.params.length), e);
        }
        

        if(args.size() > 0) {
            return args.toArray();
        } else {
            return null;
        }
    }
}
