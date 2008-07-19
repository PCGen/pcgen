package pcgen.core;

import java.util.List;

public interface AssociationStore
{
	public void addAssociation(Object obj, Object o);

	public void removeAssociation(Object obj, Object o);

	public List<Object> removeAllAssociations(Object obj);

	public int getAssociationCount(Object obj);

	public boolean hasAssociations(Object obj);

	public List<Object> getAssociationList(Object obj);

	public boolean containsAssociated(Object obj, Object o);
}
