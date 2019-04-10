# DNS Client
Command-line program for making DNS requests using sockets in Java. Supports A, MX and NS query types.

# Command syntax:
`mvn exec:java "[-t timeout] [-r max-retries] [-p port] [-mx|ns] @server_name"`

timeout: (optional) how long to wait for response. (Default: 5)<br>
max-retries: (optional) maximum number of retries the client will attempt after timing out. (Default: 3)<br>
port: (optional) UDP port number of the specified DNS server. (Default: 53)<br>
-mx or -ns: indicate whether to send an MX or NS query. Client will send a type A query if not specified.<br>
server: (required) IPV4 address of the DNS server.<br>
name: (required) domain name to query for.<br>
Note: JDK v1.8.0.0 was used for development and testing.<br>

## Installation:
1. Clone the repository with `git clone https://github.com/mattesko/telecom-lab.git`
2. Navigate to the root folder of the DnsClient project `cd <INSTALLATION_FOLDER>/telecom-lab/DnsClient`
3. Within the `DnsClient` directory, run the following maven command to install the project: `mvn install`</br>
__Note:__ Don't have maven installed? No problem, use the maven wrapper like so `./mvnw install`
