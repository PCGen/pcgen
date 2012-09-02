/*
 * Skill.java
 * Copyright 2001 (C) Bryan McRoberts <merton_monk@yahoo.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on April 21, 2001, 2:15 PM
 *
 * $Id$
 */
package pcgen.core;

import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.Type;
import pcgen.core.facade.SkillFacade;

/**
 * <code>Skill</code>.
 * 
 * @author Bryan McRoberts <merton_monk@users.sourceforge.net>
 * @version $Revision$
 */
public final class Skill extends PObject implements SkillFacade
{
	public String getKeyStatAbb()
	{
		PCStat keyStat = get(ObjectKey.KEY_STAT);
		return keyStat == null ? "" : keyStat.getAbb();
	}

	@Override
	public boolean equals(final Object obj)
	{
		return obj instanceof Skill
				&& getKeyName().equals(((Skill) obj).getKeyName())
				&& isCDOMEqual(((Skill) obj));
	}

	@Override
	public int hashCode()
	{
		return getKeyName().hashCode();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SkillFacade#getKeyStat()
	 */
	public String getKeyStat()
	{
		return getKeyStatAbb();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SkillFacade#isUntrained()
	 */
	public boolean isUntrained()
	{
		return getSafe(ObjectKey.USE_UNTRAINED);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.SkillFacade#getTypes()
	 */
	public String getDisplayType()
	{
		List<Type> trueTypeList = getTrueTypeList(true);
		return StringUtil.join(trueTypeList, ".");
	}

}
