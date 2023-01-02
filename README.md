# DNS Client
Command-line program for making DNS requests using sockets in Java. Supports A, MX and NS query types.

# Command syntax:
```
mvn -q exec:java "-Dexec.args=[-t timeout] [-r max-retries] [-p port] [-mx|ns] @DnsServerIp DomainName"
```

```
DnsServerIp:  IPV4 address of the DNS server.
                Example: @103.86.96.100
                
DomainName:   domain name to query for.
                Example: google.com

timeout:      (optional) how long to wait for response. 
                Default: 5
                
max-retries:  (optional) maximum number of retries the client will attempt after timing out. 
                Default: 3
                
port:         (optional) UDP port number of the specified DNS server. 
                Default: 53
                
-mx or -ns:   (optional) indicate whether to send an MX or NS query. 
                Default: Client will send a type A query if not specified.
```

Example query for `google.com` from NordVPN's DNS Server `103.86.96.100`:
```bash
mvn -q exec:java "-Dexec.args=@103.86.96.100 google.com"
```

## Installation:
1. Clone the repository with `git clone https://github.com/mattesko/telecom-lab.git`
2. Navigate to the root folder of the DnsClient project `cd <INSTALLATION_FOLDER>/telecom-lab/DnsClient`
3. Within the `DnsClient` directory, run the following maven command to install the project: `mvn install`</br>
__Note:__ Don't have maven installed? No problem, use the maven wrapper like so `./mvnw install`

Developed and tested with JDK v1.8.0.0
