package fr.communaywen.core.corporation;

import fr.communaywen.core.teams.Team;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CompanyOwner {

    private final Team team;
    private final UUID player;

    public CompanyOwner(Team team) {
        this.team = team;
        this.player = null;
    }

    public CompanyOwner(UUID owner) {
        this.team = null;
        this.player = owner;
    }

    public boolean isTeam() {
        return team != null;
    }

    public boolean isPlayer() {
        return player != null;
    }

}
