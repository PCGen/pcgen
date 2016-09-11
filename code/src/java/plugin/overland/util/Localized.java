/*
 * Copyright 2012 Vincent Lhote
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
package plugin.overland.util;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jdom2.Element;

/**
 * Localized String. This class is used when there is language dependent
 * information in XML. It stores an element and eventually all of its
 * translations. If PCGen UI language can only be changed after a restart, the
 * other language information is not stored to reduce memory usage.
 * 
 * @author Vincent Lhote
 * 
 */
// TODO move to pcgen.?.util or gmgen.?.util
public class Localized
{
	/** Indicates if the PCGen need to be restarted to change the locale. In that case, no storing of other locale */
	private static final boolean NEED_RESTART = true;

	// ### XML element/attribute ###

	private static final String ATTRIBUTE_NAME = "name"; //$NON-NLS-1$
	private static final String ELEMENT_LOC = "loc"; //$NON-NLS-1$
	private static final String ATTRIBUTE_LANGUAGE = "language"; //$NON-NLS-1$

	/** used to produce names based on element name when the {@value #ATTRIBUTE_DEFAULTNAME} is missing */
	private static Map<String, Integer> unnamedCount = new HashMap<>();

	/** This is the default string of the node */
	private String defaultName;
	/** This is a map of the different strings by languages. Not used if {@link #NEED_RESTART} is {@code true}. */
	private Map<String, String> languageNames;
	/** Contains the string for the current locale. Only used if {@link #NEED_RESTART} is {@code true}. */
	private String defaultLocaleName;

	/**
	 * If no attribute is defined, the default name is empty.
	 * @param element
	 * @param attribute if <code>null</code>, uses the trimmed text of the node.
	 */
	public Localized(Element element, String attribute)
	{
		if (attribute == null)
			defaultName = element.getTextTrim();
		else
			defaultName = element.getAttributeValue(attribute);
		update(element, attribute);
	}

	/**
	 * Use a name based on the element name if no {@linkplain  #defaultName} is defined.
	 * @param element
	 */
	public Localized(Element element)
	{
		this(element, ATTRIBUTE_NAME);
		// use the element name if no default name, and a unique number
		if (defaultName == null)
		{
			String name = element.getName();
			Integer i = unnamedCount.get(name);
			synchronized (unnamedCount)
			{
				if (i == null)
				{
					i = 1;
				}
				else
				{
					i += 1;
				}
				unnamedCount.put(name, i);
			}
			defaultName = element.getName() + i;
		}
	}

	private void addName(String lang, String name)
	{
		if (NEED_RESTART && Locale.getDefault().getLanguage().equals(lang))
		{
			defaultLocaleName = name;
		}
		else
		{
			if (languageNames == null)
			{
				languageNames = new HashMap<>();
			}
			languageNames.put(lang, name);
		}
	}

	private String toString(Locale l)
	{
		if (languageNames != null)
		{
			String lang = l.getLanguage();
			if (languageNames.containsKey(lang))
			{
				return languageNames.get(lang);
			}
		}
		return defaultName;
	}

	@Override
	public String toString()
	{
		if (NEED_RESTART)
		{
			if (defaultLocaleName != null)
				return defaultLocaleName;
			else return defaultName;
		}
		return toString(Locale.getDefault());
	}

	/**
	 * 
	 * @param e
	 * @param attribute if <code>null</code>, use the trimmed text.
	 */
	private void update(Element e, String attribute)
	{
		List<?> children = e.getChildren(ELEMENT_LOC);
		for (Object object : children)
		{
			if (object instanceof Element)
			{
				Element child = (Element) object;
				String lang = child.getAttributeValue(ATTRIBUTE_LANGUAGE);
				String name;
				if (attribute == null)
					name = child.getTextTrim();
				else
					name = child.getAttributeValue(attribute);
				if (lang != null && !lang.isEmpty())
					addName(lang, name);
			}
		}
	}
}
