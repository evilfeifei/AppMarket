package com.huiyun.amnews.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static Gson gson = null;
    static {
		if (gson == null) {
		    gson = new Gson();
		}
    }

    private JsonUtil() {
    }

    public static String objectToJson(Object ts) {
		String jsonStr = null;
		if (gson != null) {
		    jsonStr = gson.toJson(ts);
		}
		return jsonStr;
    }
    public static String objectToJsonDateSerializer(Object ts, final String dateformat) {
		String jsonStr = null;
		gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class,
				new JsonSerializer<Date>() {
				    public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					SimpleDateFormat format = new SimpleDateFormat(dateformat);
					return new JsonPrimitive(format.format(src));
				    }
				}).setDateFormat(dateformat).create();
		if (gson != null) {
		    jsonStr = gson.toJson(ts);
		}
		return jsonStr;
    }

    public static <T> List<T> jsonToList(String jsonStr,Class<T> clazz) {
		List<T> objList = null;
		if (gson != null) {
		    Type type = new TypeToken<List<T>>() {
		    }.getType();
		    objList = gson.fromJson(jsonStr, type);
		}
		return objList;
    }

	public static <T> List<T> stringToArray(String s, Class<T[]> clazz) {
		T[] arr = new Gson().fromJson(s, clazz);
		return Arrays.asList(arr);
	}

    public static Map<?, ?> jsonToMap(String jsonStr) {
		Map<?, ?> objMap = null;
		if (gson != null) {
		    try {
				Type type = new TypeToken<Map<?, ?>>() {
				}.getType();
				objMap = gson.fromJson(jsonStr, type);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		return objMap;
    }

    public static Object jsonToBean(String jsonStr, Class<?> cl) {
		Object obj = null;
		if (gson != null) {
		    obj = gson.fromJson(jsonStr, cl);
		}
		return obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T jsonToBeanDateSerializer(String jsonStr, Class<T> cl, final String pattern) {
		Object obj = null;
		gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
					SimpleDateFormat format = new SimpleDateFormat(pattern);
					String dateStr = json.getAsString();
					try {
					    return format.parse(dateStr);
					} catch (ParseException e) {
					    e.printStackTrace();
					}
					return null;
			    }
			}).setDateFormat(pattern).create();
		if (gson != null) {
		    obj = gson.fromJson(jsonStr, cl);
		}
		return (T) obj;
    }

    public static Object getJsonValue(String jsonStr, String key) {
		Object rulsObj = null;
		Map<?, ?> rulsMap = jsonToMap(jsonStr);
		if (rulsMap != null && rulsMap.size() > 0) {
		    rulsObj = rulsMap.get(key);
		}
		return rulsObj;
    }
    
    public static void addList(List<Map<String, Object>> listMessage, List<Map<String, Object>> listMessage2) {
    	
    	if (listMessage != null && listMessage2 != null && listMessage2.size() > 0) {
    	    for (Map<String, Object> maps : listMessage2) {
    	    	listMessage.add(maps);
    	    }
    	}
    }



}
