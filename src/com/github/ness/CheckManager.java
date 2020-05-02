package com.github.ness;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.ness.check.AbstractCheck;
import com.github.ness.check.AntiASCII;
import com.github.ness.check.AntiKb;
import com.github.ness.check.AutoClicker;
import com.github.ness.check.BadPackets;
import com.github.ness.check.CPSCheck;
import com.github.ness.check.Criticals;
import com.github.ness.check.EntitySpeedCheck;
import com.github.ness.check.FastEat;
import com.github.ness.check.FastPlace;
import com.github.ness.check.FastStairs;
import com.github.ness.check.Fly;
import com.github.ness.check.GhostHand;
import com.github.ness.check.Headless;
import com.github.ness.check.IllegalInteraction;
import com.github.ness.check.InventoryHack;
import com.github.ness.check.Jesus;
import com.github.ness.check.Killaura;
import com.github.ness.check.KillauraBotCheck;
import com.github.ness.check.NoClip;
import com.github.ness.check.NoSlowDownBow;
import com.github.ness.check.NoSlowDownFood;
import com.github.ness.check.NoSwingAnimation;
import com.github.ness.check.NoSwingAttack;
import com.github.ness.check.PatternKillauraAttack;
import com.github.ness.check.PatternKillauraMove;
import com.github.ness.check.Scaffold;
import com.github.ness.check.SpamBot;
import com.github.ness.check.Speed;
import com.github.ness.check.Sprint;

import lombok.Getter;

public class CheckManager implements AutoCloseable {
	
	private final ConcurrentHashMap<UUID, NessPlayer> players = new ConcurrentHashMap<>();
	
	private final Set<AbstractCheck<?>> checks = new HashSet<>();
	
	@Getter
	private final NESSAnticheat ness;
	
	CheckManager(NESSAnticheat ness) {
		this.ness = ness;
	}
	
	private void addCheck(AbstractCheck<?> check) {
		check.initiatePeriodicTasks();
		checks.add(check);
	}
	
	void addAllChecks() {
		addCheck(new CPSCheck(this));
		addCheck(new AntiASCII(this));
		addCheck(new AntiKb(this));
		addCheck(new AutoClicker(this));
		//addCheck(new BadPackets(this));
		addCheck(new Criticals(this));
		addCheck(new EntitySpeedCheck(this));
		addCheck(new FastEat(this));
		addCheck(new FastPlace(this));
		//addCheck(new FastStairs(this));
		addCheck(new Fly(this));
		addCheck(new GhostHand(this));
		addCheck(new Headless(this));
		addCheck(new IllegalInteraction(this));
		addCheck(new InventoryHack(this));
		addCheck(new Jesus(this));
		addCheck(new Killaura(this));
		addCheck(new KillauraBotCheck(this));
		addCheck(new NoClip(this));
		addCheck(new NoSlowDownBow(this));
		addCheck(new NoSlowDownFood(this));
		addCheck(new NoSwingAnimation(this));
		addCheck(new NoSwingAttack(this));
		addCheck(new PatternKillauraAttack(this));
		addCheck(new PatternKillauraMove(this));
		addCheck(new Scaffold(this));
		addCheck(new SpamBot(this));
		addCheck(new Speed(this));
		addCheck(new Sprint(this));
	}
	
	void registerListener() {
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			private void onAnyEvent(Event evt) {
				if (evt instanceof PlayerJoinEvent) {
					Player player = ((PlayerJoinEvent) evt).getPlayer();
					players.put(player.getUniqueId(), new NessPlayer(player));
				} else if (evt instanceof PlayerQuitEvent) {
					players.remove(((PlayerQuitEvent) evt).getPlayer().getUniqueId()).close();
				} else {
					checks.forEach((check) -> check.checkAnyEvent(evt));
				}
			}
		}, ness);
	}
	
	@Override
	public void close() {
		checks.forEach(AbstractCheck::close);
		checks.clear();
		HandlerList.unregisterAll(ness);
	}
	
	/**
	 * Gets a NessPlayer or creates one if it does not exist
	 * 
	 * @param player the corresponding player
	 * @return the ness player
	 */
	public NessPlayer getPlayer(Player player) {
		return players.get(player.getUniqueId());
	}
	
	/**
	 * Do something for each NessPlayer
	 * 
	 * @param action what to do
	 */
	public void forEachPlayer(Consumer<NessPlayer> action) {
		players.values().forEach(action);
	}
	
}
