/*
 * DecimalNumberField.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 13, 2003, 2:05 AM
 */
package pcgen.util;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;

/**
 * This text field handles decimal numbers.
 * It should be moved to gui.utils.
 *
 * @author     Greg Bingleman <byngl@hotmail.com>
 * @version    $Revision$
 */
public class DecimalNumberField extends JTextField
{
	private static final long serialVersionUID = 8756733358669554185L;
	private DecimalFormat doubleFormatter;
	private Toolkit toolkit;
	private boolean allowSign = false;
	private double lastVal;

	/**
	 * Constructor for DecimalNumberField.
	 * @param value double
	 * @param columns int
	 */
	public DecimalNumberField(double value, int columns)
	{
		super(columns);
		toolkit = Toolkit.getDefaultToolkit();
		doubleFormatter = new DecimalFormat();
		doubleFormatter.setParseIntegerOnly(false);
		setValue(value);
		addActionListener(new ActionListener()
		{
            @Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					lastVal = doubleFormatter.parse(getText()).floatValue();
				}
				catch (ParseException p)
				{
					setText(doubleFormatter.format(lastVal));
					toolkit.beep();
				}
			}
		});
	}

	/**
	 * Sets the field's value.
	 * Final as it's called from constructor.
	 * @param value double
	 */
	public final void setValue(double value)
	{
		lastVal = value;
		setText(doubleFormatter.format(value));
	}

	/**
	 * Gets the value from the field.
	 * @return double
	 */
	public double getValue()
	{
		double retVal;

		try
		{
			String text = getText();
			if (text == null || text.trim().length()==0)
			{
				text = "0";
			}
			retVal = doubleFormatter.parse(text).doubleValue();
			lastVal = retVal;
		}
		catch (ParseException e)
		{
			retVal = lastVal;
			setText(doubleFormatter.format(lastVal));
			toolkit.beep();
		}

		return retVal;
	}

	/**
	 * Creates the document model.
	 * @return Document
	 */
    @Override
	protected Document createDefaultModel()
	{
		return new DecimalNumberDocument();
	}

	/**
	 */
	private class DecimalNumberDocument extends PlainDocument
	{
		/**
		 * Inserts text if legal according to the settings.
		 * @param offs int
		 * @param str String
		 * @param a AttributeSet
		 * @throws BadLocationException
		 * @see Document#insertString(int, String, AttributeSet)
		 */
        @Override
		public void insertString(final int offs, final String str,
			final AttributeSet a) throws BadLocationException
		{
			final char[] source = str.toCharArray();
			final char[] result = new char[source.length];
			int j = 0;

			String curText = "";

			try
			{
				curText = getText(0, getLength());
			}
			catch (BadLocationException n)
			{
				//TODO: Should this really be ignored?
			}

			boolean foundPoint = (curText.indexOf('.') >= 0);
			final boolean foundSign = (curText.indexOf('-') >= 0);

			for (int i = 0; i < result.length; ++i)
			{
				final char ch = source[i];

				if (!allowSign && (ch == '-'))
				{
					toolkit.beep();
				}
				else if ((ch == '-') && ((i + offs) == 0))
				{
					if (!foundSign)
					{
						result[j++] = ch;
					}
					else
					{
						toolkit.beep();
					}
				}
				else
				{
					if (foundSign && ((i + offs) == 0))
					{
						toolkit.beep();
					}
					else if ((ch == '.') && (!foundPoint))
					{
						foundPoint = true;
						result[j++] = ch;
					}
					else
					{
						if (Character.isDigit(ch))
						{
							result[j++] = ch;
						}
						else
						{
							toolkit.beep();
						}
					}
				}
			}

			super.insertString(offs, new String(result, 0, j), a);
		}
	}
}
