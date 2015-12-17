package com.rxsync;

import com.rxsync.annotations.Defer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RxMappingProxy {
    private static ConcurrentMap<String, ObjectMapping> subscriberConcurrentMap = new ConcurrentHashMap<>();

    public static RxMappingProxy instance = new RxMappingProxy();

    public static RxMappingProxy getInstance() {
        return instance;
    }

    public void register(Object targetObject) throws NoSuchMethodException {

        ObjectMapping objectMethodsMapping = new ObjectMapping(targetObject);
        String mainDefer = objectMethodsMapping.getDefer();
        if (isIllegal(mainDefer))
            throw new IllegalArgumentException(String.format("duplication Subscriber occurs! with key : %s", mainDefer));

        subscriberConcurrentMap.put(mainDefer, objectMethodsMapping);
    }

    public void unRegister(Class clazz) {
        Class interfaceClazz = (Class) clazz.getGenericInterfaces()[0];
        Defer defer = (Defer) interfaceClazz.getAnnotation(Defer.class);
        String mainDefer = defer.value();
        subscriberConcurrentMap.remove(mainDefer);
    }

    public void exec(String message) {
        Object target = null;
        Method method = null;
        MethodMapping methodMap = null;
        String path = "";
        for (String key : subscriberConcurrentMap.keySet()) {
            if (message.startsWith(key)) {
                ObjectMapping methodParamMap = subscriberConcurrentMap.get(key);
                target = methodParamMap.getTargetObject();
                path = message.substring(key.length());
                List<MethodMapping> methodList = methodParamMap.getMethodMappingList();
                for (MethodMapping methodMapping : methodList) {
                    if (methodMapping.matchPath(path)) {
                        methodMap = methodMapping;
                        method = methodMapping.getRealMethod();
                        break;
                    }
                }
            }
        }

        if (null == target || null == method){
            throw new IllegalArgumentException("not found subscriber");
        }

        Object[] objs = methodMap.getMethodParams(path);

        try {
            method.invoke(target, objs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    private boolean isIllegal(String key) {
        return subscriberConcurrentMap.containsKey(key);
    }

}
