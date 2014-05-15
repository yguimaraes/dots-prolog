package hal.dotsandboxes;

import static com.google.common.base.Preconditions.*;

public final class DefaultEdge implements Edge {
	
	private final int mX, mY;
	
	private final Direction mDirection;
	
	private DefaultEdge(int x, int y, Direction direction) {
		mX = x;
		mY = y;
		mDirection = checkNotNull(direction);
	}
	
	public static DefaultEdge obtain(int x, int y, Direction direction) {
		return new DefaultEdge(x, y, direction);
	}
	
	@Override
	public int getX() {
		return mX;
	}

	@Override
	public int getY() {
		return mY;
	}

	@Override
	public Direction getDirection() {
		return mDirection;
	}
	
	@Override
	public int getCanX() {
		return mDirection == Direction.LEFT ? mX - 1 : mX;
	}

	@Override
	public int getCanY() {
		return mDirection == Direction.ABOVE ? mY - 1 : mY;
	}

	@Override
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
		DefaultEdge other = (DefaultEdge) obj;
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
