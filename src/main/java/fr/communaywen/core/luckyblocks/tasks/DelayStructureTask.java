package fr.communaywen.core.luckyblocks.tasks;

<<<<<<< HEAD
import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
=======
>>>>>>> upstream/main
import fr.communaywen.core.luckyblocks.utils.Structure;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;

public class DelayStructureTask extends BukkitRunnable {

    private final File file;
    private final Location target;
    private final boolean mirrorX;
    private final boolean mirrorZ;
    private final Player player;

    public DelayStructureTask(File file, Location target, boolean mirrorX, boolean mirrorZ, Player player) {
        this.file = file;
        this.target = target;
        this.mirrorX = mirrorX;
        this.mirrorZ = mirrorZ;
        this.player = player;
    }

    @Override
    public void run() {
        try {
            Structure.tryPlacingStructure(file, target, mirrorX, mirrorZ, player);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
