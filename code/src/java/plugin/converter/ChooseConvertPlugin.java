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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.gui2.converter.event.TokenProcessEvent;
import pcgen.gui2.converter.event.TokenProcessorPlugin;

public class ChooseConvertPlugin implements TokenProcessorPlugin
{
	public static Map<String, String> featAnswered = new HashMap<String, String>();
	public static Map<String, String> spelllistAnswered = new HashMap<String, String>();
	public static List<String> CHOICES = Arrays.asList(new String[] {
			"ABILITY", "ARMORPROFICIENCY", "CLASS", "DOMAIN", "EQUIPMENT",
			"FEAT", "LANG", "PCSTAT", "RACE", "SCHOOLS", "SHIELDPROFICIENCY",
			"SKILL", "SPELLS", "STRING", "TEMPLATE", "WEAPONPROFICIENCY" });

    @Override
	public String process(TokenProcessEvent tpe)
	{
		String value = tpe.getValue();
		if (value.startsWith("FEAT="))
		{
			processFeatEquals(tpe);
		}
		else if (value.startsWith("SPELLLIST|"))
		{
			processSpellList(tpe);
		}
		return null;
	}

	private void processFeatEquals(TokenProcessEvent tpe)
	{
		String value = tpe.getValue();
		String feat = value.substring(5);
		String decision = featAnswered.get(feat);
		if (decision == null)
		{
			decision =
					tpe.getDecider().getConversionDecision(
						"Need help with underlying type for "
							+ getProcessedToken() + ":" + value
							+ " which is used in " + tpe.getObjectName()
							+ " in file " + tpe.getPrimary().getSourceURI(),
						buildDescriptions(feat), CHOICES, CHOICES.size() - 1);
			featAnswered.put(feat, decision);
		}
		tpe.append(tpe.getKey());
		tpe.append(':');
		tpe.append(decision);
		tpe.append('|');
		tpe.append(value);
		tpe.consume();
	}

	private List<String> buildDescriptions(String feat)
	{
		List<String> list = new ArrayList<String>();
		list.add("Underlying Feat " + feat + " is CHOOSE:ABILITY");
		list.add("Underlying Feat " + feat + " is CHOOSE:ARMORPROFICIENCY");
		list.add("Underlying Feat " + feat + " is CHOOSE:CLASS");
		list.add("Underlying Feat " + feat + " is CHOOSE:DOMAIN");
		list.add("Underlying Feat " + feat + " is CHOOSE:EQUIPMENT");
		list.add("Underlying Feat " + feat + " is CHOOSE:FEAT");
		list.add("Underlying Feat " + feat + " is CHOOSE:LANG");
		list.add("Underlying Feat " + feat + " is CHOOSE:PCSTAT");
		list.add("Underlying Feat " + feat + " is CHOOSE:RACE");
		list.add("Underlying Feat " + feat + " is CHOOSE:SCHOOLS");
		list.add("Underlying Feat " + feat + " is CHOOSE:SHIELDPROFICIENCY");
		list.add("Underlying Feat " + feat + " is CHOOSE:SKILL");
		list.add("Underlying Feat " + feat + " is CHOOSE:SPELLS");
		list.add("Underlying Feat " + feat + " is CHOOSE:STRING");
		list.add("Underlying Feat " + feat + " is CHOOSE:TEMPLATE");
		list.add("Underlying Feat " + feat + " is CHOOSE:WEAPONPROFICIENCY");
		return list;
	}

	private void processSpellList(TokenProcessEvent tpe)
	{
		String decision = tpe.getDecider().getConversionInput(
				"Please provide class spell list which " + tpe.getObjectName()
						+ " modifies").trim();
		String stat = spelllistAnswered.get(decision);
		if (stat == null)
		{
			stat = tpe.getDecider().getConversionInput(
					"Please provide SPELLSTAT (abbreviation) for Class "
							+ decision).trim().toUpperCase();
			spelllistAnswered.put(decision, stat);
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
	}

    @Override
	public Class<? extends CDOMObject> getProcessedClass()
	{
		return CDOMObject.class;
	}

    @Override
	public String getProcessedToken()
	{
		return "CHOOSE";
	}
}
