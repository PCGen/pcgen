/*
 * EditUtil.java
 * Copyright 2003 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on January 27, 2003, 3:19 AM
 *
 * @(#) $Id: EditUtil.java,v 1.13 2005/10/18 20:23:42 binkley Exp $
 */
package pcgen.gui.editor;

import pcgen.core.Constants;
import pcgen.core.PObject;

import javax.swing.JButton;
import javax.swing.JList;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * <code>EditUtil</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision: 1.13 $
 */
final class EditUtil
{
	/**
	 * Enable the associated button if single click.
	 * Return if true if double click on JList and the associated button is enabled.
	 * @param evt
	 * @param lst
	 * @param btn
	 * @return true if double click
	 */
	static boolean isDoubleClick(MouseEvent evt, JList lst, JButton btn)
	{
		if (lst.getMinSelectionIndex() >= 0)
		{
			switch (evt.getClickCount())
			{
				case 1:
					btn.setEnabled(true);

					break;

				case 2:

					if (btn.isEnabled())
					{
						return true;
					}

					break;

				default:
					break;
			}
		}

		return false;
	}

	static void addPObjectTypes(final PObject pobj, List l)
	{
		String aString;

		for (int i = pobj.getMyTypeCount(); i > 0;)
		{
			aString = pobj.getMyType(--i);

			if (!aString.equals(Constants.s_CUSTOM))
			{
				if (!l.contains(aString))
				{
					l.add(aString);
				}
			}
		}
	}

	static String delimitArray(Object[] objArray, char delim)
	{
		StringBuffer tbuf = new StringBuffer(100);

		for (int i = 0; i < objArray.length; ++i)
		{
			if (tbuf.length() != 0)
			{
				tbuf.append(delim);
			}

			tbuf.append(objArray[i].toString());
		}

		return tbuf.toString();
	}
}
