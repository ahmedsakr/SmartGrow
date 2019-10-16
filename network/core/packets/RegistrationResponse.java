package network.core.packets;

import network.core.OpCodes;
import network.core.Packet;
import network.leaf.Identity;

/**
 * The RegistrationResponse packet is sent by the Central Processing
 * Server in response to a Leaf wanting to register with it.
 * 
 * @author Ahmed Sakr
 * @since October 10, 2019
 */
public class RegistrationResponse extends Packet {

    private boolean status;

    public RegistrationResponse() {
        super(OpCodes.REGISTRATION_RESPONSE);
    }

    /**
     * Retrieves the status of the registration request.
     * 
     * @return  true        Leaf registration succeeded
     *          false       Leaf registration failed
     */
    public boolean isRegistered() {
        return this.status;
    }

    /**
     * Overrides the leaf registration status.
     * 
     * @param identity The new leaf registration status
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Retrieves all information for this RegistrationResponse packet by reading the
     * provided payload.
     * 
     * This method is invoked by Packet::fromPayload when the caller wishes to
     * create an instance of RegistrationResponse through a given payload.
     * 
     * @param payload A prepopulated 512-byte payload used to get information from
     */
    @Override
    protected void extract(byte[] payload) {
        boolean status = payload[1] == 1 ? true : false;
        this.setStatus(status);
    }

    /**
     * Builds the parent payload by moving the state information in this object
     * into the parent packet.
     * 
     * Order of addition matters. This defines the format of the packet.
     */
    @Override
    protected void build() {
        super.addByte(this.status == true ? (byte)1 : (byte)0);
    }
}