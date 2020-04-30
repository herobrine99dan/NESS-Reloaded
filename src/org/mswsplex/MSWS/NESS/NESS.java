package org.mswsplex.MSWS.NESS;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.mswsplex.MSWS.NESS.protocol.TinyProtocol;
import org.mswsplex.MSWS.NESS.protocol.TinyProtocolListeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

public class NESS extends JavaPlugin implements PluginMessageListener {
	public FileConfiguration config;
	public FileConfiguration vl;
	// FileConfiguration data;
	File vlYml;
	File dataYml;
	File configYml;
	public HashMap<Player, Location> oldLoc;
	HashMap<Player, Location> safeLoc;
	public HashMap<Player, Location> lastHitLoc;
	HashMap<Player, Location> lastLookLoc;
	public HashMap<Player, Boolean> legit;
	HashMap<OfflinePlayer, String> nessReason;
	String prefix;
	String ver;
	double seconds;
	public boolean newpacketssystem = false;
	public boolean devMode;
	public boolean debugMode;
	public int maxpackets = 13;
	String build = "17/04/2020";
	public static String ZIG;
	public static String BSPRINT;
	public static String BSM;
	public static String BSM2;
	public static String WDLINIT;
	public static String WDLCONTROL;
	public static String MCBRAND;
	public static String WDLREQ;
	public static String SCHEMATICA;
	public static String FML;
	public static String FMLHS;
	public static NESS main;
	public TinyProtocol protocol;
	
	private NESSAnticheat anticheat;

	public NESS() {
		this.vlYml = new File(this.getDataFolder() + "/vls.yml");
		this.dataYml = new File(this.getDataFolder() + "/data.yml");
		this.oldLoc = new HashMap<Player, Location>();
		this.safeLoc = new HashMap<Player, Location>();
		this.lastHitLoc = new HashMap<Player, Location>();
		this.lastLookLoc = new HashMap<Player, Location>();
		this.legit = new HashMap<Player, Boolean>();
		this.nessReason = new HashMap<OfflinePlayer, String>();
	}

	public void onEnable() {
		NESS.main = this;
		this.ver = this.getDescription().getVersion();
		anticheat = new NESSAnticheat(this);
		this.configYml = new File(this.getDataFolder(), "config.yml");// Load the config
		this.saveDefaultConfig();
		// if (!this.configYml.exists()) {
		// this.saveResource("config.yml", true);
		// }
		this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.configYml);
		// this.data = (FileConfiguration)
		// YamlConfiguration.loadConfiguration(this.dataYml);
		this.devMode = this.config.getBoolean("Settings.DeveloperMode");
		this.debugMode = this.config.getBoolean("Settings.DebugMode");
		this.prefix = this.config.getString("Prefix");
		this.seconds = 0.0;
		this.vl = (FileConfiguration) YamlConfiguration.loadConfiguration(this.vlYml);
		this.getServer().getPluginManager().registerEvents((Listener) new OnMove(), (Plugin) this);
		this.getServer().getPluginManager().registerEvents((Listener) new MiscEvents(), (Plugin) this);
		this.getServer().getPluginManager().registerEvents((Listener) new OnAttack(), (Plugin) this);
		this.getServer().getMessenger().registerOutgoingPluginChannel((Plugin) this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel((Plugin) this, "BungeeCord",
				(PluginMessageListener) this);
		this.getCommand("ness").setExecutor((CommandExecutor) new NESSCommand());
		this.getCommand("ness").setTabCompleter((TabCompleter) new NESSCommand());
		if (this.getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
			MSG.log("&aProtocolLib found!");
			if (Bukkit.getOnlinePlayers().size() == 0) {
				new Protocols();
			}
		} else {
			MSG.log("&cProtocolLib not found! Certain checks will be disabled.");
		}
		if (useTinyProtocol()) {
			this.protocol = (TinyProtocol) new TinyProtocolListeners((Plugin) this);
		}
		for (final String res : this.vl.getKeys(false))
			this.vl.set(res, (Object) null);
		this.saveVl();
		new Timer().register();
		checkDiscordWebHook();
		MSG.log("&aSuccessfully Enabled on version: "
				+ Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-")) + "!");
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			NESSPlayer.getInstance(player);
		}
		if (this.config.getBoolean("Settings.CheckForUpdates")) {
			checkUpdate();
		}
	}

	private boolean useTinyProtocol() {
		boolean webhookurl = NESS.main.config.getBoolean("Settings.UsePackets");
		if (webhookurl) {
			if (Bukkit.getVersion().contains("1.15")) {
				newpacketssystem = true;
				MSG.log("&bNESS will use default PacketSystem!");
				return false;
			} else {
				MSG.log("&aNESS will use TinyProtocols to handle packets!");
				return true;
			}
		} else {
			MSG.log("&cNESS will not use TinyProtocols to handle packets!");
			return false;
		}
	}

	private void checkDiscordWebHook() {
		String webhookurl = NESS.main.config.getString("Configuration.WebHookURL");
		if (webhookurl == null) {
			MSG.log("&cYour configuration file has some problems! Delete it and restart the Server!");
		} else if (webhookurl.equals("")) {
			MSG.log("&dDiscord WebHook Disabled!");
		} else {
			MSG.log("&aDiscord WebHook Enabled!");
		}
	}

	private void checkUpdate() {
		int versionId = 75887;
		anticheat.checkUpdate(versionId).thenAccept((version) -> {
			if (version == null) {
				MSG.log("&cCannot look for update!");
			} else if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
				MSG.log("&aYou are using the latest version!");
			} else {
				MSG.log("&bThere is a new update avaible: " + version);
			}
		});
	}

	public void onDisable() {
		this.config.set("LastShutdown", (Object) System.currentTimeMillis());
		NESS.main = null;
	}

	public void refresh() {
		if (!this.configYml.exists()) {
			this.saveResource("config.yml", true);
			MSG.log("&cWARNING! &7Config was not found, recreating it!");
		}
		this.config = (FileConfiguration) YamlConfiguration.loadConfiguration(this.configYml);
		this.prefix = this.config.getString("Prefix");
		this.devMode = this.config.getBoolean("Settings.DeveloperMode");
	}

	public void saveVl() {
		try {
			this.vl.save(this.vlYml);
		} catch (Exception e) {
			return;
			// e.printStackTrace();
		}
	}

	public void saveConfig() {
		try {
			this.config.save(this.configYml);
		} catch (Exception e) {
			return;
			// e.printStackTrace();
		}
	}

	public void onPluginMessageReceived(final String channel, final Player player, final byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}
		final ByteArrayDataInput in = ByteStreams.newDataInput(message);
		final String subchannel = in.readUTF();
		if (subchannel.equals("NESS") && !this.config.getBoolean("ServerOnly")) {
			final short len = in.readShort();
			final byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
			final DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				final String msg = msgin.readUTF();
				for (final Player target : Bukkit.getOnlinePlayers()) {
					if (target.hasPermission("ness.notify.hacks")) {
						MSG.tell((CommandSender) target, msg);
					}
				}
			} catch (IOException ex) {
			}
		}
	}
}
