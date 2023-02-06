package com.huiwan.base.util;

import androidx.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by bigwen on 2019-08-13.
 */
public class JsonUtil {

    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) gson = new Gson();
        return gson;
    }

    @Nullable
    public static <T> T parseJson(String json, Class<T> classOfT) {
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Object object = null;
        try {
            object = getGson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (object == null) return null;
        return classOfT.cast(object);
    }


    public static void mergeJSONObject(final JSONObject source, JSONObject dest) {
        try {
            Iterator<String> srcItr = source.keys();
            while (srcItr.hasNext()) {
                String key = srcItr.next();
                Object value = source.get(key);
                dest.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String toJsonString(Object o) {
        if (o == null) return "";
        return getGson().toJson(o);
    }
}
