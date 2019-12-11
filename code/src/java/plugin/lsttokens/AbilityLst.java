/*
 * AbilityLst.java
 * Copyright 2006 (C) Aaron Divinsky <boomer70@yahoo.com>
 * Copyright 2008-12 (C) Thomas Parker <thpr@users.sourceforge.net>
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
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrereqObject;
import pcgen.cdom.base.Ungranted;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Nature;
import pcgen.cdom.helper.AbilitySelector;
import pcgen.cdom.helper.AbilityTargetSelector;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceManufacturer;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.DeferredToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Implements the ABILITY: global LST token.
 *
 * <p>
 * <b>Tag Name</b>: {@code ABILITY}:x|y|z|z<br>
 * <b>Variables Used (x)</b>: Ability Category (The Ability Category this ability will be added to).<br>
 * <b>Variables Used (y)</b>: Ability Nature (The nature of the added ability:
 * <tt>NORMAL</tt>, <tt>AUTOMATIC</tt>, or <tt>VIRTUAL</tt>)<br>
 * <b>Variables Used (z)</b>: Ability Key or TYPE(The Ability to add. Can have
 * choices specified in &quot;()&quot;)<br>
 * <b>Prereqs Allowed</b>: Yes <br>
 * <p>
 * <b>What it does:</b><br>
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
 * <b>Where it is used:</b><br>
 * Global tag can be used anywhere.
 * <p>
 * <b>Examples:</b><br>
 * {@code ABILITY:FEAT|AUTOMATIC|TYPE=Metamagic}<br>
 * Adds a Metamagic feat as an Auto feat.
 * <p>
 *
 * {@code ABILITY:CLASSFEATURE|VIRTUAL|CATEGORY=FEAT:Stunning Fist}<br>
 * Adds the Stunning Fist feat as a virtual class feature.
 * <p>
 */
