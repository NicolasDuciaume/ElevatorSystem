package elevator;

import java.sql.Timestamp;

public class FloorRequest {

	private Timestamp requestTime;
	private long travelTime;
	private long doorTime;
	private int floorRequestOrigin;
	private int floorDestination;
	private Direction direction;

	public FloorRequest(Timestamp requestTime, long travelTime, long doorTime, int floorOrigin, int floorDestination,
			Direction direction) {
		this.requestTime = requestTime;
		this.direction = direction;
		this.floorDestination = floorDestination;
		this.floorRequestOrigin = floorOrigin;
		this.travelTime = travelTime;
		this.doorTime = doorTime;
	}

	public FloorRequest() {
		this.requestTime = new Timestamp(System.currentTimeMillis());
		this.direction = Direction.STOPPED;
		this.floorDestination = -1;
		this.floorRequestOrigin = -1;
		this.travelTime = -1L;
		this.doorTime = -1L;
	}

	public Timestamp getRequestTime() {
		return this.requestTime;
	}

	public long getTravelTime() {
		return this.travelTime;
	}

	public int getFloorRequestOrigin() {
		return this.floorRequestOrigin;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public int getFloorDestination() {
		return this.floorDestination;
	}

	public long getDoorTime() {
		return this.doorTime;
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//
//		FloorRequest f = (FloorRequest) obj;
//
//		return (f.requestTime == this.getRequestTime() &&
//				f.travelTime == this.travelTime &&
//				f.doorTime == this.doorTime &&
//				f.floorRequestOrigin == this.floorRequestOrigin &&
//				f.floorDestination == this.floorDestination &&
//				f.direction == this.direction);
//	}
}