package network.core.exceptions;

/**
 * OpCodeNotRecognizedException captures the error path where a received
 * packet has an operation code that is not recognized.
 * 
 * @author Ahmed Sakr
 * @since October 12, 2019
 */
public class OpCodeNotRecognizedException extends CorruptPacketException {

    public OpCodeNotRecognizedException(String message) {
        super(message);
    }
}