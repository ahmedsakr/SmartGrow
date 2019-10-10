package network.core.packet;

public interface PacketBuilder {
    
    public static int PACKET_SIZE = 512;

    /**
     * Produces a byte-array representation of the build packet.
     * 
     * @return A 512-byte padded array
     */
    public byte[] getData();

    /**
     * Calls upon the subclass build implementation to populate the packet.
     */
    public void compile();

    /**
     * Calculate how much of the 512-byte array is free.
     * 
     * @return The amount of remaining vacant bytes in the packet
     */
    public int getFreeSpace();

    /**
     * Add a vlaue of type byte to the packet.
     * 
     * @param value The byte value
     */
    public void addByte(byte value);

    /**
     * Add a value of type integer to the packet.
     * 
     * @param value The integer value
     */
    public void addInt(int value);

    /**
     * Add a value of type String to the packet.
     * 
     * @param value The String value
     */
    public void addString(String value);

    /**
     * Add a value of type Boolean to the packet.
     * 
     * @param value The boolean value
     */
    public void addBoolean(boolean value);
}