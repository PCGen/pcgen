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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.HashMapToList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.inst.PCClassLevel;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.Qualifier;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Equipment;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.Race;
import pcgen.core.Skill;
import pcgen.core.WeaponProf;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Deals with the QUALIFY token for Abilities
 */
public class QualifyToken extends AbstractTokenWithSeparator<CDOMObject> implements CDOMPrimaryToken<CDOMObject>
{

	@Override
	public String getTokenName()
	{
		return "QUALIFY";
	}

	public List<Class<? extends CDOMObject>> getLegalTypes()
	{
		return Arrays.asList(PCClassLevel.class, Ability.class, Deity.class, Domain.class, Equipment.class,
			PCClass.class, Race.class, Skill.class, Spell.class, PCTemplate.class, WeaponProf.class);
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		if (!getLegalTypes().contains(obj.getClass()))
		{
			return new ParseResult.Fail("Cannot use QUALIFY on a " + obj.getClass());
		}
		return super.parseNonEmptyToken(context, obj, value);
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
	{
		if (!value.contains(Constants.PIPE))
		{
			return new ParseResult.Fail(
				getTokenName() + " requires at least two arguments, QualifyType and Key: " + value);
		}
		StringTokenizer st = new StringTokenizer(value, Constants.PIPE);
		String firstToken = st.nextToken();
		ReferenceManufacturer<? extends Loadable> rm = context.getManufacturer(firstToken);
		if (rm == null)
		{
			return new ParseResult.Fail(getTokenName() + " unable to generate manufacturer for type: " + value);
		}

		while (st.hasMoreTokens())
		{
			CDOMSingleRef<? extends Loadable> ref = rm.getReference(st.nextToken());
			context.getObjectContext().addToList(obj, ListKey.QUALIFY, new Qualifier(ref));
		}

		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<Qualifier> changes = context.getObjectContext().getListChanges(obj, ListKey.QUALIFY);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		Collection<Qualifier> quals = changes.getAdded();
		HashMapToList<String, CDOMSingleRef<?>> map = new HashMapToList<>();
		for (Qualifier qual : quals)
		{
			CDOMSingleRef<?> ref = qual.getQualifiedReference();
			String key = ref.getPersistentFormat();
			map.addToListFor(key, ref);
		}
		Set<CDOMSingleRef<?>> set = new TreeSet<>(ReferenceUtilities.REFERENCE_SORTER);
		Set<String> returnSet = new TreeSet<>();
		for (String key : map.getKeySet())
		{
			set.clear();
			set.addAll(map.getListFor(key));
			returnSet.add(key + Constants.PIPE + ReferenceUtilities.joinLstFormat(set, Constants.PIPE));
		}
		return returnSet.toArray(new String[0]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
