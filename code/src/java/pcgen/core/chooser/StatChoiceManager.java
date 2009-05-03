/**
 * StatChoiceManager.java
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
 * Current Version: $Revision: 285 $
 * Last Editor:     $Author: nuance $
 * Last Edited:     $Date: 2006-03-17 15:19:49 +0000 (Fri, 17 Mar 2006) $
 *
 * Copyright 2006 Andrew Wilson <nuance@sourceforge.net>
 */
package pcgen.core.chooser;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.Globals;
import pcgen.core.PCStat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.ReferenceContext;

/**
 * This is the chooser that deals with choosing a stat.
 */
public class StatChoiceManager extends AbstractBasicStringChoiceManager {

	/**
	 * Make a new stat chooser.
	 *
	 * @param aPObject
	 * @param choiceString
	 * @param aPC
	 */
	public StatChoiceManager(
			PObject         aPObject,
			String          choiceString,
			PlayerCharacter aPC)
	{
		super(aPObject, choiceString, aPC);
		setTitle("Stat choice");
	}

	/**
	 * Parse the Choice string and build a list of available choices.
	 *
	 * @param aPc
	 * @param availableList
	 * @param selectedList
	 */
	@Override
	public void getChoices(
			final PlayerCharacter aPc,
			final List<String>            availableList,
			final List<String>            selectedList)
	{
		final List<PCStat>     excludeList = new ArrayList<PCStat>();

		ReferenceContext ref = Globals.getContext().ref;
		for ( String sExclude : getChoiceList() )
		{
			PCStat stat = ref.getAbbreviatedObject(PCStat.class,
					sExclude);

			if (stat != null)
			{
				excludeList.add(stat);
			}
		}

		for (PCStat stat : Globals.getContext().ref
				.getConstructedCDOMObjects(PCStat.class))
		{
			if (!excludeList.contains(stat))
			{
				availableList.add(stat.getAbb());
			}
		}

		selectedList.addAll(aPc.getAssociationList(pobject));
		setPreChooserChoices(selectedList.size());
	}

}
