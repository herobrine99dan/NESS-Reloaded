package com.github.ness.check.movement;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.data.MovementValues;
import java.util.List;
import org.bukkit.Location;

public class Jesus extends ListeningCheck<PlayerMoveEvent> {

    public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

    public Jesus(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
        super(factory, player);
    }

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        NessPlayer nessPlayer = this.player();

        MovementValues movementValues = nessPlayer.getMovementValues();
        List<Location> locs = movementValues.getHelper().getLocationsAroundPlayerLocation(event.getTo());
        boolean lilypad = false;
        for (Location l : locs) {
            if (l.getBlock().getType().name().contains("lily")) {
                lilypad = true;
            }
        }
        double yDelta = Math.abs(movementValues.getyDiff());
        if(player.isInsideVehicle() && player.getVehicle().getType().name().contains("BOAT")) {
            return;
        }
        if (event.getTo().clone().add(0, -1.0, 0).getBlock().isLiquid() && !player.isFlying()) {
            if (!movementValues.getHelper().isOnGround(event.getTo()) && !lilypad
                    && !event.getTo().clone().add(0, 1, 0).getBlock().isLiquid()) {
                this.player().sendDevMessage("Player on Water! yDelta: " + yDelta);
                if (Double.toString(yDelta).contains("00000000") || yDelta < 0.001) {
                    this.flag();
                }
            }
        }
    }
}
