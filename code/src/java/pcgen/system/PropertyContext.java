/*
 * Copyright 2009 Connor Petty <cpmeister@users.sourceforge.net>
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
 */
package pcgen.system;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * This class acts similarly to the Properties class but behaves differently
 * in that SubContexts can be created. These SubContexts share the properties
 * of its parent, but they use a different namespace than the parent when
 * a property is set. The root parent contains all the properties of its
 * children, and likewise, all the properties of the children can be edited
 * from the root parent. In child contexts, the properties of all other children
 * that share its ancestors are visible but only that child's namespace is editable.
 * It is considered bad practice to look at the other siblings' properties from within
 * a child.
 */
public class PropertyContext implements PropertyChangeListener
{

	protected final Properties properties;
	protected final PropertyContext parent;
	protected final PropertyChangeSupport support;
	protected final String name;

	protected PropertyContext(String name)
	{
		this(name, null);
	}

	protected PropertyContext(String name, PropertyContext parent)
	{
		this.name = name;
		this.support = new PropertyChangeSupport(this);
		this.parent = parent;
		this.properties = (parent == null) ? new Properties() : parent.properties;
	}

	/**
	 * Create a new PropertyContext with a supplied properties object. As
	 * the property object is supplied, it is expected that the parent will
	 * normally be null.
	 *
	 * @param name The filename of the context. Normally ends in .ini
	 * @param parent The parent context, normally null
	 * @param properties The properties object to be used.
	 */
	protected PropertyContext(String name, PropertyContext parent, Properties properties)
	{
		this.name = name;
		this.support = new PropertyChangeSupport(this);
		this.parent = parent;
		this.properties = properties;
	}

	public String getName()
	{
		return name;
	}

	public PropertyContext createChildContext(String name)
	{
		return new PropertyContext(name, this);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		support.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String property, PropertyChangeListener listener)
	{
		support.addPropertyChangeListener(property, listener);
	}

	/**
	 * Searches for the property with the specified key in this property context.
	 * The method returns {@code null} if the property is not found.
	 *
	 * @param   key the property key.
	 * @return  the value in this property context with the specified key value.
	 */
	public String getProperty(String key)
	{
		if (parent != null)
		{
			return parent.getProperty(name + '.' + key);
		}
		return properties.getProperty(key);
	}

	/**
	 * Functions similarly to {@code getProperty(key, defaultValue)} but
	 * with the difference that if a property with the specified key does
	 * not exists, then the property will be set to the {@code defaultValue}
	 * argument and subsequently returned.
	 * @param   key the property key
	 * @param   defaultValue a default value
	 * @return  the value in this property context with the specified key value.
	 */
	public String initProperty(String key, String defaultValue)
	{
		String value = getProperty(key);
		if (value != null)
		{
			return value;
		}
		setProperty(key, defaultValue);
		return defaultValue;
	}

	/**
	 * Searches for the property with the specified key in this property context.
	 * The method returns the default value argument if the property is not found.
	 *
	 * @param   key   the property key.
	 * @param   defaultValue   a default value.
	 * @return  the value in this property context with the specified key value.
	 */
	public String getProperty(String key, String defaultValue)
	{
		if (parent != null)
		{
			return parent.getProperty(name + '.' + key, defaultValue);
		}
		return properties.getProperty(key, defaultValue);
	}

	public Object setProperty(String key, String value)
	{
		if (value == null)
		{
			return removeProperty(key);
		}
		Object oldValue;
		if (parent != null)
		{
			oldValue = parent.setProperty(name + '.' + key, value);
		}
		else
		{
			oldValue = properties.setProperty(key, value);
		}
		support.firePropertyChange(key, oldValue, value);
		return oldValue;
	}

	Object removeProperty(String key)
	{
		Object oldValue;
		if (parent != null)
		{
			oldValue = parent.removeProperty(name + '.' + key);
		}
		else
		{
			oldValue = properties.remove(key);
		}
		support.firePropertyChange(key, oldValue, null);
		return oldValue;
	}

	/**
	 * Gets a property of the specified key as a list of String. If the list is not found, an empty list is returned.
	 * Note: This converts the string into a list by splitting it using ';' as a separator
	 * @param   key the property key
	 * @return  the value in this property context with the specified key value.
	 */
	List<String> getStringList(String key)
	{
		return getStringList(key, Collections.emptyList());
	}

	/**
	 * Gets a property of the specified key as a list of String
	 * Note: This converts the string into a list by splitting it using ';' as a separator
	 * @param   key the property key
	 * @param   defaultValue the default value, if the key value is not found
	 * @return  the value in this property context with the specified key value, or {@code defaultValue}.
	 */
	List<String> getStringList(String key, List<String> defaultValue)
	{
		return Optional.ofNullable(getProperty(key))
				.map(prop -> Arrays.asList(prop.split(";")))
				.orElse(defaultValue);
	}

	/**
	 * Sets property to the specified key to a list of String
	 * Note: This converts the string list into a single string
	 * by joining them using ';' as a separator
	 * @param key    key the property key
	 * @param value  the list of String, that will be stored into the property context
	 */
	void setStringList(String key, List<String> value)
	{
		setProperty(key, String.join(";", value));
	}

	public int getInt(String key)
	{
		return NumberUtils.toInt(getProperty(key));
	}

	public int getInt(String key, int defaultValue)
	{
		return NumberUtils.toInt(getProperty(key, Integer.toString(defaultValue)));
	}

	public void setInt(String key, int integer)
	{
		setProperty(key, Integer.toString(integer));
	}

	public int initInt(String key, int defaultValue)
	{
		return NumberUtils.toInt(initProperty(key, Integer.toString(defaultValue)));
	}

	public boolean getBoolean(String key)
	{
		return Boolean.parseBoolean(getProperty(key));
	}

	public boolean getBoolean(String key, boolean defaultValue)
	{
		return Boolean.parseBoolean(getProperty(key, Boolean.toString(defaultValue)));
	}

	public void setBoolean(String key, boolean bool)
	{
		setProperty(key, Boolean.toString(bool));
	}

	public boolean initBoolean(String key, boolean defaultValue)
	{
		return Boolean.parseBoolean(initProperty(key, Boolean.toString(defaultValue)));
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		support.firePropertyChange(evt);
	}

	/**
	 * Called after properties have been loaded to allow any required
	 * post-load processing to be performed.
	 */
	protected void afterPropertiesLoaded()
	{
	}

	/**
	 * Called before properties are saved to allow any required
	 * pre-save processing to be performed.
	 */
	protected void beforePropertiesSaved()
	{
	}

}
