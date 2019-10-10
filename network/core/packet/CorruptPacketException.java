package network.core.packet;

public class CorruptPacketException extends Exception {

    public CorruptPacketException(String message) {
        super(message);
    }
}