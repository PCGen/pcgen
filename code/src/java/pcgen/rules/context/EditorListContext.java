package pcgen.rules.context;

import pcgen.cdom.base.CDOMObject;

public class EditorListContext extends AbstractListContext
{

	private final TrackingListCommitStrategy commit = new TrackingListCommitStrategy();

	@Override
	protected ListCommitStrategy getCommitStrategy()
	{
		return commit;
	}

	public void purge(CDOMObject cdo)
	{
		commit.purge(cdo);
	}

}
