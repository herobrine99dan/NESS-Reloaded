package org.mswsplex.MSWS.NESS;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;

import lombok.Getter;

public class NESSAnticheat {

	@Getter
	private final Multithreading multithreading;
	
	NESSAnticheat(NESS ness) {
		multithreading = new Multithreading((cmd) -> Bukkit.getScheduler().runTaskAsynchronously(ness, cmd),
				(cmd) -> Bukkit.getScheduler().runTask(ness, cmd));
	}
	
	CompletableFuture<String> checkUpdate(int versionId) {
		return multithreading.supplyAsync(() -> {
			try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=" + versionId).openStream(); Scanner scanner = new Scanner(inputStream)) {
	            if (scanner.hasNext()) {
	                return scanner.next();
	            }
	        } catch (IOException ignored) {
	        	
	        }
	        return null;
		});
	}
	
}
