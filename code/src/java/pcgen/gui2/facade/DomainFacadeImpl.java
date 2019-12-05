/**
 * Copyright James Dempsey, 2010
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.gui2.facade;

import java.util.List;

import pcgen.cdom.enumeration.SourceFormat;
import pcgen.cdom.enumeration.StringKey;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.QualifiedObject;
import pcgen.core.prereq.Prerequisite;
import pcgen.facade.core.DomainFacade;
import pcgen.util.SortKeyAware;

/**
 * The Class {@code DomainFacadeImpl} groups the prereqs for gaining access
 * to a domain with the domain. This allows these prereqs to be displayed and
 * enforced by the UI.
 */
public class DomainFacadeImpl extends QualifiedObject<Domain> implements DomainFacade, SortKeyAware
{

    /**
     * Constructor
     *
     * @param domain
     */
    public DomainFacadeImpl(Domain domain)
    {
        super(domain);
    }

    /**
     * Constructor
     *
     * @param domain
     * @param aPrereqList
     */
    public DomainFacadeImpl(final Domain domain, final List<Prerequisite> aPrereqList)
    {
        super(domain, aPrereqList);
    }

    @Override
    public String getKeyName()
    {
        return getRawObject().getKeyName();
    }

    @Override
    public String getSource()
    {
        return SourceFormat.getFormattedString(getRawObject(), Globals.getSourceDisplay(), true);
    }

    @Override
    public String getSourceForNodeDisplay()
    {
        return SourceFormat.getFormattedString(getRawObject(), SourceFormat.LONG, false);
    }

    @Override
    public String toString()
    {
        return getRawObject().toString();
    }

    @Override
    public boolean isNamePI()
    {
        return getRawObject().isNamePI();
    }

    @Override
    public String getType()
    {
        return getRawObject().getType();
    }

    @Override
    public String getSortKey()
    {
        String sortKey = getRawObject().get(StringKey.SORT_KEY);
        if (sortKey == null)
        {
            sortKey = getRawObject().getDisplayName();
        }
        return sortKey;
    }

}
