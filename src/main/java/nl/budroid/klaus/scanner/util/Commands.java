package nl.budroid.klaus.scanner.util;

import java.util.Arrays;

public class Commands {

    private static final String CMD_HCXDUMPTOOL_START = "hcxdumptool -o dump.pcapng -i scanner --enable_status=1 > hcxdumptool_output";
    public static final String CMD_GET_LOCATION = "gpspipe -w -n 20 | grep -m 1 TPV | jq -r [.lat,.lon]|@csv";
    public static final String CMD_START_SCAN = "hcxdumptool -i scanner --do_rcascan -o /home/robert/hcxtools/hcxdumptool/klaus/dump.pcapng";
    public static final String CMD_GET_TIMESTAMP = "tshark -r /home/robert/hcxtools/hcxdumptool/klaus/dump.pcapng -Y wlan.sa==%s -T fields -e wlan.fixed.timestamp";

    public static String[] timestampCommand(String bssid) {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < bssid.length(); i++) {
            s.append(bssid.charAt(i));
            if (Arrays.asList(1,3,5,7,9).contains(i)){
                s.append(":");
            }
        }
        String command = String.format(CMD_GET_TIMESTAMP, s);
        return toArray(command);
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
