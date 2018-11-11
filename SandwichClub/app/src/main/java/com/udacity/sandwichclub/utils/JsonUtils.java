package com.udacity.sandwichclub.utils;

import com.udacity.sandwichclub.model.Sandwich;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;

public class JsonUtils {

    private static final String SANDWICH_MAIN_NAME = "mainName";
    private static final String SANDWICH_AKA = "alsoKnownAs";
    private static final String SANDWICH_PLACE = "placeOfOrigin";
    private static final String SANDWICH_DESCRIPTION = "description";
    private static final String SANDWICH_IMAGE = "image";
    private static final String SANDWICH_INGREDIENTS = "ingredients";

    public static Sandwich parseSandwichJson(String json) throws JSONException {

        JSONObject sandwichJsonObject = new JSONObject(json);

        // Is there an error?
        if (sandwichJsonObject.has("cod")) {
            int errorCode = sandwichJsonObject.getInt("cod");

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    // Location invalid
                    return null;
                default:
                    // Server probably down
                    return null;
            }
        }

        Sandwich sandwich = new Sandwich();

        // JSON Parsing
        JSONObject nameJSONObject = sandwichJsonObject.getJSONObject("name");
        String mainName = nameJSONObject.getString(SANDWICH_MAIN_NAME);
        JSONArray alsoJSONArray = nameJSONObject.getJSONArray(SANDWICH_AKA);
        String placeOfOrigin = sandwichJsonObject.getString(SANDWICH_PLACE);
        String image = sandwichJsonObject.getString(SANDWICH_IMAGE);
        String description = sandwichJsonObject.getString(SANDWICH_DESCRIPTION);
        JSONArray ingredientsJSONArray = sandwichJsonObject.getJSONArray(SANDWICH_INGREDIENTS);

        sandwich.setMainName(mainName);
        sandwich.setImage(image);

        ArrayList sandwiches = new ArrayList<>();
        for (int i = 0; i < alsoJSONArray.length(); i++) {
            sandwiches.add(alsoJSONArray.getString(i));
        }
        sandwich.setAlsoKnownAs(sandwiches);
        sandwich.setPlaceOfOrigin(placeOfOrigin);
        sandwiches = new ArrayList();
        for (int i = 0; i < ingredientsJSONArray.length(); i++) {
            sandwiches.add(ingredientsJSONArray.getString(i));
        }
        sandwich.setIngredients(sandwiches);
        sandwich.setDescription(description);
        return sandwich;
    }
}
