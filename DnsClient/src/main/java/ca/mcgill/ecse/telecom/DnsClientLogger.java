package ca.mcgill.ecse.telecom;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
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

    public void PrintPacket(DatagramPacket dgPacket) {
        


    }
    //Parse the header and print the result (For Now)
    private static void parseResponseHeader(ByteBuffer Header, int ID) throws Exception{
        //Check For Matching ID - 2 bytes
        int r_id = (int) Header.getShort();
        if(r_id != ID){
            //Error Message and stop the process- TODO
            throw new Exception("ERROR - Response ID does not match Query ID.\n");
        }
        //Get Next Byte - 1 bytes (QR, OPCODE, AA, TC, RD)
        byte buffer = Header.get();

        //QR is Irrelevant ** ToDelete
        //OPCODE is Irrelevant ** ToDelete

        //Save Authoritative
        boolean AA = (buffer & 1<<2) == 4; //Check if 3th bit is 1

        //Check if truncate
        if((buffer & 1<<1) == 2){ //If second bit is 1 throw error
            throw new Exception("ERROR - Response was Truncated.\n");
        }
        //RD is Irrelevant ** ToDelete

        //Check for next Byte (RA, Z, RCODE)
        buffer = Header.get();





        
        //

    }

    public void printRequest() {
        System.out.printf("DnsClient sending request for %s\n", this.requestArgs.get("domainName"));
        System.out.printf("Server: %s\n", this.requestArgs.get("dnsIp"));
        System.out.printf("Request type: %s\n", this.requestArgs.get("queryType").toUpperCase());
    }
    }

