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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ParametersAreNonnullByDefault
public class DetectMythics {
    static Map<UUID, NBTTagCompound> scannedUUID = new HashMap<>();
    private static final status s = new status((byte)0);

    @SubscribeEvent
    public void itemEvent(final EntityEvent event) {
        try {
            if (!Main.enabled)
                return;
            if (s.check())
                return;
            if (Minecraft.getMinecraft().world == null)
                return;
            CompletableFuture.runAsync(() -> {
                s.on();
                final List<Entity> worldEntity = Collections.unmodifiableList(Minecraft.getMinecraft().world.getLoadedEntityList());
                final Map<UUID, NBTTagCompound> UUIDMap = new HashMap<>(scannedUUID);
                for (Entity e : worldEntity) {
                    if (!(e instanceof EntityItem)) continue;
                    if (e.isGlowing()) continue;
                    NBTTagCompound modifiedNBT = e.serializeNBT();
                    modifiedNBT.removeTag("Age");
                    modifiedNBT.removeTag("Fire");
                    modifiedNBT.removeTag("FallDistance");
                    modifiedNBT.removeTag("Motion");
                    modifiedNBT.removeTag("Pos");
                    if (UUIDMap.containsKey(e.getUniqueID()) && UUIDMap.get(e.getUniqueID()).equals(modifiedNBT))
                        continue;
                    if (Main.debug) {
                        nullSafeMessage.sendMessage(new TextComponentString("Found item of " + e.getName()).setStyle(
                                new Style().setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        new TextComponentString(format("Item name: " + (e.hasCustomName() ? e.getCustomNameTag() + "(" + e.getName() + ")" : e.getName()) + "\n" + "Item UUID: " + e.getUniqueID() + "\n\n" + e.serializeNBT() + "\n\nClick to track!")))
                                ).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/compass " +
                                        e.getPosition().getX() + " " + e.getPosition().getY() + " " + e.getPosition().getZ()))));
                    }
                    checkItem((EntityItem) e);
                    scannedUUID.put(e.getUniqueID(), modifiedNBT);
                }
                s.off();
            }).exceptionally(e -> {
                s.off();
                e.printStackTrace();
                return null;
            }).thenAccept(x -> s.off());
        } catch (NullPointerException ignored) {}
    }

    private void checkItem(final EntityItem item) {
        NBTTagCompound nbt = item.serializeNBT();
        String itemName = nbt.getCompoundTag("Item").getCompoundTag("tag").getCompoundTag("display").getString("Name");
        // Prevent CoModExc
        final Set<String> mythic2 = new HashSet<>(Main.mythic);
        final Set<String> star2 = new HashSet<>(Main.star);
        final Set<Pattern> regexStar2 = new HashSet<>(Main.regexStar);
        if (mythic2.contains(itemName)) {
            mythicFound(item, itemName, true);
            return;
        }
        if(!star2.isEmpty())
        if (star2.contains(itemName)) {
            mythicFound(item, itemName, false);
            return;
        }
        if(!regexStar2.isEmpty()) // This causes insane lag js
        for(Pattern pattern : regexStar2) {
            Matcher matcher = pattern.matcher(itemName);
            if(matcher.matches()) {
                mythicFound(item, itemName, false);
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

    private void mythicFound(@NotNull final EntityItem item, String itemName, boolean isMythic) {
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
