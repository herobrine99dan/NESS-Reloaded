package com.github.ness.check.world;

import java.time.Duration;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;
import com.github.ness.check.PeriodicTaskInfo;

import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class FastPlace extends ListeningCheck<BlockPlaceEvent> {

    private final int max;
    
	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEventWithTask(BlockPlaceEvent.class, PeriodicTaskInfo.syncTask(Duration.ofSeconds(1)));
    private int blockPlace; // For FastPlace Check

	public FastPlace(ListeningCheckFactory<?, BlockPlaceEvent> factory, NessPlayer player) {
		super(factory, player);
	      this.max = this.ness().getMainConfig().getCheckSection().fastPlace().max();
	      blockPlace = 0;
	}
	
	public interface Config {
		@DefaultInteger(10)
		int max();
	}

    @Override
    protected void checkSyncPeriodic() {
        blockPlace = 0;
    }

    @Override
    protected void checkEvent(BlockPlaceEvent e) {
        Check(e);
    }

    /**
     * A Simple FastPlace check
     *
     * @param event
     */
    public void Check(BlockPlaceEvent e) {
        blockPlace++;
        if (blockPlace > max) {
        	flagEvent(e);
			//if(player().setViolation(new Violation("FastPlace", "Placing: " + blockPlace))) e.setCancelled(true);
        }
    }

}
