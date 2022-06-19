package com.spiritlight.mythicdrops;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public class Commands extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "mythic";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            nullSafeMessage.sendMessage("/mythic toggle - Toggle alerts");
            nullSafeMessage.sendMessage("/mythic autotrack - Toggle autotrack");
            nullSafeMessage.sendMessage("Also see: /star, /pattern");
            return;
        }
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "toggle":
                Main.enabled = !Main.enabled;
                nullSafeMessage.sendMessage("Toggled mod! Mod active: " + Main.enabled);
                break;
            case "autotrack":
                Main.autoTrack = !Main.autoTrack;
                nullSafeMessage.sendMessage("Toggled autotrack! Track active: " + Main.autoTrack);
                break;
            case "reload":
                nullSafeMessage.sendMessage("Reloaded items!");
                DetectMythics.scannedUUID.clear();
                break;
            case "dump":
                System.out.println(DetectMythics.scannedUUID);
                break;
            case "debug":
                Main.debug = !Main.debug;
                nullSafeMessage.sendMessage("Toggled debug! debug active: " + Main.debug);
                break;
            case "reloadmythics":
                nullSafeMessage.sendMessage("Reloading mythic db!");
                CompletableFuture.runAsync(API::fetchItem).thenAccept(x -> nullSafeMessage.sendMessage("Successfully reloaded mythic db!"));
                break;
            case "listmythic":
                for(String s : Main.mythic) {
                    nullSafeMessage.sendMessage(s);
                }
                break;
            default:
                nullSafeMessage.sendMessage("Try /mythic for more info.");
        }
    }
}
