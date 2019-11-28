package cps.management;

import cps.accounts.Account;
import network.core.Packet;

/**
 * LeafManager provides an interface for classes to implement
 * for handling a packet received from a leaf.
 * 
 * @author Ahmed Sakr
 * @since November 6, 2019
 */
public interface LeafManager {

    /**
     * Handle a packet received from a leaf.
     *
     * @param account The account associated with the leaf.
     * @param packet A SmartGrow packet received from the leaf.
     * @return      true    If handling went as expected
     *              false   Otherwise
     */
    Packet handle(Account account, Packet packet);
}