public class AbilityLst extends AbstractTokenWithSeparator<CDOMObject>
		implements CDOMPrimaryToken<CDOMObject>, DeferredToken<CDOMObject>
{

	private static final Class<Ability> ABILITY_CLASS = Ability.class;
	private static final Class<AbilityCategory> ABILITY_CATEGORY_CLASS = AbilityCategory.class;

	@Override
	public String getTokenName()
	{
		return "ABILITY";
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context, CDOMObject obj, String value)
	{
		if (obj instanceof Ungranted)
		{
			return new ParseResult.Fail(
				"Cannot use " + getTokenName() + " on an Ungranted object type: " + obj.getClass().getSimpleName());
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		String cat = tok.nextToken();
		CDOMSingleRef<AbilityCategory> acRef =
				context.getReferenceContext().getCDOMReference(ABILITY_CATEGORY_CLASS, cat);
		if (!tok.hasMoreTokens())
		{
			return new ParseResult.Fail(
				getTokenName() + " must have a Nature, " + "Format is: CATEGORY|NATURE|AbilityName: " + value);
		}
		final String natureKey = tok.nextToken();
		Nature nature;
		try
		{
			nature = Nature.valueOf(natureKey);
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail(getTokenName() + " refers to invalid Ability Nature: " + natureKey);
		}
		if (Nature.ANY.equals(nature))
		{
			return new ParseResult.Fail(
				getTokenName() + " refers to ANY Ability Nature, cannot be used in " + getTokenName() + ": " + value);
		}
		if (!tok.hasMoreTokens())
		{
			return new ParseResult.Fail(
				getTokenName() + " must have abilities, Format is: " + "CATEGORY|NATURE|AbilityName: " + value);
		}

		String token = tok.nextToken();

		if (looksLikeAPrerequisite(token))
		{
			return new ParseResult.Fail("Cannot have only PRExxx subtoken in " + getTokenName() + ": " + value);
		}

		String lkString = "GA_CA_" + cat + '_' + natureKey;

		ArrayList<PrereqObject> edgeList = new ArrayList<>();

		CDOMReference<AbilityList> abilList = AbilityList.getAbilityListReference(acRef, nature);

		boolean first = true;
		boolean removed = false;

		ReferenceManufacturer<Ability> rm =
				context.getReferenceContext().getManufacturerByFormatName("ABILITY=" + cat, ABILITY_CLASS);
		if (rm == null)
		{
			return new ParseResult.Fail("Could not get Reference Manufacturer for Category: " + cat);
		}

		boolean prereqsAllowed = true;

		while (true)
		{
			if (Constants.LST_DOT_CLEAR.equals(token))
			{
				if (!first)
				{
					return new ParseResult.Fail(
						"  Non-sensical " + getTokenName() + ": .CLEAR was not the first list item: " + value);
				}
				context.getListContext().removeAllFromList(getTokenName(), obj, abilList);
				context.getObjectContext().removeFromList(obj, ListKey.GA_CAKEYS, ListKey.getKeyFor(ChooseSelectionActor.class, lkString));
				context.getObjectContext().removeList(obj, ListKey.getKeyFor(ChooseSelectionActor.class, lkString));
				removed = true;
			}
			else if (token.startsWith(Constants.LST_DOT_CLEAR_DOT))
			{
				String clearText = token.substring(7);
				CDOMReference<Ability> ref = TokenUtilities.getTypeOrPrimitive(rm, clearText);
				if (ref == null)
				{
					return ParseResult.INTERNAL_ERROR;
				}
				AssociatedPrereqObject assoc =
						context.getListContext().removeFromList(getTokenName(), obj, abilList, ref);
				assoc.setAssociation(AssociationKey.NATURE, nature);
				assoc.setAssociation(AssociationKey.CATEGORY, acRef);
				removed = true;
			}
			else if (Constants.LST_PERCENT_LIST.equals(token))
			{
				prereqsAllowed = false;
				AbilitySelector as = new AbilitySelector(getTokenName(), acRef, nature);
				context.getObjectContext().addToList(obj, ListKey.NEW_CHOOSE_ACTOR, as);
			}
			else
			{
				CDOMReference<Ability> ability = TokenUtilities.getTypeOrPrimitive(rm, token);
				if (ability == null)
				{
					return ParseResult.INTERNAL_ERROR;
				}
				ability.setRequiresTarget(true);
				boolean loadList = true;
				List<String> choices = null;
				if (token.indexOf('(') != -1)
				{
					choices = new ArrayList<>();
					AbilityUtilities.getUndecoratedName(token, choices);
					if (choices.size() == 1)
					{
						if (Constants.LST_PERCENT_LIST.equals(choices.get(0)) && (ability instanceof CDOMSingleRef))
						{
							CDOMSingleRef<Ability> ref = (CDOMSingleRef<Ability>) ability;
							AbilityTargetSelector ats = new AbilityTargetSelector(getTokenName(), acRef, ref, nature);
							context.getObjectContext().addToList(obj, ListKey.GA_CAKEYS, ListKey.getKeyFor(ChooseSelectionActor.class, lkString));
							context.getObjectContext().addToList(obj, ListKey.getKeyFor(ChooseSelectionActor.class, lkString), ats);
							edgeList.add(ats);
							loadList = false;
						}
					}
				}
				if (loadList)
				{
					AssociatedPrereqObject assoc =
							context.getListContext().addToList(getTokenName(), obj, abilList, ability);
					assoc.setAssociation(AssociationKey.NATURE, nature);
					assoc.setAssociation(AssociationKey.CATEGORY, acRef);
					if (choices != null)
					{
						assoc.setAssociation(AssociationKey.ASSOC_CHOICES, choices);
					}
					edgeList.add(assoc);
				}
			}
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return ParseResult.SUCCESS;
			}
			first = false;
			token = tok.nextToken();
			if (looksLikeAPrerequisite(token))
			{
				break;
			}
		}

		if (removed || !prereqsAllowed)
		{
			return new ParseResult.Fail("Cannot use PREREQs when using .CLEAR, .CLEAR., or %LIST in " + getTokenName());
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				return new ParseResult.Fail(
					"   (Did you put feats after the " + "PRExxx tags in " + getTokenName() + ":?)");
			}
			for (PrereqObject edge : edgeList)
			{
				edge.addPrerequisite(prereq);
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Collection<CDOMReference<? extends CDOMList<?>>> changedLists =
				context.getListContext().getChangedLists(obj, AbilityList.class);
		Changes<ListKey<ChooseSelectionActor<?>>> actors =
				context.getObjectContext().getListChanges(obj, ListKey.GA_CAKEYS);
		Set<String> returnSet = new TreeSet<>();
		TripleKeyMapToList<Nature, CDOMSingleRef<AbilityCategory>, List<Prerequisite>, CDOMReference<Ability>> m =
				new TripleKeyMapToList<>();
		TripleKeyMapToList<Nature, CDOMSingleRef<AbilityCategory>, List<Prerequisite>, CDOMReference<Ability>> clear =
				new TripleKeyMapToList<>();

		Changes<ChooseSelectionActor<?>> listChanges =
				context.getObjectContext().getListChanges(obj, ListKey.NEW_CHOOSE_ACTOR);
		Collection<ChooseSelectionActor<?>> listAdded = listChanges.getAdded();
		if ((listAdded != null) && !listAdded.isEmpty())
		{
			for (ChooseSelectionActor<?> csa : listAdded)
			{
				if (csa.getSource().equals(getTokenName()))
				{
					try
					{
						AbilitySelector as = (AbilitySelector) csa;
						String sb = as.getAbilityCategory().getLSTformat(false) + Constants.PIPE
								+ as.getNature() + Constants.PIPE
								+ as.getLstFormat();
						returnSet.add(sb);
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage(getTokenName() + " encountered error: " + e.getMessage());
						return null;
					}
				}
			}
		}

		for (CDOMReference ref : changedLists)
		{
			AssociatedChanges<CDOMReference<Ability>> changes =
					context.getListContext().getChangesInList(getTokenName(), obj, ref);
			if (changes.includesGlobalClear())
			{
				CDOMDirectSingleRef<AbilityList> dr = (CDOMDirectSingleRef<AbilityList>) ref;
				AbilityList al = dr.get();
				String sb = al.getCategory().getLSTformat(false) + Constants.PIPE
						+ al.getNature() + Constants.PIPE
						+ Constants.LST_DOT_CLEAR;
				returnSet.add(sb);
			}
			MapToList<CDOMReference<Ability>, AssociatedPrereqObject> mtl = changes.getAddedAssociations();
			if (mtl != null)
			{
				for (CDOMReference<Ability> ab : mtl.getKeySet())
				{
					for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
					{
						Nature nature = assoc.getAssociation(AssociationKey.NATURE);
						CDOMSingleRef<AbilityCategory> cat = assoc.getAssociation(AssociationKey.CATEGORY);
						m.addToListFor(nature, cat, assoc.getPrerequisiteList(), ab);
					}
				}
			}
			mtl = changes.getRemovedAssociations();
			if (mtl != null)
			{
				for (CDOMReference<Ability> ab : mtl.getKeySet())
				{
					for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
					{
						Nature nature = assoc.getAssociation(AssociationKey.NATURE);
						CDOMSingleRef<AbilityCategory> cat = assoc.getAssociation(AssociationKey.CATEGORY);
						clear.addToListFor(nature, cat, assoc.getPrerequisiteList(), ab);
					}
				}
			}
		}

		for (Nature nature : m.getKeySet())
		{
			for (CDOMSingleRef<AbilityCategory> category : m.getSecondaryKeySet(nature))
			{
				for (List<Prerequisite> prereqs : m.getTertiaryKeySet(nature, category))
				{
					StringBuilder sb = new StringBuilder();
					sb.append(category.getLSTformat(false)).append(Constants.PIPE);
					sb.append(nature);
					List<CDOMReference<Ability>> clearList = clear.removeListFor(nature, category, prereqs);
					if ((clearList != null) && !clearList.isEmpty())
					{
						sb.append(Constants.PIPE);
						sb.append(Constants.LST_DOT_CLEAR_DOT);
						sb.append(
							ReferenceUtilities.joinLstFormat(clearList, Constants.PIPE + Constants.LST_DOT_CLEAR_DOT));
					}
					sb.append(Constants.PIPE);
					sb.append(
						ReferenceUtilities.joinLstFormat(m.getListFor(nature, category, prereqs), Constants.PIPE));
					if ((prereqs != null) && !prereqs.isEmpty())
					{
						sb.append(Constants.PIPE);
						sb.append(getPrerequisiteString(context, prereqs));
					}
					returnSet.add(sb.toString());
				}
			}
		}
		for (Nature nature : clear.getKeySet())
		{
			for (CDOMSingleRef<AbilityCategory> category : clear.getSecondaryKeySet(nature))
			{
				for (List<Prerequisite> prereqs : clear.getTertiaryKeySet(nature, category))
				{
					StringBuilder sb = new StringBuilder();
					sb.append(category.getLSTformat(false)).append(Constants.PIPE);
					sb.append(nature).append(Constants.PIPE).append(Constants.LST_DOT_CLEAR_DOT);
					sb.append(ReferenceUtilities.joinLstFormat(clear.getListFor(nature, category, prereqs),
						Constants.PIPE + Constants.LST_DOT_CLEAR_DOT));
					if ((prereqs != null) && !prereqs.isEmpty())
					{
						sb.append(Constants.PIPE);
						sb.append(getPrerequisiteString(context, prereqs));
					}
					returnSet.add(sb.toString());
				}
			}
		}
		Collection<ListKey<ChooseSelectionActor<?>>> addedActors = actors.getAdded();
		if (addedActors != null)
		{
			for (ListKey<ChooseSelectionActor<?>> lk : addedActors)
			{
				Changes<ChooseSelectionActor<?>> cras = context.getObjectContext().getListChanges(obj, lk);
				for (ChooseSelectionActor<?> cra : cras.getAdded())
				{
					if (getTokenName().equals(cra.getSource()))
					{
						try
						{
							AbilityTargetSelector ats = (AbilityTargetSelector) cra;
							StringBuilder sb = new StringBuilder();
							sb.append(ats.getAbilityCategory().getLSTformat(false)).append(Constants.PIPE);
							sb.append(ats.getNature()).append(Constants.PIPE).append(cra.getLstFormat());
							List<Prerequisite> prereqs = ats.getPrerequisiteList();
							if ((prereqs != null) && !prereqs.isEmpty())
							{
								sb.append(Constants.PIPE);
								sb.append(getPrerequisiteString(context, prereqs));
							}
							returnSet.add(sb.toString());
						}
						catch (PersistenceLayerException e)
						{
							context.addWriteMessage(getTokenName() + " encountered error: " + e.getMessage());
							return null;
						}
					}
				}
			}
		}
		if (returnSet.isEmpty())
		{
			return null;
		}
		return returnSet.toArray(new String[0]);
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	/*
	 * This is a DeferredToken because attempting to extract "self" out of the
	 * "generic" (widely shared) CHOOSE_ACTOR list is extremely difficult since
	 * the item added is not this token but a derivative object whose reference
	 * is not saved by this token. Therefore a unique list is used to store the
	 * CHOOSE_ACTORs generated by this token and they are added into the
	 * "global" list when load is complete - thpr Dec 15, 2012
	 */
	@Override
	public boolean process(LoadContext context, CDOMObject cdo)
	{
		List<ListKey<ChooseSelectionActor<?>>> lkList = cdo.getListFor(ListKey.GA_CAKEYS);
		if (lkList != null)
		{
			for (ListKey<ChooseSelectionActor<?>> lk : lkList)
			{
				cdo.addAllToListFor(ListKey.NEW_CHOOSE_ACTOR, cdo.getListFor(lk));
			}
		}
		return true;
	}

	@Override
	public Class<CDOMObject> getDeferredTokenClass()
	{
		return CDOMObject.class;
	}
}
