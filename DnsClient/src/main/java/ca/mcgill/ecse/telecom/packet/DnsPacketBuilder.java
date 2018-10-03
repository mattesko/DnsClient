package ca.mcgill.ecse.telecom.packet;

import ca.mcgill.ecse.telecom.packet.DnsPacket;
import ca.mcgill.ecse.telecom.packet.DnsPacket.DnsPacketAnswer;
import ca.mcgill.ecse.telecom.packet.DnsPacket.DnsPacketHeader;
import ca.mcgill.ecse.telecom.packet.DnsPacket.DnsPacketQuestion;

public class DnsPacketBuilder implements PacketBuilder{
    DnsPacket packet;

    public DnsPacketHeader createHeader() {return null;}

    public DnsPacketQuestion createQuestion() {return null;}

    public DnsPacketAnswer createAnswer() {return null;}

    public DnsPacket getPacket() {return packet;}
}