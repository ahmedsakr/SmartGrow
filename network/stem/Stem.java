package network.stem;

import java.io.IOException;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cps.management.LeafManager;
import network.branch.Branch;
import network.core.NodeLocation;
import network.core.Packet;
import network.core.Transport;
import network.core.exceptions.CorruptPacketException;
import network.core.exceptions.TransportInterruptedException;
import network.core.packets.LeafRegistration;
import network.leaf.Identity;
import network.stem.threads.StemListener;

/**
 * The Stem abstraction is an aggregation of branches that manage
 * different categorizations of endpoints. In essence, the Stem
 * abstraction is the network abstraction for a central processing
 * server.
 * 
 * @author Ahmed Sakr
 * @since October 22, 2019
 */
public class Stem extends Transport {

    // The logging handle for this Stem instance.
    private static Logger logger = LogManager.getLogger(Stem.class);

    // The StemListener thread for servicing leaves.
    private StemListener stemListener;

    // There are two branches on a stem: one for the plant endpoints and one for android users.
    private Branch plants, users;

    /**
     * Initialize a stem on the specified port.
     *
     * @param port The port that the Stem should listen on.
     * @throws SocketException
     */
    public Stem(int port) throws SocketException {
        super(port);
        this.plants = new Branch("Plants");
        this.users = new Branch("Users");
        
        // Initialize the leaf-servicing thread.
        this.stemListener = new StemListener(this);
    }

    /**
     * Installs a manager for the plants in the SmartGrow system.
     *
     * @param manager A LeafManager implementing instance.
     */
    public void addPlantsManager(LeafManager manager) {
        this.plants.attachManager(manager);
    }

    /**
     * Installs a manager for the android users in the SmartGrow system.
     *
     * @param manager A LeafManager implementing instance.
     */
    public void addAndroidUsersManager(LeafManager manager) {
        this.users.attachManager(manager);
    }

    /**
     * Retrieve the branch that the plants reside on.
     *
     * @return A branch instance of the active plants.
     */
    public Branch getPlantsBranch() {
        return this.plants;
    }

    /**
     * Retrieve the branch that the android users reside on.
     *
     * @return A branch instance of the active android users.
     */
    public Branch getUsersBranch() {
        return this.users;
    }

    /**
     * Helper function that checks if the leaf exists in the plants or users branch.
     *
     * @param location The NodeLocation object representing the IPv4 address and port of the leaf
     *
     * @return      True    If the leaf is found
     *              False   Otherwise
     */
    public boolean isExistingLeaf(NodeLocation location) {
        return this.users.isExistingLeaf(location) || this.plants.isExistingLeaf(location);
    }

    /**
     * Erase the send implementation because the port the stem is listening on
     * is receive-only; any sends must be done through the dedicated servicer
     * on a branch.
     * 
     * @param p The packet intended to be sent
     */
    @Override
    public void send(Packet p) throws TransportInterruptedException, IOException {
        logger.warn("Send packet attempted on receive-only transport");
    }
}