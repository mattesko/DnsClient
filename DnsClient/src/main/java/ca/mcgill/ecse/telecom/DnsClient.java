/***
 * @description     Entry point for the DnsClient command line program. 
 *                  Performs argument parsing, directs packet building, queries DNS server requests, and directs logging
 * ======================================================================================================================
 * Modification Log
 * Ver. Author                  Contact                     Modification
 * 1.0  Matthew Lesko-Krleza    mlesko1996@gmail.com        Created the class/file. Implemented command line argument parsing.
 */

package ca.mcgill.ecse.telecom;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import ca.mcgill.ecse.telecom.packet.DnsPacket;
import ca.mcgill.ecse.telecom.packet.DnsPacketBuilder;

public final class DnsClient {

    public static final int DATAGRAM_RESPONSE_SIZE = 256;
    private static final String DEFAULT_TIMEOUT = "5";
    private static final String DEFAULT_MAX_RETRIES = "3";
    private static final String DEFAULT_PORT = "53";
    private static final String DEFAULT_QUERY_TYPE = "A";
    private static DnsPacketBuilder packetBuilder;
    private static DnsClientLogger logger;

    private DnsClient() {}
    
    /***
     * Entry point for the program
     * @param args 
     */
    public static void main(String[] args) throws Exception {
        long elapsedTime = 0;
        DatagramPacket dgRequest, dgResponse;
        HashMap<String, String> pArgs;
        DnsPacket packetModel;
        DatagramSocket socket;

        int retryCounter = 0;
		boolean received = false;
        long start;

        try { //Needed ?
            pArgs = parseArguments(args);
            logger = new DnsClientLogger(pArgs);
            packetModel = new DnsPacket();
            socket = new DatagramSocket();

            logger.printRequest();

            socket.setSoTimeout(Integer.valueOf(pArgs.get("timeout")) * 1000);
            socket.connect(InetAddress.getByName(pArgs.get("dnsIp")), Integer.valueOf(pArgs.get("port")));
            
            packetBuilder = new DnsPacketBuilder(packetModel);
            dgRequest = packetBuilder.createRequestPacket(pArgs);
            dgResponse = new DatagramPacket(new byte[DATAGRAM_RESPONSE_SIZE], DATAGRAM_RESPONSE_SIZE);

            //Saving the start time
            start = System.nanoTime();
            
            while(retryCounter < Integer.parseInt(pArgs.get("maxRetries"))) {
                //Sending Request
                try{
                    socket.send(dgRequest);
                } catch(Exception e){
                    System.err.println("ERROR   Unexpected Exception: " + e.getMessage());
				    retryCounter++;
				    continue;
                }

                //Receiving Response
                try{
                    socket.receive(dgResponse);
                    received = true;
                    elapsedTime = System.nanoTime() - start;
                    break;
                } catch(java.net.SocketTimeoutException e) {
                    System.err.println("ERROR   Socket Timeout Exception: " + e.getMessage());
                } catch(Exception e){
                    System.err.println("ERROR   Unexpected Exception: " + e.getMessage());
                } finally{
                    retryCounter++;
                }
            
            }
            if(received == false){

                System.out.println("ERROR   Maximum number of retries " + pArgs.get("maxRetries") + " exceeded");

                //Close Program
                java.lang.System.exit(1);
            }

            logger.printResponse(dgResponse, packetModel, elapsedTime, retryCounter);


            socket.close();
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @description Parses command line arguments by options, DNS IP and domain name
     * @param args The command line arguments
     * @return Arguments parsed and sorted by their respective option and argument type
     *         Keys are: timeout, maxRetries, port, queryType, dnsIp, domainName
     * @throws Exception
     */
    public static HashMap<String, String> parseArguments(String[] args) throws Exception {
        HashMap<String, String> optVals = new HashMap<>();
        // QueryType has to be determined differently from the other options because of it can have multiple different flags for the same option
        String queryType;

        Options options = new Options();
        options.addOption("t",  true,   "timeout");
        options.addOption("r",  true,   "max-retries");
        options.addOption("p",  true,   "port");
        options.addOption("mx", false , "mail-server");
        options.addOption("ns", false , "name-server");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        Pattern ipPatt = Pattern.compile(
            "@(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])"  +
            "\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])"+
            "\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])"+
            "\\.(25[0-5]|2[0-4][0-9]|1[0-9][0-9]|[1-9]?[0-9])\\b");
        Pattern domPatt = Pattern.compile("(?!:\\/\\/)([a-zA-Z0-9-_]+\\.)*[a-zA-Z0-9][a-zA-Z0-9-_]+\\.[a-zA-Z]{2,11}");

        Matcher matchIp = ipPatt.matcher(String.join(" ", cmd.getArgList()));
        Matcher matchDom = domPatt.matcher(String.join(" ", cmd.getArgList()));
        
        if (!matchIp.find() || !matchDom.find() || cmd.getArgList().size() == 0) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("DnsClient", options);
            throw new Exception("DnsClient requires a valid server IP defined by @a.b.c.d and a valid domain name to query");
        }
        
        if (cmd.hasOption("mx") && !cmd.hasOption("ns")) {
            queryType = "mx";
        }
        else if (cmd.hasOption("ns") && !cmd.hasOption("mx")) {
            queryType = "ns";
        }
        else if (cmd.hasOption("ns") && cmd.hasOption("mx")) {
            throw new Exception("DnsClient cannot accept both ns and mx options");
        }
        else {
            queryType = DEFAULT_QUERY_TYPE;
        }

        optVals.put("timeout",      cmd.getOptionValue("t", DEFAULT_TIMEOUT));
        optVals.put("maxRetries",   cmd.getOptionValue("r", DEFAULT_MAX_RETRIES));
        optVals.put("port",         cmd.getOptionValue("p", DEFAULT_PORT));
        optVals.put("queryType",    queryType);
        optVals.put("dnsIp",        matchIp.group().substring(1)); // Slice off the first character of the DnsIp string because of its @ character
        optVals.put("domainName",   matchDom.group());

        return optVals;
    }
}