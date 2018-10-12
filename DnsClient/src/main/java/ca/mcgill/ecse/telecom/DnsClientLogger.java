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
        short[] headerFields = parseResponseHeader(ByteBuffer.wrap(dataHeader), packetModel.getPacketHeader().getId());

        // Bytes taken up by the QNAME is dynamic, but QTYPE and QCLASS take up 4 bytes and need 2 additional bytes for the first and last labels
        byte[] dataQuestion = new byte[requestArgs.get("domainName").length() + 2 + 4];
        buffer.get(dataQuestion, 0, dataQuestion.length);
        parseResponseQuestion(ByteBuffer.wrap(dataQuestion));

        byte[] dataAnswer = new byte[DnsClient.DATAGRAM_RESPONSE_SIZE - dataHeader.length - dataQuestion.length];
        buffer.get(dataAnswer, 0, dataAnswer.length);

        System.out.printf("***Answer Section (%d records)***\n", headerFields[0] + headerFields[3]);
        if (headerFields[0] + headerFields[3] > 0) printAnswerRecords(ByteBuffer.wrap(dataAnswer), headerFields);

        System.out.printf("***Additional Section (%d records)***\n", headerFields[1]);
        if (headerFields[1] > 0) printAnswerRecords(buffer, headerFields);
    }

    private void printAnswerRecords(ByteBuffer answer, short[] headerFields) {
        
        for (int printedAnswers = 0; printedAnswers < headerFields[0] + headerFields[3]; printedAnswers++)  {

            short name = answer.getShort();
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
            else if (type == ANSWER_TYPE_NS) {
                answer.get(data, 0, dataLength);
                System.out.printf("NS \t %s \t %d \t %s\n", 
                    new String(data, StandardCharsets.UTF_8),
                    timeToLive,
                    headerFields[2] == 1 ? "auth" : "nonauth"
                );
            }
            else if (type == ANSWER_TYPE_CNAME) {
                answer.get(data, 0, dataLength);
                System.out.printf("CNAME \t %s \t %d \t %s\n", 
                    new String(data, StandardCharsets.UTF_8),
                    timeToLive,
                    headerFields[2] == 1 ? "auth" : "nonauth"
                );
            }
            else if (type == ANSWER_TYPE_MX) {
                short preference = answer.getShort();
                answer.get(data, 0, dataLength - 2);
                System.out.printf("MX \t %s \t %d \t %d \t %s\n", 
                    new String(data, StandardCharsets.UTF_8),
                    preference,
                    timeToLive,
                    headerFields[2] == 1 ? "auth" : "nonauth"
                );
            }
        }
    }
        
    private void parseResponseQuestion(ByteBuffer question) {


    }

    private static short[] parseResponseHeader(ByteBuffer Header, int ID) throws Exception{
        //Check For Matching ID - 2 bytes
        int r_id = (int) Header.getShort();
        if(r_id != ID){
            throw new Exception("ERROR - Response ID does not match Query ID.\n");
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
            throw new Exception("ERROR - Response was Truncated.\n");
        }
        //RD is Irrelevant

        //Get next Byte - 1 bytes (RA, Z, RCODE)
        buffer = Header.get();

        //Check if the server support recursive (1 if they do not support??)
        if((buffer & 1<<7) == 1 ){
            throw new Exception("ERROR - Server does not support recursive queries.\n");
        }
        //Z is Irrelevant

        //Check RCODE Response
        int RCODE = buffer & 0xf;
            //Code 0 - Do nothing

            //Code 1
            if((RCODE == 1) ){
                throw new Exception("ERROR - Format error: the name server was unable to interpret the query");
            }
            //Code 2
            else if((RCODE == 2) ){
                throw new Exception("ERROR - Server failure: the name server was unable to process this query due to a problem with the name server");
            }
            //Code 3
            else if((RCODE == 3) ){
                throw new Exception("NOTFOUND - Name error: meaningful only for responses from an authoritative name server, this code signifies that the domain name referenced in the query does not exist");
            }
            //Code 4
            else if((RCODE == 4) ){
                throw new Exception("ERROR - Not implemented: the name server does not support the requested kind of query");
            }
            //Code 5
            else if((RCODE == 5) ){
                throw new Exception("ERROR - Refused: the name server refuses to perform the requested operation for policy reasons");
            }

        //Get next Short (2 Bytes)
        short QDCOUNT = Header.getShort(); //Irrelevant

        //Get next Short (2 Bytes)
        short ANCOUNT = Header.getShort();

        //Get next Short (2 Bytes)
        short NSCOUNT = Header.getShort(); //Irrelevant

        //Get next Short (2 Bytes)
        short ARCOUNT = Header.getShort();

        short AN_AR_Count[] = new short[4];

        AN_AR_Count[0] = ANCOUNT;
        AN_AR_Count[1] = ARCOUNT;
        AN_AR_Count[2] = AA;
        AN_AR_Count[3] = NSCOUNT;

        return AN_AR_Count;
    }


    // private static void parseResponseAnswer(ByteBuffer Answer, short[] AN_AR_Count) throws Exception{
       
    //     //Answer Section -----------------------------------------------------
    //     int ANCOUNT = AN_AR_Count[0];

    //     //Repeat for all Resource Reccords
    //     for(int i = 0; i < ANCOUNT; i++){
    //         short Asw_Name = Answer.getShort();  //2 bytes ???
    //         short Asw_Type = Answer.getShort();

    //         //Check if Similar to QCODE
    //         short Asw_Class = Answer.getShort();
    //         if(Asw_Class != 0x0001){
    //             throw new Exception("ERROR - CLASS field not matching QCODE");
    //         }

    //         int Asw_TTL = Answer.getInt(); //Unsigned
    //         int Asw_RDLength = Answer.getShort();

    //         //Create a ByteBuffer with the RDATA in Answer
    //         byte[] RDATA = new byte[Asw_RDLength];
    //         ByteBuffer buffer = ByteBuffer.wrap(RDATA);
    //         buffer = Answer.get(RDATA);

    //         //RDATA Type Selection
    //         if(Asw_Type == 0x0001){
    //             //Print IP Address
    //             System.out.println("Answer RR #" + i + " - The IP Address is " + buffer.get() + "." + buffer.get() + "." + buffer.get() + "." + buffer.get()); 
    //         }
    //         if(Asw_Type == 0x0002){
    //             //TODO - Print Reverse QNAME
    //         }
    //         if(Asw_Type == 0x0005){
    //             //TODO - Print Name
    //         }
    //         if(Asw_Type == 0x000f){
    //             //Get Preference
    //             int pref = buffer.getShort();
    //             System.out.println("Answer RR #" + i + " - Preference (" + pref + ")");
    //             //TODO - Print Name
    //         }
    //     }

    // //Authority Section -----------------------------------------------------
    //     //How to skip it

    // //Additional Section ----------------------------------------------------
    // int ARCOUNT = AN_AR_Count[0];

    // //Repeat for all Resource Reccords
    // for(int i = 0; i < ARCOUNT; i++){
    //     //TODO
    // }

    public void printRequest() {
        System.out.printf("DnsClient sending request for %s\n", this.requestArgs.get("domainName"));
        System.out.printf("Server: %s\n", this.requestArgs.get("dnsIp"));
        System.out.printf("Request type: %s\n", this.requestArgs.get("queryType").toUpperCase());
    }
}
