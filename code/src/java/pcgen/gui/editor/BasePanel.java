/*
 * BasePanel.java
 * Copyright 2002 (C) Greg Bingleman <byngl@hotmail.com>
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
 * Created on November 5, 2002, 4:29 PM
 *
 * @(#) $Id$
 */
package pcgen.gui.editor;

import javax.swing.JPanel;

import pcgen.cdom.base.CDOMObject;

/**
 * <code>BasePanel</code>
 *
 * @author  Greg Bingleman <byngl@hotmail.com>
 * @version $Revision$
 */
abstract class BasePanel<T extends CDOMObject> extends JPanel implements PObjectUpdater<T>
{
	/**
     * Update the data in the panel with PObject
	 * @param thisPObject 
	 */
    @Override
	public abstract void updateData(T thisPObject);

    /**
     * Update the data in the view with PObject
     * @param thisPObject
     */
    @Override
	public abstract void updateView(T thisPObject);
}
