package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.PlayerCharacter;

public abstract class AbstractSimpleProfProvider<T extends CDOMObject>
		implements ProfProvider<T>
{

	private final T prof;

	public AbstractSimpleProfProvider(T proficiency)
	{
		prof = proficiency;
	}

	public boolean providesProficiency(T sp)
	{
		return prof.equals(sp);
	}

	public boolean qualifies(PlayerCharacter playerCharacter)
	{
		return true;
	}

	public boolean providesEquipmentType(String typeString)
	{
		return false;
	}

	public String getLstFormat()
	{
		return prof.getKeyName();
	}
}
