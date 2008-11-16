/*
 * AbilityLst.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
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
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.util.MapToList;
import pcgen.base.util.TripleKeyMapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMList;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.SettingsHandler;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Implements the ABILITY: global LST token.
 * 
 * <p>
 * <b>Tag Name</b>: <code>ABILITY</code>:x|y|z|z<br />
 * <b>Variables Used (x)</b>: Ability Category (The Ability Category this ability will be added to).<br />
 * <b>Variables Used (y)</b>: Ability Nature (The nature of the added ability: 
 * <tt>NORMAL</tt>, <tt>AUTOMATIC</tt>, or <tt>VIRTUAL</tt>)<br />
 * <b>Variables Used (z)</b>: Ability Key or TYPE(The Ability to add. Can have 
 * choices specified in &quot;()&quot;)<br />
 * <b>Prereqs Allowed</b>: Yes <br />
 * <p />
 * <b>What it does:</b><br/>
 * <ul>
 * <li>Adds an Ability to a character.</li>
 * <li>The Ability is added to the Ability Category specied and that category's
 * pool will be charged if the Nature is <tt>NORMAL</tt></li>
 * <li>This tag will <b>not</b> cause a chooser to appear so all required 
 * choices must be specified in the tag</li>
 * <li>Choices can be specified by including them in parenthesis after the 
 * ability key name (whitespace is ignored).</li>
 * <li>A <tt>CATEGORY</tt> tag can be added to the ability key to specify that
 * the innate ability category specified be searched for a matching ability.</li>
 * <li>If no <tt>CATEGORY</tt> is specified the standard list for the ability
 * category will be used to find a matching ability.</li>
 * <li>This tag is a replacement for the following tags: <tt>FEAT</tt>, 
 * <tt>VFEAT</tt>, and <tt>FEATAUTO</tt>.
 * </ul>
 * <b>Where it is used:</b><br />
 * Global tag can be used anywhere.
 * <p />
 * <b>Examples:</b><br />
 * <code>ABILITY:FEAT|AUTOMATIC|TYPE=Metamagic</code><br />
 * Adds a Metamagic feat as an Auto feat.
 * <p />
 * 
 * <code>ABILITY:CLASSFEATURE|VIRTUAL|CATEGORY=FEAT:Stunning Fist</code><br />
 * Adds the Stunning Fist feat as a virtual class feature.
 * <p />
 * 
 * @author boomer70 <boomer70@yahoo.com>
 * 
 * @since 5.11.1
 *
 */
public class AbilityLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "ABILITY";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
		throws PersistenceLayerException
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		String cat = tok.nextToken();
		final AbilityCategory category =
				SettingsHandler.getGame().getAbilityCategory(cat);
		if (category == null)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
				+ " refers to invalid Ability Category: " + cat);
			return false;
		}
		if (!tok.hasMoreTokens())
		{

			Logging.log(Logging.LST_ERROR, getTokenName() + " must have a Nature, "
				+ "Format is: CATEGORY|NATURE|AbilityName: " + value);
			return false;
		}
		final String natureKey = tok.nextToken();
		Ability.Nature nature;
		try
		{
			nature = Ability.Nature.valueOf(natureKey);
		}
		catch (IllegalArgumentException iae)
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
				+ " refers to invalid Ability Nature: " + natureKey);
			return false;
		}
		if (Ability.Nature.ANY.equals(nature))
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
					+ " refers to ANY Ability Nature, cannot be used in "
					+ getTokenName() + ": " + value);
			return false;
		}
		if (!tok.hasMoreTokens())
		{
			Logging.log(Logging.LST_ERROR, getTokenName()
				+ " must have abilities, Format is: "
				+ "CATEGORY|NATURE|AbilityName: " + value);
			return false;
		}

		String token = tok.nextToken();

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			Logging.log(Logging.LST_ERROR, "Cannot have only PRExxx subtoken in "
				+ getTokenName() + ": " + value);
			return false;
		}

		ArrayList<AssociatedPrereqObject> edgeList =
				new ArrayList<AssociatedPrereqObject>();

		CDOMReference<AbilityList> abilList =
				AbilityList.getAbilityListReference(category, nature);

		boolean first = true;

		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					Logging.log(Logging.LST_ERROR, "  Non-sensical " + getTokenName()
						+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
				context.getListContext().removeAllFromList(getTokenName(), obj,
					abilList);
			}
			else if (token.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				String clearText = token.substring(7);
				CDOMReference<Ability> ref =
						TokenUtilities.getTypeOrPrimitive(context,
							ABILITY_CLASS, category, clearText);
				context.getListContext().removeFromList(getTokenName(), obj,
					abilList, ref);
			}
			else
			{
				CDOMReference<Ability> ability =
						TokenUtilities.getTypeOrPrimitive(context,
							ABILITY_CLASS, category, token);
				if (ability == null)
				{
					return false;
				}
				AssociatedPrereqObject assoc =
						context.getListContext().addToList(getTokenName(), obj,
							abilList, ability);
				assoc.setAssociation(AssociationKey.NATURE, nature);
				assoc.setAssociation(AssociationKey.CATEGORY, category);
				if (token.indexOf('(') != -1)
				{
					List<String> choices = new ArrayList<String>();
					AbilityUtilities.getUndecoratedName(token, choices);
					assoc.setAssociation(AssociationKey.ASSOC_CHOICES, choices);
				}
				edgeList.add(assoc);
			}
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			first = false;
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.log(Logging.LST_ERROR, "   (Did you put feats after the "
					+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			for (AssociatedPrereqObject edge : edgeList)
			{
				edge.addPrerequisite(prereq);
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Collection<CDOMReference<? extends CDOMList<? extends PrereqObject>>> changedLists =
				context.getListContext()
					.getChangedLists(obj, AbilityList.class);
		if (changedLists == null || changedLists.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		Set<String> returnSet = new TreeSet<String>();
		TripleKeyMapToList<Ability.Nature, Category<Ability>, List<Prerequisite>, CDOMReference<Ability>> m =
				new TripleKeyMapToList<Ability.Nature, Category<Ability>, List<Prerequisite>, CDOMReference<Ability>>();
		for (CDOMReference ref : changedLists)
		{
			AssociatedChanges<CDOMReference<Ability>> changes =
					context.getListContext().getChangesInList(getTokenName(),
						obj, ref);
			MapToList<CDOMReference<Ability>, AssociatedPrereqObject> mtl =
					changes.getAddedAssociations();
			for (CDOMReference<Ability> ab : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
				{
					Ability.Nature nature =
							assoc.getAssociation(AssociationKey.NATURE);
					AbilityCategory cat =
							assoc.getAssociation(AssociationKey.CATEGORY);
					m
						.addToListFor(nature, cat, assoc.getPrerequisiteList(),
							ab);
				}
			}
		}

		for (Ability.Nature nature : m.getKeySet())
		{
			for (Category<Ability> category : m.getSecondaryKeySet(nature))
			{
				for (List<Prerequisite> prereqs : m.getTertiaryKeySet(nature,
					category))
				{
					StringBuilder sb = new StringBuilder();
					sb.append(category).append(Constants.PIPE);
					sb.append(nature).append(Constants.PIPE);
					sb.append(ReferenceUtilities.joinLstFormat(m.getListFor(
						nature, category, prereqs), Constants.PIPE));
					if (prereqs != null && !prereqs.isEmpty())
					{
						sb.append(Constants.PIPE);
						sb.append(getPrerequisiteString(context, prereqs));
					}
					returnSet.add(sb.toString());
				}
			}
		}

		return returnSet.toArray(new String[returnSet.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
