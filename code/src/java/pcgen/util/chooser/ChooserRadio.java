/**
 * ChooserRadio.java
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
 * Created on Jan 21st, 2003, 11:44 PM
 * @author Jayme Cox <jaymecox@users.sourceforge.net>
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 **/
package pcgen.util.chooser;

import java.util.ArrayList;
import java.util.List;

/**
 * This interface for a dialog accepts a list of available items,
 * and creates a set of radio buttons
 * This forces the user to choose one and only one selection
 * The dialog is always modal, so a call to show() will block execution
 **/
public interface ChooserRadio
{
	/**
	 * Set available list
	 * @param availableList
	 */
	void setAvailableList(List availableList);

	/**
	 * Set the combo box data
	 * @param cmbLabelText
	 * @param cmbData
	 */
	void setComboData(final String cmbLabelText, List cmbData);

	/**
	 * Set the message text
	 * @param messageText
	 */
	void setMessageText(String messageText);

	/**
	 * Get selected list
	 * @return selected list
	 */
	ArrayList<String> getSelectedList();

	/**
	 * Set the title
	 * @param title
	 */
	void setTitle(String title);

	/**
	 * Set visible flag
	 * @param b
	 */
	void setVisible(boolean b);

	/**
	 * Show
	 */
	void show();
}
