package elevatorTests;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Map;

import elevator.*;

/**
 * 
 * @author Nazifa Tanzim 101074707
 *
 */
public class FloorSubsystemTest extends TestCase {
	private String fileLocation;
	private FloorSubsystem floor;

	protected void setUp() throws Exception {
		super.setUp();
		fileLocation = "File_test.txt";
		floor = new FloorSubsystem(fileLocation);
	}
	
	/**
	 * Test that a request has been added to the FloorSubsystem list
	 * Since this is based on our input file, 4 floor requests will 
	 * be added each time this method is called
	 */
	public void testAddFloorRequest() {
		System.out.println(floor.getListOfRequests().size());
		// Check that there is 1 request - retrieved from File_test.txt during init
		assertEquals(1, floor.getListOfRequests().size());

		// Add another request - using the same request from File_test.txt
		floor.addFloorRequest(fileLocation);

		// Check that the size of the list has incremented by 1
		assertEquals(2, floor.getListOfRequests().size());
	}

	/**
	 * Tests that the appropriate lamp sensor is set
	 * Testing with elevator 1
	 */
	public void testSetLampsSensors() {
		// All lamps and sensors should start in off state
		String elevator = "Elevator2";
		int elevatorIndex = 1;
		int floorNum = 2;
		Map<Integer, ArrayList<Boolean>> sensors = floor.getArrivalSensors();
		ArrayList<Boolean> lamps = sensors.get(floorNum);
		boolean sensorOn = lamps.get(elevatorIndex);
		
		// Check that sensor is off
		assertEquals(false, sensorOn);
		
		floor.setLampsSensors("2", elevator, true);
		
		sensors = floor.getArrivalSensors();
		lamps = sensors.get(floorNum);
		sensorOn = lamps.get(elevatorIndex);
		
		// Check that sensor is on
		assertEquals(true, sensorOn);
		
		// Turning sensor off
		floor.setLampsSensors("2", elevator, false);
		
		sensors = floor.getArrivalSensors();
		lamps = sensors.get(floorNum);
		sensorOn = lamps.get(elevatorIndex);
		
		// Check that sensor is on
		assertEquals(false, sensorOn);
	}
}