package network.core;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.zip.CRC32;

import network.core.packets.Acknowledgement;
import network.core.packets.GenericError;
import network.core.packets.plants.AvailablePlants;
import network.core.packets.registration.LeafRegistration;
import network.core.packets.registration.RegistrationResponse;
import network.core.packets.sensors.RequestSensors;
import network.core.packets.sensors.SensorsData;
import network.core.exceptions.CRCVerificationException;
import network.core.exceptions.CorruptPacketException;
import network.core.exceptions.OpCodeNotRecognizedException;

/**
 * Payloads are expected to follow the form of well-known packets
 * within the SmartGrow Network.
 * 
 * This class provides the abstraction needed to support the
 * Packet necessity in the network. A packet will carry all
 * data communicated within the network.
 * 
 * Integrity verification is also enabled within Packets
 * using Cyclic Redundancy Checks (CRC32 specifically).
 * 
 * @author Ahmed Sakr
 * @since October 10, 2019
 */
public abstract class Packet {

    // All packets are fixed to be 512 bytes at all times.
    public static final int PACKET_SIZE = 512;

    // The intended target for this packet
    public static byte DESTINATION_SINGLE = 0;
    public static byte DESTINATION_BROADCAST = 1;

    private CRC32 crc;
    private byte[] data;
    private int size;

    private DatagramPacket packet;

    /**
     * The constructor for the Packet object
     * 
     * @param opcode The operation code for the packet
     */
    public Packet(byte opcode) {
        this.data = new byte[PACKET_SIZE];

        // The default destination for the packet is a known target.
        this.data[0] = opcode;
        this.data[1] = DESTINATION_SINGLE;

        // The first 2 bytes are initialized as the opcode and destination type
        this.size = 2;

        this.crc = new CRC32();
        this.packet = new DatagramPacket(this.data, this.data.length);
    }

    /**
     * Check if this packet is a broadcast packet.
     *
     * @return  true    if the packet is broadcast
     *          false   Otherwise
     */
    public boolean isBroadcast() {
        return this.data[1] == DESTINATION_BROADCAST;
    }

    /**
     * Override the broadcast status of this packet.
     *
     * @param broadcast The packet is intended for multiple destinations.
     */
    public void setBroadcast(boolean broadcast) {
        this.data[1] = broadcast ? DESTINATION_BROADCAST : DESTINATION_SINGLE;
    }

    /**
     * Retrieve the opcode value of this packet.
     *
     * @return The byte value of the opcode for this packet
     */
    public byte getOpCode() {
        return this.data[0];
    }

    /**
     * Retrieve the packet destination's IP address.
     * 
     * @return IPv4 address of the destination
     */
    public String getAddress() {
        return this.packet.getAddress().getHostAddress();
    }

    /**
     * Retrieve the packet destination's port address.
     * 
     * @return The remote port number of the destination
     */
    public int getPort() {
        return this.packet.getPort();
    }

    /**
     * Set the target destination ip and port using a NodeLocation object.
     *
     * @param leafLocation The NodeLocation containing the IPv4 address and port of the destination
     */
    public void setDestination(NodeLocation leafLocation) throws UnknownHostException {
        this.packet.setAddress(InetAddress.getByName(leafLocation.getIpAddress()));
        this.packet.setPort(leafLocation.getPort());
    }

    /**
     * Set the target destination ip and port for this packet.
     * 
     * @param ip The hostname of the recepient
     * @param port The remote port number of the recepient 
     */
    public void setDestination(String ip, int port) throws UnknownHostException {
        this.packet.setAddress(InetAddress.getByName(ip));
        this.packet.setPort(port);
    }

    /**
     * Creates a Packet subclass based on the opcode.
     * @return
     */
    public static Packet fromPayload(byte[] payload) throws CorruptPacketException {
        if (payload.length != PACKET_SIZE) {
            throw new CorruptPacketException("Packet size is not 512 bytes");
        }

        Packet pkt = null;
        switch (payload[0]) {
            case OpCodes.LEAF_REGISTRATION:
                pkt = new LeafRegistration();
                break;
            case OpCodes.REGISTRATION_RESPONSE:
                pkt = new RegistrationResponse();
                break;
            case OpCodes.SENSORS_DATA:
                pkt = new SensorsData();
                break;
            case OpCodes.REQUEST_SENSORS:
                pkt = new RequestSensors();
                break;
            case OpCodes.GENERIC_ERROR:
                pkt = new GenericError();
                break;
            case OpCodes.ACKNOWLEDGEMENT:
                pkt = new Acknowledgement();
                break;
            case OpCodes.AVAILABLE_PLANTS:
                pkt = new AvailablePlants();
                break;
            default:
                throw new OpCodeNotRecognizedException("Packet OpCode is not recognized");
        }

        // Verify the integrity of the packet using the CRC32 checksum
        if (!pkt.verifyPacket(payload)) {
            throw new CRCVerificationException("CRC check failed");
        }

        // Extract the destination target from the payload
        pkt.setBroadcast(payload[1] == Packet.DESTINATION_BROADCAST);

        // Extract the rest of the items from the packet using the subclass implementation
        // of extract().
        pkt.extract(Arrays.copyOfRange(payload, 2, payload.length));

        return pkt;
    }

