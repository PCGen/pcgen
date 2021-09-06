/*
 * Copyright 2014 (C) Stefan Radermacher
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
package pcgen.util.enumeration;

public enum Visibility
{

	HIDDEN("No"), // Does not show up either in the GUI or on the output sheet
	DEFAULT("Yes"), // Shows up both in the GUI and on the output sheet
	OUTPUT_ONLY("Export"), // Shows up on the output sheet, but not in the GUI
	DISPLAY_ONLY("Display"), //  Shows up in the GUI, but not on the output sheet
	QUALIFY("Qualify"); //Shows up only if qualified

	private final String text;

	Visibility(String s)
	{
		text = s;
	}

	@Override
	public String toString()
	{
		return text;
	}

	public String getLSTFormat()
	{
		return text.toUpperCase();
	}

	/**
	 * Determine if this visibility can be seen in the supplied view level.
	 * 
	 * @param view The view level.
	 * @return true if the visibility can be viewed, false if not.
	 */
	public boolean isVisibleTo(View view)
	{
		return switch (view)
				{
					case ALL -> true;
					case HIDDEN_DISPLAY -> (this == Visibility.HIDDEN || this == Visibility.OUTPUT_ONLY);
					case HIDDEN_EXPORT -> (this == Visibility.HIDDEN || this == Visibility.DISPLAY_ONLY);
					case VISIBLE_EXPORT -> (this == Visibility.DEFAULT || this == Visibility.OUTPUT_ONLY);
					case VISIBLE_DISPLAY -> (this == Visibility.DEFAULT || this == Visibility.DISPLAY_ONLY);
					default -> (this == Visibility.DEFAULT || this == Visibility.DISPLAY_ONLY);
				};
		/*
		 * TODO Need to deal with QUALIFY
		 */
	}

}
