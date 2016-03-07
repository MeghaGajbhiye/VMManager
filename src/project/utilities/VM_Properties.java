package project.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VM_Properties extends Properties {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VM_Properties(String fileName) throws IOException {
		super();
		InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
		load(is);
	}

}
