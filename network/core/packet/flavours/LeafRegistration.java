package network.core.packet.flavours;

import network.core.packet.Packet;
import network.core.packet.PacketCodes;
import network.leaf.Identity;

public class LeafRegistration extends Packet {

    private Identity identity;

    /**
     * 
     */
    public LeafRegistration(Identity identity) {
        super((byte)PacketCodes.LEAF_REGISTRATION);

        this.identity = identity;
    }

    @Override
    protected void build() {
        super.addByte((byte)identity.ordinal());
        super.addString("Hello!");
    }
}