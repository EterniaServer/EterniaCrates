package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eterniacrates.objects.CrateData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Vars {

    protected static final Map<String, CrateData> crateMap = new HashMap<>();
    protected static final Map<String, Long> userCooldownMap = new HashMap<>();
    protected static final Map<UUID, String> locCached = new HashMap<>();

}
