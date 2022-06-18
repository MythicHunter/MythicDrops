package com.spiritlight.mythicdrops;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
            nullSafeMessage.sendMessage("/star addregex/removeregex <Item RegEx>");
            nullSafeMessage.sendMessage("/star listregex");
            nullSafeMessage.sendMessage("Go to https://regexr.com/ to build a custom RegEx.");
            return;
        }
        String[] args2;
        String name;
        Pattern p;
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "list":
                nullSafeMessage.sendMessage("Starred items to log:");
                for(String s : Main.star) {
                    nullSafeMessage.sendMessage("- " + s);
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
                nullSafeMessage.sendMessage("Added " + name + " to star list.");
                save();
                break;
            case "remove":
                if(args.length <= 1) {
                    nullSafeMessage.sendMessage("You must supply a name.");
                    return;
                }
                args2 = Arrays.copyOfRange(args, 1, args.length);
                name = String.join(" ", args2);
                Main.star.remove(name);
                nullSafeMessage.sendMessage("Added " + name + " to star list.");
                save();
                break;
            case "listregex":
                nullSafeMessage.sendMessage("Starred items (RegEx) to log:");
                for(Pattern s : Main.regexStar) {
                    nullSafeMessage.sendMessage("- " + s.toString());
                }
                break;
            case "addregex":
                if(args.length <= 1) {
                    nullSafeMessage.sendMessage("You must supply a regex.");
                    return;
                }
                args2 = Arrays.copyOfRange(args, 1, args.length);
                name = String.join(" ", args2);
                try {
                    p = Pattern.compile(name);
                }  catch (PatternSyntaxException ex) {
                    ITextComponent s = new TextComponentString("The provided RegEx is invalid. Check https://regexr.com/ for RegEx (Hover for cause..)")
                            .setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(ex.getMessage()))));
                    nullSafeMessage.sendMessage(s);
                    return;
                }
                Main.regexStar.add(p);
                nullSafeMessage.sendMessage("Added " + name + " to regex star list.");
                save();
                break;
            case "removeregex":
                if(args.length <= 1) {
                    nullSafeMessage.sendMessage("You must supply a regex.");
                    return;
                }
                args2 = Arrays.copyOfRange(args, 1, args.length);
                name = String.join(" ", args2);
                try {
                    p = Pattern.compile(name);
                }  catch (PatternSyntaxException ex) {
                    ITextComponent s = new TextComponentString("The provided RegEx is invalid. Check https://regexr.com/ for RegEx (Hover for cause..)")
                            .setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(ex.getMessage()))));
                    nullSafeMessage.sendMessage(s);
                    return;
                }
                Main.regexStar.remove(p);
                nullSafeMessage.sendMessage("Added " + name + " to regex list.");
                save();
                break;
            default:
                nullSafeMessage.sendMessage("Invalid command. try /" + getName());
                break;
        }
    }

    private static void save() {
        try {
            ConfigSpirit.write();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
