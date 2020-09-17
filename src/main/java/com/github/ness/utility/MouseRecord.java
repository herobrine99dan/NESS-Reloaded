package com.github.ness.utility;

import com.github.ness.NESSAnticheat;
import com.github.ness.NessPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class MouseRecord implements Listener {

    private final static int SIZE = 100;
    NESSAnticheat ness;

    public MouseRecord(NESSAnticheat nessAnticheat) {
        this.ness = nessAnticheat;
        Bukkit.getPluginManager().registerEvents(this, ness);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        NessPlayer np = ness.getCheckManager().getExistingPlayer(e.getPlayer());
        if (np == null) {
        	return;
        }
        if (np.getMovementValues().yawDiff == 0.0) {
            return;
        }
        if (np.isMouseRecord()) {
            if (np.mouseRecordValues.size() < SIZE) {
                np.mouseRecordValues
                        .add(new Point((int) Math.round(Math.abs(np.getMovementValues().yawDiff)), (int) Math.round(Math.abs(np.getMovementValues().pitchDiff))));
            } else if (np.mouseRecordValues.size() == SIZE) {
                np.getPlayer().sendMessage("Rendering!");
                render(np);
                np.mouseRecordValues.clear();
                np.getPlayer().sendMessage("Saved!");
            }
        }
    }

    public void render(NessPlayer np) {
        BufferedImage img = new BufferedImage(1000, 920, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setBackground(Color.BLACK);
        g.setColor(Color.WHITE);
        for (Point p : np.mouseRecordValues) {
            p.setLocation(p.x * 15, p.y * 15);
        }
        Point lastValue = new Point(0, 0);
        for (Point p : np.mouseRecordValues) {
            g.drawLine((int) lastValue.getX(), (int) lastValue.getY(), (int) p.getX(), (int) p.getY());
            lastValue = p;
        }
        try {
            Path path = this.ness.getDataFolder().toPath().resolve("records")
                    .resolve(System.currentTimeMillis() + ".png");
            ImageIO.write(img, "PNG", path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (np.isDevMode()) {
            System.out.println(getAverage(np.mouseRecordValues));
        }
    }

    private double getAverage(List<Point> list) {
        double d = 0;
        for (Point p : list) {
            d += p.getX();
        }
        return d / list.size();
    }

}
