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
	private static final long NANOSEC = 1000000000;
	
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
	public int getFloorPort() {
		return Integer.parseInt(this.property.getProperty("floor_port"));
	}
	
	/**
	 * 
	 * @return
	 */
	public int getElevatorPort() {
		return Integer.parseInt(this.property.getProperty("elevator_port"));
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
	public long getTimeToOpenCloseDoors() {
		return Long.parseLong(this.property.getProperty("time_open_close_doors_sec")) * NANOSEC;
	}
	
	/**
	 * 
	 * @return
	 */
	public long getTimeBetweenFloors() {
		return Long.parseLong(this.property.getProperty("time_between_floors_sec")) * NANOSEC;
	}



}
