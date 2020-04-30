package org.mswsplex.MSWS.NESS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.mswsplex.MSWS.NESS.checks.combat.Aimbot;
import org.mswsplex.MSWS.NESS.checks.combat.AntiKb;
import org.mswsplex.MSWS.NESS.checks.combat.Killaura;
import org.mswsplex.MSWS.NESS.checks.combat.KillauraBotCheck;
import org.mswsplex.MSWS.NESS.checks.combat.NoSwing;
import org.mswsplex.MSWS.NESS.checks.combat.PatternKillaura;
import org.mswsplex.MSWS.NESS.checks.movement.Criticals;

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
		Killaura.Check6(event);
		PatternKillaura.Check(event);
		KillauraBotCheck.Check(event);
		NoSwing.damageEvent(event);
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			OnAttack.fightingPlayers.add(player.getUniqueId());
		}
	}
}