package pcgen.cdom.helper;

import pcgen.core.Equipment;
import pcgen.core.ShieldProf;

public class SimpleShieldProfProvider extends
		AbstractSimpleProfProvider<ShieldProf>
{

	public SimpleShieldProfProvider(ShieldProf proficiency)
	{
		super(proficiency);
	}

	public boolean providesProficiencyFor(Equipment eq)
	{
		return providesProficiency(eq.getShieldProf());
	}
}
