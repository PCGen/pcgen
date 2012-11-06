/*
 *  pcgen - DESCRIPTION OF PACKAGE
 *  Copyright (C) 2004 Ross M. Lodge
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *
 *  The author of this program grants you the ability to use this code
 *  in conjunction with code that is covered under the Open Gaming License
 *
 *  Utils.java
 *
 *  Created on Feb 13, 2004, 3:54:51 PM
 */
package plugin.initiative.gui;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JFormattedTextField;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

/**
 * <p>
 * A utility class for building some GUI elements
 * </p>
 *
 * @author Ross M. Lodge
 *
 */
public class Utils
{

	/**
	 * <p>Builds a formatted text field with specified min and max</p>
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 * @return JFormattedTextField
	 */
	public static JFormattedTextField buildIntegerField(int min, int max)
	{
		java.text.NumberFormat numberFormat =
				java.text.NumberFormat.getIntegerInstance();
		NumberFormatter formatter = new NumberFormatter(numberFormat);
		formatter.setMinimum(Integer.valueOf(min));
		formatter.setMaximum(Integer.valueOf(max));
		final JFormattedTextField returnValue =
				new JFormattedTextField(formatter);
		returnValue.setColumns(3);
		returnValue.addPropertyChangeListener(new PropertyChangeListener()
		{

			Border m_originalBorder = returnValue.getBorder();

            @Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName() != null
					&& evt.getPropertyName().equals("editValid"))
				{
					if (evt.getNewValue() != null
						&& evt.getNewValue() instanceof Boolean)
					{
						if (((Boolean) evt.getNewValue()).booleanValue())
						{
							returnValue.setBorder(m_originalBorder);
						}
						else
						{
							returnValue.setBorder(BorderFactory
								.createLineBorder(Color.red));
						}
					}
				}
			}
		});
		return returnValue;
	}

	/**
	 * <p>Builds a formatted text field with specified min and max</p>
	 * 
	 * @param min minimum value
	 * @param max maximum value
	 * @return JFormattedTextField
	 */
	public static JFormattedTextField buildFloatField(float min, float max)
	{
		java.text.NumberFormat numberFormat =
				java.text.NumberFormat.getNumberInstance();

		// numberFormat.setParseIntegerOnly(false);

		NumberFormatter formatter = new NumberFormatter(numberFormat);
		//formatter.getCommitsOnValidEdit();
		formatter.setMinimum(Float.valueOf(min));
		formatter.setMaximum(Float.valueOf(max));
		final JFormattedTextField returnValue =
				new JFormattedTextField(formatter);
		returnValue.setColumns(4);
		returnValue.addPropertyChangeListener(new PropertyChangeListener()
		{

			Border m_originalBorder = returnValue.getBorder();

            @Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getPropertyName() != null
					&& evt.getPropertyName().equals("editValid"))
				{
					if (evt.getNewValue() != null
						&& evt.getNewValue() instanceof Boolean)
					{
						if (((Boolean) evt.getNewValue()).booleanValue())
						{
							returnValue.setBorder(m_originalBorder);
						}
						else
						{
							returnValue.setBorder(BorderFactory
								.createLineBorder(Color.red));
						}
					}
				}
			}
		});
		return returnValue;
	}

	/**
	 *
	 * <p>
	 * Builds a formatted text field with specified min and max, and attaches
	 * the slider to it via listeners. The text field gets it's min and max from
	 * the slider.
	 * </p>
	 *
	 * @param matchingSlider
	 * @return JFormattedTextField
	 */
	public static JFormattedTextField buildIntegerFieldWithSlider(
		final JSlider matchingSlider)
	{
		final JFormattedTextField returnValue =
				buildIntegerField(matchingSlider.getMinimum(), matchingSlider
					.getMaximum());
		returnValue.addPropertyChangeListener(new PropertyChangeListener()
		{

            @Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if ("value".equals(evt.getPropertyName()))
				{
					Number value = (Number) evt.getNewValue();
					if (value != null)
					{
						matchingSlider.setValue(value.intValue());
					}
				}
			}
		});
		matchingSlider.addChangeListener(new ChangeListener()
		{
            @Override
			public void stateChanged(ChangeEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				int value = source.getValue();
				if (!source.getValueIsAdjusting())
				{ //done adjusting
					returnValue.setValue(Integer.valueOf(value)); //update ftf value
				}
				else
				{ //value is adjusting; just set the text
					returnValue.setText(String.valueOf(value));
				}
			}
		});
		return returnValue;
	}

	/**
	 *
	 * <p>Builds the specified slider.</p>
	 * @param min
	 * @param max
	 * @return JSlider
	 */
	public static JSlider buildSlider(int min, int max)
	{
		return buildSlider(min, max, 1, 5);
	}

	/**
	 *
	 * <p>Builds the specified slider.</p>
	 * @param min
	 * @param max
	 * @param minorTick
	 * @param majorTick
	 * @return JSlider
	 */
	public static JSlider buildSlider(int min, int max, int minorTick,
		int majorTick)
	{
		JSlider slider = new JSlider();
		slider.setMinimum(min);
		slider.setMaximum(max);
		slider.setMajorTickSpacing(majorTick);
		slider.setMinorTickSpacing(minorTick);
		slider.setPaintTicks(true);
		return slider;
	}

}
