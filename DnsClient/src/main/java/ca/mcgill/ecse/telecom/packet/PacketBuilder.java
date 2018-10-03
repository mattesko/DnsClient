package ca.mcgill.ecse.telecom.packet;

import ca.mcgill.ecse.telecom.packet.Packet;
import ca.mcgill.ecse.telecom.packet.Packet.PacketAnswer;
import ca.mcgill.ecse.telecom.packet.Packet.PacketHeader;
import ca.mcgill.ecse.telecom.packet.Packet.PacketQuestion;

public interface PacketBuilder {

    public PacketHeader createHeader();

    public PacketQuestion createQuestion();

    public PacketAnswer createAnswer();

    public Packet getPacket();
}
