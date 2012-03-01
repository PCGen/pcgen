/*
 * DocumentChangeAdapter.java
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
 * Created on Aug 8, 2008, 2:50:37 PM
 */
package pcgen.gui2.util.event;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public abstract class DocumentChangeAdapter implements DocumentListener
{

    public abstract void documentChanged(DocumentEvent e);

	@Override
    public void insertUpdate(DocumentEvent e)
    {
        documentChanged(e);
    }

	@Override
    public void removeUpdate(DocumentEvent e)
    {
        documentChanged(e);
    }

	@Override
    public void changedUpdate(DocumentEvent e)
    {
        documentChanged(e);
    }

}
