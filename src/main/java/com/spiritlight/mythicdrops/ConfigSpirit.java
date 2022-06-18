package com.spiritlight.mythicdrops;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigSpirit {
    public static void read() throws IOException {
        File config = new File("config/MythicDrops.json");
        if (config.exists()) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = (JsonObject)parser.parse(new FileReader("config/MythicDrops.json"));
            for (JsonElement element : jsonObject.getAsJsonArray("stars")) {
                Main.star.add(element.getAsString());
            }
        } else {
            write();
        }
    }

    public static void write() throws IOException {
        JsonWriter writer = new JsonWriter(new FileWriter("config/MythicDrops.json"));
        writer.beginObject();
        writer.name("stars");
        writer.beginArray();
        for (String name : Main.star) {
            writer.value(name);
        }
        writer.endArray();
        writer.endObject();
        writer.close();
    }
}
