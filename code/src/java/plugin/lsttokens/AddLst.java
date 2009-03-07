/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class AddLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{
	/*
	 * Template's LevelToken adjustment done in addAddsFromAllObjForLevel() in
	 * PlayerCharacter
	 */

	@Override
	public String getTokenName()
	{
		return "ADD";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			if (".CLEAR".equals(value))
			{
				if (obj instanceof PCClassLevel)
				{
					Logging
							.errorPrint("Warning: You performed an invalid .CLEAR in a ADD: Token");
					Logging
							.errorPrint("  A non-level limited .CLEAR was used in a Class Level line in "
									+ obj.getKeyName());
					return false;
				}
			}
			else if (value.startsWith(".CLEAR.LEVEL"))
			{
				if (!(obj instanceof PCClassLevel))
				{
					Logging
							.errorPrint("Warning: You performed an invalid .CLEAR in a ADD: Token");
					Logging.errorPrint("  A level limited .CLEAR ( " + value
							+ " ) was not used in a Class Level line in "
							+ obj.getClass().getSimpleName() + " "
							+ obj.getKeyName());
					return false;
				}
				String levelString = value.substring(12);
				try
				{
					int level = Integer.parseInt(levelString);
					if (level != obj.get(IntegerKey.LEVEL))
					{
						Logging
								.errorPrint("Warning: You performed an invalid .CLEAR in a ADD: Token");
						Logging.errorPrint("  A level limited .CLEAR ( "
								+ value + " ) was used in a Class Level line");
						Logging
								.errorPrint("  But was asked to clear a different Class Level ( "
										+ level
										+ " ) than the Class Level Line it appeared on: "
										+ obj.getKeyName());
						return false;
					}
				}
				catch (NumberFormatException e)
				{
					Logging
							.errorPrint("Warning: You performed an invalid .CLEAR in a ADD: Token");
					Logging.errorPrint("  A level limited .CLEAR ( " + value
							+ " ) was used in a Class Level line");
					Logging.errorPrint("  But the level ( " + levelString
							+ " ) was not an integer in: " + obj.getKeyName());
					return false;
				}
			}
			context.getObjectContext().removeList(obj, ListKey.ADD);
			return true;
		}
		return context.processSubToken(obj, getTokenName(), value.substring(0,
				pipeLoc), value.substring(pipeLoc + 1));
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		String[] unparsed = context.unparse(obj, getTokenName());
		Changes<PersistentTransitionChoice<?>> changes = context
				.getObjectContext().getListChanges(obj, ListKey.ADD);
		if (changes.includesGlobalClear())
		{
			String[] returnVal;
			if (unparsed == null)
			{
				returnVal = new String[1];
			}
			else
			{
				returnVal = new String[unparsed.length + 1];
				System.arraycopy(unparsed, 0, returnVal, 1, unparsed.length);
			}
			StringBuilder clearSB = new StringBuilder();
			clearSB.append(Constants.LST_DOT_CLEAR);
			if (obj instanceof PCClassLevel)
			{
				clearSB.append(".LEVEL");
				Integer lvl = obj.get(IntegerKey.LEVEL);
				clearSB.append(lvl);
			}
			returnVal[0] = clearSB.toString();
			unparsed = returnVal;
		}
		return unparsed;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
