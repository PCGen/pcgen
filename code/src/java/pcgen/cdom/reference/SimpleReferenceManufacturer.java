/**
 * 
 */
package pcgen.cdom.reference;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PCClass;

public class SimpleReferenceManufacturer<T extends CDOMObject> extends
		AbstractReferenceManufacturer<T> implements
		ReferenceManufacturer<T, CDOMSimpleSingleRef<T>>
{
	public SimpleReferenceManufacturer(Class<T> cl)
	{
		super(cl);
	}

	public CDOMSimpleSingleRef<T> getReference(String val)
	{
		// TODO Auto-generated method stub
		// TODO This is incorrect, but a hack for now :)
		if (val.equals(""))
		{
			throw new IllegalArgumentException(val);
		}
		try
		{
			Integer.parseInt(val);
			throw new IllegalArgumentException(val);
		}
		catch (NumberFormatException nfe)
		{
			// ok
		}
		if (val.startsWith("TYPE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equalsIgnoreCase("ANY"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.equalsIgnoreCase("ALL"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("PRE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("CHOOSE"))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("TIMES="))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("TIMEUNIT="))
		{
			throw new IllegalArgumentException(val);
		}
		if (val.startsWith("CASTERLEVEL="))
		{
			throw new IllegalArgumentException(val);
		}
		if (getCDOMClass().equals(PCClass.class))
		{
			if (val.startsWith("CLASS"))
			{
				throw new IllegalArgumentException(val);
			}
			else if (val.startsWith("SUB"))
			{
				throw new IllegalArgumentException(val);
			}
			else
			{
				try
				{
					Integer.parseInt(val);
					throw new IllegalArgumentException(val);
				}
				catch (NumberFormatException nfe)
				{
					// Want this!
				}
			}
		}

		return new CDOMSimpleSingleRef<T>(getCDOMClass(), val);
	}
}