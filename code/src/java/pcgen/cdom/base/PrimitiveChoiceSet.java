package pcgen.cdom.base;

import java.util.Set;

import pcgen.core.PlayerCharacter;

public interface PrimitiveChoiceSet<T> extends LSTWriteable
{
	public Set<T> getSet(PlayerCharacter pc);

	public Class<? super T> getChoiceClass();
}