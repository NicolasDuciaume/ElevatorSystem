package elevator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * @author Nazifa Tanzim	 101074707
 * @author Tooba Sheikh      101028915
 * 
 * Class to read variables from the configuration file
 *
 */
public class ReadPropertyFile {
	Properties property;
	
	public ReadPropertyFile() {
		property = new Properties();
		
		// Reading from file
		try {
			FileInputStream ip = new FileInputStream("config.properties");
			property.load(ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumElevators() {
		return Integer.parseInt(this.property.getProperty("elevators"));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getNumFloors() {
		return Integer.parseInt(this.property.getProperty("floors"));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTimeToOpenCloseDoors() {
		return Integer.parseInt(this.property.getProperty("time_open_close_doors_ms"));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getTimeBetweenFloors() {
		return Integer.parseInt(this.property.getProperty("time_between_floors_ms"));
	}


}
