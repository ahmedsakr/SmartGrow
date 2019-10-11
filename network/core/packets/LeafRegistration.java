package network.core.packets;

import network.core.Packet;
import network.core.PacketCodes;
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
        Identity identity = payload[1] == 1 ? Identity.ANDROID_USER : Identity.PLANT_ENDPOINT;
        this.setIdentity(identity);
    }

    @Override
    protected void build() {
        super.addByte((byte)identity.ordinal());
        super.addString("Hello!");
    }
}