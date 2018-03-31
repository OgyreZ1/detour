package ru.alphach1337.detour.managers;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.alphach1337.detour.sqlite.DataBase;

import java.util.ArrayList;
import java.util.HashMap;

public class DetourManager {
    private static final DetourManager INSTANCE = new DetourManager();

    //public ArrayList<String> players;
    public HashMap<String, Location> locations;
    public ArrayList<String> ignorePlayers;
    public FileConfiguration config = Bukkit.getPluginManager().getPlugin("Detour").getConfig();
    public ArrayList<String> party;

    private boolean isDetour = false;

    public static DetourManager getInstance() {
        return INSTANCE;
    }

    private DetourManager() {
    }

    public boolean getIsDetour() {
        return isDetour;
    }

    public boolean addPlayer(Player p){
        if(DataBase.contains(p.getUniqueId().toString(), "players") || DataBase.contains(p.getUniqueId().toString(), "ignoreplayers")){
            return false;
        }
        DataBase.insert(p.getUniqueId().toString(), "players");

        DataBase.insert(p.getUniqueId().toString(), "ignoreplayers");

        DataBase.insert(p.getName(), p.getUniqueId().toString(), "idandname");
        Location l = p.getLocation().clone();
        DataBase.insert(p.getUniqueId().toString(), l, "locations");
        return true;
    }

    public void start() {
        isDetour = true;
    }

    public void stop() {
        deleteAllTables();
        createAllTables();
        isDetour = false;
    }

    public void createAllTables(){
        DataBase.createTable("players");
        DataBase.createTable("ignoreplayers");
        DataBase.createTable("party");
        DataBase.createDuoTable("idandname",  "name");
        DataBase.createDuoTable("locations", "location");
    }

    public void deleteAllTables(){
        DataBase.deleteTable("players");
        DataBase.deleteTable("ignoreplayers");
        DataBase.deleteTable("party");
        DataBase.deleteTable("locations");
        DataBase.deleteTable("idandname");
    }
}
