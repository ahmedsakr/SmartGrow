package network.core.packet;

import java.util.ArrayList;
import java.util.zip.CRC32;

import network.core.packet.flavours.LeafRegistration;
import network.core.packet.CorruptPacketException;

import java.util.Collections;

public abstract class Packet {

    // All packets are fixed to be 512 bytes at all times.
    public static final int PACKET_SIZE = 512;

    private CRC32 crc;
    private byte[] data;
    private int size;

    public Packet(byte opcode) {
        this.data = new byte[PACKET_SIZE];
        this.data[0] = opcode;
        this.size = 1;

        this.crc = new CRC32();
    }

    /**
     * Creates a Packet subclass based on the opcode.
     * @return
     */
    public static Packet fromPayload(byte[] payload) throws CorruptPacketException {
        if (payload.length != PACKET_SIZE) {
            throw new CorruptPacketException("Packet size is not 512 bytes");
        } else {
        }

        Packet pkt = null;
        switch (payload[0]) {

            case PacketCodes.LEAF_REGISTRATION:
                pkt = new LeafRegistration();
                break;
            default:
                throw new CorruptPacketException("Packet OpCode is not recognized");
        }

        if (!pkt.verifyPacket(payload)) {
            throw new CorruptPacketException("CRC check failed");
        }

        pkt.extract(payload);
        return pkt;
    }

    protected boolean verifyPacket(byte[] payload) {
        this.crc.reset();
        this.crc.update(payload);

        return this.crc.getValue() == 0L;
    }

    /**
     * Produce a clone of the packet data.
     * 
     * @return 512 byte-array payload
     */
    public byte[] getData() {
        return this.data.clone();
    }

    /**
     * Compile the data by calling upon the subclass build method to populate
     * the data. Once the data is populated, a CRC is calculated and appended
     * to the end of the packet.
     *
     */
    public void compile() {
        
        // Reset the packet data by moving the size back to 1.
        this.size = 1;

        // Invoke the subclass implementation to bring the packet contents in.
        this.build();

        if (this.getFreeSpace() > 0) {
            this.pad();
        }

        System.arraycopy(this.computeCRC(), 0, this.data, this.data.length - 4, 4);
    }

    /**
     * Compute the number of vacant bytes in the packet.
     */
    public int getFreeSpace() {

        // The last 4 bytes of the packet are reserved for Cyclic Redundancy Check (CRC)
        // error detection codes.
        return PACKET_SIZE - this.size - 4;
    }

    /**
     * Subclass implementation for extracting the packet information from a
     * received payload.
     */
    protected abstract void extract(byte[] payload);

    /**
     * Subclass implementation of what should be built into the packet.
     * 
     * The subclass would use the addInt(), addBoolean(), and addString()
     * methods to construct the packet.
     */
    protected abstract void build();

    /**
     * Appends a byte to the packet.
     * 
     * @param value The byte value to be stored in the packet
     */
    protected void addByte(byte value) {
        if (this.getFreeSpace() == 0) {
            return;
        }

        this.data[size] = value;
        this.size++;
    }

    /**
     * Appends an integer to the packet.
     * 
     * @param value The integer value to be stored in the packet
     */
    protected void addInt(int value) {
        if (this.getFreeSpace() - Integer.BYTES < 0) {
            return;
        }

        // Copy the bytes into the array.
        System.arraycopy(this.convertIntToBytes(value), 0, this.data, this.size, Integer.BYTES);
        this.size += Integer.BYTES;
    }

    /**
     * Appends a string and a trailing null-byte ('\0') to the packet.
     * 
     * @param value The string being inserted into the packet.
     */
    protected void addString(String value) {
        if (this.getFreeSpace() - value.length() + 1 < 0) {
            return;
        }

        System.arraycopy(value.getBytes(), 0, this.data, this.size, value.length());
        this.size += value.length();
        
        // Add the trailing null-byte to the packet to delimit the string.
        this.data[size] = 0;
        this.size++;
    }

    /**
     * Appends a boolean into the packet.
     * 
     * @param value The boolean being inserted into the packet
     */
    protected void addBoolean(boolean value) {
        if (this.getFreeSpace() == 0) {
            return;
        }

        this.data[size] = value == true ? (byte)1 : (byte)0;
        this.size++;
    }

    /**
     * Computes the checksum for the whole payload including the padding.
     *
     * @return
     */
    private byte[] computeCRC() {
        this.crc.reset();
        this.crc.update(this.data, 0, this.data.length - 4);

        return this.convertIntToBytes((int) this.crc.getValue());
    }

    /**
     * Pad the packet with zeroes to fill it up to 512 bytes.
     */
    private void pad() {
        int paddingSize = this.getFreeSpace();
        for (int i = this.size; paddingSize > 0; paddingSize--) {
            this.data[i++] = 0;
            this.size++;
        }
    }

    /**
     * Converts the integer to a Big-Endian byte array.
     */
    private byte[] convertIntToBytes(int value) {
        return new byte[] {
            (byte)((value >> 24) & 0xFF),
            (byte)((value >> 16) & 0xFF),
            (byte)((value >> 8) & 0xFF),
            (byte)(value & 0xFF)
        };
    }

    /**
     * Converts a Big-Endian byte array to its integer representation.
     */
    private int convertBytesToInt(byte[] array) {
        return (int)(array[0] << 24) + (int)(array[1] << 16) + (int)(array[2] << 8) + array[3];
    }
}