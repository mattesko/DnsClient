package ca.mcgill.ecse.telecom;

import java.util.HashMap;
import ca.mcgill.ecse.telecom.packet.DnsPacket;

public class DnsClientLogger {

    private HashMap<String, String> requestArgs;
    private final int ANSWER_TYPE_A = 0x0001;
    private final int ANSWER_TYPE_NS = 0x0002;
    private final int ANSWER_TYPE_MX = 0x000f;
    private final int ANSWER_TYPE_CNAME = 0x0005;

    public DnsClientLogger(HashMap<String, String> requestArgs) {
        this.requestArgs = requestArgs;
    }

    public void printRequest() {
        System.out.printf("DnsClient sending request for %s", this.requestArgs.get("domainName"));
        System.out.printf("Server: %s", this.requestArgs.get("dnsIp"));
        System.out.printf("Request type: %s", this.requestArgs.get("queryType").toUpperCase());
    }

    public void printResponse(DnsPacket dnsPacket, long elapsedTime) {
        DnsPacket.DnsPacketHeader header = dnsPacket.getPacketHeader();
        DnsPacket.DnsPacketQuestion question = dnsPacket.getPacketQuestion();
        DnsPacket.DnsPacketAnswer answer = dnsPacket.getPacketAnswer();

        System.out.printf("Response received after %d seconds (%s retries)", elapsedTime, this.requestArgs.get("maxRetries"));
        
        if (header.getAncount() > 0) {
            System.out.printf("***Answer Section (%d records)", header.getAncount());
            printAnswerSection(header, question, answer);
        }
        else {
            System.out.println("NOTFOUND");
        }

        if (header.getArcount() > 0) {
            System.out.printf("***Additional Section (%d records)", header.getArcount());
            printAdditionalSection(header, question, answer);
        }

    }

    private void printAnswerSection(DnsPacket.DnsPacketHeader header, DnsPacket.DnsPacketQuestion question, DnsPacket.DnsPacketAnswer answer) {
        String alias = ""; // TODO Idk what this is
        String type = answer.getType() == ANSWER_TYPE_A ? "IP" : 
            answer.getType() == ANSWER_TYPE_NS ? "NS" : 
            answer.getType() == ANSWER_TYPE_MX ? "MX" : 
            answer.getType() == ANSWER_TYPE_CNAME ? "CNAME" : "";

        if (answer.getType() == ANSWER_TYPE_A) {
            
        }
        else if (answer.getType() == ANSWER_TYPE_MX) {
            
        }
        else {
            
        }
    }

    /*
        IP <tab> [ip address] <tab> [seconds can cache] <tab> [auth | nonauth]

        CNAME <tab> [alias] <tab> [seconds can cache] <tab> [auth | nonauth]
        NS <tab> [alias] <tab> [seconds can cache] <tab> [auth | nonauth]

        MX <tab> [alias] <tab> [pref] <tab> [seconds can cache] <tab> [auth | nonauth]
    */

    private void printAnswerIP(DnsPacket.DnsPacketHeader header, DnsPacket.DnsPacketAnswer answer) {
        // TODO implement me
    }

    private void printAnswerNS() {
        // TODO implement me
    }

    private void printAnswerCNAME() {
        // TODO implement me
    }

    private void printAnswerMX() {
        // TODO implement me
    }

    private void printAdditionalSection(DnsPacket.DnsPacketHeader header, DnsPacket.DnsPacketQuestion question, DnsPacket.DnsPacketAnswer answer) {
        // TODO implement me
    }
}