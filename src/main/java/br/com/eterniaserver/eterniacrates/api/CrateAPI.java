package br.com.eterniaserver.eterniacrates.api;

import br.com.eterniaserver.eterniacrates.core.CrateDTO;
import br.com.eterniaserver.eterniacrates.core.UserCooldownDto;

import java.util.UUID;

public interface CrateAPI {

    boolean existsCrate(String name);

    void create(String name);

    CrateDTO read(String name);

    void update(CrateDTO crateDTO);

    void delete(String name);

    void addItem(CrateDTO crateDTO);

    void removeItem(String crateName, Integer id);

    UserCooldownDto getUserCooldown(UUID playerUUID, String crateName);

    void updateUserCooldown(UserCooldownDto userCooldownDto);

    void putCachedLoc(UUID playerUUID, String name);

    boolean hasCachedLoc(UUID playerUUID);

    String getCachedLoc(UUID playerUUID);

}
