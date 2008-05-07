package pcgen.rules.context;

import java.util.Collection;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;

public interface AssociatedChanges<T>
{
	public boolean includesGlobalClear();

	public Collection<T> getAdded();

	public Collection<T> getRemoved();

	public MapToList<T, AssociatedPrereqObject> getAddedAssociations();

	public MapToList<T, AssociatedPrereqObject> getRemovedAssociations();

}
