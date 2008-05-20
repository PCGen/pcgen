/*
 * InfoViewModelBuilder.java
 * Copyright 2008 (C) Connor Petty <mistercpp2000@gmail.com>
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
 * Created on January 24, 2008, 5:15 PM
 */
package pcgen.gui.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.Pantheon;
import pcgen.core.Deity;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.core.Race;
import pcgen.gui.filter.Filterable;

/**
 *
 * @author Connor Petty
 */
public final class InfoViewModelBuilder
{

    private InfoViewModelBuilder()
    {
    }

    public static PObjectNode buildAlignmentView(Filterable filter, PlayerCharacter pc, Collection<Deity> pobjects)
    {
	PObjectNode root = new PObjectNode();
	Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();
	for (final Deity deity : pobjects)
	{
	    if (filter.accept(pc, deity))
	    {
		String align = deity.getAlignment();
		if (align != null && align.length() > 0)
		{
		    PObjectNode node = nodeMap.get(align);
		    if (node == null)
		    {
			node = new PObjectNode(align);
			nodeMap.put(align, node);
			root.addChild(node);
		    }
		    node.addChild(new PObjectNode(deity));
		}
	    }
	}
	return root;
    }

    public static PObjectNode buildDomainView(Filterable filter,
			PlayerCharacter pc, Collection<Deity> pobjects)
	{
		PObjectNode root = new PObjectNode();
		Map<Domain, PObjectNode> nodeMap = new HashMap<Domain, PObjectNode>();
		for (final Deity deity : pobjects)
		{
			if (filter.accept(pc, deity)
					&& !deity.getKeyName().equalsIgnoreCase("NONE"))
			{
				for (CDOMReference<Domain> ref : deity.getSafeListMods(Deity.DOMAINLIST))
				{
					for (Domain d : ref.getContainedObjects())
					{
						PObjectNode node = nodeMap.get(d);
						if (node == null)
						{
							node = new PObjectNode(d);
							nodeMap.put(d, node);
							root.addChild(node);
						}
						node.addChild(new PObjectNode(deity));
					}
				}
			}
		}
		return root;
	}

    public static PObjectNode buildPantheonView(Filterable filter,
			PlayerCharacter pc, Collection<Deity> pobjects)
	{
		PObjectNode root = new PObjectNode();
		Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();
		for (final Deity deity : pobjects)
		{
			if (filter.accept(pc, deity))
			{
				for (Pantheon pantheon : deity.getSafeListFor(ListKey.PANTHEON))
				{
					String pan = pantheon.toString();
					PObjectNode node = nodeMap.get(pan);
					if (node == null)
					{
						node = new PObjectNode(pan);
						nodeMap.put(pan, node);
						root.addChild(node);
					}
					node.addChild(new PObjectNode(deity));
				}
			}
		}
		return root;
	}

    public static PObjectNode buildNameView(Filterable filter, PlayerCharacter pc, Collection<? extends PObject> pobjects, String qFilter)
    {
	PObjectNode root = new PObjectNode();
	for (final PObject pobj : pobjects)
	{
	    if (filter.accept(pc, pobj))
	    {
		if (qFilter == null || (pobj.getDisplayName().toLowerCase().indexOf(qFilter) >= 0 || pobj.getType().toLowerCase().indexOf(qFilter) >= 0))
		{
		    root.addChild(new PObjectNode(pobj));
		}
	    }
	}
	return root;
    }

    public static PObjectNode buildAllRaceTypesView(Filterable filter, PlayerCharacter pc)
    {
	return buildAllRaceTypesView(filter, pc, Globals.getAllRaces());
    }

    public static PObjectNode buildAllRaceTypesView(Filterable filter, PlayerCharacter pc, Collection<Race> races)
    {
	PObjectNode root = new PObjectNode();
	Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();
	for (final Race race : races)
	{
	    if (filter.accept(pc, race))
	    {
		final String raceType = race.getRaceType();
		PObjectNode raceTypeNode = nodeMap.get(raceType);
		if (raceTypeNode == null)
		{
		    raceTypeNode = new PObjectNode(raceType);
		    nodeMap.put(raceType, raceTypeNode);
		    root.addChild(raceTypeNode);
		}
		for (String type : race.getTypeList(true))
		{
		    PObjectNode typeNode = nodeMap.get(type);
		    if (typeNode == null)
		    {
			typeNode = new PObjectNode(type);
			nodeMap.put(type, typeNode);
			root.addChild(typeNode);
		    }
		    typeNode.addChild(new PObjectNode(race));
		}
		raceTypeNode.addChild(new PObjectNode(race));
	    }
	}
	return root;
    }

