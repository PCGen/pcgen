package pcgen.cdom.helper;

import java.util.List;

import pcgen.cdom.base.CDOMReference;
import pcgen.core.Equipment;
import pcgen.core.ShieldProf;

public class ShieldProfProvider extends AbstractProfProvider<ShieldProf>
{

	public ShieldProfProvider(List<CDOMReference<ShieldProf>> profs,
			List<CDOMReference<Equipment>> equipTypes)
	{
		super(profs, equipTypes);
	}

	@Override
	public boolean providesProficiencyFor(Equipment eq)
	{
		/*
		 * CONSIDER using providesEquipmentType might be optimized if references
		 * can contain late-created objects, dependent upon full resolution of
		 * Tracker 2001287 - thpr Oct 15, 2008
		 */
		return providesProficiency(eq.getShieldProf())
				|| providesEquipmentType(eq.getType());
	}
}
