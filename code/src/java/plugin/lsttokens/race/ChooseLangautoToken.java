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
package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PersistentChoiceActor;
import pcgen.cdom.base.PersistentTransitionChoice;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Globals;
import pcgen.core.Language;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.util.Logging;

public class ChooseLangautoToken extends AbstractToken implements
		CDOMSecondaryToken<Race>, PersistentChoiceActor<Language>,
		DeferredToken<Race>
{

	private static final Class<Language> LANGUAGE_CLASS = Language.class;

	public String getParentToken()
	{
		return "CHOOSE";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	@Override
	public String getTokenName()
	{
		return "LANGAUTO";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		List<CDOMReference<Language>> refs =
				new ArrayList<CDOMReference<Language>>();
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		boolean foundAny = false;
		boolean foundOther = false;
		while (tok.hasMoreTokens())
		{
			String tokText = tok.nextToken();
			CDOMReference<Language> lang;
			if (Constants.LST_ALL.equals(tokText))
			{
				foundAny = true;
				lang = context.ref.getCDOMAllReference(LANGUAGE_CLASS);
			}
			else
			{
				foundOther = true;
				lang =
						TokenUtilities.getTypeOrPrimitive(context,
							LANGUAGE_CLASS, tokText);
			}
			if (lang == null)
			{
				Logging.errorPrint("  Error was encountered while parsing "
					+ getFullName() + ": " + value
					+ " had an invalid reference: " + tokText);
				return false;
			}
			refs.add(lang);
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getFullName()
				+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		ReferenceChoiceSet<Language> rcs =
				new ReferenceChoiceSet<Language>(refs);
		ChoiceSet<Language> cs = new ChoiceSet<Language>(getTokenName(), rcs);
		PersistentTransitionChoice<Language> tc =
				new PersistentTransitionChoice<Language>(cs, FormulaFactory.ONE);
		context.getObjectContext().put(race, ObjectKey.CHOOSE_LANGAUTO, tc);
		tc.setTitle("Pick a Language");
		tc.setChoiceActor(this);
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		PersistentTransitionChoice<Language> container = context
				.getObjectContext().getObject(race, ObjectKey.CHOOSE_LANGAUTO);
		if (container == null)
		{
			return null;
		}
		ChoiceSet<?> cs = container.getChoices();
		Formula f = container.getCount();
		if (f == null)
		{
			context.addWriteMessage("Unable to find " + getFullName()
					+ " Count");
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb
				.append(cs.getLSTformat().replaceAll(Constants.COMMA,
						Constants.PIPE));
		return new String[] { sb.toString() };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

	public void applyChoice(CDOMObject owner, Language choice,
		PlayerCharacter pc)
	{
		pc.addFreeLanguage(choice);
	}

	public boolean allow(Language choice, PlayerCharacter pc, boolean allowStack)
	{
		return !pc.getLanguagesList().contains(choice);
	}

	public Language decodeChoice(String s)
	{
		return Globals.getContext().ref.silentlyGetConstructedCDOMObject(
			LANGUAGE_CLASS, s);
	}

	public String encodeChoice(Object choice)
	{
		return ((Language) choice).getKeyName();
	}

	public void restoreChoice(PlayerCharacter pc, CDOMObject owner,
		Language choice)
	{
		// No action required
	}

	public Class<Race> getDeferredTokenClass()
	{
		return Race.class;
	}

	/*
	 * This is deferred into ListKey.ADD to ensure that ADD:.CLEAR doesn't
	 * impact CHOOSE:LANGAUTO. It is hoped that CHOOSE:LANGAUTO can be
	 * refactored into an ADD token in order to avoid this contortion
	 */
	public boolean process(LoadContext context, Race race)
	{
		PersistentTransitionChoice<Language> langauto = race
				.get(ObjectKey.CHOOSE_LANGAUTO);
		if (langauto != null)
		{
			race.addToListFor(ListKey.ADD, langauto);
		}
		return true;
	}
}
