/*
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
 */
package pcgen.gui2;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * The toolbar that is displayed in PCGen's main window. Provides shortcuts to
 * common PCGen activities.
 *
 * @see pcgen.gui2.PCGenFrame
 */
public final class PCGenToolBar extends JToolBar
{

	private final PCGenActionMap actionMap;

	PCGenToolBar(PCGenFrame frame)
	{
		this.actionMap = frame.getActionMap();
		initComponents();
	}

	private static JButton createToolBarButton(Action action)
	{
		JButton button = new JButton();
		button.putClientProperty("hideActionText", true);
		button.setFocusable(false);
		button.setHorizontalTextPosition(CENTER);
		button.setVerticalTextPosition(BOTTOM);
		button.setAction(action);
		return button;
	}

	private void initComponents()
	{
		setFloatable(false);
		setRollover(true);

		add(createToolBarButton(actionMap.get(PCGenActionMap.NEW_COMMAND)));
		add(createToolBarButton(actionMap.get(PCGenActionMap.OPEN_COMMAND)));
		add(createToolBarButton(actionMap.get(PCGenActionMap.CLOSE_COMMAND)));
		add(createToolBarButton(actionMap.get(PCGenActionMap.SAVE_COMMAND)));
		addSeparator();

		add(createToolBarButton(actionMap.get(PCGenActionMap.PRINT_COMMAND)));
		add(createToolBarButton(actionMap.get(PCGenActionMap.EXPORT_COMMAND)));
		addSeparator();

		add(createToolBarButton(actionMap.get(PCGenActionMap.PREFERENCES_COMMAND)));
		// addSeparator();

		// add(ToolBarUtilities.createToolBarButton(actionMap.get(PCGenActionMap.HELP_CONTEXT_COMMAND)));
	}

}
