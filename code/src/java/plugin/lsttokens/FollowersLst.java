/*
 * FollowersLst.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 * Copyright 2008 (C) Thomas Parker <thpr@users.sourceforge.net>
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
 * Current Ver: $Revision$
 * Last Editor: $Author: $
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.FollowerLimit;
import pcgen.cdom.list.CompanionList;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * This class implements support for the FOLLOWERS LST token.
 * <p />
 * <b>Tag Name</b>: <code>FOLLOWERS</code>:x|y<br />
 * <b>Variables Used (x)</b>: Text (The type of companion the limit will apply
 * to).<br />
 * <b>Variables Used (y)</b>: Number, variable or formula (Number of this type
 * of companion the master can have)
 * <p />
 * <b>What it does:</b><br/>
 * <ul>
 * <li>Limits the number of the specified type of companion the master can
 * have.</li>
 * <li>Optional, if this tag is not present no limits are placed on the number
 * of companions the character can have.</li>
 * <li>If more than one tag is encountered the highest value is used.</li>
 * <li>The value can be adjusted with the <code>BONUS:FOLLOWERS</code> tag</li>
 * </ul>
 * <b>Where it is used:</b><br />
 * Global tag, would most often be used in class and feat (ability) files,
 * should also be enabled for templates and Domains.
 * <p />
 * <b>Examples:</b><br />
 * <code>FOLLOWERS:Familiar|1</code><br />
 * A character is allowed only 1 companion of type Familiar
 * 
 * @author divaa01
 * 
 */
public class FollowersLst implements CDOMPrimaryToken<CDOMObject>
{
	/**
	 * 
	 * @return token name
	 */
	public String getTokenName()
	{
		return "FOLLOWERS"; //$NON-NLS-1$
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
							+ " has no PIPE character: Must be of the form <follower type>|<formula>");
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " has too many PIPE characters: "
					+ "Must be of the form <follower type>|<formula");
			return false;
		}

		String followerType = value.substring(0, pipeLoc);
		if (followerType.length() == 0)
		{
			Logging.log(Logging.LST_ERROR, "Follower Type in " + getTokenName()
					+ " cannot be empty");
			return false;
		}
		String followerNumber = value.substring(pipeLoc + 1);
		if (followerNumber.length() == 0)
		{
			Logging.log(Logging.LST_ERROR, "Follower Count in " + getTokenName()
					+ " cannot be empty");
			return false;
		}
		CDOMSingleRef<CompanionList> cl = context.ref.getCDOMReference(
				CompanionList.class, followerType);
		Formula num = FormulaFactory.getFormulaFor(followerNumber);
		context.getObjectContext().addToList(obj, ListKey.FOLLOWERS,
				new FollowerLimit(cl, num));
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<FollowerLimit> changes = context.getObjectContext()
				.getListChanges(obj, ListKey.FOLLOWERS);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		TreeSet<String> returnSet = new TreeSet<String>();
		for (FollowerLimit fl : changes.getAdded())
		{
			String followerType = fl.getCompanionList().getLSTformat();
			Formula followerNumber = fl.getValue();
			returnSet.add(followerType + Constants.PIPE
					+ followerNumber.toString());
		}
		return returnSet.toArray(new String[returnSet.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
