/*
 * Copyright 2026 (C) PCGen Contributors
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

import java.util.Collection;
import java.util.List;

import pcgen.cdom.base.CDOMList;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseDriver;
import pcgen.cdom.base.ChooseSelectionActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.facet.AvailableSpellFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.KnownSpellFacet;
import pcgen.cdom.facet.MasterAvailableSpellFacet;
import pcgen.cdom.helper.AvailableSpell;
import pcgen.cdom.list.ClassSpellList;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.spell.Spell;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * Token ADDSPELLTOCLASSLIST that adds spells chosen via CHOOSE:SPELLS
 * to the character's spell list for a target spellcasting class type.
 *
 * Syntax: ADDSPELLTOCLASSLIST:SourceClassName|TargetSpellType
 * Example: ADDSPELLTOCLASSLIST:Wizard|Divine
 *
 * When a spell is chosen via CHOOSE:SPELLS, this token looks up the spell's
 * level on the source class spell list and adds it to the character's
 * target class spell list (first class matching the target spell type).
 */
public class AddSpellToClassListLst implements CDOMPrimaryToken<CDOMObject>, ChooseSelectionActor<Spell>
{

	private static final String TOKEN_NAME = "ADDSPELLTOCLASSLIST";
	private static final String SOURCE = "ADDSPELLTOCLASSLIST";

	private String sourceClassName;
	private String targetSpellType;

	@Override
	public String getTokenName()
	{
		return TOKEN_NAME;
	}

	@Override
	public ParseResult parseToken(LoadContext context, CDOMObject obj, String value)
	{
		if (value == null || value.isEmpty())
		{
			return new ParseResult.Fail("ADDSPELLTOCLASSLIST requires a value in the format SourceClass|TargetSpellType");
		}

		String[] parts = value.split("\\|");
		if (parts.length != 2)
		{
			return new ParseResult.Fail(
				"ADDSPELLTOCLASSLIST requires exactly two arguments separated by '|': SourceClass|TargetSpellType. Got: " + value);
		}

		sourceClassName = parts[0];
		targetSpellType = parts[1];

		context.getObjectContext().addToList(obj, ListKey.NEW_CHOOSE_ACTOR, this);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Changes<ChooseSelectionActor<?>> changes =
				context.getObjectContext().getListChanges(obj, ListKey.NEW_CHOOSE_ACTOR);
		Collection<ChooseSelectionActor<?>> added = changes.getAdded();
		if (added == null || added.isEmpty())
		{
			return null;
		}
		for (ChooseSelectionActor<?> actor : added)
		{
			if (actor.getSource().equals(SOURCE))
			{
				return new String[]{sourceClassName + Constants.PIPE + targetSpellType};
			}
		}
		return null;
	}

	@Override
	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	@Override
	public void applyChoice(ChooseDriver obj, Spell spell, PlayerCharacter pc)
	{
		ClassSpellList sourceList = findClassSpellList(pc, sourceClassName);
		if (sourceList == null)
		{
			Logging.errorPrint("ADDSPELLTOCLASSLIST: Could not find spell list for class: " + sourceClassName);
			return;
		}

		int spellLevel = findSpellLevel(pc, sourceList, spell);
		if (spellLevel < 0)
		{
			Logging.errorPrint("ADDSPELLTOCLASSLIST: Spell " + spell.getKeyName()
				+ " not found on " + sourceClassName + " spell list");
			return;
		}

		PCClass targetClass = findTargetClass(pc, targetSpellType);
		if (targetClass == null)
		{
			Logging.errorPrint("ADDSPELLTOCLASSLIST: No class with spell type " + targetSpellType + " found");
			return;
		}

		ClassSpellList targetList = targetClass.get(ObjectKey.CLASS_SPELLLIST);
		if (targetList == null)
		{
			Logging.errorPrint("ADDSPELLTOCLASSLIST: No spell list found for class " + targetClass.getKeyName());
			return;
		}

		AvailableSpellFacet availableSpellFacet = FacetLibrary.getFacet(AvailableSpellFacet.class);
		availableSpellFacet.add(pc.getCharID(), targetList, spellLevel, spell, obj);

		KnownSpellFacet knownSpellFacet = FacetLibrary.getFacet(KnownSpellFacet.class);
		knownSpellFacet.add(pc.getCharID(), targetList, spellLevel, spell, obj);
	}

	@Override
	public void removeChoice(ChooseDriver obj, Spell spell, PlayerCharacter pc)
	{
		ClassSpellList sourceList = findClassSpellList(pc, sourceClassName);
		if (sourceList == null)
		{
			return;
		}

		int spellLevel = findSpellLevel(pc, sourceList, spell);
		if (spellLevel < 0)
		{
			return;
		}

		PCClass targetClass = findTargetClass(pc, targetSpellType);
		if (targetClass == null)
		{
			return;
		}

		ClassSpellList targetList = targetClass.get(ObjectKey.CLASS_SPELLLIST);
		if (targetList == null)
		{
			return;
		}

		AvailableSpellFacet availableSpellFacet = FacetLibrary.getFacet(AvailableSpellFacet.class);
		availableSpellFacet.remove(pc.getCharID(), targetList, spellLevel, spell, obj);

		KnownSpellFacet knownSpellFacet = FacetLibrary.getFacet(KnownSpellFacet.class);
		knownSpellFacet.remove(pc.getCharID(), targetList, spellLevel, spell, obj);
	}

	@Override
	public String getSource()
	{
		return SOURCE;
	}

	@Override
	public String getLstFormat()
	{
		return sourceClassName + Constants.PIPE + targetSpellType;
	}

	@Override
	public Class<Spell> getChoiceClass()
	{
		return Spell.class;
	}

	private ClassSpellList findClassSpellList(PlayerCharacter pc, String className)
	{
		DataSetID dsID = pc.getCharID().getDatasetID();
		MasterAvailableSpellFacet masterFacet = FacetLibrary.getFacet(MasterAvailableSpellFacet.class);
		Collection<AvailableSpell> allSpells = masterFacet.getSet(dsID);
		for (AvailableSpell as : allSpells)
		{
			CDOMList<Spell> list = as.getSpelllist();
			if (list instanceof ClassSpellList && list.getKeyName().equals(className))
			{
				return (ClassSpellList) list;
			}
		}
		return null;
	}

	private int findSpellLevel(PlayerCharacter pc, ClassSpellList sourceList, Spell spell)
	{
		DataSetID dsID = pc.getCharID().getDatasetID();
		MasterAvailableSpellFacet masterFacet = FacetLibrary.getFacet(MasterAvailableSpellFacet.class);
		List<AvailableSpell> matches = masterFacet.getMatchingSpellsInList(sourceList, dsID, spell);
		if (!matches.isEmpty())
		{
			return matches.get(0).getLevel();
		}
		return -1;
	}

	private PCClass findTargetClass(PlayerCharacter pc, String spellType)
	{
		for (PCClass pcClass : pc.getClassSet())
		{
			if (spellType.equalsIgnoreCase(pcClass.getSpellType()))
			{
				return pcClass;
			}
		}
		return null;
	}
}
