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

//	public void testSetLampsSensors() {
//		fail("Not yet implemented");
//	}
}