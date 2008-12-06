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
package plugin.lsttokens.companionmod;

import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.SubClass;
import pcgen.core.character.CompanionMod;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.MapChanges;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;

/**
 * Class deals with FOLLOWER Token
 */
public class FollowerToken extends AbstractToken implements
		CDOMPrimaryToken<CompanionMod>
{

	private static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	private static final Class<SubClass> SUBCLASS_CLASS = SubClass.class;

	@Override
	public String getTokenName()
	{
		return "FOLLOWER";
	}

	public boolean parse(LoadContext context, CompanionMod cMod, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		int equalLoc = value.indexOf('=');
		if (equalLoc == -1)
		{
			return false;
		}
		if (equalLoc != value.lastIndexOf('='))
		{
			return false;
		}
		String classString = value.substring(0, equalLoc);
		String levelString = value.substring(equalLoc + 1);
		Integer lvl = Integer.valueOf(levelString);
		context.obj.put(cMod, IntegerKey.LEVEL, lvl);

		final StringTokenizer bTok = new StringTokenizer(classString, ",");

		while (bTok.hasMoreTokens())
		{
			String classKey = bTok.nextToken();
			PCClass pcClass =
					context.ref.silentlyGetConstructedCDOMObject(PCCLASS_CLASS,
						classKey);

			if (pcClass != null)
			{
				CDOMSingleRef<PCClass> pcc =
						Globals.getContext().ref.getCDOMReference(
							PCCLASS_CLASS, classKey);
				context.getObjectContext().put(cMod, MapKey.APPLIED_CLASS, pcc,
					lvl);
			}
			else
			{
				// Now we accept VARiable names here.
				context.getObjectContext().put(cMod, MapKey.APPLIED_VARIABLE,
					classKey, lvl);
			}
		}
		return true;
	}

	public String[] unparse(LoadContext context, CompanionMod cMod)
	{
		MapChanges<CDOMSingleRef<? extends PCClass>, Integer> changes =
				context.getObjectContext().getMapChanges(cMod,
					MapKey.APPLIED_CLASS);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		SortedSet<String> set = new TreeSet<String>();
		Map<CDOMSingleRef<? extends PCClass>, Integer> map = changes.getAdded();
		for (Map.Entry<CDOMSingleRef<? extends PCClass>, Integer> me : map
			.entrySet())
		{
			CDOMSingleRef<? extends PCClass> ref = me.getKey();
			Class<? extends PCClass> refClass = ref.getReferenceClass();
			if (SUBCLASS_CLASS.equals(refClass))
			{
				Category<SubClass> parent =
						((CategorizedCDOMReference<SubClass>) ref)
							.getCDOMCategory();
				set.add(parent.toString() + "." + ref.getLSTformat() + '='
					+ me.getValue());
			}
			else
			{
				set.add(ref.getLSTformat() + '=' + me.getValue());
			}
		}
		return new String[]{StringUtil.join(set, Constants.PIPE)};
	}

	public Class<CompanionMod> getTokenClass()
	{
		return CompanionMod.class;
	}

}
