package pcgen.rules.context;

import java.net.URI;
import java.util.Collection;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;

public interface ListCommitStrategy
{

	public <T extends CDOMObject> AssociatedPrereqObject addToMasterList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<T>> list,
		T allowed);

	public Changes<CDOMReference> getMasterListChanges(
		String tokenName, CDOMObject owner, Class<? extends CDOMList<?>> cl);

	public boolean hasMasterLists();

	public <T extends CDOMObject> AssociatedChanges<T> getChangesInMasterList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl);

	public <T extends CDOMObject> AssociatedPrereqObject addToList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<? super T>> list, CDOMReference<T> allowed);

	public Collection<CDOMReference<? extends CDOMList<? extends CDOMObject>>> getChangedLists(
		CDOMObject owner, Class<? extends CDOMList<?>> cl);

	public void removeAllFromList(String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<?>> swl);

	public <T extends CDOMObject> void removeFromList(String tokenName,
		CDOMObject owner, CDOMReference<? extends CDOMList<? super T>> swl,
		CDOMReference<T> ref);

	public <T extends CDOMObject> AssociatedChanges<CDOMReference<T>> getChangesInList(
		String tokenName, CDOMObject owner,
		CDOMReference<? extends CDOMList<T>> swl);

	public void setSourceURI(URI sourceURI);

	public void setExtractURI(URI sourceURI);

	public void clearAllMasterLists(String tokenName, CDOMObject owner);
}
