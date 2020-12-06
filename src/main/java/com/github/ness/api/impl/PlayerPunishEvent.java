package com.github.ness.api.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.github.ness.NessPlayer;
import com.github.ness.api.InfractionTrigger;
import com.github.ness.api.PlayerFlagEvent;
import com.github.ness.api.Violation;

/**
 * Called when NESS executes a command as specified in the configuration.
 * 
 * @deprecated This event is inherently unstable. When it fires is dependent on arbitrary configuration values;
 * it may be disabled entirely. Which replacement should be used depends on the use case: If code listening to
 * this event intends to perform a callback or trigger after a player is flagged for a violation,
 * {@link NESSApi#addInfractionTrigger(InfractionTrigger)} is the recommended way to do that. If it is intended
 * to prevent a player from being punished in certain conditions by cancelling this event, {@link PlayerFlagEvent}
 * should be used.
 *
 */
@SuppressWarnings("deprecation")
@Deprecated
public class PlayerPunishEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final NessPlayer nessplayer;
    private final int violations;
    private final Violation violation;
    private boolean cancelled;
    private final String command;

    /**
     * This event is fired when someone is punished
     */

    public PlayerPunishEvent(Player player, NessPlayer nessplayer, Violation violation, int violations, String command) {
        super(!Bukkit.isPrimaryThread());
        this.player = player;
        this.command = command;
        this.nessplayer = nessplayer;
        this.violation = violation;
        this.violations = violations;
    }

    public Player getPlayer() {
		return player;
	}

	public NessPlayer getNessplayer() {
		return nessplayer;
	}

	public int getViolations() {
		return violations;
	}

	public Violation getViolation() {
		return violation;
	}

	public String getCommand() {
		return command;
	}

	public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}
