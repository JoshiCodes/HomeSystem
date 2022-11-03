package de.joshizockt.homesystem.api.manager;

import de.joshizockt.homesystem.common.util.SQLConnector;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

public class HomeManager {

    //       Player UUID,        <Home Name, Location>
    private HashMap<UUID, HashMap<String, Location>> cache;

    private final SQLConnector sqlConnector;

    public HomeManager(SQLConnector sqlConnector) {
        this.cache = new HashMap<>();
        this.sqlConnector = sqlConnector;
    }

    public HashMap<String, Location> getAll(UUID uuid) {
        if(cache.containsKey(uuid)) return cache.get(uuid);
        HashMap<String, Location> homes = new HashMap<>();
        try {
            ResultSet rs = sqlConnector.query("SELECT * FROM " + SQLConnector.TABLE_NAME + " WHERE uuid = '" + uuid.toString() + "';");
            while(rs.next()) {
                String home = rs.getString("home");
                String locationString = rs.getString("location");
                Location location = parseLocationString(locationString);
                if(location != null) homes.put(home.toLowerCase(Locale.ROOT), location);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        cache.put(uuid, homes);
        return homes;
    }

    public boolean hasHome(UUID uuid, String home) {
        if(cache.containsKey(uuid)) {
            return cache.get(uuid).containsKey(home.toLowerCase(Locale.ROOT));
        }
        return getAll(uuid).containsKey(home.toLowerCase(Locale.ROOT));
    }

    /**
     * Set a home for a player
     * @param uuid Player UUID
     * @param home Home name
     * @param location Location for the Home
     * @return true if the home was set, false if a home with the same name already exists or an error occurred
     */
    public boolean setHome(UUID uuid, String home, Location location) {
        if(hasHome(uuid, home.toLowerCase(Locale.ROOT))) return false;
        HashMap<String, Location> homes;
        if(cache.containsKey(uuid)) homes = cache.get(uuid);
        else homes = getAll(uuid);
        homes.put(home.toLowerCase(Locale.ROOT), location);
        cache.put(uuid, homes);
        try {
            sqlConnector.update("INSERT INTO " + SQLConnector.TABLE_NAME + " (uuid, home, location) VALUES ('" + uuid.toString() + "', '" + home.toLowerCase(Locale.ROOT) + "', '" + parseLocation(location) + "');");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a home for a player
     * @param uuid Player UUID
     * @param home Home name
     * @return true if the home was deleted, false if the home doesn't exist or an error occurred
     */
    public boolean deleteHome(UUID uuid, String home) {
        if(!hasHome(uuid, home)) return false;
        HashMap<String, Location> homes;
        if(cache.containsKey(uuid)) homes = cache.get(uuid);
        else homes = getAll(uuid);
        homes.remove(home.toLowerCase(Locale.ROOT));
        cache.put(uuid, homes);
        try {
            sqlConnector.update("DELETE FROM " + SQLConnector.TABLE_NAME + " WHERE uuid = '" + uuid.toString() + "' AND home = '" + home.toLowerCase(Locale.ROOT) + "';");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteAll(UUID uniqueId) {
        cache.remove(uniqueId);
        try {
            sqlConnector.update("DELETE FROM " + SQLConnector.TABLE_NAME + " WHERE uuid = '" + uniqueId.toString() + "';");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a home for a player
     * @param uuid Player UUID
     * @param home Home name
     * @return the location of the home or null if the home doesn't exist
     */
    public Location getHome(UUID uuid, String home) {
        if(!hasHome(uuid, home)) return null;
        if(!cache.containsKey(uuid)) return null;
        HashMap<String, Location> homes = cache.get(uuid);
        if(homes.containsKey(home.toLowerCase(Locale.ROOT))) return homes.get(home.toLowerCase(Locale.ROOT));
        try {
            ResultSet rs = sqlConnector.query("SELECT * FROM " + SQLConnector.TABLE_NAME + " WHERE uuid = '" + uuid.toString() + "' AND home = '" + home.toLowerCase(Locale.ROOT) + "';");
            if(rs.next()) {
                String locationString = rs.getString("location");
                return parseLocationString(locationString);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String parseLocation(Location location) {
        return
                Objects.requireNonNull(location.getWorld()).getName() + ";" +
                        location.getX() + ";" +
                        location.getY() + ";" +
                        location.getZ() + ";" +
                        location.getYaw() + ";" +
                        location.getPitch();
    }

    public static Location parseLocationString(String location) {
        try {
            String[] split = location.split(";");
            return new Location(
                    Bukkit.getWorld(split[0]),
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[2]),
                    Double.parseDouble(split[3]),
                    Float.parseFloat(split[4]),
                    Float.parseFloat(split[5])
            );
        } catch (NumberFormatException | NullPointerException e) {
            // In case of corrupted or wrong data
            e.printStackTrace();
            return null;
        }
    }

}
