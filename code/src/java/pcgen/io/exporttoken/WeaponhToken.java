/*
 * Copyright 2003 (C) Devon Jones <soulcatcher@evilsoft.org>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 */
package pcgen.io.exporttoken;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import pcgen.cdom.enumeration.EqModControl;
import pcgen.cdom.enumeration.IntegerKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.enumeration.Type;
import pcgen.cdom.inst.EquipmentHead;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SizeAdjustment;
import pcgen.core.WeaponProf;
import pcgen.core.character.WieldCategory;
import pcgen.io.ExportHandler;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * {@code WeaponhToken}.
 *
 */
public class WeaponhToken extends WeaponToken
{
	/** Weaponh token */
	private static final String TOKEN_NAME = "WEAPONH";

	@Override
	public String getTokenName()
	{
		return TOKEN_NAME;
	}

	/**
	 * Get the value of the supplied output token.
	 *
	 * @param tokenSource The full source of the token
	 * @param pc The character to retrieve the value for.
	 * @param eh The ExportHandler that is managing the export
	 * 						(may be null for a once off conversion).
	 * @return The value of the token.
	 */
	@Override
	public String getToken(String tokenSource, PlayerCharacter pc, ExportHandler eh)
	{
		StringTokenizer aTok = new StringTokenizer(tokenSource, ".", false);
		//Weapono Token
		aTok.nextToken();

		Equipment eq = getWeaponEquipment(pc);

        return getWeaponToken(pc, eq, aTok, tokenSource);
    }

	/**
	 * Create a fake Unarmed Strike equipment so we don't need it in the .lst files anymore
	 *
	 * @param pc The character used to generate the size.
	 * @return The Unarmed Strike equipment.
	 */
	private static Equipment getWeaponEquipment(PlayerCharacter pc)
	{
		// Creating a fake Unarmed Strike equipment so we
		// don't need it in the .lst files anymore
		WeaponProf wp = Globals.getContext().getReferenceContext().silentlyGetConstructedCDOMObject(WeaponProf.class,
			"Unarmed Strike");
		if (wp == null)
		{
			wp = new WeaponProf();
			wp.setName(LanguageBundle.getString("Equipment.UnarmedStrike"));
			wp.put(StringKey.KEY_NAME, "Unarmed Strike");
			wp.addToListFor(ListKey.TYPE, Type.SIMPLE);
			Globals.getContext().getReferenceContext().importObject(wp);
		}
		Equipment eq = new Equipment();
		eq.setName(LanguageBundle.getString("Equipment.UnarmedStrike"));
		eq.put(StringKey.KEY_NAME, "KEY_Unarmed Strike");
		eq.put(ObjectKey.WEAPON_PROF, new CDOMDirectSingleRef<>(wp));
		eq.put(StringKey.OUTPUT_NAME, LanguageBundle.getString("Equipment.UnarmedStrike"));
		eq.addType(Type.WEAPON);
		eq.addType(Type.MELEE);
		eq.addType(Type.SIMPLE);
		eq.addType(Type.UNARMED);
		eq.addType(Type.SUBDUAL);
		eq.addType(Type.STANDARD);
		eq.addType(Type.MONK);
		eq.addType(Type.BLUDGEONING);
		WieldCategory lightWC = Globals.getContext().getReferenceContext()
			.silentlyGetConstructedCDOMObject(WieldCategory.class, "Light");
		if (lightWC == null)
		{
			Logging.debugPrint("lightWC WieldCategory should not have been null?");
		}
		else
		{
			eq.put(ObjectKey.WIELD, lightWC);
		}
		eq.put(ObjectKey.COST, BigDecimal.ZERO);
		eq.put(ObjectKey.CURRENT_COST, BigDecimal.ZERO);
		eq.put(ObjectKey.WEIGHT, BigDecimal.ZERO);
		EquipmentHead head = eq.getEquipmentHead(1);
		head.put(StringKey.DAMAGE, "1d1");
		head.put(IntegerKey.CRIT_MULT, 2);
		head.put(IntegerKey.CRIT_RANGE, 1);
		eq.put(ObjectKey.MOD_CONTROL, EqModControl.NO);
		SizeAdjustment sa = pc.getSizeAdjustment();
		CDOMDirectSingleRef<SizeAdjustment> ref = CDOMDirectSingleRef.getRef(sa);
		eq.put(ObjectKey.SIZE, ref);
		eq.put(ObjectKey.BASESIZE, ref);

		return eq;
	}
}
