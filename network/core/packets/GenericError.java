package network.core.packets;

import java.util.Arrays;

import network.core.OpCodes;
import network.core.Packet;

/**
 * GenericError Packet provides all nodes in the
 * SmartGrow systems with a mechanism to communicate errors
 * or faults to other nodes in the system to facilitate recovery
 * if possible.
 * 
 * @author Ahmed Sakr
 * @since November 8, 2019
 */
public class GenericError extends Packet {

    // The type and message for the error
    private byte errorType;
    private String errorMessage;

    /**
     * Initialize the GenericError packet.
     */
    public GenericError() {
        super(OpCodes.GENERIC_ERROR);
    }

    /**
     * Initialize the GenericError packet with the error type and message.
     *
     * @param errorType The well-known id of the error
     * @param errorMessage The error message describing the error with more detail
     */
    public GenericError(byte errorType, String errorMessage) {
        super(OpCodes.GENERIC_ERROR);
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    /**
     * Retrieve the error type for this GenericError packet.
     *
     * @return The well-known id of the error as a byte
     */
    public byte getErrorType() {
        return this.errorType;
    }

    /**
     * Set the error type for this GenericError packet.
     *
     * @param errorType The well-known id of the error
     */
    public void setErrorType(byte errorType) {
        this.errorType = errorType;
    }

    /**
     * Retrieve the error message for this GenericError packet.
     *
     * @return A string representation of the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the error message for this GenericError packet.
     *
     * @param errorMessage The error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Retrieves all information for this GenericError packet by reading the
     * provided payload.
     * 
     * This method is invoked by Packet::fromPayload when the caller wishes to
     * create an instance of GenericError through a given payload.
     * 
     * @param payload A prepopulated 512-byte payload used to get information from
     */
    @Override
    public void extract(byte[] data) {
        this.setErrorType(data[1]);
        this.setErrorMessage(super.getString(Arrays.copyOfRange(data, 2, data.length)));
    }

    /**
     * Builds the parent payload by moving the state information in this object
     * into the parent packet.
     * 
     * Order of addition matters. This defines the format of the packet.
     */
    @Override
    public void build() {
        super.addByte(this.errorType);
        super.addString(this.errorMessage);
    }

    @Override
    public String toString() {
        return this.getErrorMessage();
    }
}