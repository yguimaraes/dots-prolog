package hal.util;

/**
 * An immutable tuple of two values.
 * 
 * @author Hal Blackburn
 */
public final class Pair<A, B>
{
	private final A mFirst;
	
	private final B mSecond;
	
	private Pair(A first, B second)
	{
		mFirst = first;
		mSecond = second;
	}
	
	public static <A, B> Pair<A, B> of(A first, B second)
	{
		return new Pair<A, B>(first, second);
	}
	
	public A first()
	{
		return mFirst;
	}
	
	public B second()
	{
		return mSecond;
	}
	
	public Pair<A, B> setFirst(A first)
	{
		if(first == mFirst || (first != null && first.equals(mFirst)))
			return this;
		
		return new Pair<A, B>(first, mSecond);
	}
	
	public Pair<A, B> setSecond(B second)
	{
		if(second == mSecond || (second != null && second.equals(mSecond)))
			return this;
		
		return new Pair<A, B>(mFirst, second);
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mFirst == null) ? 0 : mFirst.hashCode());
		result = prime * result + ((mSecond == null) ? 0 : mSecond.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair<?, ?> other = (Pair<?, ?>) obj;
		if (mFirst == null)
		{
			if (other.mFirst != null)
				return false;
		} else if (!mFirst.equals(other.mFirst))
			return false;
		if (mSecond == null)
		{
			if (other.mSecond != null)
				return false;
		} else if (!mSecond.equals(other.mSecond))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return String.format("Pair(%s, %s)", mFirst, mSecond);
	}
}

