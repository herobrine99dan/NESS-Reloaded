package com.github.ness.check.movement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.NumberFormatter;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.utility.Utility;
import com.github.ness.utility.excel.ExcelData;

import space.arim.dazzleconf.annote.ConfDefault.DefaultDouble;

public class Jesus extends ListeningCheck<PlayerMoveEvent> {

	public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

	double distmultiplier = 0.75;
	double lastXZDist;
	double lastYDist;
	private List<Float> yDistances;
	private List<Float> lastYDistances;

	public Jesus(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
		super(factory, player);
		this.distmultiplier = this.ness().getMainConfig().getCheckSection().jesus().distmultiplier();
		yDistances = new ArrayList<Float>();
		lastYDistances = new ArrayList<Float>();
	}

	public interface Config {
		@DefaultDouble(0.7)
		double distmultiplier();
	}

	@Override
	protected void checkEvent(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		NessPlayer nessPlayer = this.player();
		if (Utility.hasflybypass(p) || Utility.hasVehicleNear(p, 3)) {
			return;
		}
		// We handle Prediction for Y Value
		double yDist = nessPlayer.getMovementValues().getyDiff();
		double predictedY = lastYDist * 0.80D;
		predictedY -= 0.02D;
		double resultY = yDist - predictedY;
		// We handle Prediction for XZ Values
		double xzDist = nessPlayer.getMovementValues().getXZDiff();
		double predictedXZ = lastXZDist * 0.8f;
		double resultXZ = xzDist - predictedXZ;
		// We start the check only if the player is in liquid
		yDistances.add((float) yDist);
		lastYDistances.add((float) lastYDist);
		if (event.getTo().clone().add(0, -0.1, 0).getBlock().isLiquid() && event.getFrom().getBlock().isLiquid()
				&& Utility.isNearWater(event.getTo(), this.manager().getNess().getMaterialAccess())) {
			if (!nessPlayer.getMovementValues().isOnGroundCollider()) {
				if (yDist > 0.301D) {
					nessPlayer.sendDevMessage("Flag");
				} else if (resultY > 0.055) {
					nessPlayer.sendDevMessage("Flag1 " + (float) resultY);

				}
			}
			if (resultXZ > 0.06) {
				nessPlayer.sendDevMessage("Flag2 " + (float) resultXZ);
			}
		}
		if (yDistances.size() > 50) {
			ExcelData data = new ExcelData(new File("./" + System.currentTimeMillis() + ".csv"), ";");
			ArrayList<String> yDistances = new ArrayList<String>();
			for (float f : this.yDistances) {
				yDistances.add(Float.toString(f).replace(".", ","));
			}
			data.getCustomHashMap().putIfAbsent("yDistances", yDistances);
			ArrayList<String> lastYDistances = new ArrayList<String>();
			for (float f : this.lastYDistances) {
				lastYDistances.add(Float.toString(f).replace(".", ","));
			}
			data.getCustomHashMap().putIfAbsent("lastYDistances", lastYDistances);
			data.save();
			this.yDistances.clear();
			this.lastYDistances.clear();
		}
		lastXZDist = xzDist;
		lastYDist = yDist;
	}

}
