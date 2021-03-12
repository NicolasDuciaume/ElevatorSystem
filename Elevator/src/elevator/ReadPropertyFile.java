package elevator;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class ReadPropertyFile {
	Properties property;
	
	public ReadPropertyFile() {
		property = new Properties();
		
		try {
			FileInputStream ip = new FileInputStream("config.properties");
			property.load(ip);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println();
	}
	
	public int getNumElevators() {
		return Integer.parseInt(this.property.getProperty("elevators"));
	}
	
	public int getNumFloors() {
		return Integer.parseInt(this.property.getProperty("floors"));
	}
	
	public int getTimeToOpenCloseDoors() {
		return Integer.parseInt(this.property.getProperty("time_open_close_doors"));
	}
	
	public int getTimeBetweenFloors() {
		return Integer.parseInt(this.property.getProperty("time_between_floors"));
	}


}
