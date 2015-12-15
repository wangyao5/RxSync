package com.rxsync;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodParamMap {
    private Object targetObject;
    private Method method;
    private List<Integer> paramListIndex;

    public MethodParamMap(Object object, Method method) {
        this.targetObject = object;
        this.method = method;
    }

    public void addParam(int index){
        if (null == paramListIndex) paramListIndex = new ArrayList<>();
        paramListIndex.add(index);
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public Method getMethod() {
        return method;
    }

    public List<Integer> getParamIndexList() {
        return paramListIndex;
    }
}
