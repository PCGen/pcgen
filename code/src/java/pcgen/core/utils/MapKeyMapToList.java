package pcgen.core.utils;

import java.util.List;
import java.util.Set;

import pcgen.util.DoubleKeyMapToList;
import pcgen.util.MapToList;

public class MapKeyMapToList
{

	private final DoubleKeyMapToList<MapKey<?, ?>, Object, Object> dkm =
			new DoubleKeyMapToList<MapKey<?, ?>, Object, Object>();

	public <K, V> void addToListFor(MapKey<K, V> key1, K key2, V value)
	{
		dkm.addToListFor(key1, key2, value);
	}

	public <K, V> boolean containsInList(MapKey<K, V> key1, K key2, V value)
	{
		return dkm.containsInList(key1, key2, value);
	}

	public <K, V> boolean containsListFor(MapKey<K, V> key1, K key2)
	{
		return dkm.containsListFor(key1, key2);
	}

	public boolean containsListFor(MapKey<?, ?> key1)
	{
		return dkm.containsListFor(key1);
	}

	public int firstKeyCount()
	{
		return dkm.firstKeyCount();
	}

	public Set<MapKey<?, ?>> getKeySet()
	{
		return dkm.getKeySet();
	}

	public <K, V> List<V> getListFor(MapKey<K, V> key1, K key2)
	{
		return (List<V>) dkm.getListFor(key1, key2);
	}

	public <K> Set<K> getSecondaryKeySet(MapKey<K, ?> aPrimaryKey)
	{
		return (Set<K>) dkm.getSecondaryKeySet(aPrimaryKey);
	}

	public boolean isEmpty()
	{
		return dkm.isEmpty();
	}

	public <K, V> boolean removeFromListFor(MapKey<K, V> key1, K key2, V value)
	{
		return dkm.removeFromListFor(key1, key2, value);
	}

	public <K, V> List<V> removeListFor(MapKey<K, V> key1, K key2)
	{
		return (List<V>) dkm.removeListFor(key1, key2);
	}

	public <K, V> MapToList<K, V> removeListsFor(MapKey<K, V> key1)
	{
		return (MapToList<K, V>) dkm.removeListsFor(key1);
	}

	public <K> int sizeOfListFor(MapKey<K, ?> key1, K key2)
	{
		return dkm.sizeOfListFor(key1, key2);
	}

}
