/*
 * NonGuiChooserRadio.java
 * Copyright 2006 (C) James Dempsey <jdempsey@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created on 6/05/2006
 *
 * $Id:  $
 *
 */
package pcgen.gui.utils;

import java.util.ArrayList;
import java.util.List;

import pcgen.util.chooser.ChooserRadio;

/**
 * <code>NonGuiChooserRadio</code> is quick fix for running radio chooser  
 * dependant code in a non-GUI environment. It is assumed that this will 
 * only be created when the answer is already known. 
 *
 * Last Editor: $Author:  $
 * Last Edited: $Date:  $
 *
 * @author James Dempsey <jdempsey@users.sourceforge.net>
 * @version $Revision: $
 */
public class NonGuiChooserRadio implements ChooserRadio
{

	/**
	 * Construct a new NonGuiChooserRadio instance. 
	 */
	public NonGuiChooserRadio()
	{
		// Do Nothing
	}

	/**
	 * @see pcgen.util.chooser.ChooserRadio#setAvailableList(java.util.List)
	 */
    @Override
	public void setAvailableList(List availableList)
	{
		// Do Nothing
	}

	/**
	 * @see pcgen.util.chooser.ChooserRadio#setComboData(java.lang.String, java.util.List)
	 */
    @Override
	public void setComboData(String cmbLabelText, List cmbData)
	{
		// Do Nothing
	}

	/**
	 * @see pcgen.util.chooser.ChooserRadio#setMessageText(java.lang.String)
	 */
    @Override
	public void setMessageText(String messageText)
	{
		// Do Nothing
	}

	/**
	 * @see pcgen.util.chooser.ChooserRadio#getSelectedList()
	 */
    @Override
	public ArrayList<String> getSelectedList()
	{
		return null;
	}

	/**
	 * @see pcgen.util.chooser.ChooserRadio#setTitle(java.lang.String)
	 */
    @Override
	public void setTitle(String title)
	{
		// Do Nothing
	}

	/**
	 * @see pcgen.util.chooser.ChooserRadio#setVisible(boolean)
	 */
    @Override
	public void setVisible(boolean b)
	{
		throw new UnsupportedOperationException(
			"NonGuiChooserRadio cannot be shown.");
	}

	/**
	 * @see pcgen.util.chooser.ChooserRadio#show()
	 */
    @Override
	public void show()
	{
		setVisible(true);
	}

}
