package com.spiritlight.mythicdrops;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class DetectMythics {
    static boolean previouslyDisabled = false;
    static Map<UUID, NBTTagCompound> scannedUUID = new HashMap<>();

    @SubscribeEvent
    public void itemEvent(final EntityEvent event) {
        try {
            if (!Main.enabled) {
                previouslyDisabled = true;
                return;
            }
            if(!Main.fullScan) {
                if (!(event.getEntity() instanceof EntityItem)) return;
                final UUID entityUUID = event.getEntity().getUniqueID();
                if (scannedUUID.containsKey(entityUUID) && scannedUUID.get(entityUUID).equals(event.getEntity().serializeNBT()))
                    return;
                final EntityItem item = (EntityItem) event.getEntity();
                if (item.getName().equals("item.tile.air")) return;
                if (Main.debug) {
                    nullSafeMessage.sendMessage(new TextComponentString("Found item of " + item.getName()).setStyle(
                            new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                    new TextComponentString(format("Item name: " + (item.hasCustomName() ? item.getCustomNameTag() + "(" + item.getName() + ")" : item.getName()) + "\n" + "Item UUID: " + item.getUniqueID() + "\n\n" + item.serializeNBT() + "\n\nClick to track!")))
                            ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                    item.getPosition().getX() + " " + item.getPosition().getY() + " " + item.getPosition().getZ()))));
                }
                if (previouslyDisabled) {
                    scannedUUID.clear();
                    List<Entity> worldEntity = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
                    for (Entity e : worldEntity) {
                        if (!(e instanceof EntityItem)) continue;
                        checkItem((EntityItem) e);
                        scannedUUID.put(e.getUniqueID(), e.serializeNBT());
                    }
                    previouslyDisabled = false;
                    return;
                }
                checkItem(item);
                scannedUUID.put(item.getUniqueID(), item.serializeNBT());
            } else {
                List<Entity> worldEntity = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
                for (Entity e : worldEntity) {
                    if (!(e instanceof EntityItem)) continue;
                    if(e.isGlowing()) continue;
                    if (scannedUUID.containsKey(e.getUniqueID()) && scannedUUID.get(e.getUniqueID()).equals(e.serializeNBT())) continue;
                    if (Main.debug) {
                        nullSafeMessage.sendMessage(new TextComponentString("Found item of " + e.getName()).setStyle(
                                new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new TextComponentString(format("Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + e.serializeNBT() + "\n\nClick to track!")))
                                ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                        e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
                    }
                    checkItem((EntityItem) e);
                    scannedUUID.put(e.getUniqueID(), e.serializeNBT());
                }
            }
        } catch (NullPointerException ignored) {
        }
    }

    private void checkItem(EntityItem item) {
        NBTTagCompound nbt = item.serializeNBT();
        String itemName = nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
        if (Main.mythic.contains(itemName)) {
            mythicFound(item, itemName, true);
        }
        if (Main.star.contains(itemName)) {
            mythicFound(item, itemName, false);
        }
    }

    private String format(String s) {
        return s
                .replaceAll("\\{",TextFormatting.AQUA + "{" + TextFormatting.GOLD)
                .replaceAll("}",TextFormatting.AQUA + "}" + TextFormatting.GOLD)
                .replaceAll("\\[",TextFormatting.RESET + "[" + TextFormatting.GOLD)
                .replaceAll("]",TextFormatting.RESET + "]" + TextFormatting.GOLD)
                .replaceAll(",",TextFormatting.RESET + "," + TextFormatting.GOLD)
                .replaceAll(":", TextFormatting.RESET + ":" + TextFormatting.AQUA)
                .replaceAll("'", TextFormatting.YELLOW + "'" + TextFormatting.RESET)
                .replaceAll("\"", TextFormatting.GREEN + "\"" + TextFormatting.GOLD);
    }

    private void mythicFound(@NotNull EntityItem item, String itemName, boolean isMythic) {
        item.setGlowing(true);
        Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        TextComponentString tMythic = new TextComponentString(
                TextFormatting.LIGHT_PURPLE + "[" + TextFormatting.DARK_PURPLE + "!" + TextFormatting.LIGHT_PURPLE + "] " +
                        TextFormatting.LIGHT_PURPLE + "Mythic Item " + TextFormatting.DARK_PURPLE + itemName + TextFormatting.LIGHT_PURPLE +
                        " has dropped at " + TextFormatting.AQUA + item.getPosition().getX() + ", " + item.getPosition().getY() + ", " + item.getPosition().getZ() + TextFormatting.YELLOW + "!"
        );
        TextComponentString tRegular = new TextComponentString(
                TextFormatting.GOLD + "[" + TextFormatting.YELLOW + "!" + TextFormatting.GOLD + "] " +
                        TextFormatting.GREEN + "Starred item " + TextFormatting.DARK_GREEN + itemName + TextFormatting.GREEN +
                        " has dropped at " + TextFormatting.AQUA + item.getPosition().getX() + ", " + item.getPosition().getY() + ", " + item.getPosition().getZ() + TextFormatting.YELLOW + "!"
        );
        Style style = (new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.GOLD + "Click to track the location!")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " + item.getPosition().getX() + " " + item.getPosition().getY() + " " + item.getPosition().getZ())));
        nullSafeMessage.sendMessage((isMythic ? tMythic : tRegular).setStyle(style));

        if (Main.autoTrack) {
            ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, "/compass " + item.getPosition().getX() + " " + item.getPosition().getY() + " " + item.getPosition().getZ());
        }
    }
}
