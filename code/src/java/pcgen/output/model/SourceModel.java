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

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Campaign;
import pcgen.output.library.ObjectWrapperLibrary;
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
	/**
	 * The underlying CDOMObject, from which information is retrieved
	 */
	private final CDOMObject cdo;

	public SourceModel(CDOMObject cdo)
	{
		this.cdo = cdo;
	}

	@Override
	public TemplateModel get(String key) throws TemplateModelException
	{
		if (key.equals("custom"))
		{
			Boolean isCustom =
					Boolean.valueOf(cdo.isType(Constants.TYPE_CUSTOM));
			return ObjectWrapperLibrary.getInstance().wrap(isCustom);
		}
		else if (key.equals("long"))
		{
			return ObjectWrapperLibrary.getInstance().wrap(
				cdo.get(StringKey.SOURCE_LONG));
		}
		else if (key.equals("short"))
		{
			return ObjectWrapperLibrary.getInstance().wrap(
				cdo.get(StringKey.SOURCE_SHORT));
		}
		else if (key.equals("date"))
		{
			return ObjectWrapperLibrary.getInstance().wrap(
				cdo.get(ObjectKey.SOURCE_DATE));
		}
		else if (key.equals("page"))
		{
			return ObjectWrapperLibrary.getInstance().wrap(
				cdo.get(StringKey.SOURCE_PAGE));
		}
		else if (key.equals("web"))
		{
			return ObjectWrapperLibrary.getInstance().wrap(
				cdo.get(StringKey.SOURCE_WEB));
		}
		else if (key.equals("campaignsource"))
		{
			Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
			return ObjectWrapperLibrary.getInstance().wrap(
				campaign.get(StringKey.SOURCE_SHORT));
		}
		else if (key.equals("pubname"))
		{
			Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
			return ObjectWrapperLibrary.getInstance().wrap(
				campaign.getSafe(StringKey.PUB_NAME_LONG));
		}
		else if (key.equals("pubnameweb"))
		{
			Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
			return ObjectWrapperLibrary.getInstance().wrap(
				campaign.getSafe(StringKey.PUB_NAME_WEB));
		}
		throw new TemplateModelException(
			"source info does not have output of type " + key);
	}

	@Override
	public boolean isEmpty() throws TemplateModelException
	{
		return false;
	}
}
