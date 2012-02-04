/**
 * DomainFacadeImpl.java
 * Copyright James Dempsey, 2010
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
 *
 * Created on 09/02/2011 11:12:26 PM
 *
 * $Id$
 */
package pcgen.gui2.facade;

import java.util.List;

import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.Domain;
import pcgen.core.Globals;
import pcgen.core.QualifiedObject;
import pcgen.core.facade.DomainFacade;
import pcgen.core.prereq.Prerequisite;

/**
 * The Class <code>DomainFacadeImpl</code> groups the prereqs for gaining access 
 * to a domain with the domain. This allows these prereqs to be displayed and 
 * enforced by the UI.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public class DomainFacadeImpl extends QualifiedObject<Domain> implements DomainFacade
{

    /**
     * Constructor
     * @param domain
     */
	public DomainFacadeImpl(Domain domain)
	{
		super(domain);
	}

	/**
     * Constructor 
     * @param domain
     * @param aPrereqList
	 */
    public DomainFacadeImpl( final Domain domain, final List<Prerequisite> aPrereqList )
	{
		super(domain, aPrereqList);
	}

	/**
     * Constructor 
     * @param domain
     * @param prereq
	 */
    public DomainFacadeImpl( final Domain domain, Prerequisite prereq)
	{
		super(domain, prereq);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFacade#getKeyName()
	 */
	public String getKeyName()
	{
		return getRawObject().getKeyName();
	}

	/* (non-Javadoc)
	 * @see pcgen.core.facade.InfoFacade#getSource()
	 */
	public String getSource()
	{
		return SourceFormat.getFormattedString(getRawObject(),
			Globals.getSourceDisplay(), true);
	}

	/* (non-Javadoc)
	 * @see pcgen.core.QualifiedObject#toString()
	 */
	@Override
	public String toString()
	{
		return getRawObject().toString();
	}

}
