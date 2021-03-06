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
    private final int ANSWER_TYPE_SOA = 0x0006;

    public DnsClientLogger(HashMap<String, String> requestArgs) {
        this.requestArgs = requestArgs;
    }

    public void printResponse(DatagramPacket dgPacket, DnsPacket packetModel, float elapsedTime, int retryCounter) throws Exception{
        ByteBuffer buffer = ByteBuffer.wrap(dgPacket.getData());

        byte[] dataHeader = new byte[12];
        buffer.get(dataHeader, 0, 12);
        short[] headerFields = parseResponseHeader(ByteBuffer.wrap(dataHeader), packetModel.getPacketHeader().getId());

        // Bytes taken up by the QNAME is dynamic, but QTYPE and QCLASS take up 4 bytes and need 2 additional bytes for the first and last labels
        byte[] dataQuestion = new byte[requestArgs.get("domainName").length() + 2 + 4];
        buffer.get(dataQuestion, 0, dataQuestion.length);
        parseResponseQuestion(ByteBuffer.wrap(dataQuestion));

        byte[] dataAnswer = new byte[DnsClient.DATAGRAM_RESPONSE_SIZE - dataHeader.length - dataQuestion.length];
        buffer.get(dataAnswer, 0, dataAnswer.length);

        System.out.printf("Response received after %f seconds (" + (retryCounter - 1)+ " retries)\n", (double)elapsedTime / 1000000000.0); // FIXME 
        System.out.printf("***Answer Section (%d records)***\n", headerFields[0] + headerFields[3]);
        if (headerFields[0] + headerFields[3] > 0) printAnswerRecords(ByteBuffer.wrap(dataAnswer), headerFields, packetModel, dataHeader.length + dataQuestion.length);

        System.out.printf("***Additional Section (%d records)***\n", headerFields[1]);
        if (headerFields[1] > 0) printAnswerRecords(buffer, headerFields, packetModel, dataHeader.length + dataQuestion.length);
    }

    private void printAnswerRecords(ByteBuffer answer, short[] headerFields, DnsPacket packetModel, int headerLength) {
        
        for (int printedAnswers = 0; printedAnswers < headerFields[0] + headerFields[3]; printedAnswers++)  {

            short namePtr = answer.getShort();
            short type = answer.getShort();
            short class_x = answer.getShort();
            int timeToLive = answer.getInt();
            short dataLength = answer.getShort();
            byte[] data = new byte[dataLength];

            if (type == ANSWER_TYPE_A) {
                answer.get(data, 0, dataLength);
                System.out.printf("IP \t %d.%d.%d.%d \t %d \t %s\n",
                    data[0] > 0 ? data[0] : data[0] + 255,
                    data[1] > 0 ? data[1] : data[1] + 255,
                    data[2] > 0 ? data[2] : data[2] + 255,
                    data[3] > 0 ? data[3] : data[3] + 255,
                    timeToLive,
                    headerFields[2] == 1 ? "auth" : "nonauth"
                );
            }
            else if (type == ANSWER_TYPE_NS || type == ANSWER_TYPE_SOA) {
                String nameServer = parseMailbox(answer, namePtr, headerLength);
                String mailbox = parseMailbox(answer, namePtr, headerLength);
                System.out.printf("%s \t %s \t %d \t %s\n",
                    requestArgs.get("queryType").toUpperCase(),
                    requestArgs.get("queryType").toUpperCase().equals("NS") ? nameServer : mailbox,
                    timeToLive,
                    headerFields[2] == 1 ? "auth" : "nonauth"
                );
            }
            else if (type == ANSWER_TYPE_CNAME) {
                answer.get(data, 0, dataLength);
                System.out.printf("CNAME \t %s \t %d \t %s\n", 
                    requestArgs.get("domainName").contains("www") ? requestArgs.get("domainName").substring(4) : requestArgs.get("domainName"),
                    timeToLive,
                    headerFields[2] == 1 ? "auth" : "nonauth"
                );
            }
            else if (type == ANSWER_TYPE_MX) {
                short preference = answer.getShort();
                String mailExchange = parseMailExchange(answer, namePtr, headerLength);
                System.out.printf("MX \t %s \t %d \t %d \t %s\n", 
                    mailExchange,
                    preference,
                    timeToLive,
                    headerFields[2] == 1 ? "auth" : "nonauth"
                );
            }
        }
    }
        
    private void parseResponseQuestion(ByteBuffer question) {} //Still Needed ?

    private short[] parseResponseHeader(ByteBuffer Header, int ID) throws Exception{
        //Check For Matching ID - 2 bytes
        int r_id = (int) Header.getShort();
        if(r_id != ID){
            throw new Exception("ERROR  Response ID does not match Query ID.\n");
        }
        //Get next Byte - 1 bytes (QR, OPCODE, AA, TC, RD)
        byte buffer = Header.get();

        //QR is Irrelevant
        //OPCODE is Irrelevant

        //Check if server is Authoritative (Check if 3th bit is 1)
        short AA;
         if((buffer & 1<<2) == 4){
            AA = 1; //Authoritative
         } else{
            AA = 0; //Not authoritative
         }

        //Check if truncate
        if((buffer & 1<<1) == 2){ //If second bit is 1 throw error
            throw new Exception("WARNING Truncated response not supported.\n");
        }

        buffer = Header.get();
        //Check if the server support recursive (1 if they do not support??)
        if((buffer & 1<<7) == 1 ){
            throw new Exception("ERROR  Server does not support recursive queries.\n");
        }

        //Check RCODE Response
        int RCODE = buffer & 0xf;
            //Code 0 - Do nothing

            if((RCODE == 1) ){
                throw new Exception("ERROR  Format error: the name server was unable to interpret the query");
            }
            else if((RCODE == 2) ){
                throw new Exception("ERROR  Server failure: the name server was unable to process this query due to a problem with the name server");
            }
            else if((RCODE == 3) ){
                throw new Exception("NOTFOUND");
            }
            else if((RCODE == 4) ){
                throw new Exception("ERROR  Not implemented: the name server does not support the requested kind of query");
            }
            else if((RCODE == 5) ){
                throw new Exception("ERROR  Refused: the name server refuses to perform the requested operation for policy reasons");
            }

        short QDCOUNT = Header.getShort();
        short ANCOUNT = Header.getShort();
        short NSCOUNT = Header.getShort();
        short ARCOUNT = Header.getShort();
        short AN_AR_Count[] = new short[4];

        AN_AR_Count[0] = ANCOUNT;
        AN_AR_Count[1] = ARCOUNT;
        AN_AR_Count[2] = AA;
        AN_AR_Count[3] = NSCOUNT;

        return AN_AR_Count;
    }

    public void printRequest() {
        System.out.printf("DnsClient sending request for %s\n", this.requestArgs.get("domainName"));
        System.out.printf("Server: %s\n", this.requestArgs.get("dnsIp"));
        System.out.printf("Request type: %s\n", this.requestArgs.get("queryType").toUpperCase());
    }

    private String parseNameServer(ByteBuffer buffer) {

        byte len = buffer.get();
        StringBuilder sb = new StringBuilder();
        byte end = 0x00;
        
        while(len != end) {
            byte[] data = new byte[len];
            buffer.get(data, 0, len);
            sb.append(new String(data, StandardCharsets.UTF_8));
            len = buffer.get();
            if (len != end) {sb.append(".");}
        }

        return sb.toString();
    }

    private String parseMailbox(ByteBuffer buffer, short namePointer, int headerLength) {

        byte len = buffer.get();
        String domainName = requestArgs.get("domainName");
        if (domainName.contains("www")) domainName = domainName.substring(3);
        StringBuilder sb = new StringBuilder();
        byte end = (byte) (namePointer >> 8); // The 8 MSB of the pointer indicate that it is a pointer

        while(len != end && len != 0x00) {
            byte[] data = new byte[len];
            buffer.get(data, 0, len);
            sb.append(new String(data, StandardCharsets.UTF_8));
            len = buffer.get();
            if (len != end) {sb.append(".");}
        }

        // Require to append the name pointed to by the name pointer
        if (len == end) {
            sb.append(".");
            sb.append(domainName);
        }

        return sb.toString();
    }

    private String parseMailExchange(ByteBuffer buffer, short namePointer, int headerLength) {
        
        byte len = buffer.get();
        StringBuilder sb = new StringBuilder();
        byte end = (byte) (namePointer >> 8);

        while(len != end && len != 0x00) {
            byte[] data = new byte[len];
            buffer.get(data, 0, len);
            sb.append(new String(data, StandardCharsets.UTF_8));
            len = buffer.get();
            if (len != end && len != 0x00) {sb.append(".");}
        }

        // Require to append the name pointed to by the name pointer
        if (len == end) {

            int index = (((len & 0b0011_1111) << 8) | buffer.get()) - headerLength;
            len = buffer.get(index);
            sb.append(".");
            
            while(len != 0x00) {
                byte[] data = new byte[len];
                for (int i = 0; i < len; i++) {
                    data[i] = buffer.get(++index);
                }
                sb.append(new String(data, StandardCharsets.UTF_8));
                len = buffer.get(++index);
                if (len != end && len != 0x00) {sb.append(".");}
            }
        }

        return sb.toString();
    }
}
