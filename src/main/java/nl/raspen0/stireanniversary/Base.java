package nl.raspen0.stireanniversary;

import org.bukkit.Location;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Base {

    private final Location location;
    private final BaseType baseType;
    private final String name;
    private final int anniversaryWorldID;
    private final int baseID;
    private Set<UUID> members = new HashSet<>();

    public Base(Location location, BaseType baseType, int baseID, int anniversaryWorldID){
        this.location = location;
        this.name = null;
        this.baseType = baseType;
        this.baseID = baseID;
        this.anniversaryWorldID = anniversaryWorldID;
    }

    public Base(Location location, String name, BaseType baseType, int baseID, int anniversaryWorldID){
        this.location = location;
        this.name = name;
        this.baseType = baseType;
        this.baseID = baseID;
        this.anniversaryWorldID = anniversaryWorldID;
    }

    public Location getLocation() {
        return location;
    }

    public int getBaseID() {
        return baseID;
    }

    public BaseType getBaseType() {
        return baseType;
    }

    public String getName() {
        return name;
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public void addMember(UUID uuid){
        members.add(uuid);
    }

    public int getAnniversaryWorldID() {
        return anniversaryWorldID;
    }
}
