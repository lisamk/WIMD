package wimd;

public class Location {
    private String location;
    private String mac;
    private int rssi;
    private long timestamp;

    public Location(String location, String mac, int rssi, long timestamp){
        this.location = location;
        this.mac = mac;
        this.rssi = rssi;
        this.timestamp = timestamp;
    }

    public String getLocation() {
        return location;
    }

    public String getMac() {
        return mac;
    }

    public int getRSSI() {
        return rssi;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
