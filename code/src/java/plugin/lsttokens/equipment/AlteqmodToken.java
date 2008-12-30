/*
 * Copyright 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.equipment;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.helper.EqModRef;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Equipment;
import pcgen.core.EquipmentModifier;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.context.ObjectContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Deals with ALTEQMOD token
 */
public class AlteqmodToken extends AbstractToken implements
		CDOMPrimaryToken<Equipment>
{

	private static final Class<EquipmentModifier> EQMOD_CLASS = EquipmentModifier.class;

	private static final String EQMOD_WEIGHT = "_WEIGHTADD";

	private static final String EQMOD_DAMAGE = "_DAMAGE";

	@Override
	public String getTokenName()
	{
		return "ALTEQMOD";
	}

	public boolean parse(LoadContext context, Equipment eq, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('.', value))
		{
			return false;
		}

		StringTokenizer dotTok = new StringTokenizer(value, Constants.DOT);
		EquipmentHead head = eq.getEquipmentHead(2);
		while (dotTok.hasMoreTokens())
		{
			String modInfo = dotTok.nextToken();

			if (modInfo.equalsIgnoreCase(Constants.s_NONE))
			{
				Logging.deprecationPrint("'NONE' EqMod in " + getTokenName()
						+ " will be ignored");
				continue;
			}
			if (hasIllegalSeparator('|', modInfo))
			{
				return false;
			}
			StringTokenizer aTok = new StringTokenizer(modInfo, Constants.PIPE);

			// The type of EqMod, eg: ABILITYPLUS
			String eqModKey = aTok.nextToken();
			if (eqModKey.equals(EQMOD_WEIGHT))
			{
				if (aTok.hasMoreTokens())
				{
					context.obj.put(eq, ObjectKey.WEIGHT_MOD, new BigDecimal(
							aTok.nextToken().replace(',', '.')));
				}
				continue;
			}

			if (eqModKey.equals(EQMOD_DAMAGE))
			{
				if (aTok.hasMoreTokens())
				{
					context.obj.put(eq, StringKey.DAMAGE_OVERRIDE, aTok
							.nextToken());
				}
				continue;
			}
			CDOMSingleRef<EquipmentModifier> ref = context.ref
					.getCDOMReference(EQMOD_CLASS, eqModKey);
			EqModRef modref = new EqModRef(ref);

			while (aTok.hasMoreTokens())
			{
				modref.addChoice(aTok.nextToken().replace('=', '|'));
			}
			context.obj.addToList(head, ListKey.EQMOD_INFO, modref);
		}
		return true;
	}

	public String[] unparse(LoadContext context, Equipment eq)
	{
		ObjectContext obj = context.getObjectContext();
		String damage = obj.getString(eq, StringKey.DAMAGE_OVERRIDE);
		Set<String> set = new TreeSet<String>();
		if (damage != null)
		{
			set.add(EQMOD_DAMAGE + Constants.PIPE + damage);
		}
		BigDecimal weight = obj.getObject(eq, ObjectKey.WEIGHT_MOD);
		if (weight != null)
		{
			set.add(EQMOD_WEIGHT + Constants.PIPE
					+ weight.toString().replace('.', ','));
		}
		EquipmentHead head = eq.getEquipmentHeadReference(2);
		if (head != null)
		{
			Changes<EqModRef> changes = obj.getListChanges(head,
					ListKey.EQMOD_INFO);
			Collection<EqModRef> added = changes.getAdded();
			if (added != null)
			{
				for (EqModRef modRef : added)
				{
					String key = modRef.getRef().getLSTformat();
					StringBuilder sb = new StringBuilder();
					sb.append(key);
					for (String s : modRef.getChoices())
					{
						sb.append(Constants.PIPE);
						sb.append(s.replace('|', '='));
					}
					set.add(sb.toString());
				}
			}
		}
		if (set.isEmpty())
		{
			return null;
		}
		return new String[] { StringUtil.join(set, Constants.DOT) };
	}

	public Class<Equipment> getTokenClass()
	{
		return Equipment.class;
	}

}
