/*
 * Copyright 2017 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.base;

/**
 * TrainingStrategy is a DependencyStrategy used for "training" dynamic variables (These are
 * the control inputs to a dynamic variable - typically these must be static, but this
 * captures the variable that does the controlling). This assumes that only one variable
 * can control a dynamic variable.
 */
public class TrainingStrategy implements DependencyStrategy
{
	/**
	 * The Controlling VariableID as identified by this TrainingStrategy
	 */
	private VariableID<?> controlID;

	@Override
	public void addVariable(DependencyManager depManager, String varName)
	{
		if (controlID == null)
		{
			controlID = StaticStrategy.getVariableID(depManager, varName);
		}
		else
		{
			throw new UnsupportedOperationException(
				"Dynamic Dependency must be on a single control variable");
		}
	}

	/**
	 * Returns the VariableID identified as the controlling VariableID by this
	 * TrainingStrategy.
	 * 
	 * @return The VariableID identified as the controlling VariableID by this
	 *         TrainingStrategy
	 */
	public VariableID<?> getControlVar()
	{
		return controlID;
	}
}
