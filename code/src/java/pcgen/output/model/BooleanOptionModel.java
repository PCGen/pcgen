/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.model;

import pcgen.system.PCGenSettings;

import freemarker.template.TemplateBooleanModel;

/**
 * An BooleanOptionModel is designed to process an interpolation and convert
 * that into a TemplateModel representing the contents of the specific Boolean
 * Preference being requested by its name
 */
public class BooleanOptionModel implements TemplateBooleanModel
{
	/**
	 * The String indicating the preference name underlying this
	 * BooleanOptionModel (for which the value will be returned)
	 */
	private final String prefName;

	/**
	 * The "default value" for the preference if it is not set in the options
	 * panel
	 */
	private final boolean defaultValue;

	/**
	 * Constructs a new BooleanOptionModel with the given preference name
	 * 
	 * @param preferenceName
	 *            The preference name underlying this BooleanOptionModel
	 * @param defaultValue
	 *            The default value for the preference if not set in the options
	 *            panel
	 */
	public BooleanOptionModel(String preferenceName, boolean defaultValue)
	{
		this.prefName = preferenceName;
		this.defaultValue = defaultValue;
	}

	@Override
	public boolean getAsBoolean()
	{
		return PCGenSettings.OPTIONS_CONTEXT.getBoolean(prefName, defaultValue);
	}
}
