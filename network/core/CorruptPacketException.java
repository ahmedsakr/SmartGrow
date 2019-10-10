package network.core;

public class CorruptPacketException extends Exception {

    public CorruptPacketException(String message) {
        super(message);
    }
}