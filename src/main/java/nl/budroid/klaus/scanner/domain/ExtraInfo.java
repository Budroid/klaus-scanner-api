package nl.budroid.klaus.scanner.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class ExtraInfo {
    @Getter
    @Setter
    String vendor;
    @Getter
    @Setter
    String uptime;
}
