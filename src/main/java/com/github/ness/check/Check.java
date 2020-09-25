package com.github.ness.check;

import java.util.concurrent.atomic.AtomicInteger;

import com.github.ness.NessPlayer;
import com.github.ness.api.Infraction;
import com.github.ness.api.PlayerFlagEvent;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A check, associated with a player. Includes an optional async task.
 * 
 * @author A248
 *
 */
public class Check extends BaseCheck {
	
	private final NessPlayer nessPlayer;
	
	private final AtomicInteger violations = new AtomicInteger();
	
	protected Check(CheckFactory<?> factory, NessPlayer nessPlayer) {
		super(factory);
		this.nessPlayer = nessPlayer;
	}
	
	@Override
	public CheckFactory<?> getFactory() {
		return (CheckFactory<?>) super.getFactory();
	}
	
	/**
	 * Gets the player this check is for
	 * 
	 * @return the player analysed by this check
	 */
	protected NessPlayer player() {
		return nessPlayer;
	}
	
	/**
	 * Called async and periodically, if defined by {@link CheckInfo}
	 *
	 */
	protected void checkAsyncPeriodic() {
		throw new UnsupportedOperationException("Not implemented - checkAsyncPeriodic");
	}
	
	/**
	 * Flags the player for cheating
	 * 
	 */
	protected void flag() {
		if (callFlagEvent()) {
			flag0();
		}
	}
	
	/**
	 * Flags and gets the violation count
	 * 
	 * @return the updated violation count
	 */
	int flag0() {
		int violations = this.violations.getAndIncrement();
		nessPlayer.getInfractions().add(Infraction.of(getFactory(), violations));
		return violations;
	}
	
	boolean callFlagEvent() {
		JavaPlugin plugin = getFactory().getCheckManager().getNess().getPlugin();

		return callEvent(plugin, new PlayerFlagEvent(nessPlayer, getFactory()))
				&& callDeprecatedPlayerViolationEvent(plugin);
	}
	
	@SuppressWarnings("deprecation")
	private boolean callDeprecatedPlayerViolationEvent(JavaPlugin plugin) {
		if (com.github.ness.api.impl.PlayerViolationEvent.getHandlerList().getRegisteredListeners().length == 0) {
			return true;
		}
		return callEvent(plugin, new com.github.ness.api.impl.PlayerViolationEvent(
				nessPlayer.getPlayer(), nessPlayer,
				new com.github.ness.api.Violation(getFactory().getCheckName(), ""),
				violations.get()));
	}
	
	private boolean callEvent(JavaPlugin plugin, Cancellable event) {
		plugin.getServer().getPluginManager().callEvent((Event) event);
		return !event.isCancelled();
	}
	
	public int currentViolationCount() {
		return violations.get();
	}

	public void clearViolationCount() {
		violations.set(0);
	}

}
