/*
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
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.core.EquipmentModifier;
import pcgen.core.PObject;
import pcgen.persistence.lst.ChooseLoader;
import pcgen.persistence.lst.GlobalLstToken;
import pcgen.util.Logging;

/**
 * @author djones4
 * 
 */
public class ChooseLst implements GlobalLstToken
{

	public String getTokenName()
	{
		return "CHOOSE";
	}

	public boolean parse(PObject obj, String value, int anInt)
	{
		if (obj instanceof EquipmentModifier)
		{
			return false;
		}
		String key;
		String val = value;
		int activeLoc = 0;
		String count = null;
		String maxCount = null;
		List<String> prefixList = new ArrayList<String>(2);
		while (true)
		{
			int pipeLoc = val.indexOf(Constants.PIPE, activeLoc);
			if (val.startsWith("FEAT="))
			{
				key = "FEAT";
				val = val.substring(5);
			}
			else if (pipeLoc == -1)
			{
				key = val;
				val = null;
			}
			else
			{
				key = val.substring(activeLoc, pipeLoc);
				val = val.substring(pipeLoc + 1);
			}
			if (key.startsWith("COUNT="))
			{
				if (count != null)
				{
					Logging
							.errorPrint("Cannot use COUNT more than once in CHOOSE: "
									+ value);
					return false;
				}
				prefixList.add(key);
				count = key.substring(6);
				if (count == null)
				{
					Logging.errorPrint("COUNT in CHOOSE must be a formula: "
							+ value);
					return false;
				}
				Logging
						.deprecationPrint("Support for COUNT= in CHOOSE"
								+ "is tenuous, at best, use the SELECT: token "
								+ value);
			}
			else if (key.startsWith("NUMCHOICES="))
			{
				if (maxCount != null)
				{
					Logging
							.errorPrint("Cannot use NUMCHOICES more than once in CHOOSE: "
									+ value);
					return false;
				}
				prefixList.add(key);
				maxCount = key.substring(11);
				if (maxCount == null || maxCount.length() == 0)
				{
					Logging
							.errorPrint("NUMCHOICES in CHOOSE must be a formula: "
									+ value);
					return false;
				}
			}
			else
			{
				break;
			}
		}
		String prefixString = StringUtil.join(prefixList, "|");
		return ChooseLoader.parseToken(obj, prefixString, key, val);
	}
}
