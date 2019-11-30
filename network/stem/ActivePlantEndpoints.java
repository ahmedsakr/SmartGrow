package network.stem;

import java.util.ArrayList;

import cps.accounts.Account;
import network.branch.threads.DedicatedLeafServicer;
import network.core.packets.plants.AvailablePlants;
import network.stem.Stem;

/**
 * ActivePlantEndpoints is a utility class responsible for tracking
 * all plant endpoints that are currently reporting to the system.
 * 
 * @author Ahmed Sakr
 */
public class ActivePlantEndpoints {

    // The stem being serviced
    private Stem stem;

    /**
     * Initialize the object.
     *
     * @param stem The stem whose plant endpoints are being tracked
     */
    public ActivePlantEndpoints(Stem stem) {
        this.stem = stem;
    }

    /**
     * Retrieve all plant endpoints that are currently active in the SmartGrow system.
     *
     * @return A set of plant id-names for the active plants in the system.
     */
    public AvailablePlants getActivePlants() {
        ArrayList<DedicatedLeafServicer> activePlants = this.stem.getPlants().getServicers();
        AvailablePlants availablePlants = new AvailablePlants();

        for (DedicatedLeafServicer servicer : activePlants) {

            Account plantAccount = servicer.getAccount();

            // Append the plant id-name pair to the packet
            availablePlants.addPlant(plantAccount.getId(), String.format("Plant #%d", plantAccount.getId()));
        }

        return availablePlants;
    }
}