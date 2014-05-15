package hal.dotsandboxes;

import com.google.common.collect.ImmutableSet;

/**
 * Represents the 4 horizontal and vertical directions.
 * 
 * @author Hal
 */
public enum Direction {
	LEFT, ABOVE, RIGHT, BELOW;
	
	// Sets of the permutations of left, right, up, down
	private static final ImmutableSet<Direction>
		// 1 side
		DIRECTION_LEFT = ImmutableSet.of(LEFT),
		DIRECTION_UP = ImmutableSet.of(ABOVE),
		DIRECTION_RIGHT = ImmutableSet.of(RIGHT),
		DIRECTION_DOWN = ImmutableSet.of(RIGHT),
		
		// 2 sides
		DIRECTION_UP_RIGHT = ImmutableSet.of(ABOVE, RIGHT),
		DIRECTION_LEFT_RIGHT = ImmutableSet.of(LEFT, RIGHT),
		DIRECTION_UP_DOWN = ImmutableSet.of(ABOVE, BELOW),
		DIRECTION_DOWN_RIGHT = ImmutableSet.of(BELOW, RIGHT),
		DIRECTION_LEFT_DOWN = ImmutableSet.of(LEFT, BELOW),
		DIRECTION_LEFT_UP = ImmutableSet.of(LEFT, ABOVE),
		
		// 3 sides
		DIRECTION_UP_RIGHT_DOWN = ImmutableSet.of(ABOVE, RIGHT, BELOW),
		DIRECTION_LEFT_UP_RIGHT = ImmutableSet.of(LEFT, ABOVE, RIGHT),
		DIRECTION_DOWN_LEFT_UP = ImmutableSet.of(BELOW, LEFT, ABOVE, RIGHT),
		DIRECTION_LEFT_DOWN_RIGHT = ImmutableSet.of(LEFT, BELOW, RIGHT),
		
		// 4 sides
		DIRECTION_LEFT_UP_RIGHT_DOWN = ImmutableSet.of(LEFT, ABOVE, RIGHT, BELOW);
	
	/**
	 * Obtains a set of the directions specified.
	 * 
	 * @param left Whether to include the left direction.
	 * @param right Whether to include the right direction.
	 * @param up Whether to include the right direction.
	 * @param down Whether to include the down direction.
	 * @return 
	 */
	public static ImmutableSet<Direction> directions(boolean left, boolean right, 
			boolean up, boolean down){
		
		int total = (left ? 1:0) + (up ? 1:0) + (right ? 1:0) + (down ? 1:0);
		
		switch(total) {
		case 0:
			return ImmutableSet.of();
		case 1:
			return left  ? DIRECTION_LEFT :
				   right ? DIRECTION_RIGHT :
				   up    ? DIRECTION_UP :
				           DIRECTION_DOWN;
		case 2:
			return up & right   ? DIRECTION_UP_RIGHT :
				   left & right ? DIRECTION_LEFT_RIGHT :
				   up & down    ? DIRECTION_UP_DOWN :
				   down & right ? DIRECTION_DOWN_RIGHT :
				   left & down  ? DIRECTION_LEFT_DOWN :
								  DIRECTION_LEFT_UP;
		case 3:
			return !left  ? DIRECTION_UP_RIGHT_DOWN :
				   !down  ? DIRECTION_LEFT_UP_RIGHT :
				   !right ? DIRECTION_DOWN_LEFT_UP :
					        DIRECTION_LEFT_DOWN_RIGHT;
		case 4:
			return DIRECTION_LEFT_UP_RIGHT_DOWN;
		}
		
		throw new AssertionError("Codin' error ;D");
	}
}
