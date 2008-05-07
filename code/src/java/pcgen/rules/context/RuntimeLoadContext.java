package pcgen.rules.context;

public class RuntimeLoadContext extends LoadContext
{
	private final String contextType;

	public RuntimeLoadContext()
	{
		super(new ListContext(new ConsolidatedListCommitStrategy()),
			new ObjectContext(new ConsolidatedObjectCommitStrategy()));
		contextType = "Runtime";
	}

	/*
	 * Get the type of context we're running in (either Editor or Runtime)
	 */
	@Override
	public String getContextType()
	{
		return contextType;
	}

}
