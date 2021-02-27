package elevator;
import java.sql.Timestamp;

public class FloorRequest {
	
	private Timestamp requestTime;
	
	private long travelTime;
	private long doorTime;
	private int floorRequestOrigin;
	private int floorDestination;
	private Direction direction;
	
	public FloorRequest(Timestamp requestTime, long travelTime, long doorTime, int floorOrigin, int floorDestination, Direction direction) {
		this.requestTime = requestTime;
		this.direction = direction;
		this.floorDestination = floorDestination;
		this.floorRequestOrigin = floorOrigin;
		this.travelTime = travelTime;
		this.doorTime = doorTime; 
	}
	
	public FloorRequest() { // error constructor
		this.requestTime = new Timestamp(System.currentTimeMillis());
		this.direction = Direction.STOPPED;
		this.floorDestination = -1;
		this.floorRequestOrigin = -1;
		this.travelTime = -1;
		this.doorTime = -1; 
	}
	
	public Timestamp getRequestTime() {
		return requestTime;
	}

	public long getTravelTime() {
		return travelTime;
	}


	public int getFloorRequestOrigin() {
		return floorRequestOrigin;
	}


	public Direction getDirection() {
		return direction;
	}


	public int getFloorDestination() {
		return floorDestination;
	}

	public long getDoorTime() {
		return doorTime;
	}


	
}
