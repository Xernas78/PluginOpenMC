package fr.communaywen.core;

import fr.communaywen.core.commands.*;
import fr.communaywen.core.corpse.CorpseManager;
import fr.communaywen.core.friends.FriendsManager;
import fr.communaywen.core.friends.commands.FriendsCommand;
import fr.communaywen.core.levels.LevelsListeners;
import fr.communaywen.core.levels.LevelsManager;
import fr.communaywen.core.listeners.*;
import fr.communaywen.core.scoreboard.ScoreboardManagers;
import fr.communaywen.core.staff.players.PlayersCommand;
import fr.communaywen.core.teams.*;
import fr.communaywen.core.utils.*;

import fr.communaywen.core.levels.LevelsCommand;
import fr.communaywen.core.levels.LevelsDataManager;

import fr.communaywen.core.tpa.TPACommand;
import fr.communaywen.core.tpa.TpacceptCommand;
import fr.communaywen.core.tpa.TpcancelCommand;
import fr.communaywen.core.tpa.TpdenyCommand;

import fr.communaywen.core.trade.TradeCommand;
import fr.communaywen.core.trade.TradeListener;
import fr.communaywen.core.trade.TradeAcceptCommand;

import fr.communaywen.core.economy.EconomyManager;
import dev.xernas.menulib.MenuLib;
import fr.communaywen.core.utils.command.InteractiveHelpMenu;
import fr.communaywen.core.utils.database.DatabaseManager;
import fr.communaywen.core.staff.freeze.FreezeCommand;
import fr.communaywen.core.listeners.FreezeListener;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.luckperms.api.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.ArrayList;

import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;


import java.io.File;

public final class AywenCraftPlugin extends JavaPlugin {
    public static ArrayList<Player> frozenPlayers = new ArrayList<>();


    private MOTDChanger motdChanger;
    private TeamManager teamManager;
    private FileConfiguration bookConfig;
    private FileConfiguration wikiConfig;
    private static AywenCraftPlugin instance;
    private FriendsManager friendsManager;
    private CorpseManager corpseManager;
    public EconomyManager economyManager;
    public LuckPerms api;
    public ScoreboardManagers scoreboardManagers;

    private BukkitAudiences adventure;
    private InteractiveHelpMenu interactiveHelpMenu;

    private BukkitCommandHandler handler;

    private DatabaseManager databaseManager;

    public QuizManager quizManager;

    private FallingBlocksExplosionManager fbeManager;

    private LevelsManager levelsManager;

    private void loadBookConfig() {
        File bookFile = new File(getDataFolder(), "rules.yml");
        if (!bookFile.exists()) {
            saveResource("rules.yml", false);
        }
        bookConfig = YamlConfiguration.loadConfiguration(bookFile);
    }

    private void loadWikiConfig() {
        File wikiFile = new File(getDataFolder(), "wiki.yml");
        if (!wikiFile.exists()) {
            saveResource("wiki.yml", false);
        }
        wikiConfig = YamlConfiguration.loadConfiguration(wikiFile);
    }

    private FileConfiguration loadWelcomeMessageConfig() {
        File welcomeMessageConfigFile = new File(getDataFolder(), "welcomeMessageConfig.yml");
        if (!welcomeMessageConfigFile.exists()) {
            saveResource("welcomeMessageConfig.yml", false);
        }
        return YamlConfiguration.loadConfiguration(welcomeMessageConfigFile);
    }

    private FileConfiguration loadLevelsFile() {
        File levelsFile = new File(getDataFolder(), "levels.yml");
        if (!levelsFile.exists()) {
            saveResource("levels.yml", false);
        }
        return YamlConfiguration.loadConfiguration(levelsFile);
    }

