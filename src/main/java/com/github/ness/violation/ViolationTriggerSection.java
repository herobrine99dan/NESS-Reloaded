package com.github.ness.violation;

import java.awt.Color;
import java.io.IOException;
import java.util.logging.Level;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessLogger;
import com.github.ness.NessPlayer;
import com.github.ness.api.AnticheatPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.InfractionTrigger;
import com.github.ness.utility.DiscordWebhook;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import space.arim.dazzleconf.annote.ConfComment;
import space.arim.dazzleconf.annote.ConfDefault.DefaultBoolean;
import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;
import space.arim.dazzleconf.annote.ConfDefault.DefaultString;
import space.arim.dazzleconf.annote.ConfKey;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public interface ViolationTriggerSection {

	default boolean enable() {
		return violations() != -1;
	}
	
	@ConfComment("The amount of violations of a check after which to trigger this action. (-1 to disable)")
	int violations();
	
	InfractionTrigger toTrigger(ViolationManager manager, NESSAnticheat ness);
	
	interface NotifyStaff extends ViolationTriggerSection {
		
		@Override
		@DefaultInteger(6)
		int violations();
		
		@DefaultString("&8[&b&lNESS&8]&r&7> &c%PLAYER% &7failed &c%HACK%&7. Violations: %VIOLATIONS%")
		String notification();
		
		@ConfKey("discord.webhook")
		@DefaultString("")
		String discordWebHook();
		
		@ConfKey("discord.title")
		@DefaultString("Anti-Cheat")
		String discordTitle();
		
		@ConfKey("discord.description")
		@DefaultString("%HACKER% maybe is cheating!")
		String discordDescription();
		
		@ConfKey("discord.color")
		@DefaultString("#FF0000")
		Color discordColor();
		
		@DefaultBoolean(false)
		boolean bungeecord();
		
		@Override
		default InfractionTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new InfractionTrigger() {

				@Override
				public void trigger(AnticheatPlayer player, Infraction infraction) {
					if (infraction.getCount() < violations()) {
						return;
					}
					String notif = manager.addViolationVariables(notification(), player, infraction);

					// Only we can assume implementation details
					NessPlayer nessPlayer = (NessPlayer) player;
					sendWebhook(nessPlayer, infraction);

					JavaPlugin plugin = ness.getPlugin();

					if (bungeecord()) {
						ByteArrayDataOutput out = ByteStreams.newDataOutput();
						out.writeUTF("NESS-Reloaded");
						out.writeUTF(notif);
						player.getPlayer().sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
					}
					for (Player staff : plugin.getServer().getOnlinePlayers()) {
						if (staff.hasPermission("ness.notify")) {
							staff.sendMessage(notif);
						}
					}
				}
				
				private void sendWebhook(NessPlayer nessPlayer, Infraction infraction) {

					final String webhookurl = discordWebHook();
					if (webhookurl.isEmpty()) {
						return;
					}
					String hackerName = nessPlayer.getPlayer().getName();
					JavaPlugin plugin = ness.getPlugin();
					plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {

						DiscordWebhook webhook = new DiscordWebhook(webhookurl);

						DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject()
								.setTitle(discordTitle())
								.setDescription(discordDescription().replace("%HACKER%", hackerName))
								.setColor(discordColor())
								.addField("Cheater", hackerName, true)
								.addField("Cheat", infraction.getCheck().getCheckName(), true)
								.addField("Violations", Integer.toString(infraction.getCount()), false);

						webhook.addEmbed(embed);

						try {
							webhook.execute();
						} catch (IOException ex) {
							NessLogger.getLogger(NotifyStaff.class).log(Level.WARNING, "Unable to send discord webhook", ex);
						}
					});
				}
			};
		}
		
	}
	
	interface ExecuteCommand extends ViolationTriggerSection {
		
		@Override
		@DefaultInteger(20)
		int violations();
		
		@DefaultString("kick %PLAYER% Please do not cheat. Detected for: %HACK% %VIOLATIONS% times. "
				+ "If you think this is an error, contact a staff member.")
		String command();
		
		@Override
		default InfractionTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new InfractionTrigger() {

				@Override
				public void trigger(AnticheatPlayer player, Infraction infraction) {
					if (infraction.getCount() < violations()) {
						return;
					}
					String command = manager.addViolationVariables(command(), player, infraction);

					JavaPlugin plugin = ness.getPlugin();
					// Only we can assume implementation details
					NessPlayer nessPlayer = (NessPlayer) player;
					if (!callDeprecatedPlayerPunishEvent(plugin, nessPlayer, infraction, command)) {
						return;
					}
					plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command);
				}
				
				@SuppressWarnings("deprecation")
				private boolean callDeprecatedPlayerPunishEvent(JavaPlugin plugin, NessPlayer nessPlayer,
						Infraction infraction, String command) {
					if (com.github.ness.api.impl.PlayerPunishEvent.getHandlerList().getRegisteredListeners().length == 0) {
						return true;
					}
					com.github.ness.api.impl.PlayerPunishEvent event = new com.github.ness.api.impl.PlayerPunishEvent(
							nessPlayer.getPlayer(), nessPlayer,
							ViolationMigratorUtil.violationFromInfraction(infraction),
							infraction.getCount(), command);
					plugin.getServer().getPluginManager().callEvent(event);
					return !event.isCancelled();
				}
				
			};
		}
		
	}
	
	interface CancelEvent extends ViolationTriggerSection {
		
		@Override
		@DefaultInteger(3)
		int violations();
		
		@Override
		default InfractionTrigger toTrigger(ViolationManager manager, NESSAnticheat ness) {
			return new InfractionTrigger() {
				
				@Override
				public SynchronisationContext context() {
					return SynchronisationContext.EITHER;
				}

				@Override
				public void trigger(AnticheatPlayer player, Infraction infraction) {}
				
			};
		}
		
	}
	
}
