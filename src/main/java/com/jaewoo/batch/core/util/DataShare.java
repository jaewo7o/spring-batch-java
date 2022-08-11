package com.jaewoo.batch.core.util;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataShare {

    // paramMap should be ConcurrentHashMap because it could be used in multi-thread
    private static final Map<String, String> paramMap = new ConcurrentHashMap<String, String>();

    /**
     * 현재 배치작업의 파라메터에 새로운 값을 등록한다. 만약 {@code key}에 해당하는 값이 이미 등록되어 있는 경우
     * 기존값은 새로운 값으로 변경된다.
     *
     * @param key   파라메터 키
     * @param value 파라메터 값
     */
    public static void addParameter(String key, String value) {
        paramMap.put(key, value);
    }

    /**
     * 현재 배치작업의 파라메터 중 {@code key}에 해당하는 값을 반환한다.
     * 만약 {@code key}에 해당하는 파라메터가 없는 경우 null을 반환한다.
     *
     * @param key 파라메터의 키 값
     * @return {@code key}에 해당하는 파라메터 값
     */
    public static String getParameter(String key) {
        return paramMap.get(key);
    }

    /**
     * 현재 배치작업에 등록된 모든 파라메터를 반환한다.
     *
     * @return 현재 배치작업에 등록된 파라메터를 담은 Map
     */
    public static Map<String, String> getAll() {
        return Collections.unmodifiableMap(paramMap);
    }

    /**
     * 현재 배치작업에 등록된 파라메터 중 {@code key}에 해당하는 파라메터가 있는지 확인한다
     *
     * @param key 파라메터 키
     * @return {@code key}에 해당하는 파라메터가 있는 경우 true, 없는 경우 false
     */
    public static boolean containsKey(String key) {
        return paramMap.containsKey(key);
    }
}
