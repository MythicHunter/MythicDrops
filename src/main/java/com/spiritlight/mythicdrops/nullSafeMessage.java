package com.spiritlight.mythicdrops;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class nullSafeMessage {
    public static void sendMessage(String s) {
        try {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(s));
        } catch (NullPointerException ignored) {}
    }
}
