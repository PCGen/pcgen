package pcgen.cdom.content;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.PlayerCharacter;

public class ConditionalChoiceActor extends ConcretePrereqObject implements
		ChooseResultActor
{

	private final ChooseResultActor actor;

	public ConditionalChoiceActor(ChooseResultActor ca)
	{
		if (ca == null)
		{
			throw new IllegalArgumentException("Cannot have null ChoiceActor");
		}
		actor = ca;
	}

	public void apply(PlayerCharacter pc, CDOMObject obj, String o)
	{
		if (qualifies(pc))
		{
			actor.apply(pc, obj, o);
		}
	}

	public void remove(PlayerCharacter pc, CDOMObject obj, String o)
	{
		actor.remove(pc, obj, o);
	}

}
