package com.spiritlight.mythicdrops;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@ParametersAreNonnullByDefault @MethodsReturnNonnullByDefault
public class StarRegexCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "starregex";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            nullSafeMessage.sendMessage("/starregex add/remove <Item Name>");
            nullSafeMessage.sendMessage("/starregex list");
            nullSafeMessage.sendMessage("/starregex test <text>");
            nullSafeMessage.sendMessage("Go to https://regexr.com/ to build a custom RegEx.");
            return;
        }
        String[] args2;
        String name;
        Pattern p;
        switch(args[0].toLowerCase(Locale.ROOT)) {
            case "list":
                nullSafeMessage.sendMessage("Starred items (RegEx) to log:");
                for(Pattern s : Main.regexStar) {
                    TextComponentString string = new TextComponentString("- " + s.pattern());
                    string.setStyle(new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString("Click to remove!")))
                            .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/star remove " + s.pattern())));
                    nullSafeMessage.sendMessage(string);
                }
                break;
            case "add":
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
            case "remove":
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
                nullSafeMessage.sendMessage("Removed " + name + " from regex star list.");
                save();
                break;
            case "test":
                if(args.length == 1) {
                    nullSafeMessage.sendMessage("You need to supply something to test.");
                    return;
                }
                args2 = Arrays.copyOfRange(args, 1, args.length);
                name = String.join(" ", args2);
                for(Pattern pattern : Main.regexStar) {
                    Matcher matcher = pattern.matcher(name);
                    if(matcher.matches()) {
                        nullSafeMessage.sendMessage("Pattern " + pattern.pattern() + " matches " + name + ".");
                    }
                }
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
