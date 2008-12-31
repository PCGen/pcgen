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
package plugin.lsttokens.template;

import java.util.Collection;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationListKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.core.PlayerCharacter;
import pcgen.core.SubClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with FAVOREDCLASS Token
 */
public class FavoredclassToken extends AbstractToken implements
		CDOMPrimaryToken<PCTemplate>, ChooseResultActor
{

	public static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	public static final Class<SubClass> SUBCLASS_CLASS = SubClass.class;

	@Override
	public String getTokenName()
	{
		return "FAVOREDCLASS";
	}

	public boolean parse(LoadContext context, PCTemplate pct, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		return parseFavoredClass(context, pct, value);
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
					Logging.deprecationPrint("Use of " + getTokenName() + ":"
							+ token + " is deprecated. " + "Please use "
							+ getTokenName() + ":HIGHESTLEVELCLASS");
				}
			}
			else if (Constants.LST_PRECENTLIST.equalsIgnoreCase(token))
			{
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
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<CDOMReference<? extends PCClass>> changes = context
				.getObjectContext().getListChanges(pct, ListKey.FAVORED_CLASS);
		Changes<ChooseResultActor> listChanges = context.getObjectContext()
				.getListChanges(pct, ListKey.CHOOSE_ACTOR);
		SortedSet<String> set = new TreeSet<String>();
		if (changes != null && !changes.isEmpty())
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

	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
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
