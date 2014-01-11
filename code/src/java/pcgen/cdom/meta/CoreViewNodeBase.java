/*
 * CoreViewNode.java
 * Copyright James Dempsey, 2013
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
 * Created on 13/01/2013 12:05:46 PM
 *
 * $Id$
 */
package pcgen.cdom.meta;

import java.util.ArrayList;
import java.util.List;

import pcgen.core.facade.CoreViewNodeFacade;

/**
 * The Class <code>CoreViewNodeBase</code> is a base for defining nodes
 * represents an object stored in a facet.
 *
 * <br/>
 * Last Editor: $Author$
 * Last Edited: $Date$
 * 
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision$
 */
public abstract class CoreViewNodeBase implements CoreViewNodeFacade
{
	List<CoreViewNodeFacade> grantedByList = new ArrayList<CoreViewNodeFacade>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<CoreViewNodeFacade> getGrantedByNodes()
	{
		return grantedByList;
	}

	/**
	 * Add a node to the list of granted by nodes.
	 * @param node The node to add.
	 */
	public void addGrantedByNode(CoreViewNodeBase node)
	{
		grantedByList.add(node);
	}
}
