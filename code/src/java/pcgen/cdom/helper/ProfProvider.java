package pcgen.cdom.helper;

import pcgen.cdom.base.CDOMObject;
import pcgen.core.Equipment;
import pcgen.core.PlayerCharacter;

public interface ProfProvider<T extends CDOMObject>
{
	public boolean providesProficiencyFor(Equipment eq);

	public boolean providesProficiency(T sp);

	public boolean qualifies(PlayerCharacter playerCharacter);

	public boolean providesEquipmentType(String typeString);

	public String getLstFormat();
}
