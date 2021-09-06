/*
 * Copyright 2007 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content;

import java.util.Objects;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.ConcretePrereqObject;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.PCClass;

/**
 * A LevelCommandFactory is used to identify a PCClass which is to be applied
 * with a given number of levels (as defined by a Formula) to a PlayerCharacter.
 */
public class LevelCommandFactory extends ConcretePrereqObject implements Comparable<LevelCommandFactory>
{

	/**
	 * The PCClass to be applied to the PlayerCharacter when this
	 * LevelCommandFactory is executed.
	 */
	private final CDOMSingleRef<PCClass> pcClass;

	/**
	 * A Formula indicating the number of levels to be applied to the
	 * PlayerCharacter when this LevelCommandFactory is executed.
	 */
	private final Formula levels;

	/**
	 * Constructs a new LevelCommandFactory for the given PCClass and number of
	 * levels
	 * 
	 * @param classRef
	 *            A Reference to the PCClass to be applied to the
	 *            PlayerCharacter when this LevelCommandFactory is executed.
	 *            This reference must be resolved before the LevelCommandFactory
	 *            can be executed.
	 * @param lvls
	 *            A Formula indicating the number of levels to be applied to the
	 *            PlayerCharacter when this LevelCommandFactory is executed.
	 * @throws IllegalArgumentException
	 *             if the given Reference or Formula is null
	 */
	public LevelCommandFactory(CDOMSingleRef<PCClass> classRef, Formula lvls)
	{
		Objects.requireNonNull(classRef, "Class Reference for LevelCommandFactory cannot be null");
		Objects.requireNonNull(lvls, "Level Formula for LevelCommandFactory cannot be null");
		pcClass = classRef;
		levels = lvls;
	}

	/**
	 * Returns the Formula indicating the number of levels to be applied to the
	 * PlayerCharacter when this LevelCommandFactory is executed.
	 * 
	 * @return The Formula indicating the number of levels to be applied to the
	 *         PlayerCharacter when this LevelCommandFactory is executed.
	 */
	public Formula getLevelCount()
	{
		return levels;
	}

	/**
	 * Returns a Reference to the PCClass to be applied to the PlayerCharacter
	 * when this LevelCommandFactory is executed.
	 * 
	 * @return A Reference to the PCClass to be applied to the PlayerCharacter
	 *         when this LevelCommandFactory is executed.
	 */
	public PCClass getPCClass()
	{
		return pcClass.get();
	}

	/**
	 * Returns a representation of this LevelCommandFactory, suitable for
	 * storing in an LST file.
	 * 
	 * @return A representation of this LevelCommandFactory, suitable for
	 *         storing in an LST file.
	 */
	public String getLSTformat()
	{
		return pcClass.getLSTformat(false);
	}

	@Override
	public int hashCode()
	{
		return pcClass.hashCode() * 29 + levels.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (!(obj instanceof LevelCommandFactory lcf))
		{
			return false;
		}
		return levels.equals(lcf.levels) && pcClass.equals(lcf.pcClass);
	}

	/**
	 * Compares this LevelCommandFactory to another LevelCommandFactory.
	 * 
	 * @param other
	 *            The LevelCommandFactory to be compared to this
	 *            LevelCommandFactory.
	 * @return 0 if this LevelCommandFactory is equal to the given
	 *         LevelCommandFactory; -1 if this LevelCommandFactory has a PCClass
	 *         and level formula that sorts before the given
	 *         LevelCommandFactory; +1 if this LevelCommandFactory has a PCClass
	 *         and level formula that sorts before the given LevelCommandFactory
	 * @throws NullPointerException
	 *             if the given LevelCommandFactory is null
	 */
	@Override
	public int compareTo(LevelCommandFactory other)
	{
		int compareResult = ReferenceUtilities.REFERENCE_SORTER.compare(pcClass, other.pcClass);
		if (compareResult == 0)
		{
			if (levels.equals(other.levels))
			{
				return 0;
			}
            return levels.toString().compareTo(other.levels.toString());
		}
		return compareResult;
	}
}
