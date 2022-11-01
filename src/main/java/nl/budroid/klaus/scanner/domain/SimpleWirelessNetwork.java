package nl.budroid.klaus.scanner.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

public class SimpleWirelessNetwork implements Comparable<SimpleWirelessNetwork>{

    @Getter
    @Setter
    private String ssid;
    @Getter
    @Setter
    private String bssid;
    @Getter
    @Setter
    private int channel;
    @Getter
    @Setter
    private int rssi;

    public ComplexWirelessNetwork toComplex(){
        ComplexWirelessNetwork newComplex = new ComplexWirelessNetwork(this.getSsid());
        AccessPoint accessPoint = new AccessPoint();
        accessPoint.setBssid(this.getBssid());
        accessPoint.setRssi(this.getRssi());
        accessPoint.setChannel(this.getChannel());
        newComplex.getProperties().getAccessPoints().add(accessPoint);
        return newComplex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleWirelessNetwork that = (SimpleWirelessNetwork) o;
        return getChannel() == that.getChannel() && getRssi() == that.getRssi() && getSsid().equals(that.getSsid()) && getBssid().equals(that.getBssid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSsid(), getBssid(), getChannel(), getRssi());
    }

    @Override
    public int compareTo(SimpleWirelessNetwork that) {
        return Integer.compare(that.getRssi(), this.getRssi());
    }
}
