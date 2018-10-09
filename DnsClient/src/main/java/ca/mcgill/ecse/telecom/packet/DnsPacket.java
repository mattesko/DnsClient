package ca.mcgill.ecse.telecom.packet;

public class DnsPacket extends Packet {

    public DnsPacketHeader header;
    public DnsPacketQuestion question;
    public DnsPacketAnswer answer;

    public class DnsPacketHeader extends Packet.PacketHeader{
        public int id__f;
        public boolean qr__f;
        public byte opcode__f = 0x00;
        public boolean aa__f;
        public boolean tc__f;
        public boolean rd__f;
        public boolean ra__f;
        public byte z__f = 0x00;
        public byte rcode__f;
        public int qdcount__f = 0x0001;
        public int ancount__f;
        public int nscount__f; //ignore
        public int arcount__f;
    }

    public class DnsPacketQuestion extends Packet.PacketQuestion {
        public int qname__f; // FIXME variable length unknown!
        public int qtype__f;
        public int qclass__f = 0x0001;
    }

    public class DnsPacketAnswer extends Packet.PacketAnswer{
        public int name__f; // FIXME variable length unknown!
        public int type__f;
        public int class__f;
        public long ttl__f;
        public int rdlength__f; 
        public long rdata__f;
        public int preference__f;
        public int exchange__f; // FIXME variable length unknown!
    }
}
