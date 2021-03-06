package network.core.packets.registration;

import java.util.Arrays;

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

    private String registrationDetails;
    private boolean status;

    public RegistrationResponse() {
        super(OpCodes.REGISTRATION_RESPONSE);
    }
    
    /**
     * Grabs the registration status comment.
     *
     * @return A server message regarding the registration attempt.
     */
    public String getRegistrationDetails() {
        return this.registrationDetails;
    }

    /**
     * Set the server registration details.
     *
     * @param details The status of the registration request.
     */
    public void setRegistrationDetails(String details) {
        this.registrationDetails = details;
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
        this.setStatus(payload[0] == 1);
        this.setRegistrationDetails(super.getString(Arrays.copyOfRange(payload, 1, payload.length)));
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
        super.addString(this.getRegistrationDetails());
    }
}