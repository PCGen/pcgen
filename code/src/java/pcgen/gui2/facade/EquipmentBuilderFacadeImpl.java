/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.SizeAdjustment;
import pcgen.core.SpecialProperty;
import pcgen.core.analysis.EqModSpellInfo;
import pcgen.core.spell.Spell;
import pcgen.facade.core.AbilityFacade;
import pcgen.facade.core.EquipModFacade;
import pcgen.facade.core.EquipmentBuilderFacade;
import pcgen.facade.core.EquipmentFacade;
import pcgen.facade.core.InfoFacade;
import pcgen.facade.core.SizeAdjustmentFacade;
import pcgen.facade.core.UIDelegate;
import pcgen.facade.util.DefaultListFacade;
import pcgen.facade.util.DefaultReferenceFacade;
import pcgen.facade.util.ListFacade;
import pcgen.facade.util.ReferenceFacade;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.View;

/**
 * EquipmentBuilderFacadeImpl is an implementation of the 
 * {@link EquipmentBuilderFacade} interface for the new user interface. It is 
 * intended to allow the ui to control the creation of a custom item of 
 * equipment without direct interaction with the core.
 * 
 * 
 */
public class EquipmentBuilderFacadeImpl implements EquipmentBuilderFacade
{

	private final UIDelegate delegate;
	private final Equipment equip;
	private final Map<EquipmentHead, DefaultListFacade<EquipModFacade>> availListMap;
	private final Map<EquipmentHead, DefaultListFacade<EquipModFacade>> selectedListMap;
	private final PlayerCharacter character;
	private final Equipment baseEquipment;
	private final EnumSet<EquipmentHead> equipHeads;
	private final DefaultReferenceFacade<SizeAdjustmentFacade> sizeRef;

	/**
	 * Create a new EquipmentBuilderFacadeImpl instance for the customization of 
	 * a particular item of equipment for the character.
	 * @param equip The equipment item being customized (must not be the base item).
	 * @param character The character the equipment will be for.
	 * @param delegate The handler for UI functions such as dialogs.
	 */
	EquipmentBuilderFacadeImpl(Equipment equip, PlayerCharacter character, UIDelegate delegate)
	{
		this.equip = equip;
		this.character = character;
		this.delegate = delegate;

		sizeRef = new DefaultReferenceFacade<>(equip.getSizeAdjustment());

		final String sBaseKey = equip.getBaseItemKeyName();
		baseEquipment =
				Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(Equipment.class, sBaseKey);

		equipHeads = equip.isDouble() ? EnumSet.range(EquipmentHead.PRIMARY, EquipmentHead.SECONDARY)
			: EnumSet.of(EquipmentHead.PRIMARY);

		availListMap = new HashMap<>();
		selectedListMap = new HashMap<>();
		for (EquipmentHead head : equipHeads)
		{
			availListMap.put(head, new DefaultListFacade<>());
			DefaultListFacade<EquipModFacade> selectedList = new DefaultListFacade<>();
			selectedList.setContents(equip.getEqModifierList(head.isPrimary()));
			selectedListMap.put(head, selectedList);
		}
		refreshAvailList();
	}

	@Override
	public boolean addModToEquipment(EquipModFacade modifier, EquipmentHead head)
	{
		if (modifier == null || !(modifier instanceof EquipmentModifier) || head == null)
		{
			return false;
		}

		EquipmentModifier equipMod = (EquipmentModifier) modifier;

		// Trash the cost modifications
		equip.setCostMod("0");

		// Handle spells
		if (equipMod.getSafe(StringKey.CHOICE_STRING).startsWith("EQBUILDER.SPELL"))
		{
			if (!getSpellChoiceForEqMod(equipMod))
			{
				return false;
			}
		}

		equip.addEqModifier(equipMod, head.isPrimary(), character);

		if (equip.isDouble() && equipMod.getSafe(ObjectKey.ASSIGN_TO_ALL))
		{
			equip.addEqModifier(equipMod, !head.isPrimary(), character);
		}

		equip.nameItemFromModifiers(character);

		refreshAvailList();
		refreshSelectedList();

		return true;
	}

