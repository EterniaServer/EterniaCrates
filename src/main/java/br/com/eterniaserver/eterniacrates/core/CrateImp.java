package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eterniacrates.EterniaCrates;
import br.com.eterniaserver.eterniacrates.api.CrateAPI;
import br.com.eterniaserver.eterniacrates.core.Entities.Crate;
import br.com.eterniaserver.eterniacrates.core.Entities.CrateItem;
import br.com.eterniaserver.eterniacrates.core.Entities.UserCooldown;
import br.com.eterniaserver.eternialib.EterniaLib;
import br.com.eterniaserver.eternialib.database.dtos.SearchField;

import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CrateImp implements CrateAPI {

    private final EterniaCrates plugin;

    private final Map<String, CrateDTO> crateMap = new HashMap<>();
    private final Map<String, UserCooldownDto> userCooldownMap = new HashMap<>();
    private final Map<UUID, String> cachedLocMap = new HashMap<>();

    public CrateImp(EterniaCrates plugin) {
        this.plugin = plugin;
    }

    public void putCrates(List<Crate> crates) {
        for (Crate crate : crates) {
            crateMap.put(crate.getCrate(), new CrateDTO(plugin, crate));
        }
    }

    public void putCratesItems(List<CrateItem> items) {
        Map<String, List<CrateItemDTO>> crateItemMap = new HashMap<>();
        for (CrateItem item : items) {
            crateItemMap.computeIfAbsent(item.getCrateName(), k -> new ArrayList<>()).add(new CrateItemDTO(item));
        }

        for (CrateDTO crateDTO : crateMap.values()) {
            crateDTO.setItems(crateItemMap.getOrDefault(crateDTO.getCrateName(), new ArrayList<>()));
        }
    }

    public void putUserCooldowns(List<UserCooldown> cooldowns) {
        for (UserCooldown cooldown : cooldowns) {
            userCooldownMap.put(
                    getCooldownEntry(cooldown.getUuid(), cooldown.getCrateName()),
                    new UserCooldownDto(cooldown)
            );
        }
    }

    @Override
    public boolean existsCrate(String name) {
        return crateMap.containsKey(name);
    }

    @Override
    public void create(String name) {
        Crate crate = new Crate();
        crate.setCooldown(0);
        crate.setCrate(name);

        CrateDTO crateDTO = new CrateDTO(plugin, crate);
        crateMap.put(name, crateDTO);

        runAsync(() -> EterniaLib.getDatabase().insert(Crate.class, crate));
    }

    @Override
    public CrateDTO read(String name) {
        return crateMap.get(name);
    }

    @Override
    public void update(CrateDTO crateDTO) {
        Crate crate = new Crate();
        crate.setCrate(crateDTO.getCrateName());
        crate.setCooldown(crateDTO.getCooldown());
        crate.setCrateKey(crateDTO.getCrateKey().serializeAsBytes());

        runAsync(() -> EterniaLib.getDatabase().update(Crate.class, crate));
    }

    @Override
    public void delete(String name) {
        List<Optional<Integer>> ids = crateMap.get(name).getItems().stream().map(CrateItemDTO::getId).toList();

        crateMap.remove(name);

        runAsync(() -> {
            for (Optional<Integer> id : ids) {
                if (id.isPresent()) {
                    EterniaLib.getDatabase().delete(CrateItem.class, id);
                }
            }
            EterniaLib.getDatabase().delete(Crate.class, name);
        });
    }

    @Override
    public void addItem(CrateDTO crateDTO) {
        Optional<CrateItemDTO> optionalCrateItemDTO = crateDTO
                .getItems()
                .stream()
                .filter(i -> i.getId().isEmpty())
                .findFirst();

        if (optionalCrateItemDTO.isEmpty()) {
            return;
        }

        CrateItemDTO crateItemDTO = optionalCrateItemDTO.get();

        CrateItem crateItem = new CrateItem();
        crateItem.setItem(crateItemDTO.getItem().serializeAsBytes());
        crateItem.setCrateName(crateDTO.getCrateName());
        crateItem.setChance(crateItemDTO.getChance());

        EterniaLib.getDatabase().insert(CrateItem.class, crateItem);
        crateItemDTO.setId(crateItem.getId());
    }

    @Override
    public void removeItem(String crateName, Integer id) {
        CrateDTO crateDTO = crateMap.get(crateName);
        crateDTO.removeItem(id);

        runAsync(() -> EterniaLib.getDatabase().delete(CrateItem.class, id));
    }

    @Override
    public UserCooldownDto getUserCooldown(UUID playerUUID, String name) {
        String key = getCooldownEntry(playerUUID, name);

        if (userCooldownMap.containsKey(key)) {
            return userCooldownMap.get(key);
        }

        return new UserCooldownDto(playerUUID, name);
    }

    @Override
    public void updateUserCooldown(UserCooldownDto userCooldownDto) {
        String key = getCooldownEntry(userCooldownDto.getUuid(), userCooldownDto.getCrateName());

        if (userCooldownDto.getId().isEmpty()) {
            UserCooldown userCooldown = new UserCooldown();

            userCooldown.setUuid(userCooldownDto.getUuid());
            userCooldown.setCrateName(userCooldownDto.getCrateName());
            userCooldown.setCooldown(new Timestamp(System.currentTimeMillis()));

            userCooldownMap.put(key, userCooldownDto);

            runAsync(() -> EterniaLib.getDatabase().insert(UserCooldown.class, userCooldown));
            return;
        }


        runAsync(() -> {
            UserCooldown userCooldown = EterniaLib.getDatabase().findBy(
                    UserCooldown.class,
                    new SearchField("uuid", userCooldownDto.getUuid()),
                    new SearchField("crateName", userCooldownDto.getCrateName())
            );
            userCooldown.setCooldown(new Timestamp(System.currentTimeMillis()));

            EterniaLib.getDatabase().update(UserCooldown.class, userCooldown);
        });
    }

    @Override
    public void putCachedLoc(UUID playerUUID, String name) {
        cachedLocMap.put(playerUUID, name);
    }

    @Override
    public boolean hasCachedLoc(UUID playerUUID) {
        return !cachedLocMap.getOrDefault(playerUUID, "").isEmpty();
    }

    @Override
    public String getCachedLoc(UUID playerUUID) {
        String crate = cachedLocMap.get(playerUUID);

        cachedLocMap.remove(playerUUID);

        return crate;
    }

    private String getCooldownEntry(UUID uuid, String crateName) {
        return uuid + ":" + crateName;
    }

    private void runAsync(Runnable runnable) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, runnable);
    }
}
