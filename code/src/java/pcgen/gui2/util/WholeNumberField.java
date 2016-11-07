/*
 * WholeNumberField.java
 * Copyright 2001 (C) Mario Bonassin <zebuleon@peoplepc.com>
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
 * Created on April 21, 2001, 2:15 PM
 */
package pcgen.gui2.util;

import pcgen.util.Logging;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * {@code WholeNumberField} .
 *
 * @author Mario Bonassin &lt;zebuleon@users.sourceforge.net&gt;
 */
public final class WholeNumberField extends JTextField implements Serializable
{
	private NumberFormat integerFormatter;

	/**
	 * Constructor
	 */
	public WholeNumberField()
	{
		this(0, 0);
	}

	/**
	 * Constructor (uses US locale)
	 * @param value	initial value for the whole number field.
	 * @param columns size of whole number field.
	 */
	public WholeNumberField(int value, int columns)
	{
		super(columns);

		// XXX -- bad to embed locales.
		integerFormatter = NumberFormat.getNumberInstance(Locale.US);
		integerFormatter.setParseIntegerOnly(true);
		setValue(value);
	}

	/**
	 * Set the field value.
	 * @param value
	 */
	public void setValue(int value)
	{
		setText(Integer.toString(value));
	}

	/**
	 * Get the field value.
	 * @return value
	 */
	public int getValue()
	{
		int retVal = 0;

		try
		{
			String text = getText();
			if (text == null || text.trim().isEmpty())
			{
				text = "0";
			}
			retVal = integerFormatter.parse(text).intValue();
		}
		catch (ParseException e)
		{
			// This should never happen because insertString allows
			// only properly formatted data to get in the field.
			Logging.errorPrint("Failed to parse WholeNumber value.", e);
		}

		return retVal;
	}

	@Override
	protected Document createDefaultModel()
	{
		return new WholeNumberDocument();
	}

	private static class WholeNumberDocument extends PlainDocument
	{
		/**
		 * @see javax.swing.text.PlainDocument#insertString(int, java.lang.String, javax.swing.text.AttributeSet)
		 */
		@Override
		public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException
		{
			final char[] source = str.toCharArray();
			final char[] result = new char[source.length];
			int j = 0;

			for (int i = 0; i < result.length; i++)
			{
				if ((i == 0) && (source[i] == '-'))
				{
					result[j++] = source[i];
				}
				else if (Character.isDigit(source[i]))
				{
					result[j++] = source[i];
				}
				else
				{
					Logging.errorPrint("insertString: " + source[i] + " in "
						+ str);
				}
			}

			super.insertString(offs, new String(result, 0, j), a);
		}
	}
}
