package nl.budroid.klaus.scanner.util;

import nl.budroid.klaus.scanner.tasks.NotifyHackTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Commands {

    public static final String CMD_HCXDUMPTOOL_START = "hcxdumptool -o /home/robert/hcxtools/hcxdumptool/klaus/test/klaus_dump.pcapng -i scanner --enable_status=1";
    public static final String CMD_GET_LOCATION = "gpspipe -w -n 20 | grep -m 1 TPV | jq -r [.lat,.lon]|@csv";
    public static final String CMD_START_SCAN = "hcxdumptool -i scanner --do_rcascan -o /home/robert/hcxtools/hcxdumptool/klaus/dump.pcapng";
    public static final String CMD_GET_TIMESTAMP = "tshark -r /home/robert/hcxtools/hcxdumptool/klaus/dump.pcapng -Y wlan.sa==%s -T fields -e wlan.fixed.timestamp";
    public static final String CMD_GET_RSSI = "tshark -r /home/robert/hcxtools/hcxdumptool/klaus/test/klaus_dump.pcapng -2 -R wlan.sa==%s -T fields -e wlan_radio.signal_dbm";
    public static final String CMD_GET_HACKSTATE = "/home/robert/hcxtools/hcxdumptool/klaus/test/get_hack_state.sh";

    private static final Logger logger = LoggerFactory.getLogger(Commands.class);

    public static String[] rssiCommand(String bssid) {
        String command = String.format(CMD_GET_RSSI, formatMac(bssid));
        logger.info("Command constructed: " + command);
        return toArray(command);
    }


    public static String[] timestampCommand(String bssid) {
        String command = String.format(CMD_GET_TIMESTAMP, formatMac(bssid));
        return toArray(command);
    }

    private static String formatMac(String mac){
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < mac.length(); i++) {
            s.append(mac.charAt(i));
            if (Arrays.asList(1,3,5,7,9).contains(i)){
                s.append(":");
            }
        }
        return s.toString();
    }

    public static String[] startDumptool() {
        String command = String.format(CMD_HCXDUMPTOOL_START);
        return toArray(command);
    }

    public static String[] startScan() {
        String command = String.format(CMD_START_SCAN);
        return toArray(command);
    }

    public static String[] toArray(String command) {
        return command.split(" ");
    }

}
