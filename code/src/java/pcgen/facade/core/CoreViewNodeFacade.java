/*
 * CoreViewNodeFacade.java
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
 * Created on 13/01/2013 11:51:01 AM
 *
 * $Id$
 */
package pcgen.facade.core;

import java.util.List;

/**
 * The interface {@code CoreViewNodeFacade} represents a node in the Core View
 * debugging tool. This exposes the objects within the core for inspection.
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public interface CoreViewNodeFacade
{
	
	/**
	 * Retrieve a list of the nodes which grant this node.
	 * @return The child nodes.
	 */
	public List<CoreViewNodeFacade> getGrantedByNodes();
	
	/**
	 * @return The type of node that this is.
	 */
	public String getNodeType();
	
	/**
	 * @return The key of this node, if any.
	 */
	public String getKey();
	
	/**
	 * @return The source (i.e. LST dataset) where this node is defined.
	 */
	public String getSource();
	
	/**
	 * @return A description of the requirements needed to take this object.
	 */
	public String getRequirements();
}
