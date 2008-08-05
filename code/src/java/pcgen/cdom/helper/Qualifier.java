package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;

public class Qualifier
{

	private final Class<? extends CDOMObject> qualClass;
	private final CDOMReference<? extends CDOMObject> qualRef;

	public Qualifier(Class<? extends CDOMObject> cl,
		CDOMReference<? extends CDOMObject> ref)
	{
		if (cl == null)
		{
			throw new IllegalArgumentException("Class cannot be null");
		}
		if (ref == null)
		{
			throw new IllegalArgumentException("Reference cannot be null");
		}
		qualClass = cl;
		qualRef = ref;
	}

	public Class<? extends CDOMObject> getQualifiedClass()
	{
		return qualClass;
	}

	public CDOMReference<? extends CDOMObject> getQualifiedReference()
	{
		return qualRef;
	}

	@Override
	public int hashCode()
	{
		return qualClass.hashCode() * 29 + qualRef.hashCode();
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Qualifier)
		{
			Qualifier other = (Qualifier) o;
			return qualClass.equals(other.qualClass) && qualRef.equals(other.qualRef);
		}
		return false;
	}
	
}
