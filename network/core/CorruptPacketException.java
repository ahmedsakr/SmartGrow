package network.core;

/**
 * A CorruptPacketException might result from a few
 * distinct scenarios:
 * 
 * - The packet opcode is not recognized (See PacketCodes.java
 * for the known opcodes)
 * - The Cyclic Redundancy Check (CRC32) failed for the packet,
 * indicating that a transmission error has occurred.
 * 
 * This exception abstraction enables the callers to handle these
 * failing use cases in an appropriate manner.
 * 
 * @author Ahmed Sakr
 * @since October 10, 2019
 */
public class CorruptPacketException extends Exception {

    public CorruptPacketException(String message) {
        super(message);
    }
}