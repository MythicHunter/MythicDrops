package com.spiritlight.mythicdrops;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault @ParametersAreNonnullByDefault
public class SayCommand extends CommandBase {
    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getName() {
        return "say";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/" + getName();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 0) {
            nullSafeMessage.sendMessage("Message must not be empty. (/say)");
            return;
        }
        String message = String.join(" ", args);
        Minecraft.getMinecraft().player.sendChatMessage(message);
    }
}
