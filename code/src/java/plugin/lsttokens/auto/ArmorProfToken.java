/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.auto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ConditionalChoiceActor;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.helper.ArmorProfProvider;
import pcgen.cdom.helper.SimpleArmorProfProvider;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

public class ArmorProfToken extends AbstractToken implements
		CDOMSecondaryToken<CDOMObject>, ChooseResultActor
{

	private static final Class<ArmorProf> ARMORPROF_CLASS = ArmorProf.class;

	private static final Class<Equipment> EQUIPMENT_CLASS = Equipment.class;

	public String getParentToken()
	{
		return "AUTO";
	}

	@Override
	public String getTokenName()
	{
		return "ARMORPROF";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		String weaponProfs;
		Prerequisite prereq = null; // Do not initialize, null is significant!

		// Note: May contain PRExxx
		if (value.indexOf("[") == -1)
		{
			weaponProfs = value;
		}
		else
		{
			int openBracketLoc = value.indexOf("[");
			weaponProfs = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				Logging.log(Logging.LST_ERROR, "Unresolved Prerequisite in "
						+ getFullName() + " " + value + " in " + getFullName());
				return false;
			}
			prereq = getPrerequisite(value.substring(openBracketLoc + 1, value
					.length() - 1));
			if (prereq == null)
			{
				Logging.log(Logging.LST_ERROR, "Error generating Prerequisite "
						+ prereq + " in " + getFullName());
				return false;
			}
		}

		if (hasIllegalSeparator('|', weaponProfs))
		{
			return false;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(weaponProfs, Constants.PIPE);

		List<CDOMReference<ArmorProf>> armorProfs = new ArrayList<CDOMReference<ArmorProf>>();
		List<CDOMReference<Equipment>> equipTypes = new ArrayList<CDOMReference<Equipment>>();

		while (tok.hasMoreTokens())
		{
			String aProf = tok.nextToken();

			if ("%LIST".equals(aProf))
			{
				ChooseResultActor cra;
				if (prereq == null)
				{
					cra = this;
				}
				else
				{
					ConditionalChoiceActor cca = new ConditionalChoiceActor(
							this);
					cca.addPrerequisite(prereq);
					cra = cca;
				}
				context.obj.addToList(obj, ListKey.CHOOSE_ACTOR, cra);
			}
			else if (Constants.LST_ALL.equalsIgnoreCase(aProf))
			{
				foundAny = true;
				armorProfs
						.add(context.ref.getCDOMAllReference(ARMORPROF_CLASS));
			}
			else if (aProf.startsWith(Constants.LST_ARMORTYPE_OLD)
					|| aProf.startsWith(Constants.LST_ARMORTYPE))
			{
				foundOther = true;
				CDOMReference<Equipment> ref = TokenUtilities.getTypeReference(
						context, EQUIPMENT_CLASS, "ARMOR."
								+ aProf.substring(10));
				if (ref == null)
				{
					return false;
				}
				equipTypes.add(ref);
			}
			else
			{
				foundOther = true;
				armorProfs.add(context.ref.getCDOMReference(ARMORPROF_CLASS,
						aProf));
			}
		}

		if (foundAny && foundOther)
		{
			Logging.log(Logging.LST_ERROR, "Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ArmorProfProvider pp = new ArmorProfProvider(armorProfs, equipTypes);
		if (prereq != null)
		{
			pp.addPrerequisite(prereq);
		}
		context.obj.addToList(obj, ListKey.AUTO_ARMORPROF, pp);

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<ArmorProfProvider> changes = context.obj.getListChanges(obj,
				ListKey.AUTO_ARMORPROF);
		Collection<ArmorProfProvider> added = changes.getAdded();
		// TODO remove not supported?
		if (added == null || added.isEmpty())
		{
			return null;
		}
		Set<String> set = new TreeSet<String>();
		for (ArmorProfProvider spp : added)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(spp.getLstFormat());
			if (spp.hasPrerequisites())
			{
				sb.append('[');
				sb.append(this.getPrerequisiteString(context, spp
						.getPrerequisiteList()));
				sb.append(']');
			}
			set.add(sb.toString());
		}
		return set.toArray(new String[set.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void apply(PlayerCharacter pc, CDOMObject obj, String o)
	{
		ArmorProf wp = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(ARMORPROF_CLASS, o);
		if (wp != null)
		{
			pc.addAssoc(obj, AssociationListKey.ARMORPROF,
					new SimpleArmorProfProvider(wp));
		}
	}

	public void remove(PlayerCharacter pc, CDOMObject obj, String o)
	{
		ArmorProf wp = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(ARMORPROF_CLASS, o);
		if (wp != null)
		{
			pc.removeAssoc(obj, AssociationListKey.ARMORPROF,
					new SimpleArmorProfProvider(wp));
		}
	}
}
