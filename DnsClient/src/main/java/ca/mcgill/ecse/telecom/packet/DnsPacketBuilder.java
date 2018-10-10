package ca.mcgill.ecse.telecom.packet;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import ca.mcgill.ecse.telecom.packet.DnsPacket.DnsPacketAnswer;
import ca.mcgill.ecse.telecom.packet.DnsPacket.DnsPacketHeader;
import ca.mcgill.ecse.telecom.packet.DnsPacket.DnsPacketQuestion;

public class DnsPacketBuilder implements PacketBuilder{
    DnsPacket packet;
    private final int BUFFER_LENGTH = 64;
    private final short ANSWER_TYPE_A = 0x0001;
    private final short ANSWER_TYPE_NS = 0x0002;
    private final short ANSWER_TYPE_MX = 0x000f;
    private final short ANSWER_TYPE_CNAME = 0x0005;

    public DnsPacketBuilder(DnsPacket packet) {
        this.packet = packet;
    }

    public DnsPacketHeader createHeader() {return null;}

    public DnsPacketQuestion createQuestion() {return null;}

    public DnsPacketAnswer createAnswer() {return null;}

    public DnsPacket getPacket() {return packet;}

    public DatagramPacket createRequestPacket(HashMap<String,String> pArgs) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_LENGTH);
        DnsPacket.DnsPacketHeader packetHeader = packet.getPacketHeader();
        String qtype = pArgs.get("queryType");

        // ID
        buffer.putShort(packetHeader.getId());

        // QR | OPCODE | AA | TC | RD
        byte headerFields1 = (byte) ((packetHeader.getQr() << 7) | 
            (packetHeader.getOpcode() << 3) | 
            (packetHeader.getAa() << 2) | 
            (packetHeader.getTc() << 1) | 
            packetHeader.getRd());
        buffer.put(headerFields1);

        // RA | Z | RCODE
        byte headerFields2 = (byte) ((packetHeader.getRa() << 7) |
            (packetHeader.getZ() << 6) | 
            (packetHeader.getRcode())
        );
        buffer.put(headerFields2);

        buffer.putShort((short) packetHeader.getQdcount());
        buffer.putShort((short) packetHeader.getAncount());
        buffer.putShort((short) packetHeader.getNscount());
        buffer.putShort((short) packetHeader.getAncount());

        // QNAME
        for (String label : pArgs.get("domainName").split("\\.")) {
            
            // Each label is preceded by its length value as a byte
            buffer.put((byte) label.length());
            buffer.put(label.getBytes(StandardCharsets.US_ASCII));
        }
        // Terminate with 0
        buffer.put((byte) 0x00);
        
        // QTYPE
        if (qtype.equals("A")) {
            buffer.putShort(ANSWER_TYPE_A);
        }
        else if (qtype.equals("ns")) {
            buffer.putShort(ANSWER_TYPE_NS);
        }
        else if (qtype.equals("mx")) {
            buffer.putShort(ANSWER_TYPE_MX);
        }

        // QCLASS
        buffer.putShort((short) 0x0001);

        return new DatagramPacket(buffer.array(), BUFFER_LENGTH);
    }
}
