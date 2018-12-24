/*
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
 */
package pcgen.util.chooser;

import pcgen.facade.core.ChooserFacade;

/**
 * This interface indicates that the class can handle making a decision 
 * on a choice request.
 */
@Deprecated()
@FunctionalInterface
public interface ChoiceHandler
{
	/**
	 * Make a choice from the options present in the chooserFacade.
	 * @param chooserFacade The details of the choice options.
	 * @return true if a choice was made and recorded in the chooserFacade, false if not.
	 */
	public boolean makeChoice(ChooserFacade chooserFacade);
}
