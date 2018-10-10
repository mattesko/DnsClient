package ca.mcgill.ecse.telecom.packet;

import java.util.Random;

public class DnsPacket extends Packet {

    private DnsPacketHeader header;
    private DnsPacketQuestion question;
    private DnsPacketAnswer answer;

    public DnsPacket() {
        this.header = new DnsPacketHeader();
        this.question = new DnsPacketQuestion();
        this.answer = new DnsPacketAnswer();
    }

    public DnsPacketHeader getPacketHeader() {return this.header;}

    public DnsPacketQuestion getPacketQuestion() {return this.question;}

    public DnsPacketAnswer getPacketAnswer() {return this.answer;}

    public class DnsPacketHeader extends Packet.PacketHeader{
        public short id__f;
        public byte qr__f;
        public byte opcode__f;
        public byte aa__f;
        public byte tc__f;
        public byte rd__f;
        public byte ra__f;
        public byte z__f;
        public byte rcode__f;
        public short qdcount__f;
        public short ancount__f;
        public short nscount__f;
        public short arcount__f;

        public DnsPacketHeader() {
            this.id__f = (byte) new Random().nextInt(50);
            this.qr__f = 0x00;
            this.opcode__f = 0x00;
            this.aa__f = 0x00;
            this.tc__f = 0x00;
            this.rd__f = 0x01;
            this.ra__f = 0x00;
            this.z__f = 0x00;
            this.rcode__f = 0x00;
            this.qdcount__f = 0x0001;
            this.ancount__f = 0x0000;
            this.nscount__f = 0x0000;
            this.arcount__f = 0x0000;
        }

        public short getId() {return this.id__f;}
        public byte getQr() {return this.qr__f;}
        public byte getOpcode() {return this.opcode__f;}
        public byte getAa() {return this.aa__f;}
        public byte getTc() {return this.tc__f;}
        public byte getRd() {return this.rd__f;}
        public byte getRa() {return this.ra__f;}
        public byte getZ() {return this.z__f;}
        public byte getRcode() {return this.rcode__f;}
        public short getQdcount() {return this.qdcount__f;}
        public short getAncount() {return this.ancount__f;}
        public short getNscount() {return this.nscount__f;}
        public short getArcount() {return this.arcount__f;}
    }

    public class DnsPacketQuestion extends Packet.PacketQuestion {
        public int qname__f; // FIXME variable length unknown!
        public int qtype__f;
        public int qclass__f = 0x0001;

        public DnsPacketQuestion() {}

        public int getQname() {return qname__f;}

        public int getQtype() {return qtype__f;}

        public int getQclass() {return qclass__f;}

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

        public DnsPacketAnswer() {}

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
