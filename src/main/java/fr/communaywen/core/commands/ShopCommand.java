package fr.communaywen.core.commands;

import fr.communaywen.core.AywenCraftPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import fr.communaywen.core.utils.shops.*;

public class ShopCommand {
    AywenCraftPlugin plugin;
    Categories categories = new Categories();

    public ShopCommand(AywenCraftPlugin plugin) {
        this.plugin = plugin;
    }

    @Command("shop")
    @Description("Affiche le shop")
    public void onCommand(CommandSender sender) {
        // Ouvre le menu principal puis les catégories
        if (!(sender instanceof Player)) { return; }
        Player player = (Player) sender;
        categories.openInventory(player);
    }
}
