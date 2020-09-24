package com.github.ness.check.world;

import java.time.Duration;

import org.bukkit.event.block.BlockPlaceEvent;

import com.github.ness.NessPlayer;
import com.github.ness.api.Violation;
import com.github.ness.check.CheckInfos;
import com.github.ness.check.ListeningCheck;
import com.github.ness.check.ListeningCheckFactory;
import com.github.ness.check.ListeningCheckInfo;

import space.arim.dazzleconf.annote.ConfDefault.DefaultInteger;

public class FastPlace extends ListeningCheck<BlockPlaceEvent> {

    int max;
    
	public static final ListeningCheckInfo<BlockPlaceEvent> checkInfo = CheckInfos.forEventWithAsyncPeriodic(BlockPlaceEvent.class, Duration.ofSeconds(1));
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
    protected void checkAsyncPeriodic() {
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
			if(player().setViolation(new Violation("FastPlace", "Placing: " + blockPlace))) e.setCancelled(true);
        }
    }

}
