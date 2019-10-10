package network.core.packet.flavours;

import network.core.packet.Packet;
import network.core.packet.PacketCodes;
import network.leaf.Identity;

public class LeafRegistration extends Packet {

    private Identity identity;

    /**
     * 
     */
    public LeafRegistration() {
        super(PacketCodes.LEAF_REGISTRATION);
    }

    public Identity getIdentity() {
        return this.identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }

    @Override
    protected void extract(byte[] payload) {
        this.setIdentity(Identity.PLANT_ENDPOINT);
    }

    @Override
    protected void build() {
        super.addByte((byte)identity.ordinal());
        super.addString("Hello!");
    }
}