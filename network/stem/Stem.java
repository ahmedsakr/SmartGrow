package network.stem;

import java.io.IOException;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import network.branch.Branch;
import network.core.NodeLocation;
import network.core.Packet;
import network.core.Transport;
import network.core.exceptions.CorruptPacketException;
import network.core.exceptions.TransportInterruptedException;
import network.core.packets.LeafRegistration;
import network.leaf.Identity;

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

    private static Logger logger = LogManager.getLogger(Stem.class);

    // There are two branches on a stem: one for the plant endpoints and one for android users.
    private Branch plants, users;

    public Stem(int port) throws SocketException {
        super(port);

        this.plants = new Branch("Plants");
        this.users = new Branch("Users");
        this.listenForClients();
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
        return;
    }

    /**
     * Starts the thread for listening for clients on the stem port.
     */
    public void listenForClients() {

        new Thread(() -> {
            try {
                while (true) {
                    LeafRegistration packet = (LeafRegistration) this.receive();
                    logger.info("New client from " + packet.getAddress() + ":" + packet.getPort());

                    if (packet.getIdentity() == Identity.ANDROID_USER) {
                        this.users.addLeaf(new NodeLocation(packet.getAddress(), packet.getPort()));
                    } else {
                        this.plants.addLeaf(new NodeLocation(packet.getAddress(), packet.getPort()));
                    }
                }
            } catch (CorruptPacketException ex) {
                logger.error("Received corrupt packet");
            } catch (TransportInterruptedException ex) {
                logger.error("Transport interrupted");
            } catch (IOException ex) {
                logger.error("I/O exception encountered during receive");
            }
        }, "Stem-Listener-" + this.getPort()).start();
    }
}