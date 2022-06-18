package com.spiritlight.mythicdrops;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

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
                    nullSafeMessage.sendMessage("- " + s);
                }
                break;
            case "add":
                if(args.length <= 1) {
                    nullSafeMessage.sendMessage("You must supply a name.");
                }
                args2 = Arrays.copyOfRange(args, 1, args.length);
                name = String.join(" ", args2);
                Main.star.add(name);
                nullSafeMessage.sendMessage("Added " + name + " to star list.");
                save();
                break;
            case "remove":
                args2 = Arrays.copyOfRange(args, 1, args.length);
                name = String.join(" ", args2);
                Main.star.remove(name);
                nullSafeMessage.sendMessage("Added " + name + " to star list.");
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
