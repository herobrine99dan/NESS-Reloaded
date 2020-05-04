package com.github.ness.check;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.github.ness.NESSAnticheat;
import com.github.ness.Utility;

public class Freecam {

	public static void Check(Player p) {
		if (!Utility.isOnGround(p)) {
			return;
		}
	      final Location first = p.getLocation();
          Location loc = p.getLocation();
          loc.setY(loc.getY()+0.08);
          p.teleport(loc);
    	  Bukkit.getScheduler().runTaskLater(NESSAnticheat.main, new Runnable()
    	  {
    	    public void run()
    	    {
                if(!(first.getY()==p.getLocation().getY())) {
                	double result = p.getLocation().getY()-first.getY();
             	   if(p.getLocation().getY()>first.getY() && result > 0.0700000000000083 && result < 0.0800000000000083){
             		   //cheater
             	   }
                }
    	    }
    	  }, 10L);
	}
}
