package pcgen.cdom.helper;

import pcgen.core.ArmorProf;
import pcgen.core.Equipment;

public class SimpleArmorProfProvider extends
		AbstractSimpleProfProvider<ArmorProf>
{

	public SimpleArmorProfProvider(ArmorProf proficiency)
	{
		super(proficiency);
	}

	public boolean providesProficiencyFor(Equipment eq)
	{
		return providesProficiency(eq.getArmorProf());
	}

}
