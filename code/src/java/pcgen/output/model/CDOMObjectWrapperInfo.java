/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.model;

import java.util.HashMap;
import java.util.Map;

import pcgen.cdom.base.CDOMObject;
import pcgen.output.base.OutputActor;

/**
 * A CDOMObjectWrapperInfo maintains a map from interpolations to OutputActors.
 * 
 * Typically this is done with one CDOMObjectWrapperInfo serving each type of
 * CDOMObject.
 */
public class CDOMObjectWrapperInfo
{

	/**
	 * CDOMObjectWrapperInfo serves as next member of a Chain of Responsibility.
	 */
	private final CDOMObjectWrapperInfo nextInfo;

	/**
	 * The underlying Map from interpolations to OutputActors.
	 */
	private final Map<String, OutputActor<CDOMObject>> map =
			new HashMap<String, OutputActor<CDOMObject>>();

	/**
	 * Constructs a new CDOMObjectWrapperInfo with the given
	 * CDOMObjectWrapperInfo as the next member in a Chain of Responsibility.
	 * 
	 * @param nextInfo
	 *            The (optional) CDOMObjectWrapperInfo that serves as the next
	 *            member in the Chain of Responsibility
	 */
	public CDOMObjectWrapperInfo(CDOMObjectWrapperInfo nextInfo)
	{
		//parent can be null (if this is for Object.class)
		this.nextInfo = nextInfo;
	}

	/**
	 * Constructs a new interpolation/name and OutputActor combination to this
	 * CDOMObjectWrapperInfo.
	 * 
	 * If this CDOMObjectWrapperInfo already has an OutputActor for the given
	 * name, then this method will not add the given OutputActor and will return
	 * false.
	 * 
	 * @param name
	 *            The name (identifier in FreeMarker terms) of the given
	 *            OutputActor
	 * @param oa
	 *            The OutputActor used to fetch information from a CDOMObject
	 * @return true if the given name and OutputActor were successfully loaded;
	 *         false otherwise
	 */
	public boolean load(String name, OutputActor<CDOMObject> oa)
	{
		if (name == null)
		{
			throw new IllegalArgumentException(
				"Identifier name may not be null");
		}
		if (oa == null)
		{
			throw new IllegalArgumentException("OutputActor may not be null");
		}
		if (map.containsKey(name))
		{
			return false;
		}
		map.put(name, oa);
		return true;
	}

	/**
	 * Returns the OutputActor for the given identifier.
	 * 
	 * If this CDOMObjectWrapperInfo does not have an OutputActor for the given
	 * identifier, then this call is delegated to the next member of the Chain
	 * of Responsibility (presumably the CDOMObjectWrapperInfo serving the
	 * "superclass" of the class this CDOMObjectWrapperInfo serves).
	 * 
	 * May return null if the CDOMObjectWrapperInfo objects in the Chain of
	 * Responsibility do not contain an OutputActor for the given identifier.
	 * 
	 * @param identifier
	 *            The identifier for which the OutputActor should be returned
	 * @return The OutputActor for the given identifier
	 */
	public OutputActor<CDOMObject> get(String identifier)
	{
		OutputActor<CDOMObject> actor = map.get(identifier);
		if ((actor == null) && (nextInfo != null))
		{
			return nextInfo.get(identifier);
		}
		return actor;
	}
}
