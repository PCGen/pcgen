/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

/**
 * @author zaister
 *
 */
public class SourcedateLst extends AbstractNonEmptyToken<CDOMObject> implements
		CDOMPrimaryToken<CDOMObject>, InstallLstToken
{

	@Override
	public String getTokenName()
	{
		return "SOURCEDATE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		CDOMObject obj, String value)
	{
		Date theDate = getDate(value);
		if (theDate == null)
		{
			return ParseResult.INTERNAL_ERROR;
		}
		context.getObjectContext().put(obj, ObjectKey.SOURCE_DATE, theDate);
		return ParseResult.SUCCESS;
	}

	private Date getDate(String value)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM"); //$NON-NLS-1$
		Date theDate;
		try
		{
			theDate = df.parse(value);
		}
		catch (ParseException pe)
		{
			df = DateFormat.getDateInstance();
			try
			{
				theDate = df.parse(value);
			}
			catch (ParseException e)
			{
				Logging.log(Logging.LST_ERROR, "Error parsing date", e);
				return null;
			}
		}
		return theDate;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		Date date = context.getObjectContext().getObject(obj,
				ObjectKey.SOURCE_DATE);
		if (date == null)
		{
			return null;
		}
		DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
		return new String[] { df.format(date) };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public boolean parse(Campaign campaign, String value, URI sourceURI)
	{
		Date theDate = getDate(value);
		if (theDate == null)
		{
			return false;
		}
		campaign.put(ObjectKey.SOURCE_DATE, theDate);
		return true;
	}
}
