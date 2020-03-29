/*
 * Missing License Header, Copyright 2016 (C) Andrew Maitland <amaitland@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.Reducible;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.SettingsHandler;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;

public class AbilitySelection extends Selection<Ability, String> implements Comparable<AbilitySelection>, Reducible
{

	public AbilitySelection(Ability obj, String sel)
	{
		super(obj, sel);
	}

	/**
	 * Decodes the given String into an AbilitySelection. The String format to
	 * be passed into this method is defined solely by the return result of the
	 * getPersistentFormat method. There is no guarantee that the encoding is
	 * human readable, simply that the encoding is uniquely identifying such
	 * that this method is capable of decoding the String into an
	 * AbilitySelection.
	 * 
	 * @param persistentFormat
	 *            The String which should be decoded to provide an
	 *            AbilitySelection.
	 * 
	 * @return An AbilitySelection that was encoded in the given String.
	 */
	public static AbilitySelection getAbilitySelectionFromPersistentFormat(LoadContext context, String persistentFormat)
	{
		if (!persistentFormat.contains(Constants.PIPE))
		{
			return decodeFeatSelectionChoice(context, persistentFormat);
		}
		StringTokenizer st = new StringTokenizer(persistentFormat, Constants.PIPE);
		String catString = st.nextToken();
		if (!catString.startsWith("CATEGORY="))
		{
			throw new IllegalArgumentException("String in getAbilitySelectionFromPersistentFormat "
				+ "must start with CATEGORY=, found: " + persistentFormat);
		}
		String cat = catString.substring(9);
		AbilityCategory ac = SettingsHandler.getGameAsProperty().get().getAbilityCategory(cat);
		if (ac == null)
		{
			throw new IllegalArgumentException(
				"Category in getAbilitySelectionFromPersistentFormat " + "must exist found: " + cat);
		}
		String ab = st.nextToken();
		Ability a = context.getReferenceContext().getManufacturerId(ac).getActiveObject(ab);
		if (a == null)
		{
			throw new IllegalArgumentException("Second argument in String in getAbilitySelectionFromPersistentFormat "
				+ "must be an Ability, but it was not found: " + persistentFormat);
		}
		String sel = null;
		if (st.hasMoreTokens())
		{
			/*
			 * No need to check for MULT:YES/NO here, as that is checked
			 * implicitly in the construction of AbilitySelection below
			 */
			sel = st.nextToken();
		}
		else if (persistentFormat.endsWith(Constants.PIPE))
		{
			// Handle the StringTokenizer ignoring blank tokens at the end
			sel = "";
		}
		if (st.hasMoreTokens())
		{
			throw new IllegalArgumentException("String in getAbilitySelectionFromPersistentFormat "
				+ "must have 2 or 3 arguments, but found more: " + persistentFormat);
		}
		return new AbilitySelection(a, sel);
	}

	/**
	 * Decode a legacy feat selection format. This may come from a character 
	 * saved when an ability was coded with a FEATSELECTION but is loaded with 
	 * the same tag migrated to an ABILITYSELECTION.
	 *   
	 * @param context
	 *            The data loading context in use. 
	 * @param persistentFormat
	 *            The String which should be decoded to provide an
	 *            AbilitySelection.
	 * 
	 * @return An AbilitySelection that was encoded in the given String.
	 */
	private static AbilitySelection decodeFeatSelectionChoice(LoadContext context, String persistentFormat)
	{
		AbstractReferenceContext referenceContext = context.getReferenceContext();
		AbilityCategory featCategory = referenceContext.get(AbilityCategory.class, "FEAT");
		ReferenceManufacturer<Ability> featManufacturer = referenceContext.getManufacturerId(featCategory);
		Ability ability = featManufacturer.getActiveObject(persistentFormat);

		if (ability == null)
		{
			List<String> choices = new ArrayList<>();
			String baseKey = AbilityUtilities.getUndecoratedName(persistentFormat, choices);
			ability = featManufacturer.getActiveObject(baseKey);
			if (ability == null)
			{
				throw new IllegalArgumentException("String in decodeChoice " + "must be a Feat Key "
					+ "(or Feat Key with Selection if appropriate), was: " + persistentFormat);
			}
			return new AbilitySelection(ability, choices.get(0));
		}
		else if (ability.getSafe(ObjectKey.MULTIPLE_ALLOWED))
		{
			/*
			 * MULT:YES, CHOOSE:NOCHOICE can land here
			 * 
			 * TODO There needs to be better validation at some point that this
			 * is proper (meaning it is actually CHOOSE:NOCHOICE!)
			 */
			return new AbilitySelection(ability, "");
		}
		else
		{
			return new AbilitySelection(ability, null);
		}
	}

	/**
	 * Encodes the AbilitySelection into a String sufficient to uniquely
	 * identify the AbilitySelection. This may not sufficiently encode to be
	 * stored into a file or format which restricts certain characters (such as
	 * URLs), it simply encodes into an identifying String. There is no
	 * guarantee that this encoding is human readable, simply that the encoding
	 * is uniquely identifying such that the
	 * getAbilitySelectionFromPersistentFormat method of AbilitySelection is
	 * capable of decoding the String into an AbilitySelection.
	 * 
	 * @return A String sufficient to uniquely identify the AbilitySelection.
	 */
	public String getPersistentFormat()
	{
		Ability ability = getObject();
		StringBuilder sb = new StringBuilder();
		sb.append("CATEGORY=");
		sb.append(ability.getCDOMCategory().getKeyName());
		sb.append('|');
		sb.append(ability.getKeyName());
		String selection = getSelection();
		if (selection != null)
		{
			sb.append('|');
			sb.append(selection);
		}
		return sb.toString();
	}

	public String getAbilityKey()
	{
		return getObject().getKeyName();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder(50);
		sb.append(getAbilityKey());
		String selection = getSelection();
		if ((selection != null) && (!selection.isEmpty()))
		{
			sb.append(" (");
			sb.append(selection);
			sb.append(')');
		}
		return sb.toString();
	}

	@Override
	public int compareTo(AbilitySelection o)
	{
		int acompare = getObject().compareTo(o.getObject());
		if (acompare != 0)
		{
			return acompare;
		}
		String selection = getSelection();
		String oselection = o.getSelection();
		if (selection == oselection)
		{
			return 0;
		}
		if (selection == null)
		{
			return -1;
		}
		return selection.compareTo(oselection);
	}

	@Override
	public CDOMObject getCDOMObject()
	{
		return getObject();
	}
}
