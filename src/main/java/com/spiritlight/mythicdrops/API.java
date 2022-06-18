package com.spiritlight.mythicdrops;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public class API {
    // Only fetch once
    public static void fetchItem() {
        Set<String> mythicList = new HashSet<>();
        System.out.println("Collecting mythic names...");
        try {
            JSONObject json = new JSONObject(HTTP.get("https://api.wynncraft.com/public_api.php?action=itemDB&category=all"));
            JSONArray arr = json.getJSONArray("items");
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.getString("tier").equals("Mythic")) {
                    mythicList.add(o.getString("name"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Main.mythic = new HashSet<>(mythicList);
    }


}
