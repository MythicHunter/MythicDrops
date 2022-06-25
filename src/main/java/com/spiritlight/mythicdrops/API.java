package com.spiritlight.mythicdrops;

import com.google.gson.*;

import java.util.*;

public class API {
    // Only fetch once
    public static void fetchItem() {
        Set<String> mythicList = new HashSet<>();
        System.out.println("Collecting mythic names...");
        try {
            JsonParser parser = new JsonParser();
            JsonElement json = parser.parse(HTTP.get("https://api.wynncraft.com/public_api.php?action=itemDB&category=all"));
            JsonArray arr = json.getAsJsonObject().getAsJsonArray("items");
            for (JsonElement element : arr) {
                String tier = element.getAsJsonObject().get("tier").getAsString();
                if (tier.equals("Mythic")) {
                    mythicList.add(element.getAsJsonObject().get("name").getAsString());
                }
                Main.itemList.add(element.getAsJsonObject().get("name").getAsString());
            }
            json = parser.parse(HTTP.get("https://api.wynncraft.com/v2/ingredient/list"));
            for (JsonElement element : json.getAsJsonObject().getAsJsonArray("data")) {
                Main.ingredientList.add(element.getAsString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Main.mythic = new HashSet<>(mythicList);
    }
}
