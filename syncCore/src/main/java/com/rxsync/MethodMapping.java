package com.rxsync;

import com.rxsync.annotations.Path;
import com.rxsync.annotations.PathParam;
import com.rxsync.annotations.QueryParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodMapping {
    public static final String PATTERN = "(\\{[a-zA-Z0-9]+\\})";

    private String path;
    private String originalPath;
    private String parsePattern;
    private Method interfaceMethod;
    private Method realMethod;
    private List<String> paramNameList;

    private MethodMapping(Method interfaceMethod, String originalPath, List<String> paramNameList) {
        this.interfaceMethod = interfaceMethod;
        this.originalPath = originalPath;
        this.path = getMethodPathKey(originalPath);
        this.parsePattern = originalPath.replaceAll(PATTERN, "([a-zA-Z0-9]+)");
        this.paramNameList = paramNameList;
    }

    public static MethodMapping build(Method method) {
        Path path = getPath(method);
        if (null == path) return null;

        List<String> paramList = initPathParamNameSet(path.value());
        return new MethodMapping(method, path.value(), paramList);
    }

    private static Path getPath(Method method) {
        try {
            return method.getAnnotation(Path.class);
        } catch (Exception e) {

        }
        return null;
    }

    private static String getMethodPathKey(String methodPath) {
        return methodPath.replaceAll(PATTERN, "%s");
    }

    private static List<String> initPathParamNameSet(String pathString) {
        Pattern pattern = Pattern.compile("\\{([a-zA-Z0-9]+)\\}");
        Matcher matcher = pattern.matcher(pathString);
        List<String> paramNameList = new ArrayList<>();
        while (matcher.find()) {
            String pathParamName = matcher.group(1);
            if (paramNameList.contains(pathParamName)) {
                throw new IllegalArgumentException("duplication path param path");
            }
            paramNameList.add(pathParamName);
        }
        return paramNameList.size() > 0 ? paramNameList : null;
    }


    public String getPath() {
        return path;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    public Method getRealMethod() {
        return realMethod;
    }

    public void setRealMethod(Method realMethod) {
        this.realMethod = realMethod;
    }

    public boolean matchPath(String path) {
        Pattern pattern = Pattern.compile(parsePattern);
        Matcher matcher = pattern.matcher(path);
        return matcher.find();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MethodMapping) {
            MethodMapping methodMapping = (MethodMapping) obj;
            return methodMapping.path.equals(this.path);
        }

        return false;
    }

    public Object[] getMethodParams(String path) {
        List<String> list = new ArrayList<>();
        Matcher matcher = Pattern.compile(parsePattern).matcher(path);
        Annotation[][] annotations = interfaceMethod.getParameterAnnotations();
        while (matcher.find()) {
            for (Annotation[] paramAnnotation : annotations) {
                for (Annotation paramAnno : paramAnnotation) {

                    if (paramAnno instanceof PathParam) {
                        int pathIndex = paramNameList.indexOf(((PathParam) paramAnno).value());
                        list.add(matcher.group(pathIndex + 1));
                    }

                    if (paramAnno instanceof QueryParam) {
                        String name = ((QueryParam) paramAnno).value();
                        //解析QueryParam
                        Pattern pattern = Pattern.compile(name + "=" + "([a-zA-Z0-9]+)&?");

                        Matcher queryMatcher = pattern.matcher(path);
                        while (queryMatcher.find()){
                            list.add(queryMatcher.group(1));
                        }
                    }
                }

            }
        }

        int size = list.size();
        Object[] objs = new Object[size];
        for (int index = 0; index < size; index++) {
            Array.set(objs, index, list.get(index));
        }

        return objs;
    }
}
