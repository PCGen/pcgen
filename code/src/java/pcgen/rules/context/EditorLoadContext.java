package pcgen.rules.context;

import pcgen.cdom.base.CDOMObject;


public class EditorLoadContext extends LoadContext
{
	private final String contextType;

	public EditorLoadContext()
	{
		super(new EditorReferenceContext(), new EditorListContext(),
				new EditorObjectContext());
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

	@Override
	public boolean consolidate()
	{
		return false;
	}

	public void purge(CDOMObject cdo)
	{
		((EditorObjectContext) obj).purge(cdo);
		((EditorListContext) list).purge(cdo);
		((EditorReferenceContext) ref).purge(cdo);
	}
}
