package elevatorTests;

import junit.framework.TestCase;
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
		fileLocation = "File.txt";
		floor = new FloorSubsystem(fileLocation, new Scheduler());
	}

	/**
	 * Test that a request has been added to the FloorSubsystem list
	 */
	public void testAddFloorRequest() {
		// Check that there is 1 request - retrieved from File.txt during init
		assertEquals(1, floor.getListOfRequests().size());

		// Add another request - using the same request from File.txt
		floor.addFloorRequest(fileLocation);

		// Check that the size of the list has incremented by 1
		assertEquals(2, floor.getListOfRequests().size());
	}

//	public void testSetLampsSensors() {
//		fail("Not yet implemented");
//	}

}
