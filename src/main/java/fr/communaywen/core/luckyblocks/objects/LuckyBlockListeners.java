package fr.communaywen.core.luckyblocks.objects;

<<<<<<< HEAD
import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
=======
import fr.communaywen.core.credit.Credit;
import fr.communaywen.core.credit.Feature;
import org.bukkit.event.entity.EntityDeathEvent;
>>>>>>> upstream/main
import org.bukkit.event.player.PlayerQuitEvent;

@Feature("Lucky Blocks")
@Credit("Fnafgameur")
public interface LuckyBlockListeners {

    default void onPlayerQuit(PlayerQuitEvent event) {}
    default void onEntityDeath(EntityDeathEvent event) {}
}
