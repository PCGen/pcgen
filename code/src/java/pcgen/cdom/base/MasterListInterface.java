package pcgen.cdom.base;

import java.util.Collection;
import java.util.Set;

public interface MasterListInterface
{
	public <T extends CDOMObject> Set<CDOMReference> getActiveLists();

	public <T extends CDOMObject> Collection<AssociatedPrereqObject> getAssociations(
			CDOMReference<? extends CDOMList<T>> key1, T key2);

	public <T extends CDOMObject> Collection<AssociatedPrereqObject> getAssociations(
			CDOMList<T> key1, T key2);
}
