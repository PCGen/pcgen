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
package plugin.lsttokens.deprecated;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ComplexParseResult;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.token.PostDeferredToken;
import pcgen.util.Logging;

public class FeatEqToken implements CDOMSecondaryToken<CDOMObject>,
		PostDeferredToken<CDOMObject>
{

	public String getTokenName()
	{
		return "FEATEQ";
	}

	public String getParentToken()
	{
		return "CHOOSE";
	}

	public ParseResult parseToken(LoadContext context, CDOMObject obj,
		String value)
	{
		if (value == null)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " requires additional arguments", context);
		}
		if (value.indexOf(',') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not contain , : " + value, context);
		}
		if (value.indexOf('[') != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not contain [] : " + value, context);
		}
		if (value.charAt(0) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not start with | : " + value, context);
		}
		if (value.charAt(value.length() - 1) == '|')
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments may not end with | : " + value, context);
		}
		if (value.indexOf("||") != -1)
		{
			return new ParseResult.Fail("CHOOSE:" + getTokenName()
				+ " arguments uses double separator || : " + value, context);
		}
		Logging.deprecationPrint("CHOOSE:FEAT= has been deprecated, "
			+ "please use a CHOOSE of the "
			+ "appropriate type with the FEAT= primitive, "
			+ "e.g. CHOOSE:WEAPONPROFICIENCY|FEAT=Weapon Focus", context);
		ParseResult pr = ParseResult.SUCCESS;
		if (value.indexOf('|') != -1)
		{
			ComplexParseResult cpr = new ComplexParseResult();
			cpr.addWarningMessage("CHOOSE:" + getTokenName()
				+ " will ignore arguments: "
				+ value.substring(value.indexOf('|') + 1));
			pr = cpr;
		}
		context.obj.put(obj, ObjectKey.FEATEQ_STRING, context.ref
			.getCDOMReference(Ability.class, AbilityCategory.FEAT, value));
		return pr;
	}

	public String[] unparse(LoadContext context, CDOMObject cdo)
	{
		return null;
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public Class<CDOMObject> getDeferredTokenClass()
	{
		return CDOMObject.class;
	}

	public boolean process(LoadContext context, CDOMObject obj)
	{
		CDOMSingleRef<Ability> ref = obj.get(ObjectKey.FEATEQ_STRING);
		if (ref != null)
		{
			Ability ab = ref.resolvesTo();
			if (ab.get(ObjectKey.FEATEQ_STRING) != null)
			{
				process(context, ab);
			}
			ChooseInformation<?> info = ab.get(ObjectKey.CHOOSE_INFO);
			if (info == null)
			{
				Logging.errorPrint("Feat " + ref.getLSTformat(false)
					+ " was referred to in " + obj.getClass().getSimpleName()
					+ " " + obj.getKeyName()
					+ " but it was not a FEAT with CHOOSE");
				return false;
			}
			/*
			 * TODO This breaks for Abilities (no cat :( )
			 */
			context.unconditionallyProcess(obj, "CHOOSE", info.getName()
				+ "|FEAT=" + ref.getLSTformat(false));
			obj.remove(ObjectKey.FEATEQ_STRING);
		}
		return true;
	}

	public int getPriority()
	{
		return 0;
	}
}
