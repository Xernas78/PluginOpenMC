package fr.communaywen.core.listeners;

import fr.communaywen.core.commands.utils.FallBloodCommand;
import fr.communaywen.core.credit.annotations.Credit;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;
@Credit("fuzeblocks")
public class FallBloodListener implements Listener {
    /*
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.isSimilar(FallBloodCommand.getBandage())) {
                event.setCancelled(true);

                if (player.hasPotionEffect(PotionEffectType.POISON)) {
                        player.removePotionEffect(PotionEffectType.POISON);
                    int amount = item.getAmount();
                    if (amount > 1) {
                        item.setAmount(amount - 1);
                    } else {
                        player.getInventory().remove(item);
                    }
                }
            }
        }
    */
    @EventHandler
    public void onAnvil(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (e.getInventory().getType() == InventoryType.ANVIL) {
            if(e.getSlotType() == InventoryType.SlotType.RESULT) {
                if (e.getCurrentItem().isSimilar(FallBloodCommand.getBandage())) {
                    e.setCancelled(true);
                }
            }
        }
    }
    private PotionEffect getPotionEffectType() {
       return new PotionEffect(PotionEffectType.POISON, 70, 1);
    }
}
