/*
 * SelectionToken.java
 * Copyright 2008 (C) James Dempsey
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
 * Created on 4/10/2008 15:58:41
 *
 * $Id: $
 */


package plugin.lsttokens.kit.skill;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.core.kit.KitSkill;
import pcgen.persistence.lst.KitSkillLstToken;

/**
 * SELECTION token for KitSkill
 * 
 * Last Editor: $Author: $
 * Last Edited: $Date:  $
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision:  $
 */
public class SelectionToken implements KitSkillLstToken
{
	
	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	public String getTokenName()
	{
		return "SELECTION";
	}

	/**
	 * Parse the selection tag.
	 * 
	 * @param kitSkill The skill tag being modified
	 * @param value String The value of the tag.
	 * 
	 * @return boolean true if the value could be parsed. 
	 */
	public boolean parse(KitSkill kitSkill, String value)
	{
		List<String> langKeys = new ArrayList<String>();
		final StringTokenizer langToken =
				new StringTokenizer(value, Constants.COMMA);
		while (langToken.hasMoreTokens())
		{
			langKeys.add(langToken.nextToken());
		}

		kitSkill.setSelection(langKeys);
		return true;
	}
}
