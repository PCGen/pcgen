package pcgen.core;

import java.util.List;

public interface AssociationStore
{
	public void addAssociation(PObject obj, String o);

	public void removeAssociation(PObject obj, String o);

	public List<String> removeAllAssociations(PObject obj);

	public boolean hasAssociations(PObject obj);

	public List<String> getAssociationList(PObject obj);

	public boolean containsAssociated(PObject obj, String o);

	public int getSelectCorrectedAssociationCount(PObject obj);

	public int getDetailedAssociationCount(PObject obj);

}
