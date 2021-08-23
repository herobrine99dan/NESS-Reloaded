package com.github.ness.check.movement;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import space.arim.dazzleconf.annote.ConfDefault;

public class NoFall extends ListeningCheck<PlayerMoveEvent> {

    public static final ListeningCheckInfo<PlayerMoveEvent> checkInfo = CheckInfos.forEvent(PlayerMoveEvent.class);
    private final boolean applyDamage;
    private final double minFallChange;

    public NoFall(ListeningCheckFactory<?, PlayerMoveEvent> factory, NessPlayer player) {
        super(factory, player);
        this.applyDamage = this.ness().getMainConfig().getCheckSection().nofall().applyDamage();
        this.minFallChange = this.ness().getMainConfig().getCheckSection().nofall().minFallChange();
    }

    private float fallHeight = 0.0f;

    @Override
    protected boolean shouldDragDown() {
        return true;
    }

    public interface Config {

        @ConfDefault.DefaultBoolean(true)
        boolean applyDamage();

        @ConfDefault.DefaultDouble(1)
        double minFallChange();
    }

    @Override
    protected void checkEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        float deltaY = (float) this.player().getMovementValues().getyDiff();
        float fallDistance = player.getFallDistance();
        float superResult = fallHeight - fallDistance;
        if (superResult > minFallChange) {
            this.flag("fallHeight: " + fallHeight + " fallDistance: " + fallDistance);
            float damage = (fallHeight * 0.5f) - 1.5f;
            if (damage > 0 && applyDamage) {
                player.damage(damage);
            }
            //this.player().sendDevMessage("fallHeight: " + fallHeight + " fallDistance: " + fallDistance);
        }
        if (deltaY < 0.0) {
            fallHeight -= deltaY;
        }
        //TODO Tridents
        boolean isNearWater = isLiquid(event.getTo());

        boolean isOnGround = player.isOnGround();//isOnGround(event.getFrom());
        if (isOnGround || this.player().getMovementValues().getHelper().hasflybypass(this.player()) || isNearWater || player.isInsideVehicle() || event.getFrom().getBlock().getType().name().contains("WEB") || event.getTo().getBlock().getType().name().contains("WEB")) {
            fallHeight = 0.0f;
        }
    }

    private boolean isLiquid(Location loc) {
        String name = loc.getBlock().getType().name();
        return name.contains("WATER") || name.contains("LAVA");

    }

    private boolean isOnGround(Location loc) {
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
