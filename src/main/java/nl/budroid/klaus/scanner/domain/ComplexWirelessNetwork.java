package nl.budroid.klaus.scanner.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class ComplexWirelessNetwork {

    @Getter
    @Setter
    private Properties properties;

    public ComplexWirelessNetwork(String ssid){
        this.properties = new Properties(ssid);
    }

    public static class Properties{
        @Getter
        @Setter
        private String ssid;

        @Getter
        @Setter
        private List<AccessPoint> accessPoints;

        public Properties(String ssid){
            this.ssid = ssid;
            this.accessPoints = new ArrayList<>();
        }
    }

}
