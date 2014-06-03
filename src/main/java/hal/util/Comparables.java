package hal.util;

public final class Comparables {

	/**
	 * Returns the maximum of two {@link Comparable} objects.
	 * 
	 * @param a The first object.
	 * @param b The second object.
	 * @return The larger of the two objects.
	 */
	public static <T extends Comparable<? super T>> T max(T a, T b) {
		if(a.compareTo(b) < 0)
			return b;
		return a;
	}

	/**
	 * Returns the maximum of two {@link Comparable} objects, either or both of
	 * which may be null. In the case of one object being null, the other is 
	 * returned with no comparison performed.
	 * 
	 * @param a The first object.
	 * @param b The second object.
	 * @return The larger of the two objects, or null if both are null.
	 */
	public static <T extends Comparable<? super T>> T maxNull(T a, T b) {
		if(a == null)
			return b;
		if(b == null)
			return a;
		return max(a, b);
	}

	private Comparables() {
		throw new AssertionError();
	}
}