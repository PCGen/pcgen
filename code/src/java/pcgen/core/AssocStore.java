package pcgen.core;

import java.util.List;

public interface AssocStore
{
	public void addAssoc(Object obj, Object o);

	public void removeAssoc(Object obj, Object o);

	public List<Object> removeAllAssocs(Object obj);

	public int getAssocCount(Object obj);

	public boolean hasAssocs(Object obj);

	public List<Object> getAssocList(Object obj);

	public boolean containsAssoc(Object obj, Object o);
}
