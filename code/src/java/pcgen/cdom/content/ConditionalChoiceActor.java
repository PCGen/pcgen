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
package pcgen.cdom.content;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.core.PlayerCharacter;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriter;

public class ConditionalChoiceActor extends ConcretePrereqObject implements
		ChooseResultActor
{

	private final ChooseResultActor actor;

	public ConditionalChoiceActor(ChooseResultActor ca)
	{
		if (ca == null)
		{
			throw new IllegalArgumentException("Cannot have null ChoiceActor");
		}
		actor = ca;
	}

	public void apply(PlayerCharacter pc, CDOMObject obj, String o)
	{
		if (qualifies(pc))
		{
			actor.apply(pc, obj, o);
		}
	}

	public void remove(PlayerCharacter pc, CDOMObject obj, String o)
	{
		actor.remove(pc, obj, o);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj instanceof ConditionalChoiceActor)
		{
			ConditionalChoiceActor other = (ConditionalChoiceActor) obj;
			return actor.equals(other.actor) && equalsPrereqObject(other);
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return actor.hashCode();
	}

	public String getSource()
	{
		return actor.getSource();
	}

	public String getLstFormat() throws PersistenceLayerException
	{
		StringBuilder sb = new StringBuilder();
		sb.append(actor.getLstFormat());
		if (hasPrerequisites())
		{
			sb.append('[').append(
					new PrerequisiteWriter()
							.getPrerequisiteString(getPrerequisiteList()))
					.append(']');
		}
		return sb.toString();
	}

}
