package fr.communaywen.core.economy;

import fr.communaywen.core.credit.annotations.Credit;
import fr.communaywen.core.credit.annotations.Feature;
import fr.communaywen.core.quests.PlayerQuests;
import fr.communaywen.core.quests.QuestsManager;
import fr.communaywen.core.quests.qenum.QUESTS;
import fr.communaywen.core.quests.qenum.TYPE;
import fr.communaywen.core.utils.Transaction;
import fr.communaywen.core.utils.database.TransactionsManager;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Map;
import java.util.UUID;

@Feature("Economie")
@Credit("TheR0001")
public class EconomyManager {
    @Getter
    private static Map<UUID, Double> balances = Map.of();

    @Getter static EconomyManager instance;

    public EconomyManager(File dataFolder) {
        balances = EconomyData.loadBalances();
        instance = this;
    }

    public double getBalance(UUID player) {
        return balances.getOrDefault(player, 0.0);
    }

    public static double getBalanceOffline(OfflinePlayer player) {
        return balances.getOrDefault(player.getUniqueId(), 0.0);
    }


    public void addBalance(UUID uuid, double amount) {
        balances.put(uuid, getBalance(uuid) + amount);

        saveBalances(uuid);
        for(QUESTS quests : QUESTS.values()) {
            PlayerQuests pq = QuestsManager.getPlayerQuests(uuid);
            if(quests.getType() == TYPE.MONEY) {
                if(!pq.isQuestCompleted(quests)) {
                    QuestsManager.manageQuestsPlayer(uuid, quests, (int) amount, " argents récoltés");
                }
            }
        }
    }

    public void addBalance(UUID player, double amount, String reason) {
        balances.put(player, getBalance(player) + amount);

        new TransactionsManager().addTransaction(new Transaction(
                player.toString(),
                "CONSOLE",
                amount,
                reason
        ));

        saveBalances(player);
        for(QUESTS quests : QUESTS.values()) {
            PlayerQuests pq = QuestsManager.getPlayerQuests(player);
            if(quests.getType() == TYPE.MONEY) {
                if(!pq.isQuestCompleted(quests)) {
                    QuestsManager.manageQuestsPlayer(player, quests, (int) amount, " argents récoltés");
                }
            }
        }
    }

    public static void addBalanceOffline(OfflinePlayer player, double amount) {
        UUID uuid = player.getUniqueId();
        balances.put(uuid, getBalanceOffline(player) + amount);

        saveBalancesOffline(player);
    }

    public boolean withdrawBalance(UUID player, double amount) {
        double balance = getBalance(player);
        if (balance >= amount && amount > 0) {
            balances.put(player, balance - amount);
            saveBalances(player);
            for(QUESTS quests : QUESTS.values()) {
                PlayerQuests pq = QuestsManager.getPlayerQuests(player);
                if(quests.getType() == TYPE.MONEY) {
                    if(!pq.isQuestCompleted(quests)) {
                        pq.removeProgress(quests, (int) amount);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean transferBalance(Player from, Player to, double amount) {
        if (withdrawBalance(from.getUniqueId(), amount)) {
            addBalance(to.getUniqueId(), amount);
            return true;
        } else {
            return false;
        }
    }

    public static String formatValue(double value) {
        return value > 0 ? ChatColor.GREEN + String.valueOf(value) : ChatColor.RED + String.valueOf(value);
    }

    public static String formatValue(int value) {
        return value > 0 ? ChatColor.GREEN + String.valueOf(value) : ChatColor.RED + String.valueOf(value);
    }

    private void saveBalances(UUID player) {
        EconomyData.saveBalances(player, balances);
    }
    private static void saveBalancesOffline(OfflinePlayer player) {
        EconomyData.saveBalancesOffline(player, balances);
    }
}
