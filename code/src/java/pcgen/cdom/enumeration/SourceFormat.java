/*
 * Copyright 2009 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.cdom.enumeration;

import java.util.Date;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.Constants;
import pcgen.core.Campaign;
import pcgen.system.LanguageBundle;

public enum SourceFormat
{

    SHORT
            {
                @Override
                public String getField(CDOMObject cdo)
                {
                    return cdo.get(StringKey.SOURCE_SHORT);
                }

                @Override
                public String getPublisher(Campaign campaign)
                {
                    return Constants.EMPTY_STRING;
                }

                @Override
                public boolean allowsPage()
                {
                    return true;
                }
            },

    MEDIUM
            {
                @Override
                public String getField(CDOMObject cdo)
                {
                    return cdo.get(StringKey.SOURCE_LONG);
                }

                @Override
                public String getPublisher(Campaign campaign)
                {
                    return Constants.EMPTY_STRING;
                }

                @Override
                public boolean allowsPage()
                {
                    return true;
                }
            },

    LONG
            {
                @Override
                public String getField(CDOMObject cdo)
                {
                    return cdo.get(StringKey.SOURCE_LONG);
                }

                @Override
                public String getPublisher(Campaign campaign)
                {
                    return campaign.getSafe(StringKey.PUB_NAME_LONG);
                }

                @Override
                public boolean allowsPage()
                {
                    return true;
                }
            },

    DATE
            {
                @Override
                public String getField(CDOMObject cdo)
                {
                    Date date = cdo.get(ObjectKey.SOURCE_DATE);
                    return date == null ? null : date.toString();
                }

                @Override
                public String getPublisher(Campaign campaign)
                {
                    return Constants.EMPTY_STRING;
                }

                @Override
                public boolean allowsPage()
                {
                    return true;
                }
            },

    PAGE
            {
                @Override
                public String getField(CDOMObject cdo)
                {
                    return cdo.get(StringKey.SOURCE_PAGE);
                }

                @Override
                public String getPublisher(Campaign campaign)
                {
                    return Constants.EMPTY_STRING;
                }

                @Override
                public boolean allowsPage()
                {
                    return true;
                }
            },

    WEB
            {
                @Override
                public String getField(CDOMObject cdo)
                {
                    return cdo.get(StringKey.SOURCE_WEB);
                }

                @Override
                public String getPublisher(Campaign campaign)
                {
                    return campaign.getSafe(StringKey.PUB_NAME_WEB);
                }

                @Override
                public boolean allowsPage()
                {
                    return false;
                }
            };

    public abstract String getPublisher(Campaign campaign);

    public abstract String getField(CDOMObject cdo);

    /**
     * Does this format allow page information?
     *
     * <p>
     * If a format does not allow page information then page information will
     * not be included in the formatted output even if it is requested. This is
     * used primarily to prevent silly combinations like website, page number.
     *
     * @return <tt>true</tt> if the page information can be included.
     */
    public abstract boolean allowsPage();

    public static String formatShort(CDOMObject cdo, int aMaxLen)
    {
        String theShortName = cdo.get(StringKey.SOURCE_SHORT);
        if (theShortName == null)
        {
            // if this item's source is null, try to get it from theCampaign
            Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
            if (campaign != null)
            {
                theShortName = campaign.get(StringKey.SOURCE_SHORT);
            }
        }
        if (theShortName != null)
        {
            final int maxLen = Math.min(aMaxLen, theShortName.length());
            return theShortName.substring(0, maxLen);
        }
        return Constants.EMPTY_STRING;
    }

    /**
     * Returns a formatted string representation for this source based on the
     * <tt>SourceFormat</tt> passed in.
     *
     * @param cdo
     * @param format      The format to display the source in
     * @param includePage Should the page number be included in the output
     * @return A formatted string.
     */
    public static String getFormattedString(CDOMObject cdo, SourceFormat format, boolean includePage)
    {
        StringBuilder ret = new StringBuilder(100);
        if (cdo.isType(Constants.TYPE_CUSTOM))
        {
            ret.append(LanguageBundle.getString("in_custom")).append(" - ");
        }

        String source = format.getField(cdo);

        String publisher = null;
        Campaign campaign = cdo.get(ObjectKey.SOURCE_CAMPAIGN);
        if (campaign != null)
        {
            // If sourceCampaign object exists, get it's publisher entry for
            // the same key
            publisher = format.getPublisher(campaign);

            // if this item's source is null, try to get it from theCampaign
            if (source == null)
            {
                source = format.getField(campaign);
            }
        }
        if (source == null)
        {
            source = Constants.EMPTY_STRING;
        }

        if (publisher != null && !publisher.trim().isEmpty())
        {
            ret.append(publisher);
            ret.append(" - "); //$NON-NLS-1$
        }
        ret.append(source);

        if (includePage && format.allowsPage())
        {
            String thePageNumber = cdo.get(StringKey.SOURCE_PAGE);
            if (thePageNumber != null)
            {
                if (ret.length() != 0)
                {
                    ret.append(", "); //$NON-NLS-1$
                }
                ret.append(thePageNumber);
            }

        }
        return ret.toString();
    }

}
