package com.spiritlight.mythicdrops;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public class StarCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "star";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            nullSafeMessage.sendMessage("/star add/remove <Item Name>");
            nullSafeMessage.sendMessage("/star list");
            nullSafeMessage.sendMessage("Looking for regex? Try /pattern");
            return;
        }
        String[] args2;
        String name;
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "list":
                nullSafeMessage.sendMessage("Starred items to log:");
                for(String s : Main.star) {
                    TextComponentString string = new TextComponentString("- " + s);
                    string.setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to remove!")))
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/star remove " + s)));
                    Minecraft.getMinecraft().player.sendMessage(string);
                }
                break;
            case "add":
                if(args.length <= 1) {
                    nullSafeMessage.sendMessage("You must supply a name.");
                    return;
                }
                args2 = Arrays.copyOfRange(args, 1, args.length);
                name = String.join(" ", args2);
                Main.star.add(name);
                if(ItemType.Type.getType(name) == ItemType.INGREDIENT) {
                    nullSafeMessage.sendMessage("Ingredient detected, note that ingredients always use strict leniency.");
                }
                nullSafeMessage.sendMessage("Added " + name + " to star list.");
                ConfigSpirit.save();
                break;
            case "remove":
                if(args.length <= 1) {
                    nullSafeMessage.sendMessage("You must supply a name.");
                    return;
                }
                args2 = Arrays.copyOfRange(args, 1, args.length);
                name = String.join(" ", args2);
                Main.star.remove(name);
                nullSafeMessage.sendMessage("Removed " + name + " from star list.");
                ConfigSpirit.save();
                break;
            default:
                nullSafeMessage.sendMessage("Invalid command. try /" + getName());
                break;
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if(args.length == 0) {
            return Arrays.asList("list", "add", "remove");
        }
        List<String> value = new ArrayList<>();
        switch (args[0]) {
            case "add":
                if(args.length >= 2) {
                    final String[] input = Arrays.copyOfRange(args, 1, args.length);
                    final String result = String.join(" ", input);
                    for(String name : Main.itemList) {
                        if(name.toLowerCase(Locale.ROOT).startsWith(result.toLowerCase(Locale.ROOT))) {
                            value.add(name);
                        }
                    }
                    return value.subList(0, Math.min(100, value.size()));
                } else {
                    return Main.itemList.subList(0, Math.min(100, Main.itemList.size()));
                }
            case "remove":
                if(args.length >= 2) {
                    final String[] input = Arrays.copyOfRange(args, 1, args.length);
                    final String result = String.join(" ", input);
                    for(String name : Main.star) {
                        if(name.toLowerCase(Locale.ROOT).startsWith(result.toLowerCase(Locale.ROOT))) {
                            value.add(name);
                        }
                    }
                } else {
                    value = new ArrayList<>(Main.star);
                }
                return value;
            default:
                return Collections.emptyList();
        }
    }
}
