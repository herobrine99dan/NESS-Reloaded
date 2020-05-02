package com.github.ness.config;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

	private final Plugin plugin;

	public ConfigManager(final Plugin plugin) {
		this.plugin = plugin;
	}

	private int getCommentsNum(final File file) {
		if (!file.exists()) {
			return 0;
		}
		try {
			int comments = 0;
			String currentLine;
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath()), "UTF8"));
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.trim().startsWith("#")) {
					comments++;
				}
			}
			reader.close();
			return comments;
		} catch (final IOException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private InputStream getConfigContent(final File file) {
		if (!file.exists()) {
			return null;
		}
		try {
			int commentNum = 0;
			String currentLine;
			String whole = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath()), "UTF8"));
			while ((currentLine = reader.readLine()) != null) {
				if (currentLine.trim().startsWith("#")) {
					int spaceCount = 0;
					for (final char c : currentLine.toCharArray()) {
						if (c == ' ') {
							spaceCount++;
						} else {
							break;
						}
					}
					whole += currentLine.trim().replaceFirst("#", this.plugin.getDescription().getPrefix() + "_COMMENT_" + (commentNum + spaceCount) + "_" + spaceCount + ":") + "\n";
					commentNum++;
				}
			}
			reader.close();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.getPath()), "UTF8"));
			while ((currentLine = reader.readLine()) != null) {
				if (!currentLine.trim().startsWith("#")) {
					whole += currentLine + "\n";
				}
			}
			final InputStream configStream = new ByteArrayInputStream(whole.getBytes(Charset.forName("UTF-8")));
			reader.close();
			return configStream;
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private InputStream getConfigContent(final String filePath) {
		return this.getConfigContent(this.getConfigFile(filePath));
	}

	private File getConfigFile(final String file) {
		File configFile;
		if (file.contains("/")) {
			if (file.startsWith("/")) {
				configFile = new File(this.plugin.getDataFolder() + file.replace("/", File.separator));
			} else {
				configFile = new File(this.plugin.getDataFolder() + File.separator + file.replace("/", File.separator));
			}
		} else {
			configFile = new File(this.plugin.getDataFolder(), file);
		}
		return configFile;
	}

	public Config getNewConfig() {
		final File file = this.getConfigFile("config.yml");
		if (!file.exists()) {
			this.prepareFile("config.yml");
		}
		return new Config(this.getConfigContent("config.yml"), file, this.getCommentsNum(file), this.plugin);
	}

	private String prepareConfigString(final String configString) {
		final String[] lines = configString.split("\n");
		String config = "";
		final Map<Integer, String> commentLines = new HashMap<>();
		for (final String line : lines) {
			if (line.startsWith(this.plugin.getDescription().getPrefix() + "_COMMENT")) {
				final int lineNumber = Integer.parseInt(line.split("_")[2]);
				final int spIndex = line.split("_")[3].indexOf(":");
				final int spaceCount = Integer.parseInt(line.split("_")[3].substring(0, spIndex));
				String spaces = "";
				for (int i = 0; i < spaceCount; i++) {
					spaces += " ";
				}
				final String comment = spaces + "# " + line.split(":")[1].replaceAll("'", "").trim();
				commentLines.put(lineNumber, comment);
			}
		}
		int lineCount = 0;
		for (final String line : lines) {
			if (line.startsWith(this.plugin.getDescription().getPrefix() + "_COMMENT")) {
				continue;
			}
			lineCount++;
			if (commentLines.containsKey(lineCount)) {
				if (!line.startsWith(this.plugin.getDescription().getPrefix() + "_COMMENT")) {
					config += commentLines.get(lineCount) + "\n";
					config += line + "\n";
				} else {
					config += commentLines.get(lineCount) + "\n";
				}
			} else {
				if (!line.startsWith(this.plugin.getDescription().getPrefix() + "_COMMENT")) {
					config += line + "\n";
				}
			}
		}
		return config;
	}

	private void prepareFile(final String filePath) {
		final File file = this.getConfigFile(filePath);
		if (file.exists()) {
			return;
		}
		try {
			file.getParentFile().mkdirs();
			file.createNewFile();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	void saveConfig(final String configString, final File file) {
		final String configuration = this.prepareConfigString(configString);
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file.getPath()), "UTF8"));
			writer.write(configuration);
			writer.flush();
			writer.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
}
