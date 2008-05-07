package pcgen.cdom.reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pcgen.cdom.base.PrereqObject;
import pcgen.util.Logging;

public class AbstractReferenceManufacturer<T extends PrereqObject>
{
	private final Class<T> refClass;

	private Map<String[], CDOMTypeRef<T>> typeReferences = new HashMap<String[], CDOMTypeRef<T>>();

	private CDOMAllRef<T> allRef;
	
	public AbstractReferenceManufacturer(Class<T> cl)
	{
		refClass = cl;
	}

	public CDOMTypeRef<T> getTypeReference(String... types)
	{
		for (String type : types)
		{
			if (type.length() == 0)
			{
				Logging.errorPrint("Attempt to acquire empty Type "
						+ "(the type String contains an empty element)");
				return null;
			}
			if (type.indexOf('.') != -1)
			{
				throw new IllegalArgumentException(
						"Cannot build Reference with type conaining a period: "
								+ type);
			}
			if (type.indexOf('=') != -1)
			{
				throw new IllegalArgumentException(
						"Cannot build Reference with type conaining an equals: "
								+ type);
			}
			if (type.indexOf(',') != -1)
			{
				throw new IllegalArgumentException(
						"Cannot build Reference with type conaining a comma: "
								+ type);
			}
			if (type.indexOf('|') != -1)
			{
				throw new IllegalArgumentException(
						"Cannot build Reference with type conaining a pipe: "
								+ type);
			}
		}
		Arrays.sort(types);
		/*
		 * TODO FIXME This is the SLOW method - better to actually use Jakarta
		 * Commons Collections and create a map that does the lookup based on
		 * deepEquals of an Array...
		 */
		for (Entry<String[], CDOMTypeRef<T>> me : typeReferences.entrySet())
		{
			if (Arrays.deepEquals(me.getKey(), types))
			{
				return me.getValue();
			}
		}
		// Didn't find the appropriate key, create new
		CDOMTypeRef<T> cgr = new CDOMTypeRef<T>(refClass, types);
		typeReferences.put(types, cgr);
		return cgr;
	}

	public CDOMAllRef<T> getAllReference()
	{
		if (allRef == null)
		{
			allRef = new CDOMAllRef<T>(refClass);
		}
		return allRef;
	}

	public Class<T> getCDOMClass()
	{
		return refClass;
	}


}
