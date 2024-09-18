package fr.communaywen.core.commands.utils;

import fr.communaywen.core.AywenCraftPlugin;
import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
import fr.communaywen.core.utils.database.Blacklist;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.help.CommandHelp;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Command("privacy")
@Description("Affiche un lien redirigeant vers le discord.")
@Feature("Privacy")
@Credit("Gyro3630")
public class PrivacyCommand {
    private AywenCraftPlugin plugin;

    public PrivacyCommand(AywenCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @DefaultFor("~")
    public void sendHelp(BukkitCommandActor sender, CommandHelp<Component> help, ExecutableCommand thisHelpCommand, @Default("1") @Range(min = 1) int page) {
        Audience audience = AywenCraftPlugin.getInstance().getAdventure().sender(sender.getSender());
        AywenCraftPlugin.getInstance().getInteractiveHelpMenu().sendInteractiveMenu(audience, help, page, thisHelpCommand, ChatColor.BLACK + "BLACKLIST" + ChatColor.RESET);
    }

    @Subcommand("blacklist")
    @Description("Affiche votre blacklist")
    public void blacklist(Player player) throws SQLException {
        StringBuilder playersList = new StringBuilder();
        List<String> playersUUID = Blacklist.getBlacklist(player);

        for (int i = 0; i < playersUUID.size(); i++) {
            OfflinePlayer blocked = Bukkit.getOfflinePlayer(UUID.fromString(playersUUID.get(i)));
            if (i != playersUUID.size() - 1) {
                playersList.append(blocked.getName()).append(", ");
            } else {
                playersList.append(blocked.getName());
            }
        }
        player.sendMessage("Blacklist: " + playersList.toString());
    }

    @Subcommand("block")
    @Description("Ajoute quelqu'un à la blacklist")
    public void block(Player player, @Named("player") Player blocked) throws SQLException {
        if (player.getUniqueId().equals(blocked.getUniqueId())) {
            player.sendMessage("Vous ne pouvez pas vous bloquer vous-même");
            return;
        }
        Blacklist.block(player, Bukkit.getOfflinePlayer(blocked.getUniqueId()));
        player.sendMessage("Vous avez bloqué " + blocked);
    }

    @Subcommand("unblock")
    @Description("Retire quelqu'un de la blacklist")
    public void unblock(Player player, @Named("player") Player blocked) throws SQLException {
        if (player.getUniqueId().equals(blocked.getUniqueId())) {
            player.sendMessage("Vous ne pouvez pas vous débloquer vous-même");
            return;
        }
        Blacklist.unblock(player, Bukkit.getOfflinePlayer(blocked.getUniqueId()));
        player.sendMessage("Vous avez débloqué " + blocked);
    }
}
