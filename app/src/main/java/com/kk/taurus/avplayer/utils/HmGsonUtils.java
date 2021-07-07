package com.kk.taurus.avplayer.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class HmGsonUtils {
    public static final String TAG = HmGsonUtils.class.getSimpleName();

    /**无参的私有构造方法*/
    private HmGsonUtils() {

    }

    /**
     * 不用创建对象,直接使用 gson. 就可以调用方法
     */
    private static Gson gson = null;

    /**
     * 默认的时间格式化
     */
    private static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 判断gson对象是否存在了,不存在则创建对象
     */
    static {
        if (gson == null) {
            // gson = new Gson();
            // 当使用 GsonBuilder 方式时属性为空的时候输出来的json字符串是有键值key的,显示形式是"key":null，而直接 new 出来的就没有"key":null的
            gson = buildGson();
        }
    }

    /**
     * 默认的 GSON 初始化
     */
    public static Gson buildGson() {
        Gson gson = new Gson().newBuilder()
                .setDateFormat(DATE_FORMAT_DEFAULT)
                .create();
        return gson;
    }

    /**
     * 将对象转成json格式
     * Bean To Json
     *
     * @param object
     * @return String
     */
    public static String beanToJson(Object object) {
        String jsonString = null;
        try {
            if (gson != null) {
                jsonString = gson.toJson(object);
            }
        } catch (Exception e) {
            Log.e(TAG, "Bean 转 Json 格式异常:" + e);
        }
        return jsonString;
    }

    /**
     * 将 json 转成特定的 cls 的对象
     * Json To Bean
     *
     * @param jsonString
     * @param cls
     * @return
     */
    public static <T> T jsonToBean(String jsonString, Class cls) {
        T t = null;
        try {
            if (gson != null) {
                // 传入json对象和对象类型,将json转成对象
                t = gson.fromJson(jsonString, (Type) cls);
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Json 转 Bean 非法json字符串:" + e);
        }
        return t;
    }

    /**
     * json字符串转成list
     * 解决泛型问题
     * 备注：
     * List list=gson.fromJson(jsonString, new TypeToken<List>() {}.getType());
     * 该方法会报泛型类型擦除问题
     *
     * @param jsonString
     * @param cls
     * @param
     * @return
     */
    public static List jsonToList(String jsonString, Class cls) {
        List list = new ArrayList();
        try {
            if (gson != null) {
                JsonArray array = new JsonParser().parse(jsonString).getAsJsonArray();
                for (final JsonElement elem : array) {
                    list.add(gson.fromJson(elem, cls));
                }
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Json 转 List 非法json字符串:" + e);
        }
        return list;
    }

    /**
     * json 字符串转成 list map
     * Json To List<Map<String,T>>
     *
     * @param jsonString
     * @return
     */
    public static <T> List<Map<String, T>> jsonToListMaps(String jsonString) {
        List<Map<String, T>> list = null;
        try {
            if (gson != null) {
                list = gson.fromJson(jsonString,
                        new TypeToken<List<Map<String, T>>>() {
                        }.getType());
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Json 转 List非法json字符串:" + e);
        }
        return list;
    }
    /**
     * json 字符串转成 map 的
     * Json To Map
     *
     * @param jsonString
     * @return
     */
    public static <T> Map<String, T> jsonToMaps(String jsonString) {
        Map<String, T> map = null;
        try {
            if (gson != null) {
                map = gson.fromJson(jsonString, new TypeToken<Map<String, T>>() {
                }.getType());
            }
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Json 转 Map 非法json字符串:" + e);
        }
        return map;
    }


    public static JSONObject mapToJson(Map<?, ?> data) {
        JSONObject object = new JSONObject();

        for (Map.Entry<?, ?> entry : data.entrySet()) {
            /*
             * Deviate from the original by checking that keys are non-null and
             * of the proper type. (We still defer validating the values).
             */
            String key = (String) entry.getKey();
            if (key == null) {
                //throw new NullPointerException("key == null");
                continue;
            }
            try {
                object.put(key, wrap(entry.getValue()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return object;
    }

    private static Object wrap(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof JSONArray || o instanceof JSONObject) {
            return o;
        }
        try {
            if (o instanceof Collection) {
                return collectionToJson((Collection) o);
            } else if (o.getClass().isArray()) {
                return arrayToJson(o);
            }
            if (o instanceof Map) {
                return mapToJson((Map) o);
            }
            if (o instanceof Boolean ||
                    o instanceof Byte ||
                    o instanceof Character ||
                    o instanceof Double ||
                    o instanceof Float ||
                    o instanceof Integer ||
                    o instanceof Long ||
                    o instanceof Short ||
                    o instanceof String) {
                return o;
            }
            if (o.getClass().getPackage().getName().startsWith("java.")) {
                return o.toString();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static JSONArray collectionToJson(Collection data) {
        JSONArray jsonArray = new JSONArray();
        if (data != null) {
            for (Object aData : data) {
                jsonArray.put(wrap(aData));
            }
        }
        return jsonArray;
    }

    public static JSONArray arrayToJson(Object data) throws JSONException {
        if (!data.getClass().isArray()) {
            throw new JSONException("Not a primitive data: " + data.getClass());
        }
        final int length = Array.getLength(data);
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < length; ++i) {
            jsonArray.put(wrap(Array.get(data, i)));
        }

        return jsonArray;
    }

    /**
     * @param content json字符串
     * @return 如果转换失败返回null,
     */
    public static Map<String, Object> jsonToMap(String content) {
        content = content.trim();
        Map<String, Object> result = new HashMap<>();
        try {
            if (content.charAt(0) == '[') {
                JSONArray jsonArray = new JSONArray(content);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Object value = jsonArray.get(i);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(i + "", jsonToMap(value.toString().trim()));
                    } else {
                        result.put(i + "", jsonArray.getString(i));
                    }
                }
            } else if (content.charAt(0) == '{') {
                JSONObject jsonObject = new JSONObject(content);
                Iterator<String> iterator = jsonObject.keys();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    Object value = jsonObject.get(key);
                    if (value instanceof JSONArray || value instanceof JSONObject) {
                        result.put(key, jsonToMap(value.toString().trim()));
                    } else {
                        result.put(key, value.toString().trim());
                    }
                }
            } else {
                Log.e("异常", "json2Map: 字符串格式错误");
            }
        } catch (JSONException e) {
            Log.e("异常", "json2Map: ", e);
            result = null;
        }
        return result;
    }

}