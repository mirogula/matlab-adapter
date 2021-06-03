package sk.stuba.fei.jlab.matlabadapter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class MatlabAdapterProperties {
	
	private static final Properties properties = new Properties();
	
	public static void load(String propertiesFile) throws IOException {
		try(FileReader fr = new FileReader(new File(propertiesFile))) {
			properties.load(fr);
		}
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
}
