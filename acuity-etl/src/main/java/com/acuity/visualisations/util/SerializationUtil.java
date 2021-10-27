package com.acuity.visualisations.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SerializationUtil {

    private SerializationUtil() {
    }

    public static String serializeMap(Map<String, ? extends Object> inputMap) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, ? extends Object> entry : inputMap.entrySet()) {
            jsonObject.put(entry.getKey(), entry.getValue());
        }
        return jsonObject.toString();
    }

    public static String serializeList(Collection<? extends Object> inputList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object entry : inputList) {
            jsonArray.put(entry);
        }
        return jsonArray.toString();
    }

    public static String serializeSet(Set<? extends Object> inputList) throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (Object entry : inputList) {
            jsonArray.put(entry);
        }
        return jsonArray.toString();
    }

    public static Map<String, Map<String, String>> deserializeMap(String input) throws JSONException {
        Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
        JSONObject jsonObject = new JSONObject(input);
        Iterator<?> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (!result.containsKey(key)) {
                result.put(key, new HashMap<String, String>());
            }
            Map<String, String> innerResult = result.get(key);
            JSONObject inner = jsonObject.getJSONObject(key);
            Iterator<?> innerIt = inner.keys();
            while (innerIt.hasNext()) {
                String innerKey = innerIt.next().toString();
                innerResult.put(innerKey, inner.getString(innerKey));
            }
        }

        return result;
    }

    public static Map<String, List<Integer>> deserializeMapList(String input) throws JSONException {
        Map<String, List<Integer>> result = new HashMap<String, List<Integer>>();
        JSONObject jsonObject = new JSONObject(input);
        Iterator<?> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (!result.containsKey(key)) {
                result.put(key, new ArrayList<Integer>());
            }
            List<Integer> inner = result.get(key);
            JSONArray array = jsonObject.getJSONArray(key);
            for (int i = 0; i < array.length(); i++) {
                inner.add(array.getInt(i));
            }
        }
        return result;
    }

    public static Map<String, Object> deserializeSimpleMap(String input) throws JSONException {
        Map<String, Object> result = new HashMap<String, Object>();
        JSONObject jsonObject = new JSONObject(input);
        Iterator<?> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            Object value = jsonObject.get(key);
            result.put(key, value);
        }
        return result;
    }

}
