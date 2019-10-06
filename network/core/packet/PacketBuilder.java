package network.core.packet;

public interface PacketBuilder {
    
    public static int PACKET_SIZE = 512;

    /**
     * Produces a byte-array representation of the build packet.
     * 
     * @return A 512-byte padded array
     */
    public byte[] produceData();

    /**
     * Calculate how much of the 512-byte array is free.
     * 
     * @return The amount of remaining vacant bytes in the packet
     */
    public int getFreeSpace();

    /**
     * Add a value of type integer to the packet.
     * 
     * @param value The integer value
     */
    public void addInt(int value);
}