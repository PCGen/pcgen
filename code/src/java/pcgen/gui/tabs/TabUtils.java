package pcgen.gui.tabs;

import pcgen.core.EquipmentList;
import pcgen.util.Logging;
import pcgen.core.Equipment;
import pcgen.core.utils.CoreUtility;
import pcgen.util.PropertyFactory;
import java.util.ArrayList;
import java.util.List;
import pcgen.core.RuleConstants;
import java.util.Iterator;
import pcgen.core.PlayerCharacter;
import pcgen.core.Globals;

public class TabUtils
{
	public static void selectClothes(final PlayerCharacter aPC)
	{
		if (Globals.checkRule(RuleConstants.FREECLOTHES) && ((aPC.totalNonMonsterLevels()) == 1)) //$NON-NLS-1$
		{
			//
			// See what the PC is already carrying
			//
			List clothes = EquipmentList.getEquipmentOfType("Clothing.Resizable", "Magic"); //$NON-NLS-1$ //$NON-NLS-2$

			//
			// Check to see if any of the clothing the PC
			// is carrying will actually fit and
			// has a zero price attached
			//
			boolean hasClothes = false;
			final String pcSize = aPC.getSize();

			if (clothes.size() != 0)
			{
				for (Iterator e = clothes.iterator(); e.hasNext();)
				{
					final Equipment eq = (Equipment) e.next();

					if ((CoreUtility.doublesEqual(eq.getCost(aPC).doubleValue(), 0.0)) && pcSize.equals(eq.getSize()))
					{
						hasClothes = true;

						break;
					}
				}
			}

			//
			// If the PC has no clothing items, or none that
			// are sized to fit, then allow them to pick
			// a free set
			//
			if (!hasClothes)
			{
				clothes = EquipmentList.getEquipmentOfType("Clothing.Resizable.Starting", "Magic.Custom.Auto_Gen");
				if (clothes.isEmpty())
				{
					clothes = EquipmentList.getEquipmentOfType("Clothing.Resizable", "Magic.Custom.Auto_Gen");
				}

				List selectedClothes = new ArrayList();
				Globals.getChoiceFromList(PropertyFactory.getString("in_sumSelectAFreeSetOfClothing"), clothes, selectedClothes, 1); //$NON-NLS-1$

				if (selectedClothes.size() != 0)
				{
					Equipment eq = (Equipment)selectedClothes.get(0);

					if (eq != null)
					{
						eq = (Equipment) eq.clone();
						eq.setQty(new Float(1));

						//
						// Need to resize to fit?
						//
						if (!pcSize.equals(eq.getSize()))
						{
							eq.resizeItem(aPC, pcSize);
						}

						eq.setCostMod('-' + eq.getCost(aPC).toString()); // make cost 0

						if (aPC.getEquipmentNamed(eq.nameItemFromModifiers(aPC)) == null)
						{
							aPC.addEquipment(eq);
						}
						else
						{
							Logging.errorPrint("Cannot add duplicate equipment to PC"); //$NON-NLS-1$
						}
					}
				}
			}
		}

	}
}
