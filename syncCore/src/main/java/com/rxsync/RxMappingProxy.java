package com.rxsync;

import com.rxsync.annotations.Defer;
import com.rxsync.annotations.Param;
import com.rxsync.annotations.Path;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RxMappingProxy {
    private static ConcurrentMap<String, MethodParamMap> subscriberConcurrentMap = new ConcurrentHashMap<>();
    public static RxMappingProxy instance = new RxMappingProxy();

    public static RxMappingProxy getInstance() {
        return instance;
    }

    public void register(Object targetObject) throws NoSuchMethodException {
        Class interfaceClazz = (Class) targetObject.getClass().getGenericInterfaces()[0];
        Defer defer = (Defer) interfaceClazz.getAnnotation(Defer.class);
        String mainDefer = defer.value();

        Method[] methods = interfaceClazz.getDeclaredMethods();
        String key = "";
        for (Method method : methods) {
            Path path = method.getAnnotation(Path.class);
            String pathString = path.value();
            key = getKey(mainDefer, pathString);
            checkKeyIllegal(key);

            List<String> params = getParams(pathString);

            Annotation[][] annotations = method.getParameterAnnotations();
            Method targetMethod = null;
            try {
                targetMethod = targetObject.getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            if (null == targetMethod) {
                throw new NoSuchMethodException(String.format("Method named : %s not found Exception", method.getName()));
            }

            MethodParamMap methodParamMap = new MethodParamMap(targetObject, targetMethod);
            for (Annotation[] paramAnnotation : annotations) {
                String value = ((Param) paramAnnotation[0]).value();
                if (params.contains(value)) {
                    int index = params.indexOf(value);
                    methodParamMap.addParam(index);
                    continue;
                } else {
                    throw new IllegalArgumentException(String.format("Subscriber method parameter(%s) not found!", value));
                }
            }
            subscriberConcurrentMap.put(key, methodParamMap);
        }
    }

    public void unRegister(Object targetObject) {
        Class interfaceClazz = (Class) targetObject.getClass().getGenericInterfaces()[0];
        Defer defer = (Defer) interfaceClazz.getAnnotation(Defer.class);
        String mainDefer = defer.value();

        Method[] methods = interfaceClazz.getDeclaredMethods();
        for (Method method : methods) {
            Path path = method.getAnnotation(Path.class);
            String pathString = path.value();
            String key = getKey(mainDefer, pathString);
            subscriberConcurrentMap.remove(key);
        }
    }

    public void exec(String message) {
        for (String key : subscriberConcurrentMap.keySet()) {
            if (message.startsWith(key)) {
                MethodParamMap methodParamMap = subscriberConcurrentMap.get(key);

                List<Integer> paramIndex = methodParamMap.getParamIndexList();
                int paramLength = paramIndex.size();

                String paramString = message.substring(key.length() + 1);
                String[] paramers = parseParamer(paramString);
                Object[] objs = new Object[paramLength];
                for (int index = 0; index < paramLength; index++) {
                    Array.set(objs, index, paramers[paramIndex.get(index).intValue()]);
                }
                Object target = methodParamMap.getTargetObject();
                Method method = methodParamMap.getMethod();
                try {
                    method.invoke(target, objs);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        throw new IllegalArgumentException(String.format("not found subscriber to deal with message : %s", message));
    }

    private String getKey(String mainDefer, String pathString) {
        int pre = pathString.indexOf("/{");
        return mainDefer + pathString.substring(0, pre);
    }

    private List<String> getParams(String pathString) {
        Pattern paramPattern = Pattern.compile("\\{([\\s\\S\\d]+)\\}+$");

        int pre = pathString.indexOf("/{");
        String clDefer = pathString.substring(0, pre);
        String pathParam = pathString.substring(pre + 1);

        List<String> paramList = new ArrayList<>();
        String[] params = pathParam.split("/");
        for (String param : params) {
            Matcher matcher = paramPattern.matcher(param);
            while (matcher.find()) {
                String groupName = matcher.group(1);
                paramList.add(groupName);
            }
        }
        return paramList;
    }

    private void checkKeyIllegal(String key) {
        for (String subscriberKey : subscriberConcurrentMap.keySet()) {
            if (key.startsWith(subscriberKey)) {
                throw new IllegalArgumentException(String.format("duplication Subscriber occurs! with key : %s as %s", key, subscriberKey));
            }
        }
    }

    private String[] parseParamer(String pathParamers) {
        return pathParamers.split("/");
    }
}
