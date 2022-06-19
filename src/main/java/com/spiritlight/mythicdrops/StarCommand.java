package com.spiritlight.mythicdrops;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

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
                    nullSafeMessage.sendMessage(string);
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
                nullSafeMessage.sendMessage("Removed " + name + " from star list.");
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
