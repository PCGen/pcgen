package pcgen.cdom.choiceset;

import java.util.HashSet;
import java.util.Set;

import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.PrereqHandler;

public class QualifiedDecorator<T extends PObject> implements
		PrimitiveChoiceSet<T>
{

	private final PrimitiveChoiceSet<T> set;

	public <U> QualifiedDecorator(PrimitiveChoiceSet<T> underlyingSet)
	{
		set = underlyingSet;
	}

	public Class<? super T> getChoiceClass()
	{
		return set.getChoiceClass();
	}

	public String getLSTformat()
	{
		return set.getLSTformat();
	}

	public Set<T> getSet(PlayerCharacter pc)
	{
		Set<T> returnSet = new HashSet<T>();
		for (T item : set.getSet(pc))
		{
			if (PrereqHandler.passesAll(item.getPrerequisiteList(), pc, item))
			{
				returnSet.add(item);
			}
		}
		return returnSet;
	}

}
