/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
 * Derived from PCClass.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SpellProhibitor;
import pcgen.core.SubClass;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.chooser.ChooserInterface;
import pcgen.util.enumeration.ProhibitedSpellType;

public class SubClassApplication
{

	public static void checkForSubClass(PlayerCharacter aPC, PCClass cl)
	{
		List<SubClass> subClassList = cl.getListFor(ListKey.SUB_CLASS);
		if (subClassList == null || subClassList.isEmpty())
		{
			return;
		}
	
		List<String> columnNames = new ArrayList<String>(3);
		columnNames.add("Name");
		columnNames.add("Cost");
		columnNames.add("Other");
	
		List<List> choiceList = new ArrayList<List>();
		String subClassKey = aPC.getSubClassName(cl);
		boolean subClassSelected = subClassKey != null
				&& !subClassKey.equals(Constants.NONE)
				&& !subClassKey.equals("");
	
		for (SubClass sc : subClassList)
		{
			if (!PrereqHandler.passesAll(sc.getPrerequisiteList(), aPC, cl))
			{
				continue;
			}
	
			final List<Object> columnList = new ArrayList<Object>(3);
	
			columnList.add(sc);
			columnList.add(Integer.toString(sc.getSafe(IntegerKey.COST)));
			columnList.add(SubClassApplication.getSupplementalDisplayInfo(sc));
	
			// If a subclass has already been selected, only add that one
			if (!subClassSelected
					|| sc.getKeyName().equals(
							aPC.getSubClassName(cl)))
			{
				choiceList.add(columnList);
			}
		}
	
		Collections.sort(choiceList, new Comparator<List>()
		{
			public int compare(List o1, List o2)
			{
				try
				{
					PCClass class1 = ((List<PCClass>) o1).get(0);
					PCClass class2 = ((List<PCClass>) o2).get(0);
					return class1.compareTo(class2);
				}
				catch (RuntimeException e)
				{
					return 0;
				}
			}
		});
	
		// add base class to the chooser at the TOP
		if (cl.getSafe(ObjectKey.ALLOWBASECLASS)
				&& (!subClassSelected || cl.getKeyName().equals(
						aPC.getSubClassName(cl))))
		{
			final List<Object> columnList2 = new ArrayList<Object>(3);
			columnList2.add(cl);
			columnList2.add("0");
			columnList2.add("");
			choiceList.add(0, columnList2);
		}
	
		/*
		 * REFACTOR This makes an assumption that SubClasses are ONLY Schools, which may
		 * not be a fabulous assumption
		 */
		final ChooserInterface c = ChooserFactory.getChooserInstance();
	
		c.setTitle("School Choice (Specialisation)");
		c
			.setMessageText("Make a selection.  The cost column indicates the cost of that selection. "
				+ "If this cost is non-zero, you will be asked to also "
				+ "select items from this list to give up to cover that cost.");
		c.setTotalChoicesAvail(1);
		c.setPoolFlag(false);
	
		// c.setCostColumnNumber(1); // Allow 1 choice, regardless of
		// cost...cost will be applied in second phase
		c.setAvailableColumnNames(columnNames);
		c.setAvailableList(choiceList);
	
		if (choiceList.size() == 1)
		{
			c.setSelectedList(choiceList);
		}
		else if (choiceList.size() != 0)
		{
			c.setVisible(true);
		}
	
		List<List<PCClass>> selectedList;
		if (!cl.getSafe(ObjectKey.ALLOWBASECLASS))
		{
			while (c.getSelectedList().size() == 0)
			{
				c.setVisible(true);
			}
			selectedList = c.getSelectedList();
	
		}
		else
		{
			selectedList = c.getSelectedList();
		}
	
		if (selectedList.size() == 0)
		{
			return;
		}
	
		List<PCClass> selectedRow = selectedList.get(0);
		if (selectedRow.size() == 0)
		{
			return;
		}
		PCClass subselected = selectedRow.get(0);
	
		if (!selectedList.isEmpty() && subselected instanceof SubClass)
		{
			aPC.removeProhibitedSchools(cl);
			/*
			 * CONSIDER What happens to this reset during PCClass/PCClassLevel split
			 */
			aPC.removeAssoc(cl, AssociationKey.SPECIALTY);
	
			SubClass sc = (SubClass) subselected;
	
			choiceList = new ArrayList<List>();
	
			for (SubClass sub : subClassList)
			{
				if (sub.equals(sc))
				{
					//Skip the selected specialist school
					continue;
				}
				if (!PrereqHandler.passesAll(sub.getPrerequisiteList(), aPC, cl))
				{
					continue;
				}
	
				final List<Object> columnList = new ArrayList<Object>(3);
	
				int displayedCost = sub.getProhibitCost();
				if (displayedCost == 0)
				{
					continue;
				}
	
				columnList.add(sub);
				columnList.add(Integer.toString(displayedCost));
				columnList.add(SubClassApplication.getSupplementalDisplayInfo(sub));
				columnList.add(sub.getChoice());
	
				choiceList.add(columnList);
			}
	
			setSubClassKey(aPC, cl, sc.getKeyName());
	
			if (sc.get(ObjectKey.CHOICE) != null)
			{
				aPC.setAssoc(cl, AssociationKey.SPECIALTY, sc.getChoice());
			}
	
			columnNames.add("Specialty");
	
			if (sc.getSafe(IntegerKey.COST) != 0)
			{
				final ChooserInterface c1 = ChooserFactory.getChooserInstance();
				c1.setTitle("School Choice (Prohibited)");
				c1.setAvailableColumnNames(columnNames);
				c1.setAvailableList(choiceList);
				c1
					.setMessageText("Make a selection.  You must make as many selections "
						+ "necessary to cover the cost of your previous selections.");
				c1.setTotalChoicesAvail(sc.getSafe(IntegerKey.COST));
				c1.setPoolFlag(true);
				c1.setCostColumnNumber(1);
				c1.setNegativeAllowed(true);
				c1.setVisible(true);
				selectedList = c1.getSelectedList();
	
				for (Iterator<List<PCClass>> i = selectedList.iterator(); i
					.hasNext();)
				{
					final List columns = i.next();
					sc = (SubClass) columns.get(0);
					SpellProhibitor prohibSchool = new SpellProhibitor();
					prohibSchool.setType(ProhibitedSpellType.SCHOOL);
					prohibSchool.addValue(sc.getChoice());
					SpellProhibitor prohibSubSchool = new SpellProhibitor();
					prohibSubSchool.setType(ProhibitedSpellType.SUBSCHOOL);
					prohibSubSchool.addValue(sc.getChoice());
					aPC.addProhibitedSchool(prohibSchool, cl);
					aPC.addProhibitedSchool(prohibSubSchool, cl);
				}
			}
		}
	}

	public static void setSubClassKey(PlayerCharacter pc, PCClass cl, final String aKey)
	{
		if (aKey == null || cl == null)
		{
			return;
		}
		
		pc.setSubClassName(cl, aKey);
	
		if (!aKey.equals(cl.getKeyName()))
		{
			final SubClass a = cl.getSubClassKeyed(aKey);
	
			if (a != null)
			{
				cl.inheritAttributesFrom(a);
				pc.reInheritClassLevels(cl);
			}
		}
	
		cl.getSpellLists(pc);
	}

	private static String getSupplementalDisplayInfo(SubClass sc) {
		boolean added = false;
		StringBuffer displayInfo = new StringBuffer();
		if (sc.getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY) != 0) {
			displayInfo.append("SPECIALTY SPELLS:").append(
					sc.getSafe(IntegerKey.KNOWN_SPELLS_FROM_SPECIALTY));
			added = true;
		}
	
		if (sc.getSpellBaseStat() != null) {
			if (added) {
				displayInfo.append(" ");
			}
			displayInfo.append("SPELL BASE STAT:").append(sc.getSpellBaseStat());
			added = true;
		}
	
		if (!added) {
			displayInfo.append(' ');
		}
		return displayInfo.toString();
	}

}
