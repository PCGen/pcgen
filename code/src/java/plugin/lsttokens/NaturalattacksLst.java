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

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.FormulaKey;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Equipment;
import pcgen.core.SizeAdjustment;
import pcgen.core.SpecialProperty;
import pcgen.core.WeaponProf;
import pcgen.core.bonus.Bonus;
import pcgen.core.bonus.BonusObj;
import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteUtilities;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.rules.persistence.token.PostDeferredToken;
import pcgen.util.Logging;

public class NaturalattacksLst extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>, PostDeferredToken<CDOMObject>
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;
	private static final int MIN_TOKEN_COUNT = 4;

	@Override
	public String getTokenName()
	{
		return "NATURALATTACKS"; //$NON-NLS-1$
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	/**
	 * NATURAL WEAPONS CODE <p>first natural weapon is primary, the rest are
	 * secondary; NATURALATTACKS:primary weapon name,weapon type,num
	 * attacks,damage|secondary1 weapon name,weapon type,num
	 * attacks,damage|secondary2 format is exactly as it would be in an
	 * equipment lst file Type is of the format Weapon.Natural.Melee.Bludgeoning
	 * number of attacks is the number of attacks with that weapon at BAB (for
	 * primary), or BAB - 5 (for secondary)
	 */
	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		// Currently, this isn't going to work with monk attacks
		// - their unarmed stuff won't be affected.

		/*
		 * This does not immediately resolve the Size, because it is an order of
		 * operations issue. This token must allow the SIZE token to appear
		 * AFTER this token in the LST file. Thus a deferred resolution (using a
		 * Resolver) is required.
		 */

		int count = 1;
		StringTokenizer attackTok = new StringTokenizer(value, Constants.PIPE);

		// This is wrong as we need to replace old natural weapons
		// with "better" ones

		while (attackTok.hasMoreTokens())
		{
			String tokString = attackTok.nextToken();
			ParseResult pr = checkForIllegalSeparator(',', tokString);
			if (!pr.passed())
			{
				return pr;
			}
			String wpn = tokString.intern();

			StringTokenizer commaTok = new StringTokenizer(wpn, Constants.COMMA);

			int numTokens = commaTok.countTokens();
			if (numTokens < MIN_TOKEN_COUNT)
			{
				return new ParseResult.Fail("Invalid Build of " + "Natural Weapon in " + getTokenName() + ": " + wpn);
			}

			String attackName = commaTok.nextToken();

			if (attackName.equalsIgnoreCase(Constants.LST_NONE))
			{
				return new ParseResult.Fail(
					"Attempt to Build 'None' as a " + "Natural Weapon in " + getTokenName() + ": " + wpn);
			}

			attackName = attackName.intern();
			Equipment naturalWeapon = new Equipment();
			naturalWeapon.setName(attackName);
			naturalWeapon.put(ObjectKey.PARENT, obj);
			/*
			 * This really can't be raw equipment... It really never needs to be
			 * referred to, but this means that duplicates are never being detected
			 * and resolved... this needs to have a KEY defined, to keep it
			 * unique... hopefully this is good enough :)
			 *
			 * CONSIDER This really isn't that great, because it's String dependent,
			 * and may not remove identical items... it certainly works, but is ugly
			 */
			// anEquip.setKeyName(obj.getClass().getSimpleName() + ","
			// + obj.getKeyName() + "," + wpn);
			/*
			 * Perhaps the construction above should be through context just to
			 * guarantee uniqueness of the key?? - that's too paranoid
			 */

			EquipmentHead equipHead = naturalWeapon.getEquipmentHead(1);

			String profType = commaTok.nextToken();
			pr = checkForIllegalSeparator('.', profType);
			if (!pr.passed())
			{
				return pr;
			}
			StringTokenizer dotTok = new StringTokenizer(profType, Constants.DOT);
			while (dotTok.hasMoreTokens())
			{
				Type type = Type.getConstant(dotTok.nextToken());
				naturalWeapon.addToListFor(ListKey.TYPE, type);
			}

			String numAttacks = commaTok.nextToken();
			boolean attacksFixed = !numAttacks.isEmpty() && numAttacks.charAt(0) == '*';
			if (attacksFixed)
			{
				numAttacks = numAttacks.substring(1);
			}
			naturalWeapon.put(ObjectKey.ATTACKS_PROGRESS, !attacksFixed);
			try
			{
				int bonusAttacks = Integer.parseInt(numAttacks) - 1;
				final BonusObj aBonus = Bonus.newBonus(context, "WEAPON|ATTACKS|" + bonusAttacks);

				if (aBonus == null)
				{
					return new ParseResult.Fail(
						getTokenName() + " was given invalid number of attacks: " + bonusAttacks);
				}
				naturalWeapon.addToListFor(ListKey.BONUS, aBonus);
			}
			catch (NumberFormatException exc)
			{
				return new ParseResult.Fail(
					"Non-numeric value for number of attacks in " + getTokenName() + ": '" + numAttacks + '\'');
			}

			equipHead.put(StringKey.DAMAGE, commaTok.nextToken());

			// sage_sam 02 Dec 2002 for Bug #586332
			// allow hands to be required to equip natural weapons
			int handsrequired = 0;
			while (commaTok.hasMoreTokens())
			{
				final String handsOrSpropString = commaTok.nextToken();
				if (handsOrSpropString.startsWith("SPROP="))
				{
					naturalWeapon.addToListFor(ListKey.SPECIAL_PROPERTIES,
						SpecialProperty.createFromLst(handsOrSpropString.substring(6)));
				}
				else
				{
					try
					{
						handsrequired = Integer.parseInt(handsOrSpropString);
					}
					catch (NumberFormatException exc)
					{
						return new ParseResult.Fail(
							"Non-numeric value for hands required: '" + handsOrSpropString + '\'');
					}
				}
			}
			naturalWeapon.put(IntegerKey.SLOTS, handsrequired);

			naturalWeapon.put(ObjectKey.WEIGHT, BigDecimal.ZERO);

			WeaponProf weaponProf =
					context.getReferenceContext().silentlyGetConstructedCDOMObject(WEAPONPROF_CLASS, attackName);
			if (weaponProf == null)
			{
				weaponProf = context.getReferenceContext().constructNowIfNecessary(WEAPONPROF_CLASS, attackName);
				weaponProf.addToListFor(ListKey.TYPE, Type.NATURAL);
			}
			CDOMSingleRef<WeaponProf> wp = context.getReferenceContext().getCDOMReference(WEAPONPROF_CLASS, attackName);
			naturalWeapon.put(ObjectKey.WEAPON_PROF, wp);
			naturalWeapon.addToListFor(ListKey.IMPLIED_WEAPONPROF, wp);

			if (!ControlUtilities.hasControlToken(context, CControl.CRITRANGE))
			{
				equipHead.put(IntegerKey.CRIT_RANGE, 1);
			}
			if (!ControlUtilities.hasControlToken(context, CControl.CRITMULT))
			{
				equipHead.put(IntegerKey.CRIT_MULT, 2);
			}

			if (count == 1)
			{
				naturalWeapon.setModifiedName("Natural/Primary");
			}
			else
			{
				naturalWeapon.setModifiedName("Natural/Secondary");
			}

			naturalWeapon.setOutputIndex(0);
			naturalWeapon.setOutputSubindex(count);
			// these values need to be locked.
			naturalWeapon.setQty(Float.valueOf(1));
			naturalWeapon.setNumberCarried(1.0f);

			context.getObjectContext().addToList(obj, ListKey.NATURAL_WEAPON, naturalWeapon);
			count++;
		}
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<Equipment> changes = context.getObjectContext().getListChanges(obj, ListKey.NATURAL_WEAPON);
		Collection<Equipment> eqadded = changes.getAdded();
		if (eqadded == null || eqadded.isEmpty())
		{
			return null;
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Equipment lstw : eqadded)
		{
			if (!first)
			{
				sb.append(Constants.PIPE);
			}
			Equipment eq = Equipment.class.cast(lstw);
			String name = eq.getDisplayName();
			// TODO objcontext.getString(eq, StringKey.NAME);
			if (name == null)
			{
				context.addWriteMessage(getTokenName() + " expected Equipment to have a name");
				return null;
			}
			sb.append(name).append(Constants.COMMA);
			List<Type> type = eq.getListFor(ListKey.TYPE);
			if (type == null || type.isEmpty())
			{
				context.addWriteMessage(getTokenName() + " expected Equipment to have a type");
				return null;
			}
			sb.append(StringUtil.join(type, Constants.DOT));
			sb.append(Constants.COMMA);
			Boolean attProgress = eq.get(ObjectKey.ATTACKS_PROGRESS);
			if (attProgress == null)
			{
				context.addWriteMessage(getTokenName() + " expected Equipment to know ATTACKS_PROGRESS state");
				return null;
			}
			else if (!attProgress)
			{
				sb.append(Constants.CHAR_ASTERISK);
			}
			List<BonusObj> bonuses = eq.getListFor(ListKey.BONUS);
			if (bonuses == null || bonuses.isEmpty())
			{
				sb.append('1');
			}
			else
			{
				if (bonuses.size() != 1)
				{
					context.addWriteMessage(getTokenName() + " expected only one BONUS on Equipment: " + bonuses);
					return null;
				}
				// TODO Validate BONUS type?
				BonusObj extraAttacks = bonuses.iterator().next();
				sb.append(Integer.parseInt(extraAttacks.getValue()) + 1);
			}
			sb.append(Constants.COMMA);
			EquipmentHead head = eq.getEquipmentHeadReference(1);
			if (head == null)
			{
				context.addWriteMessage(getTokenName() + " expected an EquipmentHead on Equipment");
				return null;
			}
			String damage = head.get(StringKey.DAMAGE);
			if (damage == null)
			{
				context.addWriteMessage(getTokenName() + " expected a Damage on EquipmentHead");
				return null;
			}
			sb.append(damage);

			Integer hands = eq.get(IntegerKey.SLOTS);
			if (hands != null && hands != 0)
			{
				sb.append(',').append(hands);
			}

			List<SpecialProperty> spropList = eq.getSafeListFor(ListKey.SPECIAL_PROPERTIES);
			for (SpecialProperty sprop : spropList)
			{
				sb.append(",SPROP=").append(sprop.toString());
			}

			first = false;
		}
		return new String[]{sb.toString()};
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public boolean process(LoadContext context, CDOMObject obj)
	{
		List<Equipment> natWeapons = obj.getListFor(ListKey.NATURAL_WEAPON);
		if (natWeapons != null)
		{
			Formula sizeFormula = obj.getSafe(FormulaKey.SIZE);
			// If the size was just a default, check for a size prereq and use that instead.
			if (obj.get(FormulaKey.SIZE) == null && obj.hasPreReqTypeOf("SIZE"))
			{
				Integer requiredSize = getRequiredSize(context, obj);
				if (requiredSize != null)
				{
					sizeFormula = FormulaFactory.getFormulaFor(requiredSize);
				}
			}

			if (sizeFormula.isStatic())
			{
				int isize = sizeFormula.resolveStatic().intValue();
				SizeAdjustment size = context.getReferenceContext()
					.getSortedList(SizeAdjustment.class, IntegerKey.SIZEORDER).get(isize);
				for (Equipment e : natWeapons)
				{
					CDOMDirectSingleRef<SizeAdjustment> sizeRef = CDOMDirectSingleRef.getRef(size);
					e.put(ObjectKey.BASESIZE, sizeRef);
					e.put(ObjectKey.SIZE, sizeRef);
				}
			}
			else
			{
				Logging.errorPrint("SIZE in " + obj.getClass().getSimpleName() + ' ' + obj.getKeyName()
					+ " must not be a variable " + "if it contains a NATURALATTACKS token");
			}
		}
		return true;
	}

	/**
	 * Retrieve the required size (i.e. PRESIZE) for the object defining the attack. Will
	 * only return a value if there is a single size. 
	 * @param obj The defining object. 
	 * @return The size integer, or null if none (or multiple) specified.
	 */
	private Integer getRequiredSize(LoadContext context, CDOMObject obj)
	{
		Set<Prerequisite> sizePrereqs = new HashSet<>();
		for (Prerequisite prereq : obj.getPrerequisiteList())
		{
			sizePrereqs.addAll(PrerequisiteUtilities.getPreReqsOfKind(prereq, "SIZE"));
		}

		Integer requiredSize = null;
		for (Prerequisite prereq : sizePrereqs)
		{
			SizeAdjustment sa = context.getReferenceContext().silentlyGetConstructedCDOMObject(SizeAdjustment.class,
				prereq.getOperand());
			final int targetSize = sa.get(IntegerKey.SIZEORDER);
			if (requiredSize != null && requiredSize != targetSize)
			{
				return null;
			}
			requiredSize = targetSize;
		}
		return requiredSize;
	}

	@Override
	public Class<CDOMObject> getDeferredTokenClass()
	{
		return getTokenClass();
	}

	@Override
	public int getPriority()
	{
		return 0;
	}

}
