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
package plugin.lsttokens.equipment;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.ArmorProf;
import pcgen.core.Equipment;
import pcgen.core.ShieldProf;
import pcgen.core.WeaponProf;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

/**
 * Deals with PROFICIENCY token
 */
public class ProficiencyToken extends AbstractToken implements
		CDOMPrimaryToken<Equipment>, DeferredToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "PROFICIENCY";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
	{
		if (isEmpty(value))
		{
			return false;
		}
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Equipment Token PROFICIENCY syntax "
							+ "without a Subtoken is invalid: " + value);
			return false;
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " expecting only one '|', "
					+ "format is: SubToken|ProfName value was: " + value);
			return false;
		}
		String subtoken = value.substring(0, pipeLoc);
		String prof = value.substring(pipeLoc + 1);
		if (prof == null || prof.length() == 0)
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"PROFICIENCY cannot have " + "empty second argument: "
							+ value);
			return false;
		}
		if (subtoken.equals("WEAPON"))
		{
			CDOMSingleRef<WeaponProf> wp = context.ref.getCDOMReference(
					WeaponProf.class, prof);
			context.getObjectContext().put(eq, ObjectKey.WEAPON_PROF, wp);
		}
		else if (subtoken.equals("ARMOR"))
		{
			CDOMSingleRef<ArmorProf> wp = context.ref.getCDOMReference(
					ArmorProf.class, prof);
			context.getObjectContext().put(eq, ObjectKey.ARMOR_PROF, wp);
		}
		else if (subtoken.equals("SHIELD"))
		{
			CDOMSingleRef<ShieldProf> wp = context.ref.getCDOMReference(
					ShieldProf.class, prof);
			context.getObjectContext().put(eq, ObjectKey.SHIELD_PROF, wp);
		}
		else
		{
			Logging.addParseMessage(Logging.LST_ERROR,
					"Unknown Subtoken for PROFICIENCY: " + subtoken);
			Logging.addParseMessage(Logging.LST_ERROR, "  Subtoken must be "
					+ "WEAPON, ARMOR or SHIELD");
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		CDOMSingleRef<WeaponProf> wp = context.getObjectContext().getObject(eq,
				ObjectKey.WEAPON_PROF);
		CDOMSingleRef<ShieldProf> sp = context.getObjectContext().getObject(eq,
				ObjectKey.SHIELD_PROF);
		CDOMSingleRef<ArmorProf> ap = context.getObjectContext().getObject(eq,
				ObjectKey.ARMOR_PROF);
		if (wp == null)
		{
			if (sp == null)
			{
				if (ap == null)
				{
					return null;
				}
				return new String[] { "ARMOR|" + ap.getLSTformat() };
			}
			else
			{
				if (ap == null)
				{
					return new String[] { "SHIELD|" + sp.getLSTformat() };
				}
				context.addWriteMessage("Equipment may not have both "
						+ "ARMOR and SHIELD Proficiencies");
				return null;
			}
		}
		if (sp == null)
		{
			if (ap == null)
			{
				return new String[] { "WEAPON|" + wp.getLSTformat() };
			}
			context.addWriteMessage("Equipment may not have both "
					+ "ARMOR and WEAPON Proficiencies");
			return null;
		}
		else
		{
			context.addWriteMessage("Equipment may not have both "
					+ "WEAPON and SHIELD Proficiencies");
			return null;
		}
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

	public Class<Equipment> getDeferredTokenClass()
	{
		return Equipment.class;
	}

	public boolean process(LoadContext context, Equipment eq)
	{
		CDOMSingleRef<WeaponProf> wp = eq.get(ObjectKey.WEAPON_PROF);
		if (eq.get(ObjectKey.SHIELD_PROF) != null)
		{
			if (eq.get(ObjectKey.ARMOR_PROF) != null)
			{
				Logging.errorPrint("Equipment " + eq.getKeyName()
						+ " may not have both "
						+ "ARMOR and SHIELD Proficiencies");
				return false;
			}
			if (wp != null)
			{
				Logging.errorPrint("Equipment " + eq.getKeyName()
						+ " may not have both "
						+ "WEAPON and SHIELD Proficiencies");
				return false;
			}
		}
		if (wp != null)
		{
			if (eq.get(ObjectKey.ARMOR_PROF) != null)
			{
				Logging.errorPrint("Equipment " + eq.getKeyName()
						+ " may not have both "
						+ "ARMOR and WEAPON Proficiencies");
				return false;
			}
		}
		return true;
	}
}
