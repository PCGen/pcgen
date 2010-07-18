/*
 * Copyright (c) 2010 Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.facet;

import java.util.Collection;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.MasterListInterface;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Globals;
import pcgen.core.Skill;

public class MasterSkillFacet
{

	private HashMapToList<ClassSkillList, Skill> hml;

	private void initialize()
	{
		hml = new HashMapToList<ClassSkillList, Skill>();
		MasterListInterface masterLists = Globals.getMasterLists();
		for (CDOMReference ref : masterLists.getActiveLists())
		{
			Collection objects = masterLists.getObjects(ref);
			for (Object cl : ref.getContainedObjects())
			{
				if (cl instanceof ClassSkillList)
				{
					hml.addAllToListFor((ClassSkillList) cl, objects);
				}
			}
		}
	}

	public boolean hasMasterSkill(ClassSkillList csl, Skill sk)
	{
		if (hml == null)
		{
			initialize();
		}
		return hml.containsInList(csl, sk);
	}
}
