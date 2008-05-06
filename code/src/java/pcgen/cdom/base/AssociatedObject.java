package pcgen.cdom.base;

import java.util.Collection;

import pcgen.cdom.enumeration.AssociationKey;

public interface AssociatedObject
{

	public <T> void setAssociation(AssociationKey<T> name, T value);

	public <T> T getAssociation(AssociationKey<T> name);

	public Collection<AssociationKey<?>> getAssociationKeys();

	public boolean hasAssociations();

}
