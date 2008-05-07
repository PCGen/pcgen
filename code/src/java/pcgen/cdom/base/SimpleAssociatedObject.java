package pcgen.cdom.base;

import java.util.Collection;

import pcgen.cdom.enumeration.AssociationKey;

public class SimpleAssociatedObject extends ConcretePrereqObject implements
		AssociatedPrereqObject
{

	private final AssociationSupport assoc = new AssociationSupport();

	public <T> T getAssociation(AssociationKey<T> name)
	{
		return assoc.getAssociation(name);
	}

	public Collection<AssociationKey<?>> getAssociationKeys()
	{
		return assoc.getAssociationKeys();
	}

	public boolean hasAssociations()
	{
		return assoc.hasAssociations();
	}

	public <T> void setAssociation(AssociationKey<T> name, T value)
	{
		assoc.setAssociation(name, value);
	}

	@Override
	public int hashCode()
	{
		return assoc.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof SimpleAssociatedObject)
		{
			SimpleAssociatedObject other = (SimpleAssociatedObject) o;
			return assoc.equals(other.assoc) && equalsPrereqObject(other);
		}
		return false;
	}
}
