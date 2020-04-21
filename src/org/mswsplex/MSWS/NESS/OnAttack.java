package org.mswsplex.MSWS.NESS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mswsplex.MSWS.NESS.checks.Aimbot;
import org.mswsplex.MSWS.NESS.checks.AntiKb;
import org.mswsplex.MSWS.NESS.checks.Criticals;
import org.mswsplex.MSWS.NESS.checks.Killaura;
import org.mswsplex.MSWS.NESS.checks.KillauraBotCheck;

public class OnAttack implements Listener {
	protected static List<UUID> fightingPlayers = new ArrayList<UUID>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAttack(EntityDamageByEntityEvent event) {
		Killaura.Check(event);
		Killaura.Check1(event);
		AntiKb.Check(event);
		Criticals.Check(event);
		Killaura.Check2(event);
		Killaura.Check3(event);
		Killaura.Check4(event);
		Killaura.Check5(event);
		KillauraBotCheck.Check(event);
		Killaura.Check7(event);
		Killaura.Check8(event);
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			OnAttack.fightingPlayers.add(player.getUniqueId());
		}
	}
}