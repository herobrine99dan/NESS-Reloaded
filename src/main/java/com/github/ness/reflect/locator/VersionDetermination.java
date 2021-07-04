package com.github.ness.reflect.locator;

import java.util.Objects;

public class VersionDetermination {

	private final String nmsVersion;

	private static final int MINIMUM_SUPPORTED_VERSION = 8;
	private static final int LATEST_MINECRAFT_VERSION = 17; // This is here just because i'm lazy to write always the
															// same

	public VersionDetermination(String nmsVersion) {
		this.nmsVersion = Objects.requireNonNull(nmsVersion);
	}

	public static String getNmsVersion(String craftbukkitPackage) {
		return craftbukkitPackage.substring(craftbukkitPackage.lastIndexOf(".") + 1);
	}

	boolean is16OrBelow() {
		if (isVersion(7)) {
			throw new IllegalStateException(
					"You're running on an outdated server version. NESS does not support 1.7 or below.");
		}
		for (int version = MINIMUM_SUPPORTED_VERSION; version < LATEST_MINECRAFT_VERSION; version++) {
			if (isVersion(version)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Is the Minecraft server using 1.8?
	 * 
	 * @return if the server's version is 1.8
	 */
	public boolean is18() {
		return isVersion(8);
	}

	/**
	 * Does the server has Combat Update? (Versions with Combat Update starts from
	 * 1.9)
	 * 
	 * @return if the server's version is newer than 1.8
	 */
	public boolean hasCombatUpdate() {
		for (int version = 9; version <= LATEST_MINECRAFT_VERSION; version++) {
			if (isVersion(version)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Does the server has Acquatic Update? (Versions with Acquatic Update starts
	 * from 1.13)
	 * 
	 * @return if the server's version is newer than 1.13
	 */
	public boolean hasAquaticUpdate() {
		for (int version = 13; version <= LATEST_MINECRAFT_VERSION; version++) {
			if (isVersion(version)) {
				return true;
			}
		}
		return false;
	}

	private boolean isVersion(int version) {
		return nmsVersion.startsWith("v1_" + version);
	}
}
