package nl.budroid.klaus.scanner.domain;

import lombok.*;

@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HackInfo{
    @Getter
    @Setter
    @NonNull
    @EqualsAndHashCode.Include
    private String ssid;

    @Getter
    @Setter
    @NonNull
    @EqualsAndHashCode.Include
    private String ap;

    @Getter
    @Setter
    @NonNull
    @EqualsAndHashCode.Include
    private String hashLine;

    @Getter
    @Setter
    private int rssi;
}