package ca.mcgill.ecse.telecom.packet;

public class DnsPacket extends Packet {

    public DnsPacketHeader header;
    public DnsPacketQuestion question;
    public DnsPacketAnswer answer;

    public DnsPacketHeader getPacketHeader() {return this.header;}

    public DnsPacketQuestion getPacketQuestion() {return this.question;}

    public DnsPacketAnswer getPacketAsnwer() {return this.answer;}

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

        public int getId() {return id__f;}

        public boolean getQr() {return qr__f;}

        public byte getOpcode() {return opcode__f;}

        public boolean getAa() {return aa__f;}

        public boolean getTc() {return tc__f;}

        public boolean getRd() {return rd__f;}

        public boolean getRa() {return ra__f;}

        public byte getZ() {return z__f;}

        public byte getRcode() {return rcode__f;}

        public int getQdcount() {return qdcount__f;}

        public int getAncount() {return ancount__f;}

        public int getArcount() {return arcount__f;}

    }

    public class DnsPacketQuestion extends Packet.PacketQuestion {
        public int qname__f; // FIXME variable length unknown!
        public int qtype__f;
        public int qclass__f = 0x0001;

        public int getQname() {return qname__f;}

        public int getQtype() {return qtype__f;}

        public int getQclass() {return qclass__f;}

    }

    public class DnsPacketAnswer extends Packet.PacketAnswer{
        private int name__f; // FIXME variable length unknown!
        private int type__f;
        private int class__f;
        private long ttl__f;
        private int rdlength__f; 
        private long rdata__f;
        private int preference__f;
        private int exchange__f; // FIXME variable length unknown!

        public int getName() {return name__f;}

        public int getType() {return type__f;}

        public int getClassField() {return class__f;}

        public long getTtl() {return ttl__f;}

        public int getRdlength() {return rdlength__f;}

        public long getRdata() {return rdata__f;}

        public int getPreference() {return preference__f;}

        public int getExchange() {return exchange__f;}

    }
}
