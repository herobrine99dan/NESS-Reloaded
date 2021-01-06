package com.github.ness.utility.excel;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.github.ness.utility.excel.CustomHashMap.HashMapValue;

public class ExcelData {
	private CustomHashMap values = new CustomHashMap();
	private File file;
	private String splitter;

	public ExcelData(File f, String splitter) {
		this.file = f;
		this.splitter = splitter;
	}

	public boolean load() {
		try {
			// TODO Should use UTF-8 (read
			// https://stackoverflow.com/questions/26268132/all-inclusive-charset-to-avoid-java-nio-charset-malformedinputexception-input)
			List<String> lines = Files.readAllLines(file.toPath(), Charset.forName("ISO-8859-1"));
			for (int i = 0; i < lines.size(); i++) {
				final String line = lines.get(i);
				String[] splitted = line.split(splitter);
				if (i == 0) {
					for (String s1 : splitted) {
						values.putIfAbsent(s1, new ArrayList<String>());
					}
				} else {
					/*
					 * int j = 0; for (HashMapValue hp : values.getList()) {
					 * System.out.println("Classe: '" + hp.getKey() + "' " + "Indice: " + j); j++; }
					 */
					for (int k = 0; k < splitted.length; k++) {
						HashMapValue v = this.values.getHashMapValue(k);
						// System.out.println(splitted[k] + " " + k + " " + v.getKey());
						v.getValue().add(splitted[k]);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public List<HashMapValue> getData() {
		return this.values.getList();
	}
	
	public CustomHashMap getCustomHashMap() {
		return this.values;
	}
	
	public void removeItem(String s1) {
		this.values.remove(s1);
	}
	
	public ArrayList<String> getItem(String s1) {
		return this.values.get(s1);
	}

	public void save() {
		try {
			List<String> lines = new ArrayList<String>();
			String header = "";
			for (HashMapValue hmp : values.getList()) {
				header = header + hmp.getKey() + splitter;
			}
			lines.add(header.substring(0, header.length() - 1));
			for (int k = 0; k < this.values.getHashMapValue(0).getValue().size(); k++) {
				String result = "";
				for (HashMapValue mp : this.values.getList()) {
					result = result + mp.getValue().get(k) + splitter;
				}
				lines.add(result);
			}
			Files.write(this.file.toPath(), lines);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
