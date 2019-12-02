/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content.fact;

import java.util.Objects;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.ContentDefinition;
import pcgen.cdom.enumeration.DataSetID;
import pcgen.cdom.enumeration.FactKey;
import pcgen.cdom.facet.CDOMWrapperInfoFacet;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.output.actor.FactKeyActor;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * A FactDefinition is a definition of a legal entry for a FACT: in a file. This
 * contains both the legal location (e.g. SKILL) as well as the name of the fact
 * (e.g. Possibility).
 * 
 * This is a structure used to contain information about a Fact. This is then
 * used to derive the other necessary components, including input tokens,
 * enforcement of required, output tokens, etc.
 * 
 * A FactDefinition is created when a FACTDEF: line is encountered in the Data
 * Control LST file.
 * 
 * @param <T>
 *            The Type of object upon which the FACT for this FactDefintion can
 *            be applied
 * @param <F>
 *            The format of the data stored in the Fact
 */
public class FactDefinition<T extends CDOMObject, F> extends ContentDefinition<T, F> implements FactInfo<T, F>
{

	/**
	 * The Fact Name for this FactDefinition.
	 */
	private String factName;

	@Override
	protected void activateKey()
	{
		//Necessary to get the key with getConstant early so later items can use valueOf
		getFactKey();
	}

	@Override
	protected void activateOutput(DataSetID dsID)
	{
		FactKeyActor<?> fca = new FactKeyActor<>(getFactKey());
		CDOMWrapperInfoFacet wiFacet = FacetLibrary.getFacet(CDOMWrapperInfoFacet.class);
		if (!wiFacet.set(dsID, getUsableLocation(), factName.toLowerCase(), fca))
		{
			Logging.errorPrint(getUsableLocation().getSimpleName() + " output " + factName.toLowerCase()
				+ " already exists, ignoring Visibility to EXPORT for FACT: " + factName);
		}
	}

	@Override
	protected void activateTokens(LoadContext context)
	{
		context.loadLocalToken(new FactParser<>(this));
		Boolean required = getRequired();
		if ((required != null) && required)
		{
			context.loadLocalToken(new FactDefinitionEnforcer<>(this));
		}
		Boolean selectable = getSelectable();
		if ((selectable != null) && selectable)
		{
			context.loadLocalToken(new FactGroupDefinition<>(this));
		}
	}

	/**
	 * Sets the Fact Name for this FactDefinition
	 * 
	 * @param name
	 *            The Fact Name for this FactDefinition
	 * @throws IllegalArgumentException
	 *             if the given name is null or empty
	 */
	public void setFactName(String name)
	{
		Objects.requireNonNull(name, "Fact Name cannot be null");
		if (name.isEmpty())
		{
			throw new IllegalArgumentException("Fact Name cannot be empty");
		}
		factName = name;
	}

	@Override
	public String getFactName()
	{
		return factName;
	}

	@Override
	public FactKey<F> getFactKey()
	{
		return FactKey.getConstant(getFactName(), getFormatManager());
	}

	@Override
	public String toString()
	{
		return "Fact Definition: " + getUsableLocation().getSimpleName() + ":" + factName + " ("
			+ getFormatManager().getIdentifierType() + ")";
	}

}
