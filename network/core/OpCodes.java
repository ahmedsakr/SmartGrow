package network.core;

/**
 * OpCodes defines the operation codes (opcodes) for all
 * supported packets in the SmartGrow network.
 * 
 * @author Ahmed Sakr
 * @since October 10, 2019
 */
public class OpCodes {

    public static final byte LEAF_REGISTRATION = 0;
    public static final byte REGISTRATION_RESPONSE = 1;
    public static final byte SENSORS_DATA = 2;
    public static final byte REQUEST_SENSORS = 3;
    public static final byte GENERIC_ERROR = 4;
    public static final byte ACKNOWLEDGEMENT = 5;
    public static final byte AVAILABLE_PLANTS = 6;
}