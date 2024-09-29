package fr.communaywen.core.luckyblocks.events.neutrals;

import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
import fr.communaywen.core.luckyblocks.enums.EventType;
import fr.communaywen.core.luckyblocks.objects.LuckyBlockEvent;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Feature("Lucky Blocks")
@Credit("Fnafgameur")
public class LBBatman extends LuckyBlockEvent {

    public LBBatman() {
        super(
                "batman",
                "Je suis Batman",
                "Vous êtes sur une chauve souris !",
                0.3f,
                EventType.NEUTRAL,
                new ItemStack(Material.BAT_SPAWN_EGG)
        );
    }

    @Override
    public void onOpen(Player player, Block block) {
        super.onOpen(player, block);

        World world = block.getWorld();
        Bat bat = world.spawn(block.getLocation(), Bat.class);

        bat.setCustomName("§8§lBatmobile");
        bat.setCustomNameVisible(true);

        bat.addPassenger(player);
    }
}
