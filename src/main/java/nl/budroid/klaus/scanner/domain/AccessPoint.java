package nl.budroid.klaus.scanner.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@NoArgsConstructor
public class AccessPoint {

    @Getter
    @Setter
    private String bssid;

    @Setter
    @Getter
    private int rssi;

    @Setter
    @Getter
    private int channel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessPoint that = (AccessPoint) o;
        return getBssid().equals(that.getBssid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBssid());
    }
}