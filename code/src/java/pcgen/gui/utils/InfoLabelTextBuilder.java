/*
 * InfoLabelTextBuilder.java
 * Copyright 2007 (C) Koen Van Daele
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
 * Created on 4-feb-07
 *
 * $Id$
 */
package pcgen.gui.utils;

import pcgen.gui.HTMLUtils;
import pcgen.system.LanguageBundle;

/**
 * <code>InfoLabelTextBuilder</code> is a helper class for the various
 * setInfoLabelText methods in the gui tabs.
 *
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 * @author Koen Van Daele <vandaelek@users.sourceforge.net>
 * @version $Revision$
 */
public class InfoLabelTextBuilder {

	private final StringBuilder buffer = new StringBuilder(300);
	
	public InfoLabelTextBuilder()
	{
		buffer.append("<html>");
	}
	
	/**
	 * @param title Element that will be added as the start of the string and emphasized.
	 */
	public InfoLabelTextBuilder(String title)
	{
		buffer.append("<html>");
		appendTitleElement(title);
	}
	
	/**
	 * Adds a string to the LabelText.
	 * @param string String to add
	 * @return InfoLabelTextBuilder
	 */
	public InfoLabelTextBuilder append(final String string)
	{
		buffer.append(string);
		return this;
	}
	
	/**
	 * Adds a character to the LabelText.
	 * @param ch Char to add
	 * @return InfoLabelTextBuilder
	 */
	public InfoLabelTextBuilder append(final char ch)
	{
		buffer.append(ch);
		return this;
	}
	
	/**
	 * Adds a line break to the LabelText.
	 * @return InfoLabelTextBuilder
	 */
	public InfoLabelTextBuilder appendLineBreak()
	{
		buffer.append("<br>");
		return this;
	}
	
	/**
	 * Adds a spacer to the LabelText.
	 * @return InfoLabelTextBuilder
	 */
	public InfoLabelTextBuilder appendSpacer()
	{
		buffer.append(HTMLUtils.THREE_SPACES);
		return this;
	}
	
	/**
	 * Append a title element with a regular sized font.
	 * 
	 * @param title the title
	 */
	public void appendSmallTitleElement(final String title)
	{
		buffer.append("<b>").append(title).append("</b>");
	}
	
	/**
	 * Append a title element with a larger sized font.
	 * 
	 * @param title the title
	 */
	public void appendTitleElement(final String title)
	{
		buffer.append("<b><font size=+1>").append(title).append("</font></b>");
	}
	
	/**
	 * Adds an element to the labelText. The key will be put in bold-face.
	 * @param key The string that will be used as the key in the LabelText, e.g. SOURCE. 
	 * @param value The value that belongs to the key.
	 * @return InfoLabelTextBuilder
	 */
	public InfoLabelTextBuilder appendElement(final String key, final String value)
	{
		buffer.append("<b>").append(key).append(":</b>&nbsp;").append(value);
		return this;
	}
	
	/**
	 * Used for internationalisation. Looks up the property through the 
	 * <code>ProperyFactory</code> and uses that as the key.
	 * @param propertyKey The name of a property in the LanguageProperties file.
	 * @param value The value that belongs to the key.
	 * @return InfoLabelTextBuilder
	 */
	public InfoLabelTextBuilder appendI18nElement(final String propertyKey, final String value)
	{
		return appendElement(LanguageBundle.getString(propertyKey),value);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		buffer.append("</html>");
		return buffer.toString();
	}
}
