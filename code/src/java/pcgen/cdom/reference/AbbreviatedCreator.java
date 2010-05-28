package pcgen.cdom.reference;

import pcgen.cdom.base.CDOMObject;
import pcgen.rules.context.LoadContext;

public class AbbreviatedCreator<T extends CDOMObject> implements
		SelectionCreator<T>
{
	private final SelectionCreator<T> creator;
	private final LoadContext context;

	public AbbreviatedCreator(LoadContext lc, SelectionCreator<T> sc)
	{
		creator = sc;
		context = lc;
	}

	public static <T extends CDOMObject> SelectionCreator<T> get(
			LoadContext context, SelectionCreator<T> sc)
	{
		return new AbbreviatedCreator<T>(context, sc);
	}

	public CDOMGroupRef<T> getAllReference()
	{
		return creator.getAllReference();
	}

	public CDOMSingleRef<T> getReference(String key)
	{
		T ao = context.ref.getAbbreviatedObject(creator.getReferenceClass(),
				key);
		if (ao == null)
		{
			return null;
		}
		return CDOMDirectSingleRef.getRef(ao);
	}

	public Class<T> getReferenceClass()
	{
		return creator.getReferenceClass();
	}

	public CDOMGroupRef<T> getTypeReference(String... types)
	{
		return creator.getTypeReference(types);
	}
}
