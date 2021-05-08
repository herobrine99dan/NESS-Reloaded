package com.github.ness.check.required;

import java.time.Duration;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;
import com.github.ness.reflect.FieldInvoker;
import com.github.ness.reflect.MemberDescriptions;
import com.github.ness.reflect.MethodInvoker;

public class TeleportEvent extends ListeningCheck<PlayerTeleportEvent> {

	public static final ListeningCheckInfo<PlayerTeleportEvent> checkInfo = CheckInfos
			.forEventWithTask(PlayerTeleportEvent.class, PeriodicTaskInfo.syncTask(Duration.ofMillis(1500)));
	private MethodInvoker<?> entityPlayerMethod;
	FieldInvoker<?> fieldPing;
	public TeleportEvent(ListeningCheckFactory<?, PlayerTeleportEvent> factory, NessPlayer player) {
		super(factory, player);
		entityPlayerMethod = this.ness().getReflectHelper().getMethod(ness().getReflectHelper().getObcClass("entity.CraftPlayer"),
				MemberDescriptions.forMethod("getHandle", new Class<?>[] {}));
		fieldPing = ness().getReflectHelper().getField(ness().getReflectHelper().getNmsClass("EntityPlayer"), MemberDescriptions.forField("ping"));
	}

	@Override
	protected void checkSyncPeriodic() {
		player().setTeleported(false);
		player().setHasSetback(false);
		Player bukkitPlayer = this.player().getBukkitPlayer();
		Object entityPlayer = entityPlayerMethod.invoke(bukkitPlayer);
		int ping = (int) fieldPing.get(entityPlayer);
		player().setPing(ping);
	}

	protected void checkEvent(PlayerTeleportEvent e) {
		NessPlayer nessPlayer = this.player();
		if (!nessPlayer.isHasSetback()) {
			nessPlayer.setTeleported(true);
		}
		nessPlayer.setHasSetback(false);
	}

}
