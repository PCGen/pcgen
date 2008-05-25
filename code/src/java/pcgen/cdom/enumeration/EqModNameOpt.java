/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import java.util.List;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.Constants;
import pcgen.core.AssociatedChoice;
import pcgen.core.EquipmentModifier;
import pcgen.core.Globals;
import pcgen.core.SettingsHandler;
import pcgen.core.spell.Spell;
import pcgen.core.utils.CoreUtility;

public enum EqModNameOpt
{

	/*
	 * NORMAL: the name with a list of choices in parenthesis
	 * 
	 * NOTHING: a blank string
	 * 
	 * NOLIST: just the name of the object
	 * 
	 * NONAME: just the list of choices
	 * 
	 * SPELL: treats the first entry in associated as a spell, outputs the
	 * details
	 */
	NORMAL
	{
		@Override
		public String returnName(EquipmentModifier mod)
		{
			StringBuilder sb = new StringBuilder(100);
			sb.append(mod.getDisplayName());
			sb.append(" (");
			sb.append(mod.associatedList());
			sb.append(')');
			return sb.toString().trim().replace('|', ' ');
		}
	},
	NOLIST
	{
		@Override
		public String returnName(EquipmentModifier mod)
		{
			return mod.getDisplayName().toString().trim().replace('|', ' ');
		}
	},
	NONAME
	{
		@Override
		public String returnName(EquipmentModifier mod)
		{
			StringBuilder sb = new StringBuilder(100);
			sb.append(mod.getDisplayName());
			sb.append(mod.associatedList());
			return sb.toString().trim().replace('|', ' ');
		}
	},
	NOTHING
	{
		@Override
		public String returnName(EquipmentModifier mod)
		{
			return Constants.EMPTY_STRING;
		}
	},
	SPELL
	{
		@Override
		public String returnName(EquipmentModifier mod)
		{
			StringBuilder sb = new StringBuilder(100);
			boolean first = true;
			for (AssociatedChoice<String> a : mod.getAssociatedList())
			{
				if (!first)
				{
					sb.append(", ");
				}
				first = false;
				String listEntry = a.getDefaultChoice();

				String spellName = EquipmentModifier.getSpellInfoString(
						listEntry, "SPELLNAME");

				if (SettingsHandler.guiUsesOutputNameSpells())
				{
					final Spell aSpell = Globals.getSpellKeyed(spellName);

					if (aSpell != null)
					{
						spellName = aSpell.getOutputName();
					}
				}

				sb.append(spellName);

				final String info = EquipmentModifier.getSpellInfoString(
						listEntry, "VARIANT");

				if (info.length() != 0)
				{
					sb.append(" (").append(info).append(')');
				}
				
				String metaFeat = EquipmentModifier.getSpellInfoString(
						listEntry, "METAFEATS");
				List<String> metaFeats = CoreUtility.split(metaFeat, ',');
				
				if (!metaFeats.isEmpty())
				{
					sb.append('/').append(StringUtil.join(metaFeats, "/"));
				}

				sb.append('/').append(
						EquipmentModifier.getSpellInfoString(listEntry,
								"CASTER"));
				sb.append('/').append(
						CoreUtility.ordinal(EquipmentModifier.getSpellInfo(
								listEntry, "CASTERLEVEL")));
			}

			return sb.toString().trim().replace('|', ' ');
		}
	},
	TEXT
	{
		@Override
		public String returnName(EquipmentModifier mod)
		{
			return mod.get(StringKey.NAME_TEXT);
		}
	};

	public abstract String returnName(EquipmentModifier mod);
}
