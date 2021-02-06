package elevatorTests;

import junit.framework.TestCase;

import elevator.*;

/**
 * 
 * @author Nazifa Tanzim, 101074707
 *
 */
public class SchedularTest extends TestCase {
	private Schedular scheduler;
	private String data;

	protected void setUp() throws Exception {
		super.setUp();
		scheduler = new Schedular();
		data = "Hello";
	}

	/*
	 * Expected:
	 * 	dataFloor = "test"
	 * 	emptyFloor = false
	 */
	public void testRecieveFromFloor() {
		scheduler.recieveFromFloor(data);
		assertEquals(data, scheduler.getDataFloor());
		assertEquals(false, scheduler.isEmptyFloor());
	}

	/*
	 * Expected:
	 * 	dataElevator = "test"
	 * 	emptyElevator = false
	 */
	public void testRecieveFromElevator() {
		scheduler.recieveFromElevator(data);
		assertEquals(data, scheduler.getDataElevator());
		assertEquals(false, scheduler.isEmptyElevator());
	}

	/*
	 * Expected:
	 * 	dataElevator = ""
	 * 	emptyFloor = true
	 */
	public void testSendToFloor() {
		scheduler.recieveFromElevator(data);
		String dataForFloor = scheduler.sendToFloor();
		
		assertEquals(data, dataForFloor);
		assertEquals("", scheduler.getDataElevator());
		assertEquals(true, scheduler.isEmptyFloor());
	}

	/*
	 * Expected:
	 * 	dataFloor = ""
	 * 	emptyFloor = true
	 */
	public void testSendToElevator() {
		scheduler.recieveFromFloor(data);
		String dataForElevator = scheduler.sendToElevator();
		
		assertEquals(data, dataForElevator);
		assertEquals("", scheduler.getDataFloor());
		assertEquals(true, scheduler.isEmptyElevator());
	}

	/*
	public void testCheckPriority() {
		fail("Not yet implemented");
	}
	*/

}
