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

import java.text.MessageFormat;
import java.util.Objects;

import pcgen.base.lang.CaseInsensitiveString;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.MapKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.cdom.helper.InfoUtilities;
import pcgen.util.Logging;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * A InfoModel is a TemplateHashModel that wraps the Info data in a CDOMObject
 */
public class InfoModel implements TemplateHashModel
{
	/**
	 * The underlying CharID for this InfoModel
	 */
	private final CharID id;

	/**
	 * The underlying CDOMObject for this InfoModel
	 */
	private final CDOMObject cdo;

	/**
	 * Constructs a new InfoModel with the given CharID and CDOMObject as the
	 * underlying information.
	 * 
	 * @param id
	 *            The CharID that underlies this InfoModel
	 * @param cdo
	 *            The CDOMObject that underlies this InfoModel
	 */
	public InfoModel(CharID id, CDOMObject cdo)
	{
		Objects.requireNonNull(id, "CharID cannot be null");
		Objects.requireNonNull(cdo, "CDOMObject cannot be null");
		this.id = id;
		this.cdo = cdo;
	}

	/**
	 * Acts as a hash for producing the contents of this model.
	 */
	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		CaseInsensitiveString cis = new CaseInsensitiveString(key);
		MessageFormat info = cdo.get(MapKey.INFO, cis);

		StringBuffer sb = new StringBuffer(200);
		if (info != null)
		{
			Object[] infoVars = InfoUtilities.getInfoVars(id, cdo, cis);
			info.format(infoVars, sb, null);
		}
		else
		{
			//TODO: This should actually throw an error but we can't for
			//now due to it breaking too many thing...
			//So we are just logging it for now.
			//--Connor Petty
			Logging.errorPrint("CDOMObject [" + cdo.getDisplayName() + "] does not have INFO of type " + key);
			//			throw new TemplateModelException(
			//				"CDOMObject did not have INFO of type " + key);
		}
		return FacetLibrary.getFacet(ObjectWrapperFacet.class).wrap(id, sb.toString());
	}

	@Override
	public boolean isEmpty()
	{
		return cdo.getKeysFor(MapKey.INFO).isEmpty();
	}
}
