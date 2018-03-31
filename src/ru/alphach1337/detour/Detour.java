package ru.alphach1337.detour;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import ru.alphach1337.detour.commands.*;
import ru.alphach1337.detour.events.EventListener;
import ru.alphach1337.detour.managers.DetourManager;

import java.util.ArrayList;


public class Detour extends JavaPlugin implements Listener {
    private ArrayList<Player> party = new ArrayList<>();

    private boolean isDetour = false;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        this.getConfig().addDefault("allowOffline", false);
        this.getConfig().addDefault("hoursToAllowDetour", 12);
        this.getConfig().options().copyDefaults(true);
        saveConfig();

        CommandHandler cw = new CommandHandler("detour");
        cw.commands.add(new Join());
        cw.commands.add(new Start());
        cw.commands.add(new Stop());
        cw.commands.add(new Next());
        cw.commands.add(new Party());
        cw.commands.add(new Stick());
        this.getCommand("detour").setExecutor(cw);

        DetourManager.getInstance().createAllTables();
    }

    @Override
    public void onDisable() {

    }
}