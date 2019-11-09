package network.core;

/**
 * NetworkErrors provides ids to well-known errors in the SmartGrow system.
 * 
 * @author Ahmed Sakr
 * @since November 8, 2019
 */
public class NetworkErrors {

    // Used by the central processing server when it receives a registration request from an existing leaf.
    public static final byte LEAF_ALREADY_REGISTERED = 0;

    // Used by any node that receives a packet that it did not expect (e.g., Plant endpoint receiving
    // a SensorsData packet)
    public static final byte WRONG_PACKET = 1;
}