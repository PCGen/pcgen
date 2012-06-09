/*
 * TempBonusFacadeImpl.java
 * Copyright James Dempsey, 2012
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
 * Created on 08/06/2012 10:34:56 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.text.Collator;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.core.Ability;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Skill;
import pcgen.core.facade.TempBonusFacade;
import pcgen.core.spell.Spell;
import pcgen.system.LanguageBundle;

/**
 * The Class <code>TempBonusFacadeImpl</code> ...
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */

public class TempBonusFacadeImpl implements TempBonusFacade, Comparable<TempBonusFacadeImpl>
{
	
	private final CDOMObject originObj;

	TempBonusFacadeImpl(CDOMObject theOrigin)
	{
		this.originObj = theOrigin;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSource()
	{
		return SourceFormat.getFormattedString(getOriginObj(),
			Globals.getSourceDisplay(), true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getSourceForNodeDisplay()
	{
		return SourceFormat.getFormattedString(getOriginObj(),
			SourceFormat.LONG, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKeyName()
	{
		return getOriginObj().getKeyName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNamePI()
	{
    	return getOriginObj().getSafe(ObjectKey.NAME_PI);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOriginType()
	{
		if (getOriginObj() instanceof Ability)
		{
			return LanguageBundle.getString("in_Ability"); //$NON-NLS-1$
		}
		if (getOriginObj() instanceof Equipment)
		{
			return LanguageBundle.getString("in_igEqModelColItem"); //$NON-NLS-1$
		}
		if (getOriginObj() instanceof Spell)
		{
			return LanguageBundle.getString("in_Spell"); //$NON-NLS-1$
		}
		if (getOriginObj() instanceof PCClass || getOriginObj() instanceof PCClassLevel)
		{
			return LanguageBundle.getString("in_classString"); //$NON-NLS-1$
		}
		if (getOriginObj() instanceof PCTemplate)
		{
			return LanguageBundle.getString("in_template"); //$NON-NLS-1$
		}
		if (getOriginObj() instanceof Skill)
		{
			return LanguageBundle.getString("in_iskSkill"); //$NON-NLS-1$
		}
		return LanguageBundle.getString("in_itmRemBonButUnkownBonusType"); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(TempBonusFacadeImpl o)
	{
		final Collator collator = Collator.getInstance();

		// Check sort keys first
		String key1 = this.getOriginObj().get(StringKey.SORT_KEY);
		if (key1 == null)
		{
			key1 = this.getOriginObj().getDisplayName();
		}
		String key2 = o.getOriginObj().get(StringKey.SORT_KEY);
		if (key2 == null)
		{
			key2 = o.getOriginObj().getDisplayName();
		}
		if (!key1.equals(key2))
		{
			return collator.compare(key1, key2);
		}
		return collator.compare(this.getOriginObj().getDisplayName(),
			o.getOriginObj().getDisplayName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return getOriginObj().toString();
	}

	/**
	 * @return the object defining the temporary bonus. 
	 */
	public CDOMObject getOriginObj()
	{
		return originObj;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result =
				prime * result
					+ ((originObj == null) ? 0 : originObj.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		TempBonusFacadeImpl other = (TempBonusFacadeImpl) obj;
		if (originObj == null)
		{
			if (other.originObj != null)
			{
				return false;
			}
		}
		else if (!originObj.equals(other.originObj))
		{
			return false;
		}
		return true;
	}
}
