/*
 * SpellsToken.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 * Created on March 6, 2006
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */

package plugin.lsttokens.kit.spells;

import java.util.ArrayList;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.KnownSpellIdentifier;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.kit.KitSpells;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.util.Logging;

/**
 * SPELLS token for KitSpells
 */
public class SpellsToken extends AbstractToken implements
		CDOMSecondaryToken<KitSpells>
{
	private static final Class<Spell> SPELL_CLASS = Spell.class;
	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	/**
	 * Gets the name of the tag this class will parse.
	 * 
	 * @return Name of the tag this class handles
	 */
	@Override
	public String getTokenName()
	{
		return "SPELLS";
	}

	public Class<KitSpells> getTokenClass()
	{
		return KitSpells.class;
	}

	public String getParentToken()
	{
		return "*KITTOKEN";
	}

	public boolean parse(LoadContext context, KitSpells kitSpell, String value)
	{
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);
		while (aTok.hasMoreTokens())
		{
			String field = aTok.nextToken();
			if (field.startsWith("SPELLBOOK="))
			{
				if (kitSpell.getSpellBook() != null)
				{
					Logging.errorPrint("Cannot reset SPELLBOOK in SPELLS: "
						+ value);
					return false;
				}
				kitSpell.setSpellBook(field.substring(10));
			}
			else if (field.startsWith("CLASS="))
			{
				if (kitSpell.getCastingClass() != null)
				{
					Logging.errorPrint("Cannot reset CLASS" + " in SPELLS: "
						+ value);
					return false;
				}
				String className = field.substring(6);
				if (className.equalsIgnoreCase("Default"))
				{
					Logging
						.deprecationPrint("Use of Default for CLASS= in KIT "
							+ "SPELLS line is unnecessary: Ignoring");
				}
				else
				{
					kitSpell.setCastingClass(context.ref.getCDOMReference(
						PCClass.class, className));
				}
			}
			else
			{
				int count = 1;
				int equalLoc = field.indexOf("=");
				if (equalLoc != -1)
				{
					String countStr = field.substring(equalLoc + 1);
					try
					{
						count = Integer.parseInt(countStr);
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Expected an Integer COUNT,"
							+ " but found: " + countStr + " in " + value);
						return false;
					}
					field = field.substring(0, equalLoc);
				}
				StringTokenizer subTok = new StringTokenizer(field, "[]");
				String filterString = subTok.nextToken();

				// must satisfy all elements in a comma delimited list
				Integer levelLim = null;
				CDOMReference<Spell> sp = null;

				if (filterString.startsWith("LEVEL="))
				{
					// if the argument starts with LEVEL=, compare the level to
					// the desired spellLevel
					try
					{
						levelLim = Integer.valueOf(filterString.substring(6));
					}
					catch (NumberFormatException e)
					{
						Logging.errorPrint("Invalid Number in "
							+ getTokenName() + ": " + value);
						Logging.errorPrint("  Level must be an integer");
						return false;
					}
					sp = context.ref.getCDOMAllReference(SPELL_CLASS);
				}
				else
				{
					sp =
							TokenUtilities.getTypeOrPrimitive(context,
								SPELL_CLASS, filterString);
					if (sp == null)
					{
						Logging.errorPrint("  encountered Invalid limit in "
							+ getTokenName() + ": " + value);
						return false;
					}
				}

				KnownSpellIdentifier ksi =
						new KnownSpellIdentifier(sp, levelLim);

				ArrayList<CDOMSingleRef<Ability>> featList =
						new ArrayList<CDOMSingleRef<Ability>>();
				while (subTok.hasMoreTokens())
				{
					String featName = subTok.nextToken();
					CDOMSingleRef<Ability> feat =
							context.ref.getCDOMReference(ABILITY_CLASS,
								AbilityCategory.FEAT, featName);
					featList.add(feat);
				}
				kitSpell.addSpell(ksi, featList, count);
			}
		}
		if (kitSpell.getSpellBook() == null)
		{
			kitSpell.setSpellBook(Globals.getDefaultSpellBook());
		}
		return true;
	}

	public String[] unparse(LoadContext context, KitSpells kitSkill)
	{
		return null;
	}

	//TODO DeferredToken 
	/*
	 if (!aClass.getSafe(ObjectKey.MEMORIZE_SPELLS)
	 && !spellBook.equals(Globals.getDefaultSpellBook()))
	 {
	 warnings.add("SPELLS: " + aClass.getDisplayName()
	 + " can only add to " + Globals.getDefaultSpellBook());
	 return false;
	 }
	 */
}
