/*
 * Copyright 2014-15 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.content.factset;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.ContentDefinition;
import pcgen.cdom.enumeration.FactSetKey;
import pcgen.output.actor.FactSetKeyActor;
import pcgen.output.wrapper.CDOMObjectWrapper;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

public class FactSetDefinition<T extends CDOMObject, F> extends
		ContentDefinition<T, F> implements FactSetInfo<T, F>
{

	/**
	 * The FactSet name for this FactSetDefinition
	 */
	private String factSetName;

	/**
	 * @see pcgen.cdom.content.ContentDefinition#activateOutput()
	 */
	@Override
	protected void activateOutput()
	{
		FactSetKeyActor<F> fca = new FactSetKeyActor<F>(getFactSetKey());
		CDOMObjectWrapper cow = CDOMObjectWrapper.getInstance();
		if (!cow.load(getUsableLocation(), factSetName.toLowerCase(), fca))
		{
			Logging
				.errorPrint(getUsableLocation().getSimpleName()
					+ " output "
					+ factSetName.toLowerCase()
					+ " already exists, ignoring Visibility to EXPORT for FACTSET: "
					+ factSetName);
		}
	}

	/**
	 * @see pcgen.cdom.content.ContentDefinition#activateTokens(pcgen.rules.context.LoadContext)
	 */
	@Override
	protected void activateTokens(LoadContext context)
	{
		context.loadLocalToken(new FactSetParser<T, F>(this));
		Boolean required = getRequired();
		if ((required != null) && required.booleanValue())
		{
			context.loadLocalToken(new FactSetDefinitionEnforcer<T, F>(this));
		}
		Boolean selectable = getSelectable();
		if ((selectable != null) && selectable.booleanValue())
		{
			context.loadLocalToken(new FactSetGroupDefinition<T, F>(this));
		}
	}

	/**
	 * Sets the Fact Set Name for this FactDefinition
	 * 
	 * @param name
	 *            The Fact Set Name for this FactDefinition
	 * @throws IllegalArgumentException
	 *             if the given name is null or empty
	 */
	public void setFactSetName(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Fact Set Name cannot be null");
		}
		if (name.length() == 0)
		{
			throw new IllegalArgumentException("Fact Set Name cannot be empty");
		}
		factSetName = name;
	}

	/**
	 * @see pcgen.cdom.content.factset.FactSetInfo#getFactSetName()
	 */
	@Override
	public String getFactSetName()
	{
		return factSetName;
	}

	/**
	 * @see pcgen.cdom.content.factset.FactSetInfo#getFactSetKey()
	 */
	@Override
	public FactSetKey<F> getFactSetKey()
	{
		return FactSetKey.getConstant(factSetName, getFormatManager());
	}

}
