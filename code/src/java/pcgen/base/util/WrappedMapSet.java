package pcgen.base.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class WrappedMapSet<T> extends AbstractSet<T> implements Set<T>
{
	private static final Object PRESENCE = new Object();

	private Map<T, Object> map;

	public <C extends Map> WrappedMapSet(Class<C> cl)
	{
		try
		{
			map = cl.newInstance();
		}
		catch (InstantiationException e)
		{
			throw new IllegalArgumentException(
					"Expected a Class passed to WrappedMapSet to "
							+ "have a zero argument constructor", e);
		}
		catch (IllegalAccessException e)
		{
			throw new IllegalArgumentException(
					"Expected a Class passed to WrappedMapSet to "
							+ "have a public, zero argument constructor", e);
		}
	}

	public <C extends Map> WrappedMapSet(Class<C> cl, Collection<? extends T> c)
	{
		this(cl);
		addAll(c);
	}

	@Override
	public Iterator<T> iterator()
	{
		return map.keySet().iterator();
	}

	@Override
	public int size()
	{
		return map.size();
	}

	@Override
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	@Override
	public boolean contains(Object o)
	{
		return map.containsKey(o);
	}

	@Override
	public boolean add(T o)
	{
		return map.put(o, PRESENCE) == null;
	}

	@Override
	public boolean remove(Object o)
	{
		return map.remove(o) == PRESENCE;
	}

	@Override
	public void clear()
	{
		map.clear();
	}
}
