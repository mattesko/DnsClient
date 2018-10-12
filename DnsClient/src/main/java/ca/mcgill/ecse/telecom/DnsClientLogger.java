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

    public void printResponse(DatagramPacket dgPacket, DnsPacket packetModel, long elapsedTime) throws Exception{
        ByteBuffer buffer = ByteBuffer.wrap(dgPacket.getData());

        byte[] dataHeader = new byte[12];
        buffer.get(dataHeader, 0, 12);
        parseResponseHeader(ByteBuffer.wrap(dataHeader), packetModel.getPacketHeader().getId());

        // Bytes taken up by the QNAME is dynamic, but QTYPE and QCLASS take up 4 bytes and need 2 additional bytes for the first and last labels
        byte[] dataQuestion = new byte[requestArgs.get("domainName").length() + 2 + 4];
        buffer.get(dataQuestion, 0, dataQuestion.length);
        parseResponseQuestion(ByteBuffer.wrap(dataQuestion));

        byte[] dataAnswer = new byte[DnsClient.DATAGRAM_RESPONSE_SIZE - dataHeader.length - dataQuestion.length];
        buffer.get(dataAnswer, 0, dataAnswer.length);
        printAnswerRecords(ByteBuffer.wrap(dataAnswer), packetModel);
    }

    private void printAnswerRecords(ByteBuffer answer, DnsPacket packetModel) {

        while (answer.remaining() > 0) {

            short name = answer.getShort();
            short type = answer.getShort();
            short class_x = answer.getShort();
            int timeToLive = answer.getInt();
            short dataLength = answer.getShort();
            byte[] data = new byte[dataLength];

            if (type == ANSWER_TYPE_A) {
                answer.get(data, 0, dataLength);
                System.out.printf("IP \t %d.%d.%d.%d \t %d \t %s",
                    data[0] > 0 ? data[0] : data[0] + 255,
                    data[1] > 0 ? data[1] : data[1] + 255,
                    data[2] > 0 ? data[2] : data[2] + 255,
                    data[3] > 0 ? data[3] : data[3] + 255,
                    timeToLive,
                    "auth | nonauth"
                );
            }
            else if (type == ANSWER_TYPE_NS) {
                answer.get(data, 0, dataLength);
                System.out.printf("NS \t %s \t %d \t %s", 
                    new String(data, StandardCharsets.UTF_8),
                    timeToLive,
                    "auth | nonauth"
                );
            }
            else if (type == ANSWER_TYPE_CNAME) {
                answer.get(data, 0, dataLength);
                System.out.printf("CNAME \t %s \t %d \t %s", 
                    new String(data, StandardCharsets.UTF_8),
                    timeToLive,
                    "auth | nonauth"
                );
            }
            else if (type == ANSWER_TYPE_MX) {
                short preference = answer.getShort();
                answer.get(data, 0, dataLength - 2);
                System.out.printf("MX \t %s \t %d \t %d \t %s", 
                    new String(data, StandardCharsets.UTF_8),
                    preference,
                    timeToLive,
                    "auth | nonauth"
                );
            }
        }
    }
        
    private void parseResponseQuestion(ByteBuffer question) {


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

