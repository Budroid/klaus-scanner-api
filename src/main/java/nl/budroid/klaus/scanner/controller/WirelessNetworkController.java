package nl.budroid.klaus.scanner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.budroid.klaus.scanner.domain.ExtraInfo;
import nl.budroid.klaus.scanner.util.CommandLineRunner;
import nl.budroid.klaus.scanner.util.Commands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/wirelessnetwork")
public class WirelessNetworkController {

    private static final Logger logger = LoggerFactory.getLogger(WirelessNetworkController.class);

    @GetMapping("/extra")
    public ResponseEntity<Map<String, ExtraInfo>> getExtraInfo(@RequestParam List<String> networks) {
        Map<String, ExtraInfo> extraInfoMap = new HashMap<>();
        networks.parallelStream().forEach(bssid -> {
            extraInfoMap.put(bssid, new ExtraInfo(getVendor(bssid), getUptime(bssid)));
        });
        return ResponseEntity.ok(extraInfoMap);
    }

    private String getUptime(String bssid) {
        List<String> result;
        try {
            result = CommandLineRunner.run(Commands.timestampCommand(bssid));
        } catch (IOException e) {
            return "Unknown";
        }
        String timestampString = result.get(result.size() - 1);
        // Calculate uptime from timestamp
        long timestamp;
        try {
            timestamp = Long.parseLong(timestampString);
        }catch (NumberFormatException nfe){
            return "Unknown";
        }

        final long SECS = 1000000;
        final long MINS = SECS * 60;
        final long HOURS = MINS * 60;
        final long DAYS = HOURS * 24;

        long daysUp = Math.floorDiv(timestamp, DAYS);
        long rest = timestamp % DAYS;
        long hoursUP = Math.floorDiv(rest, HOURS);
        rest = rest % HOURS;
        long minutesUp = Math.floorDiv(rest, MINS);
        rest = rest % MINS;
        long secondsUp = Math.floorDiv(rest, SECS);
        return String.format("%dd %02d:%02d:%02d", daysUp, hoursUP, minutesUp, secondsUp);
    }

    private String getVendor(String bssid)  {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map = mapper.readValue(Paths.get("/home/robert/hcxtools/hcxdumptool/klaus/oui.json").toFile(), Map.class);
        } catch (IOException e) {
            return "Unknown vendor";
        }
        String oui = bssid.replace(":", "").substring(0,6).toUpperCase();
        String vendor = map.get(oui);
        return vendor == null ? "Unknown vendor" : vendor;
    }
}
