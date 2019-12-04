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
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Deals with PROFICIENCY token
 */
public class ProficiencyToken extends AbstractNonEmptyToken<Equipment>
		implements CDOMPrimaryToken<Equipment>, DeferredToken<Equipment>
{

	@Override
	public String getTokenName()
	{
		return "PROFICIENCY";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Equipment eq, String value)
	{
		int pipeLoc = value.indexOf(Constants.PIPE);
		if (pipeLoc == -1)
		{
			return new ParseResult.Fail(
				"Equipment Token PROFICIENCY syntax " + "without a Subtoken is invalid: PROFICIENCY:" + value);
		}
		if (pipeLoc != value.lastIndexOf(Constants.PIPE))
		{
			return new ParseResult.Fail(
				getTokenName() + " expecting only one '|', " + "format is: SubToken|ProfName value was: " + value);
		}
		String subtoken = value.substring(0, pipeLoc);
		String prof = value.substring(pipeLoc + 1);
		if (prof == null || prof.isEmpty())
		{
			return new ParseResult.Fail("PROFICIENCY cannot have " + "empty second argument: " + value);
		}
        switch (subtoken) {
            case "WEAPON": {
                // This can be reactivated if .CLEAR is implemented, to allow .MOD to override the proficiency
                //			if (context.getObjectContext().getObject(eq, ObjectKey.WEAPON_PROF) != null)
                //			{
                //				return new ParseResult.Fail(
                //					"Only one PROFICIENCY:WEAPON is allowed per item. Token was PROFICIENCY:"
                //						+ value, context);
                //			}
                CDOMSingleRef<WeaponProf> wp = context.getReferenceContext().getCDOMReference(WeaponProf.class, prof);
                context.getObjectContext().put(eq, ObjectKey.WEAPON_PROF, wp);
                break;
            }
            case "ARMOR": {
                //			if (context.getObjectContext().getObject(eq, ObjectKey.ARMOR_PROF) != null)
                //			{
                //				return new ParseResult.Fail(
                //					"Only one PROFICIENCY:ARMOR is allowed per item. Token was PROFICIENCY:"
                //						+ value, context);
                //			}

                CDOMSingleRef<ArmorProf> wp = context.getReferenceContext().getCDOMReference(ArmorProf.class, prof);
                context.getObjectContext().put(eq, ObjectKey.ARMOR_PROF, wp);
                break;
            }
            case "SHIELD": {
                //			if (context.getObjectContext().getObject(eq, ObjectKey.SHIELD_PROF) != null)
                //			{
                //				return new ParseResult.Fail(
                //					"Only one PROFICIENCY:SHIELD is allowed per item. Token was PROFICIENCY:"
                //						+ value, context);
                //			}

                CDOMSingleRef<ShieldProf> wp = context.getReferenceContext().getCDOMReference(ShieldProf.class, prof);
                context.getObjectContext().put(eq, ObjectKey.SHIELD_PROF, wp);
                break;
            }
            default:
                ComplexParseResult cpr = new ComplexParseResult();
                cpr.addErrorMessage("Unknown Subtoken for PROFICIENCY: " + subtoken);
                cpr.addErrorMessage("  Subtoken must be " + "WEAPON, ARMOR or SHIELD");
                return cpr;
        }
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Equipment eq)
	{
		CDOMSingleRef<WeaponProf> wp = context.getObjectContext().getObject(eq, ObjectKey.WEAPON_PROF);
		CDOMSingleRef<ShieldProf> sp = context.getObjectContext().getObject(eq, ObjectKey.SHIELD_PROF);
		CDOMSingleRef<ArmorProf> ap = context.getObjectContext().getObject(eq, ObjectKey.ARMOR_PROF);
		if (wp == null)
		{
			if (sp == null)
			{
				if (ap == null)
				{
					return null;
				}
				return new String[]{"ARMOR|" + ap.getLSTformat(false)};
			}
			else
			{
				if (ap == null)
				{
					return new String[]{"SHIELD|" + sp.getLSTformat(false)};
				}
				context.addWriteMessage("Equipment may not have both " + "ARMOR and SHIELD Proficiencies");
				return null;
			}
		}
		if (sp == null)
		{
			if (ap == null)
			{
				return new String[]{"WEAPON|" + wp.getLSTformat(false)};
			}
			context.addWriteMessage("Equipment may not have both " + "ARMOR and WEAPON Proficiencies");
        }
		else
		{
			context.addWriteMessage("Equipment may not have both " + "WEAPON and SHIELD Proficiencies");
        }
        return null;
    }

	@Override
	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

	@Override
	public Class<Equipment> getDeferredTokenClass()
	{
		return Equipment.class;
	}

	@Override
	public boolean process(LoadContext context, Equipment eq)
	{
		CDOMSingleRef<WeaponProf> wp = eq.get(ObjectKey.WEAPON_PROF);
		if (eq.get(ObjectKey.SHIELD_PROF) != null)
		{
			if (eq.get(ObjectKey.ARMOR_PROF) != null)
			{
				Logging.errorPrint(
					"Equipment " + eq.getKeyName() + " may not have both " + "ARMOR and SHIELD Proficiencies");
				return false;
			}
			if (wp != null)
			{
				Logging.errorPrint(
					"Equipment " + eq.getKeyName() + " may not have both " + "WEAPON and SHIELD Proficiencies");
				return false;
			}
		}
		if (wp != null)
		{
			if (eq.get(ObjectKey.ARMOR_PROF) != null)
			{
				Logging.errorPrint(
					"Equipment " + eq.getKeyName() + " may not have both " + "ARMOR and WEAPON Proficiencies");
				return false;
			}
		}
		return true;
	}
}
