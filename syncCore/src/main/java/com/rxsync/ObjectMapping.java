package com.rxsync;

import com.rxsync.annotations.Defer;
import com.rxsync.annotations.Path;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectMapping {
    private String defer;
    private Object targetObject;
    private List<MethodMapping> methodMappingList = new ArrayList<>();

    public ObjectMapping(Object object){
        Class interfaceClazz = (Class) object.getClass().getGenericInterfaces()[0];
        Defer defer = (Defer) interfaceClazz.getAnnotation(Defer.class);
        this.defer = defer.value();
        targetObject = object;
        registerMethod(interfaceClazz);
    }

    private void registerMethod(Class clazz){
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            MethodMapping methodMapping = MethodMapping.build(method);
            if (isLegal(methodMapping)){
                Method realMethod = null;
                try {
                    realMethod = targetObject.getClass().getMethod(method.getName(),method.getParameterTypes());
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                if (null == realMethod){
                    throw new IllegalArgumentException(String.format("class : %s does not implement method : %s", targetObject.getClass().getName(), method.getName()));
                }
                methodMapping.setRealMethod(realMethod);
                methodMappingList.add(methodMapping);
            } else {
                System.out.println(String.format("illegal method occurs : %s", methodMapping.getOriginalPath()));
            }
        }
    }

    private boolean isLegal(MethodMapping methodMapping){
        if (null == methodMapping) return false;

        for (MethodMapping method : methodMappingList){
            if (method.equals(methodMapping)){
                return false;
            }
        }
        return true;
    }

    public String getDefer() {
        return defer;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public List<MethodMapping> getMethodMappingList() {
        return methodMappingList;
    }
}
