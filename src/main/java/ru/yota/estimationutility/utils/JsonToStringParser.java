package ru.yota.estimationutility.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonToStringParser {
    public String parsePredSubjects(String httpresponse, int position) {
        JsonArray jsonArray = new JsonParser().parse(httpresponse).getAsJsonArray();
        JsonObject jsonObject = (JsonObject) jsonArray.get(position);
        if (jsonObject != null) {
            String subject = jsonObject.get("subject").getAsString();
            return subject;
        }
        return null;
    }

    public String parsePredConfidence(String httpresponse, int position) {
        JsonArray jsonArray = new com.google.gson.JsonParser().parse(httpresponse).getAsJsonArray();
        JsonObject jsonObject = (JsonObject) jsonArray.get(position);
        if (jsonObject != null) {
            String confidence = jsonObject.get("confidence").getAsString();
            return confidence;
        }
        return null;
    }
}
