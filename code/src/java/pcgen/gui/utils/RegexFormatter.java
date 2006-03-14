/*
 * RegexFormatter.java Portions Copyright 1994-2004 (C) Sun Microsystems
 * http://java.sun.com/products/jfc/tsc/articles/reftf/RegexFormatter.java
 *
 * Copyright 1994-2004 Sun Microsystems, Inc. All Rights Reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *
 * Redistribution of source code must retain the above Copyright 1994-2004 Sun
 * Microsystems, Inc. or the names of contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND
 * ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT
 * OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR
 * PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 *
 *
 * Created on September 10, 2003
 *
 */
package pcgen.gui.utils;

import javax.swing.text.DefaultFormatter;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * A regular expression based implementation of <code>AbstractFormatter</code>.
 */
public class RegexFormatter extends DefaultFormatter
{

	private Pattern pattern;

	private Matcher matcher;

	public RegexFormatter()
	{
		super();
	}

	/**
	 * Creates a regular expression based <code>AbstractFormatter</code>.
	 * <code>pattern</code> specifies the regular expression that will be
	 * used to determine if a value is legal.
	 * @param pattern
	 * @throws PatternSyntaxException
	 */
	public RegexFormatter(String pattern) throws PatternSyntaxException
	{
		this();
		setPattern(Pattern.compile(pattern));
	}

	/**
	 * Creates a regular expression based <code>AbstractFormatter</code>.
	 * <code>pattern</code> specifies the regular expression that will be
	 * used to determine if a value is legal.
	 * @param pattern
	 */
	public RegexFormatter(Pattern pattern)
	{
		this();
		setPattern(pattern);
	}

	/**
	 * Sets the pattern that will be used to determine if a value is legal.
	 * @param pattern
	 */
	public void setPattern(Pattern pattern)
	{
		this.pattern = pattern;
	}

	/**
	 * Returns the <code>Pattern</code> used to determine if a value is
	 * legal.
	 * @return Pattern
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	/**
	 * Sets the <code>Matcher</code> used in the most recent test if a value
	 * is legal.
	 * @param matcher
	 */
	protected void setMatcher(Matcher matcher)
	{
		this.matcher = matcher;
	}

	/**
	 * Returns the <code>Matcher</code> from the most test.
	 * @return matcher
	 */
	protected Matcher getMatcher()
	{
		return matcher;
	}

	/**
	 * Parses <code>text</code> returning an arbitrary Object. Some
	 * formatters may return null.
	 * <p>
	 * If a <code>Pattern</code> has been specified and the text completely
	 * matches the regular expression this will invoke <code>setMatcher</code>.
	 *
	 * @param text String to convert
	 * @return Object representation of text
	 * @throws ParseException if there is an error in the conversion
	 */
	public Object stringToValue(String text) throws ParseException
	{
		Pattern aPattern = getPattern();

		if (aPattern != null)
		{
			Matcher aMatcher = aPattern.matcher(text);

			if (aMatcher.matches())
			{
				setMatcher(aMatcher);
				return super.stringToValue(text);
			}
			throw new ParseException("Pattern did not match", 0);
		}
		return text;
	}
}
