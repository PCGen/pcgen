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

import java.util.Date;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.CharID;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.cdom.facet.FacetLibrary;
import pcgen.cdom.facet.ObjectWrapperFacet;
import pcgen.core.Campaign;
import pcgen.output.base.SimpleWrapperLibrary;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * An SourceModel is designed to process an interpolation and convert that into
 * a TemplateHashModel representing the source information about an underlying
 * CDOMObject.
 */
public class SourceModel implements TemplateHashModel
{

	private static final ObjectWrapperFacet WRAPPER_FACET = FacetLibrary.getFacet(ObjectWrapperFacet.class);

	/**
	 * The underlying CharID used to get items from the underlying SourceModel
	 */
	private final CharID id;

	/**
	 * The underlying CDOMObject, from which information is retrieved
	 */
	private final CDOMObject cdo;

	/**
	 * Constructs a new SourceModel with the underlying CharID and CDOMObject.
	 * 
	 * @param id
	 *            The underlying CharID used to get items from the underlying SourceModel
	 * @param cdo
	 *            The underlying CDOMObject, from which information is retrieved
	 */
	public SourceModel(CharID id, CDOMObject cdo)
	{
		this.id = id;
		this.cdo = cdo;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		switch (key)
		{
			case "custom" -> {
				Boolean isCustom = cdo.isType(Constants.TYPE_CUSTOM);
				return SimpleWrapperLibrary.wrap(isCustom);
			}
			case "long" -> {
				String sourceLong = getSource(StringKey.SOURCE_LONG);
				return SimpleWrapperLibrary.wrap(sourceLong);
			}
			case "short" -> {
				String sourceShort = getSource(StringKey.SOURCE_SHORT);
				return SimpleWrapperLibrary.wrap(sourceShort);
			}
			case "date" -> {
				Date sourceDate = cdo.get(ObjectKey.SOURCE_DATE);
				//Fall back on Campaign if necessary
				if (sourceDate == null)
				{
					Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
					sourceDate = campaign.get(ObjectKey.SOURCE_DATE);
				}
				return SimpleWrapperLibrary.wrap(sourceDate);
			}
			case "page" -> {
				String sourcePage = getSource(StringKey.SOURCE_PAGE);
				return SimpleWrapperLibrary.wrap(sourcePage);
			}
			case "web" -> {
				String sourceWeb = getSource(StringKey.SOURCE_WEB);
				return SimpleWrapperLibrary.wrap(sourceWeb);
			}
			case "campaignsource" -> {
				Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
				return WRAPPER_FACET.wrap(id, campaign.get(StringKey.SOURCE_SHORT));
			}
			case "pubname" -> {
				Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
				return WRAPPER_FACET.wrap(id, campaign.getSafe(StringKey.PUB_NAME_LONG));
			}
			case "pubnameweb" -> {
				Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
				return WRAPPER_FACET.wrap(id, campaign.getSafe(StringKey.PUB_NAME_WEB));
			}
		}
		throw new TemplateModelException("source info does not have output of type " + key);
	}

	private String getSource(StringKey sourceWeb)
	{
		String sourceValue = cdo.get(sourceWeb);
		//Fall back on Campaign if necessary
		if (sourceValue == null)
		{
			Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
			sourceValue = campaign.get(sourceWeb);
		}
		return sourceValue;
	}

	@Override
	public boolean isEmpty()
	{
		return false;
	}
}
