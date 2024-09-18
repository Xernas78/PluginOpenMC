package fr.communaywen.core.teams.utils;

import fr.communaywen.core.claim.ClaimConfigDataBase;
import fr.communaywen.core.teams.Team;
import fr.communaywen.core.utils.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamUtils {

    public static boolean quit(Team team, Player player) {
        MethodState state = team.removePlayer(player.getUniqueId());
        if (state == MethodState.WARNING || state == MethodState.SUCCESS)
            CommandUtils.sendMessage(player, "Vous avez quitté la team !", false);
        if (state == MethodState.WARNING){
            ClaimConfigDataBase.removeAllClaims(team.getName());
            CommandUtils.sendMessage(player, ChatColor.DARK_RED + "La team a été supprimée !", false);
        }
        if (state == MethodState.ERROR)
            return CommandUtils.sendMessage(player, ChatColor.DARK_RED + "Impossible de quitter, la team serait supprimée et il reste des items dans l'inventaire !", true);
        if (state == MethodState.SPECIAL)
            return CommandUtils.sendMessage(player, ChatColor.DARK_RED + "Impossible de quitter, votre team possède une guilde et elle serait supprimée, si l'erreur persiste veuillez contacter un admin, modo ou manager !", true);
        for (UUID teamPlayer : team.getPlayers()) {
            Player teamPlayerOnline = Bukkit.getPlayer(teamPlayer);
            if (teamPlayerOnline != null) {
                CommandUtils.sendMessage(teamPlayerOnline, player.getName() + " a quitté la team !", false);
            }
        }
        return true;
    }
}