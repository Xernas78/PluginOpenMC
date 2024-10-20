package fr.communaywen.core.guideline.advancements.dream.trees;

import com.fren_gor.ultimateAdvancementAPI.advancement.Advancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.visibilities.VanillaVisibility;
import dev.lone.itemsadder.api.CustomStack;
import fr.communaywen.core.utils.ItemsAdderUtils;
import org.jetbrains.annotations.NotNull;

public class CloudAdvDream extends BaseAdvancement implements VanillaVisibility {
    public CloudAdvDream(@NotNull Advancement parent) {
        super(
                "cloud",
                new AdvancementDisplay(
                        ItemsAdderUtils.getNonNullCustomStack("aywen:cloud"),
                        "Gash-pilleur",
                        AdvancementFrameType.TASK,
                        true,
                        false,
                        1F,4F,
                        "Vous avez coupé un nuage"
                ),
                parent
        );
    }
}
