/*
 * LevelAbilityFeat.java
 * Copyright 2001 (C) Dmitry Jemerov
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
 *
 * Created on July 24, 2001, 10:11 PM
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.core.levelability;

import pcgen.core.Equipment;
import pcgen.core.EquipmentList;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.pclevelinfo.PCLevelInfo;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserInterface;

import java.util.Collections;
import java.util.List;

/**
 * Represents a equipment that a character gets when gaining a level
 * (an ADD:EQUIP entry in the LST file).
 *
 * @author Felipe Diniz <fdiniz@imagelink.com.br>
 * @version $Revision$
 */
final class LevelAbilityEquipment extends LevelAbility
{
	LevelAbilityEquipment(final PObject aowner, final int aLevel, final String aString)
	{
		super(aowner, aLevel, aString);
	}

	List<String> getChoicesList(final String bString, final PlayerCharacter aPC)
	{
		final List<String> aList = super.getChoicesList(bString.substring(6), aPC);
		Collections.sort(aList);

		return aList;
	}

	/**
	 * Performs the initial setup of a chooser.
	 * @param c
	 * @param aPC
	 * @return String
	 **/
	String prepareChooser(final ChooserInterface c, PlayerCharacter aPC)
	{
		super.prepareChooser(c, aPC);
		c.setTitle("Equipment Choice");

		return rawTagData;
	}

	/**
	 * Process the choice selected by the user.
	 * @param selectedList
	 * @param aPC
	 * @param pcLevelInfo
	 * @param aArrayList
	 */
	public boolean processChoice(final List<String> aArrayList, final List<String> selectedList, final PlayerCharacter aPC, final PCLevelInfo pcLevelInfo)
	{
		for ( String equipmentName : selectedList )
		{
			final Equipment aEquipment = EquipmentList.getEquipmentNamed(equipmentName);

			if (aEquipment == null)
			{
				Logging.errorPrint("LevelAbilityEquipment: Equipment not found: " + equipmentName);

				break;
			}

			final Equipment bEquipment = (Equipment) aEquipment.clone();
			bEquipment.setQty(1);
			aPC.addEquipment(bEquipment);
		}
		return true;
	}

	/**
	 * Processes a single token in the comma-separated list of the ADD:
	 * field and adds the choices to be shown in the list to anArrayList.
	 *
	 * @param aToken the token to be processed.
	 * @param anArrayList the list to add the choice to.
	 * @param aPC the PC this Level ability is adding to.
	 **/
	void processToken(
			final String          aToken,
			final List<String>            anArrayList,
			final PlayerCharacter aPC)
	{

		if (aToken.startsWith("TYPE=") || aToken.startsWith("TYPE."))
		{
			final String eqType = aToken.substring(5);
			List<Equipment> equip = EquipmentList.getEquipmentOfType( eqType, "" );
			for ( Equipment eq : equip )
			{
				anArrayList.add( eq.getDisplayName() );
			}
		}
		else
		{
			final String equipmentName = aToken;
			final Equipment aEquipment = EquipmentList.getEquipmentNamed(equipmentName);

			if (aEquipment == null)
			{
				Logging.errorPrint("LevelAbilityEquipment: Equipment not found: " + equipmentName);

				return;
			}

			if (!aPC.getEquipmentMasterList().contains(aEquipment))
			{
				if (PrereqHandler.passesAll(aEquipment.getPreReqList(), aPC, aEquipment))
				{
					anArrayList.add(aToken);
				}
			}
		}
	}
}
