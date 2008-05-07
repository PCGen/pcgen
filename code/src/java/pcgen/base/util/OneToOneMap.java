package pcgen.base.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OneToOneMap<K, V>
{
	private final HashMap<K, V> forwardMap = new HashMap<K, V>();
	private final HashMap<V, K> reverseMap = new HashMap<V, K>();

	public void clear()
	{
		forwardMap.clear();
		reverseMap.clear();
	}

	public boolean containsKey(Object key)
	{
		return forwardMap.containsKey(key);
	}

	public boolean containsValue(Object value)
	{
		return reverseMap.containsKey(value);
	}

	public V get(Object key)
	{
		return forwardMap.get(key);
	}

	public K getKeyFor(Object key)
	{
		return reverseMap.get(key);
	}

	public boolean isEmpty()
	{
		return forwardMap.isEmpty();
	}

	public Set<K> keySet()
	{
		return new HashSet<K>(forwardMap.keySet());
	}

	public V put(K key, V value)
	{
		reverseMap.put(value, key);
		return forwardMap.put(key, value);
	}

	public void putAll(Map<? extends K, ? extends V> m)
	{
		for (Map.Entry<? extends K, ? extends V> me : m.entrySet())
		{
			put(me.getKey(), me.getValue());
		}
	}

	public V remove(Object key)
	{
		V value = forwardMap.remove(key);
		reverseMap.remove(value);
		return value;
	}

	public int size()
	{
		return forwardMap.size();
	}

	public Collection<V> values()
	{
		return new HashSet<V>(reverseMap.keySet());
	}

	@Override
	public String toString()
	{
		return "OneToOneMap: " + forwardMap.toString();
	}

}
