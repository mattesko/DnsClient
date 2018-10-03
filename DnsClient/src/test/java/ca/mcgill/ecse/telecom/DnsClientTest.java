package ca.mcgill.ecse.telecom;

import org.apache.commons.cli.Options;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Unit test for simple DnsClient.
 */
public class DnsClientTest {

    @Test
    public void testCommandLine1() {
        String[] args = {"-t", "10", "-r", "2", "-mx", "@8.8.8.8", "mcgill.ca"};
        try {
            HashMap<String, String> pArgs = DnsClient.parseArguments(args);
        
            assertEquals("10", pArgs.get("timeout"));
            assertEquals("2", pArgs.get("maxEntries"));
            assertEquals("53", pArgs.get("port"));
            assertEquals("mx", pArgs.get("queryType"));
            assertEquals("@8.8.8.8", pArgs.get("dnsIp"));
            assertEquals("mcgill.ca", pArgs.get("domainName"));
        }
        catch(Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testCommandLineFail1() {
        String[] args = {"-t", "10", "-r", "2", "-mx", "@8.8.8.8", "mcgill.ca"};
        try {
            HashMap<String, String> pArgs = DnsClient.parseArguments(args);
        
            assertEquals("10", pArgs.get("timeout"));
            assertEquals("2", pArgs.get("maxEntries"));
            assertEquals("53", pArgs.get("port"));
            assertEquals("mx", pArgs.get("queryType"));
            assertEquals("@8.8.8.8", pArgs.get("dnsIp"));
            assertEquals("mcgill.ca", pArgs.get("domainName"));
        }
        catch(Exception e) {
            fail(e.getMessage());
        } 
    }
}
