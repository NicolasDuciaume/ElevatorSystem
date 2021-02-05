package elevatorTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import elevator.*;

/**
 * 
 * @author Nazifa Tanzim, 101074707
 *
 */
class SchedulerTest {
	private Scheduler scheduler;
	private String data;
	
	@BeforeEach
	void setUp() throws Exception {
        scheduler = new Scheduler();
        data = "test";
	}

	/*
	 * Expected:
	 * 	dataFloor = "test"
	 * 	emptyFloor = false
	 */
	@Test
	void testRecieveFromFloor() {
		scheduler.recieveFromFloor(data);
		assertEquals(data, scheduler.getDataFloor());
		assertEquals(false, scheduler.isEmptyFloor());
	}

	/*
	 * Expected:
	 * 	dataElevator = "test"
	 * 	emptyElevator = false
	 */
	@Test
	void testRecieveFromElevator() {
		scheduler.recieveFromElevator(data);
		assertEquals(data, scheduler.getDataElevator());
		assertEquals(false, scheduler.isEmptyElevator());
	}

	/*
	 * Expected:
	 * 	dataElevator = ""
	 * 	emptyFloor = true
	 */
	@Test
	void testSendToFloor() {
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
	@Test
	void testSendToElevator() {
		scheduler.recieveFromFloor(data);
		String dataForElevator = scheduler.sendToElevator();
		
		assertEquals(data, dataForElevator);
		assertEquals("", scheduler.getDataFloor());
		assertEquals(true, scheduler.isEmptyElevator());
	}
	
	/*
	@Test
	void testCheckPriority() {
		fail("Not yet implemented");
	}
 	*/
}
