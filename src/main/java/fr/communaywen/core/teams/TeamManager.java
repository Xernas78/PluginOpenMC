package fr.communaywen.core.teams;

import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.corporation.Company;
import fr.communaywen.core.corporation.CompanyManager;
import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
import fr.communaywen.core.teams.cache.TeamCache;
import fr.communaywen.core.teams.utils.MethodState;
import fr.communaywen.core.utils.Queue;
import fr.communaywen.core.utils.database.DatabaseConnector;
import fr.communaywen.core.utils.serializer.BukkitSerializer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Credit({"Xernas", "Axeno", "Gyro3630", "Martinouxx"})
@Feature("Teams")
public class TeamManager extends DatabaseConnector {

    // Laissez ce truc
    // Zeub - By Xernas 05/07/2024 à 00:05 (UTC+2)
    // Le createur de ce truc

    AywenCraftPlugin plugin;

    @Getter
    private final TeamCache teamCache;

    public TeamManager(AywenCraftPlugin plugin) {
        this.plugin = plugin;
        this.teamCache = new TeamCache(plugin);

        try {
            ResultSet rs = connection.prepareStatement("SELECT * FROM teams").executeQuery();

            while (rs.next()) {
                Team team = createTeam(UUID.fromString(rs.getString("owner")), rs.getString("teamName"));
                PreparedStatement query = connection.prepareStatement("SELECT * FROM teams_player WHERE teamName = ?");
                query.setString(1, rs.getString("teamName"));

                if (rs.getBytes("inventory") != null) {
                    ItemStack[] newInventory = new BukkitSerializer().deserializeItemStacks(rs.getBytes("inventory"));
                    if (newInventory != null) {
                        team.setInventory(newInventory);
                    }
                }

                ResultSet players = query.executeQuery();
                while (players.next()) {
                    team.addPlayerWithoutSave(UUID.fromString(players.getString("player")));
                    teamCache.addPlayer(team.getName(), UUID.fromString(players.getString("player")));
                }

                if(!teamCache.getTeamPlayers(team.getName()).contains(team.getOwner())){
                    team.delete();
                    teams.remove(team);
                }
            }

            EconomieTeam.loadBalances();

        } catch (Exception e){
            e.printStackTrace();
            plugin.getLogger().severe("Impossible de charger les teams :'(");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    @Getter
    private final List<Team> teams = new ArrayList<>();
    private final Queue<UUID, Team> pendingInvites = new Queue<>(20);

    public Team createTeam(UUID owner, String name) {
        Team team = new Team(owner, name, plugin, teamCache);
        if (!teamExists(name)) {
            teams.add(team);
        }

        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO teams (teamName, owner, balance) VALUES (?, ?, 0) ON DUPLICATE KEY UPDATE teamName = teamName");
            statement.setString(1, name);
            statement.setString(2, owner.toString());

            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().severe("Impossible de sauvegarder la team '"+name+"'");
        }

        return team;
    }

    public MethodState deleteTeam(Team team) {
        int items = 0;
        for (ItemStack item : team.getInventory().getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                items++;
            }
        }
        if (items > 0) {
            return MethodState.FAILURE;
        }
        teams.remove(team);
        try {
            team.delete();
            CompanyManager companyManager = plugin.getManagers().getCompanyManager();
            Company company = companyManager.getCompany(team);
            if (company != null) {
                if (!companyManager.liquidateCompany(company)) {
                    return MethodState.ERROR;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Impossible de supprimer la team '"+team.getName()+"'");
        }
        return MethodState.SUCCESS;
    }

    public boolean isInTeam(UUID player) {
        for (Team team : teams) {
            if (team.getPlayers().contains(player)) {
                return true;
            }
        }
        return false;
    }

    public boolean teamExists(String name) {
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Team getTeamByPlayer(UUID player) {
        for (Team team : teams) {
            if (team.getPlayers().contains(player)) {
                return team;
            }
        }
        return null;
    }

    public boolean invite(UUID player, Team team) {
        if (team.getPlayers().size() >= 20) {
            return false;
        }
        pendingInvites.add(player, team);
        return true;
    }

    public Team acceptInvite(UUID player) {
        Team team = pendingInvites.get(player);
        if (team != null) {
            team.addPlayer(player);
            pendingInvites.remove(player);
        }
        return team;
    }

    public Team getTeamByName(String name) {
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(name)) {
                return team;
            }
        }
        return null;
    }

}