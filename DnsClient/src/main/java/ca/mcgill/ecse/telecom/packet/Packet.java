package ca.mcgill.ecse.telecom.packet;

public abstract class Packet {

    public PacketHeader header;
    public PacketQuestion question;
    public PacketAnswer answer;

    public abstract class PacketHeader {}

    public abstract class PacketQuestion {}

    public abstract class PacketAnswer {}
}