    public static PObjectNode buildRaceTypeView(Filterable filter, PlayerCharacter pc)
    {
	return buildRaceTypeView(filter, pc, Globals.getAllRaces());
    }

    public static PObjectNode buildRaceTypeView(Filterable filter, PlayerCharacter pc, Collection<Race> races)
    {
	PObjectNode root = new PObjectNode();
	Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();
	for (Race race : races)
	{
	    if (filter.accept(pc, race))
	    {
		String type = race.getRaceType();
		PObjectNode typeNode = nodeMap.get(type);
		if (typeNode == null)
		{
		    typeNode = new PObjectNode(type);
		    nodeMap.put(type, typeNode);
		    root.addChild(typeNode);
		}
		typeNode.addChild(new PObjectNode(race));
	    }
	}
	return root;
    }

    public static PObjectNode buildRaceTypeSubTypeView(Filterable filter, PlayerCharacter pc)
    {
	return buildRaceTypeSubTypeView(filter, pc, Globals.getAllRaces());
    }

    public static PObjectNode buildRaceTypeSubTypeView(Filterable filter, PlayerCharacter pc, Collection<Race> races)
    {
	PObjectNode root = new PObjectNode();
	Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();
	for (Race race : races)
	{
	    if (filter.accept(pc, race))
	    {
		String type = race.getRaceType();
		PObjectNode typeNode = nodeMap.get(type);
		if (typeNode == null)
		{
		    typeNode = new PObjectNode(type);
		    nodeMap.put(type, typeNode);
		    root.addChild(typeNode);
		}
		List<String> raceSubTypes = race.getRacialSubTypes();
		if (raceSubTypes.size() > 0)
		{
		    for (String subtype : raceSubTypes)
		    {
			String key = type + ":" + subtype;
			PObjectNode subtypeNode = nodeMap.get(key);
			if (subtypeNode == null)
			{
			    subtypeNode = new PObjectNode(subtype);
			    nodeMap.put(key, subtypeNode);
			    typeNode.addChild(subtypeNode);
			}
			subtypeNode.addChild(new PObjectNode(race));
		    }
		}
		else
		{
		    typeNode.addChild(new PObjectNode(race));
		}
	    }
	}
	return root;
    }

    public static PObjectNode buildTypeView(Filterable filter, PlayerCharacter pc, Collection<? extends PObject> pobjects)
    {
	PObjectNode root = new PObjectNode();
	Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();
	for (final PObject pobj : pobjects)
	{
	    if (filter.accept(pc, pobj))
	    {
		final String type = pobj.getType();
		PObjectNode node = nodeMap.get(type);
		if (node == null)
		{
		    node = new PObjectNode(type);
		    nodeMap.put(type, node);
		    root.addChild(node);
		}
		node.addChild(new PObjectNode(pobj));
	    }
	}
	return root;
    }

    public static PObjectNode buildSourceView(Filterable filter, PlayerCharacter pc, Collection<? extends PObject> pobjects)
    {
	PObjectNode root = new PObjectNode();
	Map<String, PObjectNode> nodeMap = new HashMap<String, PObjectNode>();
	for (final PObject pobj : pobjects)
	{
	    if (filter.accept(pc, pobj))
	    {
		final String source = pobj.getSourceEntry().getSourceBook().getLongName();
		if (source != null && source.length() > 0)
		{
		    PObjectNode node = nodeMap.get(source);
		    if (node == null)
		    {
			node = new PObjectNode(source);
			nodeMap.put(source, node);
			root.addChild(node);
		    }
		    node.addChild(new PObjectNode(pobj));
		}
	    }
	}
	return root;
    }
}
