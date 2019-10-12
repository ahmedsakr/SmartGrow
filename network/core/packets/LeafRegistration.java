package network.core.packets;

import network.core.Packet;
import network.core.PacketCodes;
import network.leaf.Identity;

/**
 * The LeafRegistration packet is used when an android
 * user or a plant endpoint have started up and wish to
 * communicate with the Central Processing Server.
 * 
 * @author Ahmed Sakr
 * @since October 10, 2019
 */
public class LeafRegistration extends Packet {

    private Identity identity;

    public LeafRegistration() {
        super(PacketCodes.LEAF_REGISTRATION);
    }

    /**
     * Retrieves the leaf identity of this LeafRegistration packet.
     * 
     * @return The Identity of the Leaf Packet
     */
    public Identity getIdentity() {
        return this.identity;
    }

    /**
     * Overrides the leaf identity.
     * 
     * @param identity The new leaf identity of this packet
     */
    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    /**
     * Retrieves all information for this LeafRegistration packet by reading the
     * provided payload.
     * 
     * This method is invoked by Packet::fromPayload when the caller wishes to
     * create an instance of LeafRegistration through a given payload.
     * 
     * @param payload A prepopulated 512-byte payload used to get information from
     */
    @Override
    protected void extract(byte[] payload) {
        Identity identity = payload[1] == 1 ? Identity.ANDROID_USER : Identity.PLANT_ENDPOINT;
        this.setIdentity(identity);
    }

    /**
     * Builds the parent payload by moving the state information in this object
     * into the parent packet.
     * 
     * Order of addition matters. This defines the format of the packet.
     */
    @Override
    protected void build() {
        super.addByte((byte)identity.ordinal());
    }
}