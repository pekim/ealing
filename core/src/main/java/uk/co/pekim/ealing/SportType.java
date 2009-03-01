package uk.co.pekim.ealing;

import java.util.HashMap;
import java.util.Map;

public enum SportType {
    RUNNING(0, "running"),
    BIKING(1, "biking"),
    OTHER(2, "other"),
    ;
    
    static Map<Integer, SportType> idMap;
    
    static {
        idMap = new HashMap<Integer, SportType>();
        for (SportType sportType : SportType.values()) {
            idMap.put(sportType.id, sportType);
        }
    }
    
    private final Integer id;
    private final String description;

    private SportType(int id, String description) {
        this.id = new Integer(id);
        this.description = description;
    }

    public int getId() {
        return id.intValue();
    }

    public String getDescription() {
        return description;
    }
    
    public static SportType valueOf(int id) {
        return idMap.get(new Integer(id));
    }
    
    @Override
    public String toString() {
        return description;
    }
}
