/*
 *  EnhancedDialog.java - Handles OK/Cancel for you
 *  :noTabs=false:
 *
 *  Copyright (C) 2003 Devon Jones
 *  Derived from jEdit by Slava Pestov Copyright (C) 1998, 1999, 2001
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package gmgen.pluginmgr;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.event.*;

/**
 *  A dialog box that handles window closing, the ENTER key and the ESCAPE key
 *  for you. All you have to do is implement ok() (called when Enter is pressed)
 *  and cancel() (called when Escape is pressed, or window is closed).
 *
 *  Don't use this class, it's a special case for the plugin code
 *
 *@author     Soulcatcher
 *@since        GMGen 3.3
 */
public abstract class EnhancedDialog extends JDialog
{
	// protected members

	/**
	 *  Description of the Field
	 *@since        GMGen 3.3
	 */
	protected KeyHandler keyHandler;

	/**
	 *  Constructor for the EnhancedDialog object
	 *
	 *@param  parent  Description of the Parameter
	 *@param  title   Description of the Parameter
	 *@param  modal   Description of the Parameter
	 *@since        GMGen 3.3
	 */
	public EnhancedDialog(Frame parent, String title, boolean modal)
	{
		super(parent, title, modal);

		((Container) getLayeredPane()).addContainerListener(new ContainerHandler());
		getContentPane().addContainerListener(new ContainerHandler());

		keyHandler = new KeyHandler();
		addKeyListener(keyHandler);
		addWindowListener(new WindowHandler());

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	/**
	 *  method for Cancel
	 *@since        GMGen 3.3
	 */
	public abstract void cancel();

	/**
	 *  method for Ok
	 *@since        GMGen 3.3
	 */
	public abstract void ok();

	/**
	 * Recursively adds our key listener to sub-components
	 */
	public class ContainerHandler extends ContainerAdapter
	{
		/**
		 *  Description of the Method
		 *
		 *@param  evt  Description of the Parameter
		 *@since        GMGen 3.3
		 */
		public void componentAdded(ContainerEvent evt)
		{
			componentAdded(evt.getChild());
		}

		/**
		 *  Description of the Method
		 *
		 *@param  evt  Description of the Parameter
		 *@since        GMGen 3.3
		 */
		public void componentRemoved(ContainerEvent evt)
		{
			componentRemoved(evt.getChild());
		}

		private void componentAdded(Component comp)
		{
			comp.addKeyListener(keyHandler);

			if (comp instanceof Container)
			{
				Container cont = (Container) comp;
				cont.addContainerListener(this);

				Component[] comps = cont.getComponents();

				for (int i = 0; i < comps.length; i++)
				{
					componentAdded(comps[i]);
				}
			}
		}

		private void componentRemoved(Component comp)
		{
			comp.removeKeyListener(keyHandler);

			if (comp instanceof Container)
			{
				Container cont = (Container) comp;
				cont.removeContainerListener(this);

				Component[] comps = cont.getComponents();

				for (int i = 0; i < comps.length; i++)
				{
					componentRemoved(comps[i]);
				}
			}
		}
	}

	/**
	 * KeyHandler
	 */
	public class KeyHandler extends KeyAdapter
	{
		/**
		 *  Description of the Method
		 *
		 *@param  evt  Description of the Parameter
		 *@since        GMGen 3.3
		 */
		public void keyPressed(KeyEvent evt)
		{
			if (evt.isConsumed())
			{
				return;
			}

			if (evt.getKeyCode() == KeyEvent.VK_ENTER)
			{
				// crusty workaround
				Component comp = getFocusOwner();

				while (comp != null)
				{
					if (comp instanceof JComboBox)
					{
						JComboBox combo = (JComboBox) comp;

						if (combo.isEditable())
						{
							Object selected = combo.getEditor().getItem();

							if (selected != null)
							{
								combo.setSelectedItem(selected);
							}
						}

						break;
					}

					comp = comp.getParent();
				}

				ok();
				evt.consume();
			}
			else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE)
			{
				cancel();
				evt.consume();
			}
		}
	}

	class WindowHandler extends WindowAdapter
	{
		/**
		 *  Description of the Method
		 *
		 *@param  evt  Description of the Parameter
		 *@since        GMGen 3.3
		 */
		public void windowClosing(WindowEvent evt)
		{
			cancel();
		}
	}
}
