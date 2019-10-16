package network.core.exceptions;

/**
 * CRCVerificationException captures the error path where a received
 * packet fails verification when comparing the payload to the expected
 * Cyclic Redundancy Check (CRC) checksum.
 * 
 * @author Ahmed Sakr
 * @since October 12, 2019
 */
public class CRCVerificationException extends CorruptPacketException {

    public CRCVerificationException(String message) {
        super(message);
    }
}