package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public abstract class BaseConfig {

	public static void initialize(String resource, Properties properties) {
		try {
			InputStream stream = BaseConfig.class.getResourceAsStream("/"+resource);
			System.out.println("The resource file is "+resource);
			BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));
			try {
				properties.load(br);
				stream.close();
			} catch (IOException e) {
				throw new RuntimeException(e); // here, log utility is not ready
												
			}			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