	@Override
	public boolean removeModFromEquipment(EquipModFacade modifier, EquipmentHead head)
	{
		if (modifier == null || !(modifier instanceof EquipmentModifier))
		{
			return false;
		}

		EquipmentModifier equipMod = (EquipmentModifier) modifier;

		if (baseEquipment.getEqModifierList(true).contains(equipMod))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getFormattedString("in_eqCust_RemoveBaseErr", equipMod));
			return false;
		}

		// Trash the cost modifications
		equip.setCostMod("0");

		equip.removeEqModifier(equipMod, head.isPrimary(), character);
		equip.nameItemFromModifiers(character);

		refreshAvailList();
		refreshSelectedList();

		return true;
	}

	@Override
	public boolean setName(String name)
	{
		if (StringUtils.isEmpty(name))
		{
			return false;
		}

		String aString = name.trim();

		if ((aString.indexOf('|') >= 0) || (aString.indexOf(':') >= 0) || (aString.indexOf(';') >= 0)
			|| (aString.indexOf(',') >= 0))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getString("in_eqCust_InvalidNameChar"));
			return false;
		}

		String oldName = "(" + equip.getItemNameFromModifiers() + ")";
		// Replace illegal characters in old name
		oldName = oldName.replaceAll(";:\\|,", "@");

		if (!oldName.toUpperCase().startsWith(Constants.GENERIC_ITEM))
		{
			equip.addToListFor(ListKey.SPECIAL_PROPERTIES, SpecialProperty.createFromLst(oldName));
		}
		equip.setName(aString);

		return true;
	}

	@Override
	public boolean setSProp(String sprop)
	{
		String aString = StringUtils.trimToEmpty(sprop);

		if ((aString.indexOf('|') >= 0) || (aString.indexOf(':') >= 0) || (aString.indexOf(';') >= 0))
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getString("in_eqCust_InvalidSpropChar"));
			return false;
		}

		equip.removeListFor(ListKey.SPECIAL_PROPERTIES);
		if (!aString.equals(""))
		{
			equip.addToListFor(ListKey.SPECIAL_PROPERTIES, SpecialProperty.createFromLst(aString));
		}

		return true;
	}

	@Override
	public boolean setCost(String newValue)
	{
		if (StringUtils.isEmpty(newValue))
		{
			return false;
		}

		String aString = newValue.trim();

		try
		{
			BigDecimal newCost = new BigDecimal(aString);

			if (newCost.doubleValue() < 0)
			{
				delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_eqCust_CostNegativeErr"));
				return false;
			}

			equip.setCostMod("0");
			equip.setCostMod(newCost.subtract(equip.getCost(character)));
			return true;
		}
		catch (Exception e)
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getString("in_eqCust_InvalidNumberErr"));
		}

		return false;
	}

	@Override
	public boolean setWeight(String newValue)
	{
		if (StringUtils.isEmpty(newValue))
		{
			return false;
		}

		String aString = newValue.trim();

		try
		{
			BigDecimal newWeight = new BigDecimal(aString);

			if (newWeight.doubleValue() < 0)
			{
				delegate.showErrorMessage(Constants.APPLICATION_NAME,
					LanguageBundle.getString("in_eqCust_WeightNegativeErr"));
				return false;
			}

			equip.put(ObjectKey.WEIGHT_MOD, BigDecimal.ZERO);
			equip.put(ObjectKey.WEIGHT_MOD, newWeight.subtract(new BigDecimal(equip.getWeightAsDouble(character))));
			return true;
		}
		catch (Exception e)
		{
			delegate.showErrorMessage(Constants.APPLICATION_NAME,
				LanguageBundle.getString("in_eqCust_InvalidNumberErr"));
		}

		return false;
	}

	@Override
	public boolean setDamage(String newValue)
	{
		if (StringUtils.isEmpty(newValue))
		{
			return false;
		}

		String aString = newValue.trim();

		equip.put(StringKey.DAMAGE_OVERRIDE, aString);
		return true;
	}

	@Override
	public ListFacade<EquipModFacade> getAvailList(EquipmentHead head)
	{
		return availListMap.get(head);
	}

	@Override
	public ListFacade<EquipModFacade> getSelectedList(EquipmentHead head)
	{
		return selectedListMap.get(head);
	}

	@Override
	public EquipmentFacade getEquipment()
	{
		return equip;
	}

	private void refreshAvailList()
	{
		List<String> aFilter = equip.typeList();

		for (EquipmentHead head : equipHeads)
		{
			List<EquipModFacade> newEqMods = new ArrayList<>();
			for (EquipmentModifier aEqMod : Globals.getContext().getReferenceContext()
				.getConstructedCDOMObjects(EquipmentModifier.class))
			{
				if (equip.isVisible(character, aEqMod, head.isPrimary(), View.VISIBLE_DISPLAY))
				{
					if (aEqMod.isType("ALL"))
					{
						newEqMods.add(aEqMod);
					}
					else
					{
						for (String aType : aFilter)
						{
							if (aEqMod.isType(aType))
							{
								newEqMods.add(aEqMod);
								break;
							}
						}
					}
				}
			}
			availListMap.get(head).updateContents(newEqMods);
		}
	}

	private void refreshSelectedList()
	{
		for (EquipmentHead eqHead : equipHeads)
		{
			selectedListMap.get(eqHead).updateContents(equip.getEqModifierList(eqHead.isPrimary()));
		}
	}

	@Override
	public boolean canAddModifier(EquipModFacade eqModFacade, EquipmentHead head)
	{
		if (!(eqModFacade instanceof EquipmentModifier))
		{
			return false;
		}

		EquipmentModifier eqMod = (EquipmentModifier) eqModFacade;

		return equip.canAddModifier(character, eqMod, head.isPrimary());
	}

	@Override
	public boolean isResizable()
	{
		return Globals.canResizeHaveEffect(equip, equip.typeList());
	}

	@Override
	public void setSize(SizeAdjustmentFacade newSize)
	{
		if (newSize == null || !(newSize instanceof SizeAdjustment))
		{
			return;
		}

		equip.resizeItem(character, (SizeAdjustment) newSize);
		equip.nameItemFromModifiers(character);
		sizeRef.set(newSize);
	}

	@Override
	public ReferenceFacade<SizeAdjustmentFacade> getSizeRef()
	{
		return sizeRef;
	}

	@Override
	public EnumSet<EquipmentHead> getEquipmentHeads()
	{
		return equipHeads;
	}

	private boolean getSpellChoiceForEqMod(EquipmentModifier eqMod)
	{
		String choiceValue = eqMod.getSafe(StringKey.CHOICE_STRING).substring(15);

		SpellBuilderFacadeImpl spellBuilderFI = new SpellBuilderFacadeImpl(choiceValue, character, equip);
		if (!delegate.showCustomSpellDialog(spellBuilderFI))
		{
			return false;
		}

		InfoFacade castingClass = spellBuilderFI.getClassRef().get();
		Spell theSpell = (Spell) spellBuilderFI.getSpellRef().get();
		String variant = spellBuilderFI.getVariantRef().get();
		if (variant == null)
		{
			variant = "";
		}
		String spellType = spellBuilderFI.getSpellTypeRef().get();
		int baseSpellLevel = spellBuilderFI.getSpellLevelRef().get();
		int casterLevel = spellBuilderFI.getCasterLevelRef().get();
		ListFacade<AbilityFacade> metamagicFeatsList = spellBuilderFI.getSelectedMetamagicFeats();
		Object[] metamagicFeats = new Object[metamagicFeatsList.getSize()];
		for (int i = 0; i < metamagicFeats.length; i++)
		{
			metamagicFeats[i] = metamagicFeatsList.getElementAt(i);
		}

		int charges = getNumCharges(eqMod);

		EquipmentModifier existingEqMod = equip.getEqModifierKeyed(eqMod.getKeyName(), true);
		if (existingEqMod == null)
		{
			equip.addEqModifier(eqMod, true, character);
		}
		existingEqMod = equip.getEqModifierKeyed(eqMod.getKeyName(), true);

		EqModSpellInfo.setSpellInfo(equip, existingEqMod, (PObject) castingClass, theSpell, variant, spellType,
			baseSpellLevel, casterLevel, metamagicFeats, charges);

		return true;
	}

	private int getNumCharges(EquipmentModifier eqMod)
	{
		int charges = -1;

		Integer min = eqMod.get(IntegerKey.MIN_CHARGES);
		if (min != null && min > 0)
		{
			Integer max = eqMod.get(IntegerKey.MAX_CHARGES);
			for (;;)
			{
				String selectedValue = delegate.showInputDialog(Constants.APPLICATION_NAME,
					LanguageBundle.getFormattedString("in_csdChargesMessage", min, max), Integer.toString(max));

				if (selectedValue != null)
				{
					try
					{
						final String aString = selectedValue.trim();
						charges = Integer.parseInt(aString);

						if (charges < min)
						{
							continue;
						}

						if (charges > max)
						{
							continue;
						}

						break;
					}
					catch (NumberFormatException exc)
					{
						// Request a new charges figure
					}
				}
			}
		}
		return charges;
	}

	@Override
	public String getBaseItemName()
	{
		return equip.getBaseItemName();
	}

	@Override
	public boolean isWeapon()
	{
		return equip.isWeapon();
	}

	@Override
	public String getDamage()
	{
		return equip.getDamage(character);
	}
}
