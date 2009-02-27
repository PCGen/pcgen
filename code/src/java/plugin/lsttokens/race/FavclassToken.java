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
package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ClassReferenceChoiceSet;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with FAVCLASS Token
 */
public class FavclassToken extends AbstractToken implements
		CDOMPrimaryToken<Race>, ChooseResultActor
{
	public static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	public static final Class<SubClass> SUBCLASS_CLASS = SubClass.class;

	@Override
	public String getTokenName()
	{
		return "FAVCLASS";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		context.getObjectContext().remove(race, ObjectKey.ANY_FAVORED_CLASS);
		context.getObjectContext().removeList(race, ListKey.FAVORED_CLASS);
		context.getObjectContext().removeFromList(race, ListKey.CHOOSE_ACTOR, this);
		context.getObjectContext().remove(race, ObjectKey.FAVCLASS_CHOICE);

		if (value.startsWith(Constants.LST_CHOOSE))
		{
			return parseFavoredChoose(context, race, value.substring(7));
		}
		return parseFavoredClass(context, race, value);
	}

	private boolean parseFavoredChoose(LoadContext context, Race race,
			String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		List<CDOMReference<? extends PCClass>> refList = new ArrayList<CDOMReference<? extends PCClass>>();
		while (tok.hasMoreTokens())
		{
			CDOMReference<? extends PCClass> ref;
			String token = tok.nextToken();
			if (Constants.LST_ALL.equalsIgnoreCase(token)
					|| Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(PCCLASS_CLASS);
			}
			else
			{
				foundOther = true;
				int dotLoc = token.indexOf('.');
				if (dotLoc == -1)
				{
					// Primitive
					ref = context.ref.getCDOMReference(PCCLASS_CLASS, token);
				}
				else
				{
					// SubClass
					String parent = token.substring(0, dotLoc);
					String subclass = token.substring(dotLoc + 1);
					SubClassCategory scc = SubClassCategory.getConstant(parent);
					ref = context.ref.getCDOMReference(SUBCLASS_CLASS, scc,
							subclass);
				}
			}
			refList.add(ref);
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		PrimitiveChoiceSet<PCClass> rcs = new ClassReferenceChoiceSet(refList);
		ChoiceSet<? extends PCClass> cs = new ChoiceSet<PCClass>(
				getTokenName(), rcs);
		TransitionChoice<PCClass> tc = new TransitionChoice<PCClass>(cs,
				FormulaFactory.ONE);
		context.getObjectContext().put(race, ObjectKey.FAVCLASS_CHOICE, tc);
		tc.setTitle("Select favored class");
		tc.setRequired(true);

		Logging
				.deprecationPrint("Use of FAVCLASS:CHOOSE is deprecated. "
						+ "Please use FAVCLASS:%LIST with a CHOOSE:CLASS insetad of FAVCLASS:CHOOSE|"
						+ value);

		return true;
	}

	public boolean parseFavoredClass(LoadContext context, CDOMObject cdo,
			String value)
	{
		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_ALL.equalsIgnoreCase(token)
					|| Constants.LST_ANY.equalsIgnoreCase(token)
					|| Constants.HIGHESTLEVELCLASS.equalsIgnoreCase(token))
			{
				foundAny = true;
				context.getObjectContext().put(cdo,
						ObjectKey.ANY_FAVORED_CLASS, true);
				if (Constants.LST_ALL.equalsIgnoreCase(token)
						|| Constants.LST_ANY.equalsIgnoreCase(token))
				{
					Logging.deprecationPrint("Use of FAVCLASS:" + token
							+ " is deprecated. "
							+ "Please use FAVCLASS:HIGHESTLEVELCLASS");
				}
			}
			else if (Constants.LST_PRECENTLIST.equalsIgnoreCase(token))
			{
				foundOther = true;
				context.getObjectContext().addToList(cdo, ListKey.CHOOSE_ACTOR,
						this);
			}
			else
			{
				CDOMReference<? extends PCClass> ref;
				foundOther = true;
				int dotLoc = token.indexOf('.');
				if (dotLoc == -1)
				{
					// Primitive
					ref = context.ref.getCDOMReference(PCCLASS_CLASS, token);
				}
				else
				{
					if (hasIllegalSeparator('.', token))
					{
						return false;
					}
					// SubClass
					String parent = token.substring(0, dotLoc);
					String subclass = token.substring(dotLoc + 1);
					SubClassCategory scc = SubClassCategory.getConstant(parent);
					ref = context.ref.getCDOMReference(SUBCLASS_CLASS, scc,
							subclass);
				}
				context.getObjectContext().addToList(cdo,
						ListKey.FAVORED_CLASS, ref);
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains HIGHESTLEVELCLASS and a specific reference: "
					+ value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Changes<CDOMReference<? extends PCClass>> changes = context
				.getObjectContext().getListChanges(race, ListKey.FAVORED_CLASS);
		Changes<ChooseResultActor> listChanges = context.getObjectContext()
				.getListChanges(race, ListKey.CHOOSE_ACTOR);
		Boolean anyfavored = context.getObjectContext().getObject(race,
				ObjectKey.ANY_FAVORED_CLASS);
		SortedSet<String> set = new TreeSet<String>();
		if (anyfavored != null && anyfavored)
		{
			set.add("HIGHESTLEVELCLASS");
		}
		if (changes != null && !changes.isEmpty() && changes.hasAddedItems())
		{
			for (CDOMReference<? extends PCClass> ref : changes.getAdded())
			{
				Class<? extends PCClass> refClass = ref.getReferenceClass();
				if (SUBCLASS_CLASS.equals(refClass))
				{
					Category<SubClass> parent = ((CategorizedCDOMReference<SubClass>) ref)
							.getCDOMCategory();
					set.add(parent.toString() + "." + ref.getLSTformat());
				}
				else
				{
					set.add(ref.getLSTformat());
				}
			}
		}
		Collection<ChooseResultActor> listAdded = listChanges.getAdded();
		if (listAdded != null && !listAdded.isEmpty())
		{
			for (ChooseResultActor cra : listAdded)
			{
				if (cra.getSource().equals(getTokenName()))
				{
					try
					{
						set.add(cra.getLstFormat());
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
								+ e);
						return null;
					}
				}
			}
		}
		if (set.isEmpty())
		{
			// Zero indicates no add or clear
			return null;
		}
		return new String[] { StringUtil.join(set, Constants.PIPE) };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.cdom.base.ChooseResultActor#apply(pcgen.core.PlayerCharacter,
	 *      pcgen.cdom.base.CDOMObject, java.lang.String)
	 */
	public void apply(PlayerCharacter pc, CDOMObject obj, String o)
	{
		PCClass cls = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(PCCLASS_CLASS, o);
		if (cls != null)
		{
			pc.addAssoc(obj, AssociationListKey.FAVCLASS, cls);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pcgen.cdom.base.ChooseResultActor#remove(pcgen.core.PlayerCharacter,
	 *      pcgen.cdom.base.CDOMObject, java.lang.String)
	 */
	public void remove(PlayerCharacter pc, CDOMObject obj, String o)
	{
		PCClass cls = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(PCCLASS_CLASS, o);
		if (cls != null)
		{
			pc.removeAssoc(obj, AssociationListKey.FAVCLASS, cls);
		}

	}

	public String getSource()
	{
		return getTokenName();
	}

	public String getLstFormat()
	{
		return "%LIST";
	}
}
