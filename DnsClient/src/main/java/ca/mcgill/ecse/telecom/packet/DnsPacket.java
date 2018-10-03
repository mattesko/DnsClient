package ca.mcgill.ecse.telecom.packet;

public class DnsPacket extends Packet {

    public DnsPacketHeader header;
    public DnsPacketQuestion question;
    public DnsPacketAnswer answer;

    public class DnsPacketHeader extends Packet.PacketHeader{
        public int hid__f;
        public boolean hqr__f;
        public byte hopcode__f = 0x00;
        public boolean haa__f;
        public boolean htc__f;
        public boolean hrd__f;
        public boolean hra__f;
        public byte hz__f = 0x00;
        public byte hrcode__f;
        public int hqdcount__f = 0x0001;
        public int hancount__f;
        public int hnscount__f; //ignore
        public int harcount__f;
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
