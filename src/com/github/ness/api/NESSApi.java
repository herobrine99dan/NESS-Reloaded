package com.github.ness.api;

import org.bukkit.entity.Player;
import org.mswsplex.MSWS.NESS.MSG;
import org.mswsplex.MSWS.NESS.NESS;
import org.mswsplex.MSWS.NESS.NESSPlayer;
import org.mswsplex.MSWS.NESS.PlayerManager;
import com.github.ness.MovementPlayerData;
import com.github.ness.Utilities;
import com.github.ness.Utility;
import com.github.ness.discord.DiscordWebhook;

public class NESSApi {
	
	public NESSPlayer getNESSPlayer(Player p) {
		return NESSPlayer.getInstance(p);
	}
	
	public MovementPlayerData getMovementPlayerData(Player p) {
		return MovementPlayerData.getInstance(p);
	}
	
	public MSG getMSGClass() {
		return new MSG();
	}
	
	public int getPing(Player p) {
		return PlayerManager.getPing(p);
	}
	
	public Utility getUtilityClass(Player p) {
		return new Utility();
	}
	
	public Utilities getUtilitiesClass(Player p) {
		return new Utilities();
	}
	
	public String getDiscordWebhook(String webhook) {
		String webhookurl = NESS.main.config.getString("Configuration.WebHookURL");
		if (webhookurl == null || webhookurl == "") {
			return "";
		}
		return webhookurl;
	}
	
	public DiscordWebhook getDiscordWebhookClass(String webhook) {
		return new DiscordWebhook(webhook);
	}
}
