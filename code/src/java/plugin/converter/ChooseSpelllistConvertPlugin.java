/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.converter;

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.gui.converter.event.TokenProcessEvent;
import pcgen.gui.converter.event.TokenProcessorPlugin;

public class ChooseSpelllistConvertPlugin implements TokenProcessorPlugin
{
	public static Map<String, String> answered = new HashMap<String, String>();

	public String process(TokenProcessEvent tpe)
	{
		String value = tpe.getValue();
		if (!value.startsWith("SPELLLIST|"))
		{
			// Don't consume
			return null;
		}
		String decision = tpe.getDecider().getConversionInput(
				"Please provide class spell list which " + tpe.getObjectName()
						+ " modifies").trim();
		String stat = answered.get(decision);
		if (stat == null)
		{
			stat = tpe.getDecider().getConversionInput(
					"Please provide SPELLSTAT (abbreviation) for Class "
							+ decision).trim().toUpperCase();
			answered.put(decision, stat);
		}
		tpe.append(tpe.getKey());
		tpe.append(":SPELLS|CLASSLIST=");
		tpe.append(decision);
		tpe.append("[KNOWN=YES]\tSELECT:");
		tpe.append(stat);
		tpe.append("\tPRECLASS:1,");
		tpe.append(decision);
		tpe.append("=1");
		tpe.consume();
		return null;
	}

	public Class<? extends CDOMObject> getProcessedClass()
	{
		return CDOMObject.class;
	}

	public String getProcessedToken()
	{
		return "CHOOSE";
	}
}
