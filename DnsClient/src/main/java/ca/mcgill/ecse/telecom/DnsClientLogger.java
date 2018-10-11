package ca.mcgill.ecse.telecom;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.net.InetAddress;
import java.net.DatagramSocket;

//TODO - MAX
public class DnsClientLogger {
    
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





}