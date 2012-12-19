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
import java.util.List;

import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.rules.context.ReferenceContext;

public class ClassSkillApplication
{

	public static void chooseClassSkillList(PlayerCharacter pc, PCClass cl)
	{
		TransitionChoice<ClassSkillList> csc = cl.get(ObjectKey.SKILLLIST_CHOICE);
		// if no entry or no choices, just return
		if (csc == null || (pc.getLevel(cl) < 1))
		{
			return;
		}
	
		pc.removeAllAssocs(cl, AssociationListKey.CLASSSKILLLIST);
		for (ClassSkillList st : csc.driveChoice(pc))
		{
			pc.addAssoc(cl, AssociationListKey.CLASSSKILLLIST, st);
		}
	}

	public static final List<ClassSkillList> getClassSkillList(PlayerCharacter pc, PCClass cl)
	{
		List<ClassSkillList> classSkillList = pc.getAssocList(cl, AssociationListKey.CLASSSKILLLIST);
		if (classSkillList == null)
		{
			List<ClassSkillList> returnList = new ArrayList<ClassSkillList>(2);
			ReferenceContext ref = Globals.getContext().ref;
			Class<ClassSkillList> csl = ClassSkillList.class;
			ClassSkillList l = ref.silentlyGetConstructedCDOMObject(csl, cl.getKeyName());
			if (l != null)
			{
				returnList.add(l);
			}
			String subClassKey = pc.getSubClassName(cl);
			if (subClassKey != null)
			{
				l = ref.silentlyGetConstructedCDOMObject(csl, subClassKey);
				if (l != null)
				{
					returnList.add(l);
				}
			}
			return returnList;
		}
		else
		{
			return classSkillList;
		}
	}

}
