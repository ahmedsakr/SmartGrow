package network.core;

/**
 * NodeLocation stores the location of a node within the
 * SmartGrow network, importantly its IPv4 address and its
 * port.
 * 
 * @author Ahmed Sakr
 * @since October 17, 2019
 */
public class NodeLocation {

    // The address and remote port of the SmartGrow node
    private String ipAddress;
    private int port;
    
    /**
     * Constructs a NodeLocation object with the specified ipAddress and port.
     * 
     * @param ipAddress The IPv4 address representation of the node
     * @param port The remote port that the node is listening on.
     */
    public NodeLocation(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * Retrieve the IP Address of this node.
     *
     * @return The IP Address
     */
    public String getIpAddress() {
        return this.ipAddress;
    }

    /**
     * Retrieve the remote port of this node.
     * 
     * @return The remote port
     */
    public int getPort() {
        return this.port;
    }

    /**
     * Override the default equals method to compare the port and IP address.
     * 
     * @param object The other NodeLocation object that is being compared to.
     *
     * @return      true    If both instances have the same port and Ip address
     *              false   Otherwise
     */
    @Override
    public boolean equals(Object object) {
        if (object == null || !(object instanceof NodeLocation)) {
            return false;
        }

        NodeLocation location = (NodeLocation) object;
        return this.getPort() == location.getPort() && this.getIpAddress().equals(this.ipAddress);
    }

}