/*
 * Copyright 2004 (C) Ross M. Lodge
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
package plugin.initiative.gui;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * <p>
 * A utility class for building some GUI elements
 * </p>
 */
final class Utils
{

	private Utils()
	{
	}

	/**
	 * <p>Builds a formatted text field with specified min and max</p>
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 * @return JFormattedTextField
	 */
	static JSpinner buildIntegerField(int min, int max)
	{
		SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(min, min, max, 1);
		return new JSpinner(spinnerNumberModel);
	}

	/**
	 * <p>Builds a formatted text field with specified min and max</p>
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 * @return JFormattedTextField
	 */
	static JSpinner buildFloatField(float min, float max, double step)
	{
		SpinnerNumberModel spinnerNumberModel = new SpinnerNumberModel(min, min, max, step);
		return new JSpinner(spinnerNumberModel);
	}


}
