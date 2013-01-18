package pcgen.gui2.facade;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.RuleConstants;
import pcgen.core.SizeAdjustment;
import pcgen.core.utils.CoreUtility;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

public class CharacterUtils
{
	public static void selectClothes(final PlayerCharacter aPC)
	{
		if (Globals.checkRule(RuleConstants.FREECLOTHES)
			&& ((aPC.getDisplay().totalNonMonsterLevels()) == 1)) //$NON-NLS-1$
		{
			//
			// See what the PC is already carrying
			//
			List<Equipment> clothes =
					EquipmentList.getEquipmentOfType(
						"Clothing.Resizable", "Magic"); //$NON-NLS-1$ //$NON-NLS-2$

			//
			// Check to see if any of the clothing the PC
			// is carrying will actually fit and
			// has a zero price attached
			//
			boolean hasClothes = false;
			SizeAdjustment pcSizeAdj = aPC.getDisplay().getSizeAdjustment();

			if (clothes.size() != 0)
			{
				for (Equipment eq : clothes)
				{
					if ((CoreUtility.doublesEqual(
						eq.getCost(aPC).doubleValue(), 0.0))
						&& pcSizeAdj.equals(eq.getSafe(ObjectKey.SIZE)))
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
				clothes =
						EquipmentList.getEquipmentOfType(
							"Clothing.Resizable.Starting",
							"Magic.Custom.Auto_Gen");
				if (clothes.isEmpty())
				{
					clothes =
							EquipmentList.getEquipmentOfType(
								"Clothing.Resizable", "Magic.Custom.Auto_Gen");
				}

				List<Equipment> selectedClothes = new ArrayList<Equipment>();
				selectedClothes = Globals.getChoiceFromList(
					LanguageBundle.getString("in_sumSelectAFreeSetOfClothing"), //$NON-NLS-1$ 
					clothes, selectedClothes, 1, aPC);

				if (selectedClothes.size() != 0)
				{
					Equipment eq = selectedClothes.get(0);

					if (eq != null)
					{
						eq = eq.clone();
						eq.setQty(new Float(1));

						//
						// Need to resize to fit?
						//
						if (!pcSizeAdj.equals(eq.getSafe(ObjectKey.SIZE)))
						{
							eq.resizeItem(aPC, pcSizeAdj);
						}

						eq.setCostMod('-' + eq.getCost(aPC).toString()); // make cost 0

						if (aPC
							.getEquipmentNamed(eq.nameItemFromModifiers(aPC)) == null)
						{
							aPC.addEquipment(eq);
						}
						else
						{
							Logging
								.errorPrint("Cannot add duplicate equipment to PC"); //$NON-NLS-1$
						}
					}
				}
			}
		}

	}
}
