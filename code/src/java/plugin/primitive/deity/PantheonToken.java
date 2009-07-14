package plugin.primitive.deity;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.enumeration.GroupingState;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.PrimitiveToken;

public class PantheonToken implements PrimitiveToken<Deity>
{

	private static final Class<Deity> DEITY_CLASS = Deity.class;
	
	private Pantheon pantheon;

	public boolean initialize(LoadContext context, String value, String args)
	{
		if (args != null)
		{
			return false;
		}
		pantheon = Pantheon.getConstant(value);
		return true;
	}

	public String getTokenName()
	{
		return "PANTHEON";
	}

	public Class<Deity> getReferenceClass()
	{
		return DEITY_CLASS;
	}

	public String getLSTformat()
	{
		return pantheon.toString();
	}

	public boolean allow(PlayerCharacter pc, Deity deity)
	{
		return deity.containsInList(ListKey.PANTHEON, pantheon);
	}

	public Set<Deity> getSet(PlayerCharacter pc)
	{
		HashSet<Deity> deitySet = new HashSet<Deity>();
		for (Deity deity : Globals.getContext().ref
				.getConstructedCDOMObjects(DEITY_CLASS))
		{
			if (deity.containsInList(ListKey.PANTHEON, pantheon))
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
