package plugin.primitive.deity;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.PCAlignment;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class AlignToken implements PrimitiveToken<Deity>
{

	private static final Class<PCAlignment> ALIGNMENT_CLASS = PCAlignment.class;
	private static final Class<Deity> DEITY_CLASS = Deity.class;
	private PCAlignment ref;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		ref = context.ref.getAbbreviatedObject(ALIGNMENT_CLASS, value);
		return ref != null;
	}

	public String getTokenName()
	{
		return "ALIGN";
	}

	public Class<Deity> getReferenceClass()
	{
		return DEITY_CLASS;
	}

	public String getLSTformat()
	{
		return ref.getLSTformat();
	}

	public boolean allow(PlayerCharacter pc, Deity deity)
	{
		return ref.equals(deity.get(ObjectKey.ALIGNMENT));
	}

	public Set<Deity> getSet(PlayerCharacter pc)
	{
		HashSet<Deity> deitySet = new HashSet<Deity>();
		for (Deity deity : Globals.getContext().ref
				.getConstructedCDOMObjects(DEITY_CLASS))
		{
			if (ref.equals(deity.get(ObjectKey.ALIGNMENT)))
			{
				deitySet.add(deity);
			}
		}
		return deitySet;
	}

	public GroupingState getGroupingState()
	{
		return GroupingState.ANY;
	}

}
