/*
 * Copyright (c) Thomas Parker, 2012.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.facet.input;

import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.PlayerCharacterTrackingFacet;
import pcgen.cdom.facet.base.AbstractScopeFacet;
import pcgen.cdom.facet.model.ClassFacet;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeEvent;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelChangeListener;
import pcgen.cdom.facet.model.ClassFacet.ClassLevelObjectChangeEvent;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;

/**
 * ClassSkillListFacet stores the ClassSkillListFacet choices for a
 * PCClass of a Player Character
 * 
 * @author Thomas Parker (thpr [at] yahoo.com)
 */
public class ClassSkillListFacet extends
		AbstractScopeFacet<PCClass, ClassSkillList> implements
		ClassLevelChangeListener
{
	private final PlayerCharacterTrackingFacet trackingFacet = FacetLibrary
		.getFacet(PlayerCharacterTrackingFacet.class);

	private ClassFacet classFacet;

	@Override
	public void levelChanged(ClassLevelChangeEvent lce)
	{
		if ((lce.getOldLevel() == 0) && (lce.getNewLevel() > 0))
		{
			PCClass cl = lce.getPCClass();
			CharID id = lce.getCharID();
			TransitionChoice<ClassSkillList> csc =
					cl.get(ObjectKey.SKILLLIST_CHOICE);
			if (csc != null)
			{
				removeAllFromSource(id, cl);
				PlayerCharacter pc = trackingFacet.getPC(id);
				for (ClassSkillList st : csc.driveChoice(pc))
				{
					add(id, cl, st, cl);
				}
			}
		}
		else if ((lce.getOldLevel() > 0) && (lce.getNewLevel() == 0))
		{
			removeAllFromSource(lce.getCharID(), lce.getPCClass());
		}
	}

	@Override
	public void levelObjectChanged(ClassLevelObjectChangeEvent lce)
	{
		//ignore
	}

	public void setClassFacet(ClassFacet classFacet)
	{
		this.classFacet = classFacet;
	}

	public void init()
	{
		classFacet.addLevelChangeListener(this);
	}
}
