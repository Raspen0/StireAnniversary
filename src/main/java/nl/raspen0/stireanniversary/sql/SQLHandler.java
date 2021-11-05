package nl.raspen0.stireanniversary.sql;

import nl.raspen0.stireanniversary.Base;
import nl.raspen0.stireanniversary.BaseType;
import nl.raspen0.stireanniversary.Logger;
import nl.raspen0.stireanniversary.StireAnniversary;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.sql.*;
import java.util.*;

public class SQLHandler {

    private final StireAnniversary plugin;
    private final ConnectionPoolManager pool;

    public SQLHandler(StireAnniversary plugin) {
        this.plugin = plugin;
        pool = new ConnectionPoolManager(plugin);
        createTables();
    }

    /**
     * Creates the st_stats table if it doesn't exist.
     */
    public void createTables() {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS sa_bases (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "type ENUM('FACTION', 'TOWN', 'ESSENTIALS', 'BED') NOT NULL," +
                    "anniversary_world_id INT NOT NULL," +
                    "name VARCHAR (128) DEFAULT NULL," +
                    "world VARCHAR (32) NOT NULL," +
                    "x DOUBLE NOT NULL," +
                    "y DOUBLE NOT NULL," +
                    "z DOUBLE NULL," +
                    "yaw FLOAT NOT NULL," +
                    "pitch FLOAT NOT NULL)");
            ps.executeUpdate();

            ps = conn.prepareStatement("CREATE TABLE IF NOT EXISTS sa_players (" +
                    "id INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
                    "uuid BINARY(16) NOT NULL ," +
                    "world VARCHAR (32) NOT NULL," +
                    "baseid INT NOT NULL, " +
                    "CONSTRAINT baseid FOREIGN KEY (baseid) REFERENCES sa_bases (id) ON DELETE CASCADE ON UPDATE CASCADE)");
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getSALogger().log(Logger.LogType.ERROR, "Error creating SQL tables!");
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, null);
        }
    }

    public boolean addBases(Set<Base> baseSet) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = pool.getConnection();
            for(Base base : baseSet) {
                Location loc = base.getLocation();
                ps = conn.prepareStatement("INSERT INTO sa_bases (type, anniversary_world_id, name, world, x, y, z, yaw, pitch) VALUES "
                        + "(?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, base.getBaseType().toString());
                ps.setInt(2, base.getAnniversaryWorldID());
                ps.setString(3, base.getName());
                ps.setString(4, loc.getWorld().getName());
                ps.setDouble(5, loc.getX());
                ps.setDouble(6, loc.getY());
                ps.setDouble(7, loc.getZ());
                ps.setFloat(8, loc.getYaw());
                ps.setFloat(9, loc.getPitch());
                ps.executeUpdate();

                rs = ps.getGeneratedKeys();
                int key;

                if(rs.next()){
                    key = rs.getInt(1);
                } else {
                    plugin.getSALogger().log(Logger.LogType.ERROR, "Error getting ID of added base!");
                    pool.close(conn, ps, rs);
                    return false;
                }

                for(UUID uuid : base.getMembers()){
                    ps = conn.prepareStatement("INSERT INTO sa_players (uuid, world, baseid) VALUES "
                            + "(UNHEX(?), ?, ?)");
                    String uuidString = formatUUIDForSQL(uuid.toString());
                    ps.setString(1, uuidString);
                    ps.setString(2, loc.getWorld().getName());
                    ps.setInt(3, key);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            plugin.getSALogger().log(Logger.LogType.ERROR, "Error adding bases to database!");
            e.printStackTrace();
            return false;
        } finally {
            pool.close(conn, ps, rs);
        }
        return true;
    }

    public Map<Integer, Base> loadBases() {
        Map<Integer, Base> map = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        try {
            conn = pool.getConnection();
            ps = conn.prepareStatement("SELECT * FROM sa_bases");
            res = ps.executeQuery();
            while (res.next()) {
                int baseID = res.getInt("id");
                int anniversaryWorldID = res.getInt("anniversary_world_id");
                double x = res.getDouble("x");
                double y = res.getDouble("y");
                double z = res.getDouble("z");
                float yaw = res.getFloat("yaw");
                float pitch = res.getFloat("pitch");
                String world = res.getString("world");
                String name = res.getString("name");
                BaseType baseType = BaseType.valueOf(res.getString("type"));

                if(Bukkit.getWorld(world) == null){
                    plugin.getSALogger().log(Logger.LogType.WARNING, "Skipping base " + res.getInt("id") + " due to missing world: " + world);
                    continue;
                }

                Base base = new Base(new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch), name, baseType, baseID, anniversaryWorldID);
                map.put(res.getInt("id"), base);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, res);
        }
        return map;
    }

    public Set<Integer> loadPlayerBases(UUID uuid) {
        Set<Integer> baseList = new HashSet<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet res = null;

        try {
            conn = pool.getConnection();
            String uuidString = formatUUIDForSQL(uuid.toString());
            ps = conn.prepareStatement("SELECT * FROM sa_players WHERE uuid = UNHEX(?)");
            ps.setString(1, uuidString);
            res = ps.executeQuery();

            while (res.next()) {
                baseList.add(res.getInt("baseid"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            pool.close(conn, ps, res);
        }
        return baseList;
    }


    private String formatUUIDForSQL(String UUID){
        return UUID.replace("-", "");
    }

    public void closeConnection(){
        pool.closePool();
    }
}
