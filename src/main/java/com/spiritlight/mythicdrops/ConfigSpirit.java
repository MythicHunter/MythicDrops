package com.spiritlight.mythicdrops;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ConfigSpirit {
    private static boolean antiLoop = false;
    public static void read() throws IOException {
        File config = new File("config/MythicDrops.json");
        if (config.exists()) {
            try {
                JsonParser parser = new JsonParser();
                JsonObject jsonObject = (JsonObject) parser.parse(new FileReader("config/MythicDrops.json"));
                for (JsonElement element : jsonObject.getAsJsonArray("stars")) {
                    Main.star.add(element.getAsString());
                }
                for (JsonElement element : jsonObject.getAsJsonArray("regex")) {
                    try {
                        Main.regexStar.add(Pattern.compile(element.getAsString()));
                    } catch (PatternSyntaxException ex) {
                        System.out.println("Error caught trying to add pattern " + element.getAsString() + "! Ignoring it...");
                        ex.printStackTrace();
                    }
                }
                Main.unidOnly = jsonObject.get("unidOnly").getAsBoolean();
                Main.leniency = jsonObject.get("leniency").getAsInt();
            } catch (NullPointerException exception) {
                System.out.println("New configuration files found?");
                if(antiLoop) return;
                antiLoop = true;
                write();
                read();
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
        writer.name("regex");
        writer.beginArray();
        for (Pattern pattern : Main.regexStar) {
            writer.value(pattern.pattern());
        }
        writer.endArray();
        writer.name("unidOnly").value(Main.unidOnly);
        writer.name("leniency").value(Main.leniency);
        writer.endObject();
        writer.close();
    }

    public static boolean save() {
        try {
            write();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
