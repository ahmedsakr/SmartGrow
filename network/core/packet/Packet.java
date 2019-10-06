package network.core.packet;

import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.Collections;

public class Packet implements PacketBuilder {

    private CRC32 crc;
    private byte[] data;
    private int size;

    public Packet(byte opcode) {
        this.data = new byte[PacketBuilder.PACKET_SIZE];
        this.data[0] = opcode;
        this.size = 1;

        this.crc = new CRC32();
    }

    public byte[] produceData() {
        if (this.getFreeSpace() > 0) {
            this.pad();
        }

        System.arraycopy(this.computeCRC(), 0, this.data, this.data.length - 4, 4);
        return this.data.clone();
    }

    /**
     * Compute the number of vacant bytes in the packet.
     */
    public int getFreeSpace() {

        // The last 4 bytes of the packet are reserved for Cyclic Redundancy Check (CRC)
        // error detection codes.
        return PacketBuilder.PACKET_SIZE - this.size - 4;
    }

    /**
     * Appends an integer to the packet.
     * 
     * @param value The integer value to be stored in the packet
     */
    public void addInt(int value) {
        if (this.getFreeSpace() - Integer.BYTES <= 0) {
            return;
        }

        // Copy the bytes into the array.
        System.arraycopy(this.convertIntToBytes(value), 0, this.data, this.size, Integer.BYTES);
        this.size += Integer.BYTES;
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