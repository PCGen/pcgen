/*
 * CampaignURL.java
 * Copyright 2008 (C) James Dempsey
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

package pcgen.cdom.content;

import java.net.URI;

/**
 * {@code CampaignURL} encapsulates a typed and labelled URL for a
 * campaign.
 * <p>
 * (Tue, 23 Dec 2008) $
 */
public class CampaignURL implements Comparable<CampaignURL>
{
    /**
     * An enum for the various types of Campaign URLs.
     */
    public enum URLKind
    {
        /**
         * Link is to a web site
         */
        WEBSITE,
        /**
         * Link is to a survey
         */
        SURVEY,
        /**
         * Link is to an eCommerce site to purchase the source
         */
        PURCHASE
    }

    /**
     * The kind of url
     */
    private final URLKind urlKind;
    /**
     * The name of the url (only for ecommerce sites)
     */
    private final String urlName;
    /**
     * The URI itself
     */
    private final URI uri;
    /**
     * The displayed description of the url
     */
    private final String urlDesc;

    /**
     * Instantiates a new campaign url.
     *
     * @param kind        the kind of url
     * @param name        the name of the url (only for ecommerce sites)
     * @param uri         The URI itself.
     * @param description the displayed description of the url
     */
    public CampaignURL(URLKind kind, String name, URI uri, String description)
    {
        this.urlKind = kind;
        this.urlName = name;
        this.uri = uri;
        this.urlDesc = description;
    }

    /**
     * @return the urlKind
     */
    public URLKind getUrlKind()
    {
        return urlKind;
    }

    /**
     * @return the urlName
     */
    public String getUrlName()
    {
        return urlName;
    }

    /**
     * @return the url
     */
    public URI getUri()
    {
        return uri;
    }

    /**
     * @return the urlDesc
     */
    public String getUrlDesc()
    {
        return urlDesc;
    }

    @Override
    public int compareTo(final CampaignURL that)
    {
        if (this == that)
        {
            return 0;
        }

        if (that.getUrlKind() != urlKind)
        {
            return urlKind.compareTo(that.getUrlKind());
        }

        if (!that.getUrlName().equals(urlName))
        {
            return urlName.compareTo(that.getUrlName());
        }

        if (!that.getUri().equals(uri))
        {
            return uri.toString().compareTo(that.getUri().toString());
        }

        return urlDesc.compareTo(that.getUrlDesc());
    }

    @Override
    public int hashCode()
    {
        return uri.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj instanceof CampaignURL)
        {
            CampaignURL that = (CampaignURL) obj;
            return that.getUrlKind() == urlKind && that.getUrlName().equals(urlName) && that.getUri().equals(uri)
                    && urlDesc.equals(that.getUrlDesc());
        }
        return false;
    }
}
