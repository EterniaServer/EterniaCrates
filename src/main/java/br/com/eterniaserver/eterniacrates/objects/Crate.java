package br.com.eterniaserver.eterniacrates.objects;

import br.com.eterniaserver.eterniacrates.Constants;
import br.com.eterniaserver.eterniacrates.core.APIServer;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Crate {

    private final Block block;
    private final Chest chest;
    private final PersistentDataContainer persistentDataContainer;

    public Crate(Block block) {
        this.block = block;
        BlockState blockState = block.getState();

        if (blockState instanceof Chest) {
            this.chest = (Chest) blockState;
            this.persistentDataContainer = chest.getPersistentDataContainer();
        } else {
            this.chest = null;
            this.persistentDataContainer = null;
        }
    }

    public boolean isChest() {
        return persistentDataContainer != null;
    }

    public boolean isCrate() {
        String crate = persistentDataContainer.get(NamespacedKey.minecraft(Constants.ETERNIA_CRATE), PersistentDataType.STRING);
        if (crate != null) {
            CrateData crateData = APIServer.getCrate(crate);
            return crateData.isValid(block);
        }
        return false;
    }

    public void setCrate(String crate) {
        persistentDataContainer.set(NamespacedKey.minecraft(Constants.ETERNIA_CRATE), PersistentDataType.STRING, crate);
        chest.update();
        CrateData crateData = APIServer.getCrate(crate);
        crateData.setCratesLocation(block.getX() + ":" + block.getY() + ":" + block.getZ());
    }

    public CrateData getCrate() {
        return APIServer.getCrate(persistentDataContainer.get(NamespacedKey.minecraft(Constants.ETERNIA_CRATE), PersistentDataType.STRING));
    }

}
