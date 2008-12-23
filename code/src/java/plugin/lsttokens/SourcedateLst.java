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
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * @author zaister
 * 
 */
public class SourcedateLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>, InstallLstToken
{

	@Override
	public String getTokenName()
	{
		return "SOURCEDATE";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
			throws PersistenceLayerException
	{
		if (isEmpty(value))
		{
			return false;
		}
		Date theDate = getDate(value);
		if (theDate == null)
		{
			return false;
		}
		context.getObjectContext().put(obj, ObjectKey.SOURCE_DATE, theDate);
		return true;
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
		Date title = context.getObjectContext().getObject(obj,
				ObjectKey.SOURCE_DATE);
		if (title == null)
		{
			return null;
		}
		return new String[] { title.toString() };
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
