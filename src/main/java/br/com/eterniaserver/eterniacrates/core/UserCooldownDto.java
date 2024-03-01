package br.com.eterniaserver.eterniacrates.core;

import br.com.eterniaserver.eterniacrates.core.Entities.UserCooldown;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class UserCooldownDto {

    private final UUID uuid;
    private final String crateName;

    private Integer id;
    private Long cooldown;

    protected UserCooldownDto(UserCooldown userCooldown) {
        this.id = userCooldown.getId();
        this.uuid = userCooldown.getUuid();
        this.crateName = userCooldown.getCrateName();
        this.cooldown = userCooldown.getCooldown().getTime();
    }

    protected UserCooldownDto(UUID uuid, String crateName) {
        this.uuid = uuid;
        this.crateName = crateName;
        this.cooldown = 1L;
    }

    public Optional<Integer> getId() {
        return Optional.ofNullable(id);
    }

}
