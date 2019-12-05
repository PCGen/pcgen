/*
 * Copyright 2014 (C) Stefan Radermacher
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
 */
package plugin.lsttokens;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Campaign;
import pcgen.persistence.lst.InstallLstToken;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class SourcedateLst extends AbstractNonEmptyToken<CDOMObject>
        implements CDOMPrimaryToken<CDOMObject>, InstallLstToken
{

    @Override
    public String getTokenName()
    {
        return "SOURCEDATE";
    }

    @Override
    protected ParseResult parseNonEmptyToken(LoadContext context, CDOMObject obj, String value)
    {
        Date theDate = getDate(value);
        if (theDate == null)
        {
            return ParseResult.INTERNAL_ERROR;
        }
        context.getObjectContext().put(obj, ObjectKey.SOURCE_DATE, theDate);
        return ParseResult.SUCCESS;
    }

    private static Date getDate(String value)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM", Locale.ROOT); //$NON-NLS-1$
        Date theDate;
        try
        {
            theDate = df.parse(value);
        } catch (ParseException pe)
        {
            df = DateFormat.getDateInstance();
            try
            {
                theDate = df.parse(value);
            } catch (ParseException e)
            {
                try
                {
                    DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.ROOT);
                    theDate = formatter.parse(value);
                } catch (ParseException ipe)
                {
                    Logging.log(Logging.LST_ERROR, "Error parsing date", ipe);
                    return null;
                }
            }
        }
        return theDate;
    }

    @Override
    public String[] unparse(LoadContext context, CDOMObject obj)
    {
        Date date = context.getObjectContext().getObject(obj, ObjectKey.SOURCE_DATE);
        if (date == null)
        {
            return null;
        }
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        return new String[]{df.format(date)};
    }

    @Override
    public Class<CDOMObject> getTokenClass()
    {
        return CDOMObject.class;
    }

    @Override
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
