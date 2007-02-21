package pcgen.core.utils;

import java.util.Map;
import java.util.Set;

import pcgen.util.DoubleKeyMap;

public class MapKeyMapToObject
{

	private final DoubleKeyMap<MapKey<?, ?>, Object, Object> dkm =
			new DoubleKeyMap<MapKey<?, ?>, Object, Object>();

	public <SK> boolean containsKey(MapKey<SK, ?> key1, SK key2)
	{
		return dkm.containsKey(key1, key2);
	}

	public boolean containsKey(MapKey<?, ?> key1)
	{
		return dkm.containsKey(key1);
	}

	public <SK, SV> SV get(MapKey<SK, SV> key1, SK key2)
	{
		return (SV) dkm.get(key1, key2);
	}

	public <SK, SV> SV put(MapKey<SK, SV> key1, SK key2, SV value)
	{
		return (SV) dkm.put(key1, key2, value);
	}

	/*
	 * TODO Need to fix the generics here...
	 */
	public <SK, SV> Map<Object, Object> removeAll(MapKey<SK, SV> key1)
	{
		return dkm.removeAll(key1);
	}

	public <SK, SV> SV remove(MapKey<SK, SV> key1, SK key2)
	{
		return (SV) dkm.remove(key1, key2);
	}

	public <SK> Set<SK> getKeySet(MapKey<SK, ?> key1)
	{
		return (Set<SK>) dkm.getSecondaryKeySet(key1);
	}
}
