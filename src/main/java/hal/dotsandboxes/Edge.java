package hal.dotsandboxes;

import static com.google.common.base.Preconditions.*;

public final class Edge{
	
	private final int mX, mY;
	
	private final Direction mDirection;
	
	private Edge(int x, int y, Direction direction) {
		mX = x;
		mY = y;
		mDirection = checkNotNull(direction);
	}
	
	public static Edge obtain(int x, int y, Direction direction) {
		return new Edge(x, y, direction);
	}
	
	public int getX() {
		return mX;
	}

	public int getY() {
		return mY;
	}

	public Direction getDirection() {
		return mDirection;
	}
	
	public int getCanX() {
		return mDirection == Direction.LEFT ? mX - 1 : mX;
	}

	public int getCanY() {
		return mDirection == Direction.ABOVE ? mY - 1 : mY;
	}

	public Direction getCanDirection() {
		switch(mDirection) {
			case LEFT: return Direction.RIGHT;
			case ABOVE: return Direction.BELOW;
			default: return mDirection;		
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + getCanDirection().hashCode();
		result = prime * result + getCanX();
		result = prime * result + getCanY();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Edge other = (Edge) obj;
		if (getCanDirection() != other.getCanDirection())
			return false;
		if (getCanX() != other.getCanX())
			return false;
		if (getCanY() != other.getCanY())
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return String.format("Edge(from: (%d, %d), direction: %s)", 
				mX, mY, mDirection);
	}
}
