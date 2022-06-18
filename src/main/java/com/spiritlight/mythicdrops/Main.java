package com.spiritlight.mythicdrops;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Mod(modid = Main.MODID, name = Main.NAME, version = Main.VERSION)
public class Main
{
    public static final String MODID = "mifik";
    public static final String NAME = "MythicDrops";
    public static final String VERSION = "1.0";
    static boolean autoTrack = false;
    static boolean enabled = true;
    static boolean debug = false;
    static Set<String> mythic = new HashSet<>();
    static Set<String> star = new HashSet<>();
    static Set<Pattern> regexStar = new HashSet<>();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CompletableFuture.runAsync(API::fetchItem);
        MinecraftForge.EVENT_BUS.register(new DetectMythics());
        ClientCommandHandler.instance.registerCommand(new Commands());
        ClientCommandHandler.instance.registerCommand(new StarCommand());
        try {
            ConfigSpirit.read();
        } catch (IOException ex) {
            ex.printStackTrace();
            try {
                ConfigSpirit.write();
            } catch (IOException exc) {
                exc.printStackTrace();
            }
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {

    }
}
