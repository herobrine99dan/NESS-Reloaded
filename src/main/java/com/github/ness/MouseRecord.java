package com.github.ness;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class MouseRecord implements Listener {

	private final static int SIZE = 100;
	NESSAnticheat ness;

	public MouseRecord(NESSAnticheat nessAnticheat) {
		this.ness = nessAnticheat;
		Bukkit.getPluginManager().registerEvents(this, ness);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		NessPlayer np = ness.getCheckManager().getPlayer(e.getPlayer());
		Location to = e.getTo().clone();
		if (np.isMouseRecord()) {
			if (np.mouseRecordValues.size() < SIZE) {
				np.mouseRecordValues
						.add(new Point(Math.round(Math.abs(to.getYaw())), Math.round(Math.abs(to.getPitch()))));
			} else if (np.mouseRecordValues.size() == SIZE) {
				np.getPlayer().sendMessage("Rendering!");
				render(np);
				np.mouseRecordValues.clear();
				np.getPlayer().sendMessage("Saved!");
			}
		}
	}

	public void render(NessPlayer np) {
		BufferedImage img = new BufferedImage(400, 220, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		g.setBackground(Color.BLACK);
		g.setColor(Color.WHITE);
		Point lastValue = new Point(0, 0);
		for (Point p : np.mouseRecordValues) {
			g.drawLine((int) lastValue.getX(), (int) lastValue.getY(), (int) p.getX(), (int) p.getY());
			lastValue = p;
		}
		try {
			ImageIO.write(img, "PNG", new File(this.ness.getDataFolder(), System.currentTimeMillis() + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
