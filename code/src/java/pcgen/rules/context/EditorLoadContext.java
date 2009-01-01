package pcgen.rules.context;


public class EditorLoadContext extends LoadContext
{
	private final String contextType;

	public EditorLoadContext()
	{
		super(new ReferenceContext(), new ListContext(), new ObjectContext());
		contextType = "Editor";
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
