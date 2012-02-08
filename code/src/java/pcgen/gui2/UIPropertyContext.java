/*
 * UIPropertyContext.java
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Apr 1, 2010, 4:26:54 PM
 */
package pcgen.gui2;

import java.awt.Color;
import java.io.File;
import pcgen.core.facade.CharacterFacade;
import pcgen.system.PropertyContext;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public final class UIPropertyContext extends PropertyContext
{

	public static final String CUSTOM_ITEM_COLOR = "customItemColor";
	public static final String NOT_QUALIFIED_COLOR = "notQualifiedColor";
	public static final String AUTOMATIC_COLOR = "automaticColor";
	public static final String VIRTUAL_COLOR = "virtualColor";
	public static final String QUALIFIED_COLOR = "qualifiedColor";
	public static final String ALWAYS_OPEN_EXPORT_FILE = "alwaysOpenExportFile";
	/**
	 * The character property for the initial tab to open
	 * this property corresponds to an integer value
	 */
	public static final String C_PROP_INITIAL_TAB = "initialTab";
	private static UIPropertyContext instance = null;

	private UIPropertyContext()
	{
		super("UIConfig.ini");
		setColor(CUSTOM_ITEM_COLOR, Color.BLUE);
		setColor(NOT_QUALIFIED_COLOR, Color.RED);
		setColor(AUTOMATIC_COLOR, Color.decode("0xB2B200"));
		setColor(VIRTUAL_COLOR, Color.MAGENTA);
		setColor(QUALIFIED_COLOR, Color.BLACK);

	}

	private UIPropertyContext(String name, UIPropertyContext parent)
	{
		super(name, parent);
	}

	@Override
	public UIPropertyContext createChildContext(String name)
	{
		return new UIPropertyContext(name, this);
	}

	public static UIPropertyContext createContext(String name)
	{
		return getInstance().createChildContext(name);
	}

	public static UIPropertyContext getInstance()
	{
		if (instance == null)
		{
			instance = new UIPropertyContext();
		}
		return instance;
	}

	public Color getColor(String key)
	{
		String prop = getProperty(key);
		if (prop == null)
		{
			return null;
		}
		return new Color(Integer.parseInt(prop, 16));
	}

	public Color getColor(String key, Color defaultValue)
	{
		String prop = getProperty(key, Integer.toString(defaultValue.getRGB(), 16));
		return new Color(Integer.parseInt(prop, 16));
	}

	public void setColor(String key, Color color)
	{
		setProperty(key, Integer.toString(color.getRGB(), 16));
	}

	public Color initColor(String key, Color defaultValue)
	{
		String prop = initProperty(key, Integer.toString(defaultValue.getRGB(), 16));
		return new Color(Integer.parseInt(prop, 16));
	}

	public static Color getCustomItemColor()
	{
		return getInstance().getColor(CUSTOM_ITEM_COLOR);
	}

	public static void setCustomItemColor(Color color)
	{
		getInstance().setColor(CUSTOM_ITEM_COLOR, color);
	}

	public static void setQualifiedColor(Color color)
	{
		getInstance().setColor(QUALIFIED_COLOR, color);
	}

	public static Color getQualifiedColor()
	{
		return getInstance().getColor(QUALIFIED_COLOR);
	}

	public static void setNotQualifiedColor(Color color)
	{
		getInstance().setColor(NOT_QUALIFIED_COLOR, color);
	}

	public static Color getNotQualifiedColor()
	{
		return getInstance().getColor(NOT_QUALIFIED_COLOR);
	}

	public static void setAutomaticColor(Color color)
	{
		getInstance().setColor(AUTOMATIC_COLOR, color);
	}

	public static Color getAutomaticColor()
	{
		return getInstance().getColor(AUTOMATIC_COLOR);
	}

	public static void setVirtualColor(Color color)
	{
		getInstance().setColor(VIRTUAL_COLOR, color);
	}

	public static Color getVirtualColor()
	{
		return getInstance().getColor(VIRTUAL_COLOR);
	}

	/**
	 * Attempts to create the property key for this character for the given property.
	 * This allows for character specific properties such that the key created with this method
	 * can be used as the key for any of the other PropertyContext methods.
	 * The following is a typical example of its usage:
	 * <br>
	 * <samp>
	 * String charKey = UIPropertyContext.createCharacterPropertyKey(aCharacter, "allowNegativeMoney");<br>
	 * if(charKey != null){<br>
	 * boolean bool = UIPropertyContext.getInstance().getBoolean(charKey);<br>
	 * }<br>
	 * </samp>
	 * @param character a CharacterFacade
	 * @param key a String property key
	 * @return the character property key or null if it could not be created
	 */
	public static String createCharacterPropertyKey(CharacterFacade character, String key)
	{
		return createFilePropertyKey(character.getFileRef().getReference(), key);
	}

	public static String createFilePropertyKey(File file, String key)
	{
		if (file == null)
		{
			return null;
		}
		String path = file.getAbsolutePath();
		return path + "." + key;
	}

}
