package com.rkyyn.libcommon.utils;

/**
 * author : hepeng
 * time   : 2023/05/18
 * desc   :
 */
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author:hepeng
 * Date:2018-05-17
 * Description:
 */

public class GsonUtils {
    /**
     * 增加后台返回""和"null"的处理
     * 1.int=>0
     * 2.double=>0.00
     * 3.long=>0L
     *
     * @return
     */
    public static Gson buildGson() {

        Gson gson = null;
        try {
            gson = new GsonBuilder()
                    .registerTypeAdapter(int.class, new IntDefault0Adapter())
                    .registerTypeAdapter(Integer.class, new IntegerDefault0Adapter())
                    .registerTypeAdapter(Float.class, new FloatDefault0Adapter())
                    .registerTypeAdapter(float.class, new Float1Default0Adapter())
                    .registerTypeAdapter(Double.class, new DoubleDefault0Adapter())
                    .registerTypeAdapter(double.class, new Double1Default0Adapter())
                    .registerTypeAdapter(Long.class, new LongDefault0Adapter())
                    .registerTypeAdapter(long.class, new Long1Default0Adapter())
//                    .registerTypeAdapter(JsonArray.class, new JaonArrayDefault0Adapter())
//                    .registerTypeAdapter(List.class, new ListDefault0Adapter())
                    .registerTypeHierarchyAdapter(List.class, new JsonDeserializer<List<?>>() {
                        @Override
                        public List<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            if (json.isJsonArray()) {
                                JsonArray array = json.getAsJsonArray();
                                Type itemType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
                                List list = new ArrayList<>();
                                for (int i = 0; i < array.size(); i++) {
                                    JsonElement element = array.get(i);
                                    Object item = context.deserialize(element, itemType);
                                    list.add(item);
                                }
                                return list;
                            } else {
                                //和接口类型不符，返回空List
                                return Collections.EMPTY_LIST;
                            }
                        }
                    })
                    .create();
        } catch (Exception e) {
            LogUtils.e("",e.getMessage());
            gson = new Gson();
        }
        return gson;
    }
    /**
     * int
     */
    private static class IntDefault0Adapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为int类型,如果后台返回""或者null,则返回0
                    return 0;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }
    /**
     * Integer
     */
    private static class IntegerDefault0Adapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为int类型,如果后台返回""或者null,则返回0
                    return 0;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }
    /**
     * Long
     */
    private static class LongDefault0Adapter implements JsonSerializer<Long>, JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为long类型,如果后台返回""或者null,则返回0
                    return 0l;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsLong();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

    /**
     * long
     */
    private static class Long1Default0Adapter implements JsonSerializer<Long>, JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为long类型,如果后台返回""或者null,则返回0
                    return 0l;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsLong();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }
    /**
     * Double
     */
    private static class DoubleDefault0Adapter implements JsonSerializer<Double>, JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为double类型,如果后台返回""或者null,则返回0.00
                    return 0.00;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsDouble();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }
    /**
     * double
     */
    private static class Double1Default0Adapter implements JsonSerializer<Double>, JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为double类型,如果后台返回""或者null,则返回0.00
                    return 0.00;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsDouble();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }
    /**
     * Double
     */
    private static class FloatDefault0Adapter implements JsonSerializer<Float>, JsonDeserializer<Float> {
        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为double类型,如果后台返回""或者null,则返回0.00
                    return 0.0f;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsFloat();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }
    /**
     * double
     */
    private static class Float1Default0Adapter implements JsonSerializer<Float>, JsonDeserializer<Float> {
        @Override
        public Float deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为double类型,如果后台返回""或者null,则返回0.00
                    return 0.0f;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsFloat();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }
    private static class ListDefault0Adapter implements JsonSerializer<List<?>>, JsonDeserializer<List<?>> {
        @Override
        public List<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//            if (json.isJsonArray()){
//                //这里要自己负责解析了
////                Gson newGson = new Gson();
//                return buildGson().fromJson(json,typeOfT);
//            }else {
//                //和接口类型不符，返回空List
//                return Collections.EMPTY_LIST;
//            }
            try {
                if (jsonElement.getAsString().equals("") || jsonElement.getAsString().equals("null")) {
                    return new ArrayList<>();
                }
            } catch (Exception ignore) {
            }
            try {
                Gson newGson = new Gson();
                return newGson.fromJson(jsonElement,type);
            } catch (Exception e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(List<?> objects, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(String.valueOf(objects));
        }


    }


}

