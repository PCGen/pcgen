/*
 * Copyright 2014-20 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.output.factory;

import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Globals;
import pcgen.output.base.ModelFactory;
import pcgen.output.channel.ChannelUtilities;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * An ChannelFactory is a ModelFactory that can wrap the variable in a CodeControl and
 * produce an ChennelModel for a given CharID
 */
public class ChannelFactory implements ModelFactory
{
	/**
	 * The global ObjectWrapperFacet used to wrap the current value of a
	 * variable
	 */
	private final ObjectWrapperFacet wrapperFacet = FacetLibrary.getFacet(ObjectWrapperFacet.class);

	/**
	 * The CControl for which this ChannelFactory can produce a ModelFactory.
	 */
	private final CControl control;

	/**
	 * Constructs a new ChannelFactory for the given CodeControl.
	 * 
	 * @param control
	 *            The CControl for which this ChannelFactory will produce ModelFactory
	 *            objects
	 */
	public ChannelFactory(CControl control)
	{
		this.control = control;
	}

	@Override
	public TemplateModel generate(CharID id)
	{
		String channelName = ControlUtilities.getControlToken(Globals.getContext(), control);
		Object value = ChannelUtilities.readGlobalChannel(id, channelName);
		try
		{
			return wrapperFacet.wrap(id, value);
		}
		catch (TemplateModelException e)
		{
			throw new IllegalArgumentException(
				"Unable to generate wrapper for Channel: " + control.getName()
					+ " value type: " + value.getClass().getCanonicalName(),
				e);
		}
	}
}
