package fr.communaywen.core.corporation;

import fr.communaywen.core.utils.InverseStack;

import java.util.UUID;

public class DoubleSupplyInverseStack extends InverseStack<Supply> {

    public boolean isItemID(UUID itemID) {
        for (int i = size() - 1; i >= 0; i--) {
            Supply supply = getStack().get(i);
            if (supply.getItemId().equals(itemID)) {
                return true;
            }
        }
        return false;
    }

}
