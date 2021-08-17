package com.github.ness.check.movement.oldmovementchecks;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class NoFall extends ListeningCheck<PlayerMoveEvent> {

    public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);

    public NoFall(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
        super(factory, player);
    }

    private float fallHeight = 0.0f;

    @Override
    protected boolean shouldDragDown() {
        return true;
    }

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        float deltaY = (float) this.player().getMovementValues().getyDiff();
        float fallDistance = player.getFallDistance();
        float superResult = fallHeight - fallDistance;
        if(superResult > 1) {
            this.player().sendDevMessage("fallHeight: " + fallHeight + " fallDistance: " + fallDistance);
        }
        if (deltaY < 0.0) {
            fallHeight -= deltaY;
        }
        //TODO Tridents
        boolean isNearWater = event.getTo().getBlock().isLiquid();
        boolean isOnGround = player.isOnGround();//isOnGround(event.getFrom());
        if (isOnGround || this.player().getMovementValues().getHelper().hasflybypass(this.player()) || isNearWater || player.isInsideVehicle()) {
            fallHeight = 0.0f;
        }
    }

    public boolean isOnGround(Location loc) {
        final double limit = 0.3;
        for (double x = -limit; x <= limit; x += limit) {
            for (double z = -limit; z <= limit; z += limit) {
                if (loc.clone().add(x, -0.301, z).getBlock().getType().isSolid()) {
                    return true;
                }
            }
        }
        return false;
    }

}
