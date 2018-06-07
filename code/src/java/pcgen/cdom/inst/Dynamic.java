/*
 * Copyright (c) 2016 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.cdom.inst;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import pcgen.base.formula.base.VarScoped;
import pcgen.cdom.base.Categorized;
import pcgen.cdom.base.Category;
import pcgen.cdom.formula.PCGenScoped;

/**
 * A Dynamic is an object where the type of object is defined in the data (e.g.
 * "DYNAMICSCOPE:MOVEMENT").
 */
public class Dynamic implements PCGenScoped, Categorized<Dynamic>
{

	/**
	 * The URI in which the Dynamic object was defined.
	 */
	private URI sourceURI;

	/**
	 * The name of this Dynamic object.
	 */
	private String name;

	/**
	 * The Category for this Dynamic object... This is the scope of the object.
	 */
	private Category<Dynamic> category;

	@Override
	public URI getSourceURI()
	{
		return sourceURI;
	}

	@Override
	public void setSourceURI(URI source)
	{
		sourceURI = source;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String getDisplayName()
	{
		return name;
	}

	@Override
	public String getKeyName()
	{
		return getDisplayName();
	}

	@Override
	public boolean isInternal()
	{
		return false;
	}

	@Override
	public boolean isType(String type)
	{
		return false;
	}

	@Override
	public Category<Dynamic> getCDOMCategory()
	{
		return category;
	}

	@Override
	public void setCDOMCategory(Category<Dynamic> cat)
	{
		category = cat;
	}

	@Override
	public String getLocalScopeName()
	{
		return "PC." + category.getKeyName();
	}

	@Override
	public VarScoped getVariableParent()
	{
		return null;
	}

	@Override
	public PCGenScoped getLocalChild(String childType, String childName)
	{
		return null;
	}

	@Override
	public List<String> getChildTypes()
	{
		return Collections.emptyList();
	}

	@Override
	public List<PCGenScoped> getChildren(String childType)
	{
		return null;
	}

	@Override
	public String toString()
	{
		return category.getDisplayName() + ":" + name;
	}
}
