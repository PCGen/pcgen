/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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
import pcgen.core.chooser.CDOMChooserFacadeImpl;
import pcgen.core.prereq.PrereqHandler;
import pcgen.facade.core.ChooserFacade.ChooserTreeViewType;
import pcgen.gui2.facade.Gui2InfoFactory;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;
import pcgen.util.chooser.ChooserFactory;
import pcgen.util.enumeration.ProhibitedSpellType;

public final class SubClassApplication
{

	private SubClassApplication()
	{
	}

	public static void checkForSubClass(PlayerCharacter aPC, PCClass cl)
	{
		List<SubClass> subClassList = cl.getListFor(ListKey.SUB_CLASS);
		if (subClassList == null || subClassList.isEmpty())
		{
			return;
		}

		List<PCClass> availableList = new ArrayList<>();
		String subClassKey = aPC.getSubClassName(cl);
		boolean subClassSelected =
				subClassKey != null && !subClassKey.equals(Constants.NONE) && !subClassKey.equals("");

		for (SubClass sc : subClassList)
		{
			if (!PrereqHandler.passesAll(sc, aPC, cl))
			{
				continue;
			}

			// If a subclass has already been selected, only add that one
			if (!subClassSelected || sc.getKeyName().equals(aPC.getSubClassName(cl)))
			{
				availableList.add(sc);
			}
		}

		// add base class to the chooser
		if (cl.getSafe(ObjectKey.ALLOWBASECLASS)
			&& (!subClassSelected || cl.getKeyName().equals(aPC.getSubClassName(cl))))
		{
			availableList.add(0, cl);
		}

		/*
		 * REFACTOR This makes an assumption that SubClasses are ONLY Schools, which may
		 * not be a fabulous assumption
		 */
		List<PCClass> selectedSubClasses;
		CDOMChooserFacadeImpl<PCClass> chooserFacade =
			new CDOMChooserFacadeImpl<>(LanguageBundle.getString("in_schoolSpecChoice"), availableList, //$NON-NLS-1$
				new ArrayList<>(), 1);
		chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
		chooserFacade.setInfoFactory(new Gui2InfoFactory(aPC));

		if (availableList.size() == 1)
		{
			selectedSubClasses = availableList;
		}
		else if (availableList.isEmpty())
		{
			if (Logging.isLoggable(Logging.WARNING))
			{
				Logging.log(Logging.WARNING, "No subclass choices avaialble for " + cl);
			}
			return;
		}
		else
		{
			ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
			selectedSubClasses = chooserFacade.getFinalSelected();
		}

		if (!cl.getSafe(ObjectKey.ALLOWBASECLASS))
		{
			while (selectedSubClasses.isEmpty())
			{
				ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);
				selectedSubClasses = chooserFacade.getFinalSelected();
			}
		}

		if (selectedSubClasses.isEmpty())
		{
			return;
		}

		PCClass subselected = selectedSubClasses.get(0);

		if (subselected instanceof SubClass)
		{
			aPC.removeProhibitedSchools(cl);
			/*
			 * CONSIDER What happens to this reset during PCClass/PCClassLevel split
			 */
			aPC.removeAssoc(cl, AssociationKey.SPECIALTY);

			SubClass sc = (SubClass) subselected;

			availableList.clear();

			for (SubClass sub : subClassList)
			{
				if (sub.equals(sc))
				{
					//Skip the selected specialist school
					continue;
				}
				if (!PrereqHandler.passesAll(sub, aPC, cl))
				{
					continue;
				}

				int displayedCost = sub.getProhibitCost();
				if (displayedCost == 0)
				{
					continue;
				}

				availableList.add(sub);
			}

			setSubClassKey(aPC, cl, sc.getKeyName());

			if (sc.get(ObjectKey.CHOICE) != null)
			{
				aPC.setAssoc(cl, AssociationKey.SPECIALTY, sc.getChoice());
			}

			if (sc.getSafe(IntegerKey.COST) != 0)
			{
				chooserFacade =
						new CDOMChooserFacadeImpl<>(LanguageBundle.getString("in_schoolProhibitChoice"), //$NON-NLS-1$
								availableList, new ArrayList<>(), sc.getSafe(IntegerKey.COST));
				chooserFacade.setDefaultView(ChooserTreeViewType.NAME);
				chooserFacade.setInfoFactory(new Gui2InfoFactory(aPC));
				chooserFacade.setRequireCompleteSelection(true);
				ChooserFactory.getDelegate().showGeneralChooser(chooserFacade);

                for (PCClass choice : chooserFacade.getFinalSelected())
				{
					sc = (SubClass) choice;
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
	}

}
