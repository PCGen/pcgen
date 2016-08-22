/*
 * RollMethodFacade.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Aug 31, 2008, 1:39:10 AM
 */
package pcgen.facade.core.generator;

import java.util.List;

/**
 *
 * @author Connor Petty &lt;cpmeister@users.sourceforge.net&gt;
 */
public interface RollMethodFacade extends StatGenerationFacade
{

	public List<String> getDiceExpressions();

	/**
	 * For any roll method that is assignable will show the rolls
	 * in a drop down menus that the user can then modify. If
	 * this roll method is not assignable, then the generated rolls
	 * will be immutable once they are rolled into the stat boxes.
	 * @return whether this generator is assignable
	 */
	public boolean isAssignable();

	public void setAssignable(boolean assignable);

	public void setDiceExpressions(List<String> expressions);

}