    @Override
    public void onEnable() {
        super.getLogger().info("Hello le monde, ici le plugin AywenCraft !");
        saveDefaultConfig();

        instance = this;

        /* UTILS */
        databaseManager = new DatabaseManager(this);
        try {
            databaseManager.init(); // Créer les tables nécessaires
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        LinkerAPI linkerAPI = new LinkerAPI(databaseManager);
        FriendsUtils friendsUtils = new FriendsUtils(databaseManager);

        scoreboardManagers = new ScoreboardManagers();

        quizManager = new QuizManager(this, loadQuizzes());

        OnPlayers onPlayers = new OnPlayers();
        onPlayers.setLinkerAPI(linkerAPI);

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
            onPlayers.setLuckPerms(api);
        }

        MenuLib.init(this);
        economyManager = new EconomyManager(getDataFolder());
        loadBookConfig();
        loadWikiConfig();

        LevelsDataManager.setLevelsFile(loadLevelsFile(),new File(getDataFolder(), "levels.yml"));

        friendsManager = new FriendsManager(friendsUtils, this);
        corpseManager = new CorpseManager();

        motdChanger = new MOTDChanger();
        motdChanger.startMOTDChanger(this);
        teamManager = new TeamManager();
        fbeManager = new FallingBlocksExplosionManager();

        levelsManager = new LevelsManager();

        this.adventure = BukkitAudiences.create(this);

        /* ----- */

        String webhookUrl = "https://discord.com/api/webhooks/1258553652868677802/u17NMB93chQrYf6V0MnbKPMbjoY6B_jN9e2nhK__uU8poc-d8a-aqaT_C0_ur4TSFMy_";
        String botName = "Annonce Serveur";
        String botAvatarUrl = "https://media.discordapp.net/attachments/1161296445169741836/1258408047412383804/image.png?ex=66889812&is=66874692&hm=4bb38f7b6460952afc21811f7145a6b289d7210861d81d91b1ca8ee264f0ab0d&=&format=webp&quality=lossless&width=1131&height=662";
        DiscordWebhook discordWebhook = new DiscordWebhook(webhookUrl, botName, botAvatarUrl);

        /*  COMMANDS  */

        this.handler = BukkitCommandHandler.create(this);
        this.interactiveHelpMenu = InteractiveHelpMenu.create();
        this.handler.accept(interactiveHelpMenu);

        this.handler.getAutoCompleter().registerSuggestion("featureName", SuggestionProvider.of(wikiConfig.getKeys(false)));

        this.handler.register(new SpawnCommand(this), new VersionCommand(this), new RulesCommand(bookConfig),
                new TeamCommand(), new MoneyCommand(this.economyManager), new ScoreboardCommand(), new ProutCommand(),
                new FeedCommand(this), new TPACommand(this), new TpacceptCommand(), new TpcancelCommand(), new TpdenyCommand(),
                new CreditCommand(), new ExplodeRandomCommand(), new LinkCommand(linkerAPI), new ManualLinkCommand(linkerAPI),
                new RTPCommand(this), new FreezeCommand(), new PlayersCommand(), new FBoomCommand(), new BaltopCommand(this),
                new FriendsCommand(friendsManager, this, adventure), new PrivacyCommand(this), new LevelsCommand(levelsManager),
                new TailleCommand(), new WikiCommand(wikiConfig), new GithubCommand(this), new TradeCommand(this),
                new TradeAcceptCommand(this), new ShopCommand(this)
        );

        /*  --------  */

        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    if(!scoreboardManagers.disableSBPlayerList.contains(player)) {
                        scoreboardManagers.setScoreboard(player);
                    }
                }
            }
        }.runTaskTimer(this, 0L, 100L);

        /* LISTENERS */
        getServer().getPluginManager().registerEvents(new KebabListener(this), this);
        getServer().getPluginManager().registerEvents(new AntiTrampling(),this);
        getServer().getPluginManager().registerEvents(new RTPWand(this), this);
        getServer().getPluginManager().registerEvents(onPlayers, this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
        getServer().getPluginManager().registerEvents(new SleepListener(),this);
        getServer().getPluginManager().registerEvents(new ChatListener(this, discordWebhook), this);
        getServer().getPluginManager().registerEvents(new FreezeListener(this), this);
        getServer().getPluginManager().registerEvents(new WelcomeMessage(loadWelcomeMessageConfig()), this);
        getServer().getPluginManager().registerEvents(new Insomnia(), this);
        getServer().getPluginManager().registerEvents(new VpnListener(this), this);
        getServer().getPluginManager().registerEvents(new ThorHammer(), this);
        getServer().getPluginManager().registerEvents(new FriendsListener(friendsManager), this);
        getServer().getPluginManager().registerEvents(new PlayersMenuListener(), this);
        getServer().getPluginManager().registerEvents(new TablistListener(this), this);
        getServer().getPluginManager().registerEvents(new LevelsListeners(levelsManager), this);
        getServer().getPluginManager().registerEvents(new CorpseListener(corpseManager), this);
        getServer().getPluginManager().registerEvents(new TradeListener(), this);
        /* --------- */

        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new FarineListener(), this);
        createFarineRecipe();
    }

    private FileConfiguration loadQuizzes() {
        File quizzesFile = new File(getDataFolder(), "quizzes.yml");
        if (!quizzesFile.exists()) {
            saveResource("quizzes.yml", false);
        }
        return YamlConfiguration.loadConfiguration(quizzesFile);
    }

    public BukkitCommandHandler getHandler() {
        return handler;
    }

    public BukkitAudiences getAdventure() {
        return adventure;
    }

    @Override
    public void onDisable() {
        System.out.println("DISABLE");
        this.databaseManager.close();
        this.quizManager.close();
        this.corpseManager.removeAll();
    }

    public ArrayList<Player> getFrozenPlayers() {
        return frozenPlayers;
    }

    public int getBanDuration() {
        return getConfig().getInt("deco_freeze_nombre_de_jours_ban", 30);
    }


    public TeamManager getTeamManager() {
        return teamManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public FallingBlocksExplosionManager getFbeManager() {
        return fbeManager;
    }

    public InteractiveHelpMenu getInteractiveHelpMenu() {
        return interactiveHelpMenu;
    }

    public static AywenCraftPlugin getInstance() {
        return instance;
    }


    // Farine pour fabriquer du pain

    private void createFarineRecipe() {
        ItemStack farine = new ItemStack(Material.SUGAR);
        ItemMeta meta = farine.getItemMeta();
        meta.setDisplayName("Farine");
        farine.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(new NamespacedKey(this, "farine"), farine);
        recipe.shape(" A ", " B ", "   ");
        recipe.setIngredient('A', Material.GUNPOWDER);
        recipe.setIngredient('B', Material.WHEAT);

        Bukkit.addRecipe(recipe);
    }


    /**
     * Format a permission with the permission prefix.
     *
     * @param category the permission category
     * @param suffix the permission suffix
     * @return The formatted permission.
     * @see PermissionCategory #PERMISSION_PREFIX
     */
    public static @NotNull String formatPermission(final @NotNull PermissionCategory category,
                                                   final @NotNull String suffix) {
        return category.formatPermission(suffix);
    }
}
