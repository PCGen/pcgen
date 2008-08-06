package pcgen.cdom.base;

import pcgen.core.PlayerCharacter;

public interface ChoiceActor<T>
{
	public void applyChoice(T choice, PlayerCharacter pc);
}
