package ca.mcgill.ecse.telecom;

import java.util.HashMap;
import ca.mcgill.ecse.telecom.packet.DnsPacket;

public class DnsClientLogger {
    
    private HashMap<String, String> requestArgs;
    
    public DnsClientLogger(HashMap<String, String> requestArgs) {
        this.requestArgs = requestArgs;
    }

    public void printRequest() {
        System.out.printf("DnsClient sending request for %s", this.requestArgs.get("domainName"));
        System.out.printf("Server: %s", this.requestArgs.get("dnsIp"));
        System.out.printf("Request type: %s", this.requestArgs.get("queryType").toUpperCase());
    }
}