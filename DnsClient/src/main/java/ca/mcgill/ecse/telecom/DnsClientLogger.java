package ca.mcgill.ecse.telecom;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.net.InetAddress;
import java.net.DatagramSocket;

public class DnsClientLogger {
    
    public void PrintPacket(DatagramPacket dgPacket) {
        


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
            if((RCODE == 2) ){
                throw new Exception("ERROR - Server failure: the name server was unable to process this query due to a problem with the name server");
            }
            //Code 3
            if((RCODE == 3) ){
                throw new Exception("NOTFOUND - Name error: meaningful only for responses from an authoritative name server, this code signifies that the domain name referenced in the query does not exist");
            }
            //Code 4
            if((RCODE == 4) ){
                throw new Exception("ERROR - Not implemented: the name server does not support the requested kind of query");
            }
            //Code 5
            if((RCODE == 5) ){
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

<<<<<<< HEAD
        short AN_AR_Count[] = new short[3];

        AN_AR_Count[0] = ANCOUNT;
        AN_AR_Count[1] = ARCOUNT;
        AN_AR_Count[2] = AA;

=======
        short AN_AR_Count[] = new short[2];

        AN_AR_Count[0] = ANCOUNT;
        AN_AR_Count[1] = ARCOUNT;
>>>>>>> a5dadabadcb67af3a9c3f0ea869e6d378f06a9da

        return AN_AR_Count;
    }


    private static void parseResponseAnswer(ByteBuffer Answer, short[] AN_AR_Count) throws Exception{
       
<<<<<<< HEAD
        //Answer Section -----------------------------------------------------
=======
        //Answer Section
>>>>>>> a5dadabadcb67af3a9c3f0ea869e6d378f06a9da
        int ANCOUNT = AN_AR_Count[0];

        //Repeat for all Resource Reccords
        for(int i = 0; i < ANCOUNT; i++){
<<<<<<< HEAD
            short Asw_Name = Answer.getShort();  //2 bytes ???
            short Asw_Type = Answer.getShort();

=======
            short Asw_Name = Answer.getShort();
            short Asw_Type = Answer.getShort();
            
>>>>>>> a5dadabadcb67af3a9c3f0ea869e6d378f06a9da
            //Check if Similar to QCODE
            short Asw_Class = Answer.getShort();
            if(Asw_Class != 0x0001){
                throw new Exception("ERROR - CLASS field not matching QCODE");
            }

            int Asw_TTL = Answer.getInt(); //Unsigned
            int Asw_RDLength = Answer.getShort();

            //Create a ByteBuffer with the RDATA in Answer
            byte[] RDATA = new byte[Asw_RDLength];
            ByteBuffer buffer = ByteBuffer.wrap(RDATA);
            buffer = Answer.get(RDATA);

            //RDATA Type Selection
            if(Asw_Type == 0x0001){
                //Print IP Address
<<<<<<< HEAD
                System.out.println("Answer RR #" + i + " - The IP Address is " + buffer.get() + "." + buffer.get() + "." + buffer.get() + "." + buffer.get()); 
=======
                System.out.println("The IP Address is " + buffer.get() + "." + buffer.get() + "." + buffer.get() + "." + buffer.get()); 
>>>>>>> a5dadabadcb67af3a9c3f0ea869e6d378f06a9da
            }
            if(Asw_Type == 0x0002){
                //TODO - Print Reverse QNAME
            }
            if(Asw_Type == 0x0005){
                //TODO - Print Name
            }
            if(Asw_Type == 0x000f){
                //Get Preference
                int pref = buffer.getShort();
<<<<<<< HEAD
                System.out.println("Answer RR #" + i + " - Preference (" + pref + ")");
=======
>>>>>>> a5dadabadcb67af3a9c3f0ea869e6d378f06a9da
                //TODO - Print Name
            }
        }

<<<<<<< HEAD
    //Authority Section -----------------------------------------------------
        //How to skip it

    //Additional Section ----------------------------------------------------
    int ARCOUNT = AN_AR_Count[0];

    //Repeat for all Resource Reccords
    for(int i = 0; i < ARCOUNT; i++){
        //TODO
    }

=======
    //Authority Section


    //Additional Section


>>>>>>> a5dadabadcb67af3a9c3f0ea869e6d378f06a9da
    }



}