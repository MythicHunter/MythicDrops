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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class DetectMythics {
    static final Map<UUID, NBTTagCompound> scannedUUID = new ConcurrentHashMap<>();
    private static final status s = new status();

    @SubscribeEvent
    public void itemEvent(final EntityEvent event) {
        if (!Main.enabled)
            return;
        if (s.check())
            return;
        if (Minecraft.getMinecraft().world == null)
            return;
        try {
            CompletableFuture.runAsync(() -> {
                s.on();
                final List<Entity> worldEntity = new ArrayList<>(Minecraft.getMinecraft().world.getLoadedEntityList());
                for (Entity e : worldEntity) {
                    if (!(e instanceof EntityItem)) continue;
                    if (e.isGlowing()) continue;
                    if(e.getName().contains("NPC")) continue;
                    NBTTagCompound trimmedNBT = e.serializeNBT();
                    trimmedNBT.removeTag("Age");
                    trimmedNBT.removeTag("Motion");
                    trimmedNBT.removeTag("Pos");
                    trimmedNBT.removeTag("Fire");
                    trimmedNBT.removeTag("FallDistance");
                    trimmedNBT.removeTag("PickupDelay");
                    trimmedNBT.removeTag("OnGround");
                    if (scannedUUID.containsKey(e.getUniqueID()) && scannedUUID.get(e.getUniqueID()).equals(trimmedNBT))
                        continue;
                    if (Main.debug) {
                        nullSafeMessage.sendMessage(new TextComponentString("Found item of " + e.getName()).setStyle(
                                new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new TextComponentString(format("Wynncraft Item Name:" + e.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name") + "\n\n" + "Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + e.serializeNBT() + "\n\nClick to track!")))
                                ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                        e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
                    }
                    checkItem((EntityItem) e);
                    scannedUUID.put(e.getUniqueID(), trimmedNBT);
                }
                s.off();
            }).exceptionally(e -> {
                s.off();
                nullSafeMessage.sendMessage("An error has occurred, please check logs.");
                e.printStackTrace();
                return null;
            }).thenAccept(x -> s.off());
        } catch (NullPointerException ignored) {}
    }

    private void checkItem(final EntityItem item) {
        NBTTagCompound nbt = item.serializeNBT();
        String itemName = nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
        boolean preIdentified = nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").toString().contains("identifications");
        if(preIdentified && Main.unidOnly) return;
        Set<String> mythic2 = new HashSet<>(Main.mythic);
        boolean strictSearch = true;
        if (mythic2.contains(itemName)) {
            itemFound(item, preIdentified);
            return;
        }
        final Set<String> star2 = new HashSet<>(Main.star);
        if(Main.leniency % 2 == 1) {
            itemName = itemName.toLowerCase(Locale.ROOT);
        }
        if(Main.leniency >= 2) {
            strictSearch = false;
        }
        final String finalItemName = itemName;
        if(!star2.isEmpty()) {
            if ((strictSearch ? star2.contains(itemName) : star2.stream().anyMatch(i -> i.toLowerCase(Locale.ROOT).contains(finalItemName)))) {
                itemFound(item, preIdentified);
                return;
            }
        }
        itemName = nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
        final Set<Pattern> regexStar2 = new HashSet<>(Main.regexStar);
        if(!regexStar2.isEmpty()) // This causes insane lag js
        for(Pattern pattern : regexStar2) {
            Matcher matcher = pattern.matcher(itemName);
            if(matcher.find()) {
                itemFound(item, preIdentified);
                return;
            }
        }
    }

    private @NotNull String format(String s) {
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

    private void itemFound(@NotNull final EntityItem item, boolean preIdentified) {
        item.setGlowing(true);
        Minecraft.getMinecraft().player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        final String itemName = item.serializeNBT().getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
        final ItemType type = ItemType.Type.getType(itemName);
        final TextComponentString iTextComponents;
        switch(type) {
            case MYTHIC:
            iTextComponents = new TextComponentString(
                    TextFormatting.LIGHT_PURPLE + "[" + TextFormatting.DARK_PURPLE + "!" + TextFormatting.LIGHT_PURPLE + "] " +
                            TextFormatting.LIGHT_PURPLE + (preIdentified ? "Identified Mythic " : "Mythic Item Unidentified ") + TextFormatting.DARK_PURPLE + itemName + TextFormatting.LIGHT_PURPLE +
                            " has dropped at " + TextFormatting.AQUA + item.getPosition().getX() + ", " + item.getPosition().getY() + ", " + item.getPosition().getZ() + TextFormatting.YELLOW + "!"
            );
            break;
            case INGREDIENT:
                iTextComponents = new TextComponentString(
                        TextFormatting.GOLD + "[" + TextFormatting.YELLOW + "!" + TextFormatting.GOLD + "] " +
                                TextFormatting.GREEN + "Starred ingredient " + TextFormatting.DARK_GREEN + itemName + TextFormatting.GREEN +
                                " has dropped at " + TextFormatting.AQUA + item.getPosition().getX() + ", " + item.getPosition().getY() + ", " + item.getPosition().getZ() + TextFormatting.YELLOW + "!"
                );
                break;
            case ITEM:
            iTextComponents = new TextComponentString(
                    TextFormatting.GOLD + "[" + TextFormatting.YELLOW + "!" + TextFormatting.GOLD + "] " +
                            TextFormatting.GREEN + (preIdentified ? "Identified starred item " : "Starred item Unidentified ") + TextFormatting.DARK_GREEN + itemName + TextFormatting.GREEN +
                            " has dropped at " + TextFormatting.AQUA + item.getPosition().getX() + ", " + item.getPosition().getY() + ", " + item.getPosition().getZ() + TextFormatting.YELLOW + "!"
            );
            break;
            case UNKNOWN:
            default:
                return; // Unknown items usually cannot be labelled
        }
        Style style = (new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(TextFormatting.GOLD + "Click to track the location!")))
                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " + item.getPosition().getX() + " " + item.getPosition().getY() + " " + item.getPosition().getZ())));
        nullSafeMessage.sendMessage(iTextComponents.setStyle(style));

        if (Main.autoTrack) {
            ClientCommandHandler.instance.executeCommand(Minecraft.getMinecraft().player, "/compass " + item.getPosition().getX() + " " + item.getPosition().getY() + " " + item.getPosition().getZ());
        }
    }
}
