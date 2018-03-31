package ru.alphach1337.detour.sqlite;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import ru.alphach1337.detour.Settings;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static io.netty.handler.codec.mqtt.MqttMessageBuilders.connect;

public class DataBase {
    static Connection co;
    static Statement statement;
    private static void open() { //открыть (создать) бд
        try {
            Class.forName("org.sqlite.JDBC");
            co = DriverManager.getConnection("jdbc:sqlite:detour.db");
            statement = co.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void close() { //закрыть бд
        try {
            statement.close();
            co.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTable(String table) { //создать таблицу с uuid игроков
        open();
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                    "uuid VARCHAR(50));";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }

    public static void createDuoTable(String table, String column) { //создать таблицу с двумя столбцами
        open();
        try {
            String query = "CREATE TABLE IF NOT EXISTS " + table + " (" +
                    column + " VARCHAR(100), " +
                     "uuid VARCHAR(100));";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }

    public static ArrayList<String> selectAll(String table) { //получить список uuid игроков
        open();
        ArrayList<String> list = new ArrayList<>();
        String query = "SELECT uuid " +
                "FROM " + table;
        try(ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("uuid");
                list.add(name);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        close();
        return list;
    }

    public static String selectNameById(String uuid, String table) { //получить имя по UUID
        open();
        HashMap<String, String> map = new HashMap<>();
        String query = "SELECT  name " +
                "FROM " + table + " WHERE uuid='" + uuid + "';";
        try {
            ResultSet rs = statement.executeQuery(query);
            rs.next();
            String name = rs.getString("name");
            rs.close();
            return name;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return null;
    }

    public static HashMap<String, Location> selectAllLocations(String table) { //получить мапу uuid:location
        open();
        HashMap<String, Location> map = new HashMap<>();
        String query = "SELECT uuid, location " +
                "FROM " + table;
        try(ResultSet rs = statement.executeQuery(query)) {
            World world;
            double[] xyz = new double[3];
            while (rs.next()) {
                String[] loc = rs.getString("location").split("&");
                world = Bukkit.getServer().getWorld(loc[0]);
                for (int i = 1; i <= 3; i++) {
                    xyz[i-1] = Double.parseDouble(loc[i]);
                }
                String uuid = rs.getString("uuid");
                map.put(uuid, new Location(world, xyz[0], xyz[1], xyz[2]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
        return map;
    }

    public static void insert(String uuid, Location location, String table) { //вставить запись uuid:location
        open();
        try {
            String loc = location.getWorld().getName() + "&" + location.getX() + "&" + location.getY() + "&" + location.getZ();
            String query = "INSERT INTO " + table + " (uuid, location) " +
                    "VALUES('"+uuid+"', '" + loc + "');";
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
    }

    public static void insert(String uuid, String table) { //вставить uuid ользователя в таблицу
        open();
        try {
            String query = "INSERT INTO " + table + " (uuid) " +
                    "VALUES('" + uuid + "');";
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
    }

    public static void insert(String name, String uuid, String table) { //вставить запись uuid:name где name - имя игрока
        open();
        try {
            String query = "INSERT INTO " + table + " (uuid, name) " +
                    "VALUES('" + uuid + "', '" + name + "');";
            statement.executeUpdate(query);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
    }

    public static boolean contains(String uuid, String table) { //проверить, есть uuid игрока в таблице
        try {
            ArrayList<String> list = selectAll(table);
            for (String s : list) {
                if (uuid.equals(s)) return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void delete(String uuid, String table) { //удалить uudi из таблицы
        open();
        try {
            String query = "DELETE FROM " + table +
                    " WHERE uuid='" + uuid + "';";
            statement.executeUpdate(query);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }

    public static void deleteTable(String table) { //удалить таблицу из бд
        open();
        try {
            String query = "DROP TABLE IF EXISTS " + table + ";";
            statement.execute(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        close();
    }
}