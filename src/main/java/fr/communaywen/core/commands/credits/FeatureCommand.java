package fr.communaywen.core.commands.credits;

import fr.communaywen.core.credit.FeatureData;
import fr.communaywen.core.credit.FeatureManager;
import fr.communaywen.core.credit.menus.FeatureListMenu;
import fr.communaywen.core.utils.CommandUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Command("feature")
@Description("Get information about features")
public class FeatureCommand {

    private final FeatureManager featureManager;

    public FeatureCommand(FeatureManager featureManager) {
        this.featureManager = featureManager;
    }

    @DefaultFor("~")
    public void feature(Player player) {
        player.sendMessage("/feature <get/list/player> <feature/player>");
    }

    @Subcommand("get")
    @Description("Get information about a feature")
    public void getFeature(Player player, @Named("feature") String feature) {
        FeatureData featureData = featureManager.getFeature(feature);
        if (featureData == null) {
            player.sendMessage( ChatColor.RED + "Cette feature n'existe pas");
            return;
        }
        sendFeatureInfo(player, featureData);
    }

    @Subcommand("list")
    @Description("List all features")
    public void listFeatures(Player player) {
        List<FeatureData> features = featureManager.getFeatures();
        for (FeatureData feature : features) {
            sendFeatureInfo(player, feature);
        }
    }

    @Subcommand("dev")
    @Description("List all features of a player")
    public void devFeatures(Player player, @Named("dev") String target) {
        player.sendMessage(ChatColor.GOLD + "=====================");
        player.sendMessage(ChatColor.GOLD + "Features de " + ChatColor.LIGHT_PURPLE + target + " :");
        for (FeatureData feature : featureManager.getFeatures()) {
            List<FeatureData> devFeatures = new ArrayList<>();
            List<FeatureData> collabFeatures = new ArrayList<>();
            if (isDeveloper(target, feature)) {
                devFeatures.add(feature);
            }
            if (isCollaborator(target, feature)) {
                collabFeatures.add(feature);
            }
            Collections.shuffle(devFeatures);
            Collections.shuffle(collabFeatures);
            for (FeatureData playerFeature : removeIdenticalFeatures(devFeatures)) {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "DEV - " + playerFeature.getFeature());
            }
            for (FeatureData playerFeature : removeIdenticalFeatures(collabFeatures)) {
                player.sendMessage(ChatColor.GRAY + "COL - " + playerFeature.getFeature());
            }
        }
    }

    private void sendFeatureInfo(Player player, FeatureData feature) {
        player.sendMessage(ChatColor.GOLD + "=====================");
        player.sendMessage(ChatColor.GOLD + "Feature : " + feature.getFeature());
        player.sendMessage(ChatColor.GOLD + "Par :");
        for (String credit : feature.getDevelopers()) {
            player.sendMessage(ChatColor.LIGHT_PURPLE + " - " + credit);
        }
        if (feature.getCollaborators() != null) {
            player.sendMessage(ChatColor.GRAY + "Mentions spéciales : ");
            for (String collaborator : feature.getCollaborators()) {
                player.sendMessage(ChatColor.GRAY + collaborator);
            }
        }
    }

    private boolean isDeveloper(String target, FeatureData feature) {
        for (String developer : feature.getDevelopers()) {
            if (developer.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private boolean isCollaborator(String target, FeatureData feature) {
        if (feature.getCollaborators() == null) {
            return false;
        }
        for (String collaborator : feature.getCollaborators()) {
            if (collaborator.equalsIgnoreCase(target)) {
                return true;
            }
        }
        return false;
    }

    private List<FeatureData> removeIdenticalFeatures(List<FeatureData> featureData) {
        List<FeatureData> features = new ArrayList<>();
        for (int i = 0; i < featureData.size(); i++) {
            FeatureData feature = featureData.get(i);
            if (i >= features.size()) {
                features.add(feature);
                continue;
            }
            FeatureData newFeature = features.get(i);
            if (!newFeature.getFeature().equalsIgnoreCase(feature.getFeature())) {
                features.add(feature);
            }
        }
        return features;
    }

}
