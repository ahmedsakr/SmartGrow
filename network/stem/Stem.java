package network.stem;

import java.io.IOException;
import java.net.SocketException;

import cps.management.LeafManager;
import logging.SmartLog;
import network.branch.Branch;
import network.core.NodeLocation;
import network.core.Packet;
import network.core.Transport;
import network.core.exceptions.CorruptPacketException;
import network.core.exceptions.TransportInterruptedException;
import network.leaf.Identity;
import network.stem.threads.StemListener;
import network.stem.LeafAccountHandler;

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
    private static SmartLog logger = new SmartLog(Stem.class.getName());

    // The StemListener thread for servicing leaves.
    private StemListener stemListener;

    // There are two branches on a stem: one for the plant endpoints and one for android users.
    private Branch plants, users;

    // The account handler implementation for this stem.
    private LeafAccountHandler accountHandler;

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
     * Register a manager for leaves with the provided identity.
     *
     * @param leafIdentity The identity of leaves to be managed by the given manager
     * @param manager The manager providing the interaction behaviour with the targetted leaves
     */
    public void addManager(Identity leafIdentity, LeafManager manager) {
        if (leafIdentity == Identity.PLANT_ENDPOINT) {
            this.plants.attachManager(manager);
        } else {
            this.users.attachManager(manager);
        }
    }

    /**
     * Attach an account handler for all leaves under this stem.
     *
     * @param accountHandler The account handler implementation being attached to this stem.
     */
    public void addAccountHandler(LeafAccountHandler accountHandler) {
        this.accountHandler = accountHandler;
    }

    /**
     * Retrieve the account handler for this stem.
     *
     * @return The LeafAccountHandler implementation for this stem.
     */
    public LeafAccountHandler getAccountHandler() {
        return this.accountHandler;
    }

    /**
     * Register a leaf by inserting it into the appropriate branch.
     *
     * @param location The NodeLocation object containing the IPv4 address and port of the leaf.
     * @param leafIdentity The identity that the leaf provided
     * 
     * @throws SocketException If the branch failed to start a DedicatedLeafServicer
     */
    public void registerLeaf(NodeLocation location, Identity leafIdentity) throws SocketException {
        if (leafIdentity == Identity.PLANT_ENDPOINT) {
            this.plants.addLeaf(location);
        } else {
            this.users.addLeaf(location);
        }
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
}