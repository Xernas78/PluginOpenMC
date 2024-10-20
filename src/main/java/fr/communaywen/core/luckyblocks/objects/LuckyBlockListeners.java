package fr.communaywen.core.luckyblocks.objects;

<<<<<<< HEAD
import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
=======
>>>>>>> upstream/main
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public interface LuckyBlockListeners {

    default void onPlayerQuit(PlayerQuitEvent event) {}
    default void onEntityDeath(EntityDeathEvent event) {}
}
