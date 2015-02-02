package pcgen.rules.context;

public class RuntimeListContext extends AbstractListContext
{

	private final ListCommitStrategy commit;

	public RuntimeListContext(ListCommitStrategy commitStrategy)
	{
		if (commitStrategy == null)
		{
			throw new IllegalArgumentException("Commit Strategy cannot be null");
		}
		commit = commitStrategy;
	}

	@Override
	protected ListCommitStrategy getCommitStrategy()
	{
		return commit;
	}

}