    /**
     * Verify the payload using the CRC32 at the end of the payload.
     * 
     * @param payload A 512-byte array (with the last 4 bytes being the CRC32)
     * 
     * @return        true      if the payload is valid
     *                false     otherwise
     */
    protected boolean verifyPacket(byte[] payload) {
        this.crc.reset();
        this.crc.update(payload, 0, payload.length - 4);
        byte[] payload_crc = Arrays.copyOfRange(payload, payload.length - 4, payload.length);

        return (int)this.crc.getValue() == this.convertBytesToInt(payload_crc);
    }

    /**
     * Provide the fully qualified DatagramPacket.
     * 
     * @return The finalized DatagramPacket
     */
    public DatagramPacket getDatagram() {
        return this.packet;
    }

    /**
     * Compile the data by calling upon the subclass build method to populate
     * the data. Once the data is populated, a CRC is calculated and appended
     * to the end of the packet.
     *
     */
    public void compile() {
        
        // Reset the packet data by moving the size back to 2.
        this.size = 2;

        // Invoke the subclass implementation to bring the packet contents in.
        this.build();

        if (this.getFreeSpace() > 0) {
            this.pad();
        }

        // Calculate the checksum using the payload data and insert it in the
        // last 4 bytes in the array.
        System.arraycopy(this.computeCRC(), 0, this.data, PACKET_SIZE - 4, 4);
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
     * 
     * Order of addition matters. This defines the format of the packet.
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
     * Appends a double to the packet.
     * 
     * @param value The double value to be stored in the packet
     */
    protected void addDouble(double value) {
        if (this.getFreeSpace() - Double.BYTES < 0) {
            return;
        }

        byte[] doubleArray = new byte[Double.BYTES];
        ByteBuffer.wrap(doubleArray).putDouble(value);

        System.arraycopy(doubleArray, 0, this.data, this.size, Double.BYTES);
        this.size += Double.BYTES;
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
     * Extracts an integer from the array starting at the specified index.
     *
     * @param payload The payload to extract the integer from
     * @param startingIndex The index into the array from which to extract the integer.
     * @return An integer value composed of the 4 bytes from the array.
     */
    protected int getInt(byte[] payload, int startingIndex) {

        // Index would overlap with CRC or exceed array
        if (startingIndex + Integer.BYTES > this.PACKET_SIZE - 4) {
            return -1;
        }

        return this.convertBytesToInt(Arrays.copyOfRange(payload, startingIndex, startingIndex + Integer.BYTES));
    }

    /**
     * Extracts a double from the array starting at the specified index.
     *
     * @param payload The payload to extract the double from
     * @param startingIndex The index into the array from which to extract the integer.
     * @return A double value composed of the 8 bytes from the array.
     */
    protected double getDouble(byte[] payload, int startingIndex) {

        // Index would overlap with CRC or exceed array
        if (startingIndex + Double.BYTES > payload.length - 4) {
            return -1.0;
        }

        return this.convertBytesToDouble(Arrays.copyOfRange(payload, startingIndex, startingIndex + Double.BYTES));
    }


    /**
     * Extracts string from the array by searching for the terminating byte.
     *
     * @param array The byte-array containing the characters of the string
     * @return A string representation of the byte array.
     */
    protected String getString(byte[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == (byte)0) {
                return new String(array, 0, i);
            }
        }

        return new String(array, 0, array.length);
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
     * Converts the integer to a Big-Endian byte array.
     * 
     * @param value The integer representation of the value
     */
    protected byte[] convertIntToBytes(int value) {
        return new byte[] {
            (byte)((value >> 24)), (byte)((value >> 16)), (byte)((value >> 8)), (byte)(value)
        };
    }

    /**
     * Converts a Big-Endian byte array to its integer representation.
     * 
     * @param array The 4-byte array containing the value of the integer
     */
    protected int convertBytesToInt(byte[] array) {
        return  (int)((array[0] << 24) & 0xFF000000) +
                (int)((array[1] << 16) & 0x00FF0000) +
                (int)((array[2] << 8) & 0x0000FF00) +
                (int)(array[3] & 0xFF);
    }

    /**
     * Converts the byte array into a double value.
     * 
     * @param array The 8-byte array containing the value of the double
     */
    protected double convertBytesToDouble(byte[] array) {
        return ByteBuffer.wrap(array).getDouble();
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
        }
    }
}