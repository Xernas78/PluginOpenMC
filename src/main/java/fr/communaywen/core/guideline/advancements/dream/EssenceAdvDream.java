package fr.communaywen.core.guideline.advancements.dream;

import com.fren_gor.ultimateAdvancementAPI.advancement.BaseAdvancement;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementDisplay;
import com.fren_gor.ultimateAdvancementAPI.advancement.display.AdvancementFrameType;
import com.fren_gor.ultimateAdvancementAPI.visibilities.VanillaVisibility;
import dev.lone.itemsadder.api.CustomStack;
import fr.communaywen.core.utils.ItemsAdderUtils;

public class EssenceAdvDream extends BaseAdvancement implements VanillaVisibility {
    public EssenceAdvDream() {
        super(
                "essence",
                new AdvancementDisplay(
                        ItemsAdderUtils.getNonNullCustomStack("aywen:dream_essence"),
                        "Première essence",
                        AdvancementFrameType.TASK,
                        true,
                        false,
                        1F,7.5F,
                        "Fait attention..."
                ),
                DreamTabManager.getRoot()
        );
    }
}
