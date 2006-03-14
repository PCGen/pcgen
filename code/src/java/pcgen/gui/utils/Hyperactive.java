/*
 * Hyperactive.java
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
 * Created on January 23, 2003, 10:03 PM
 *
 * $Id$
 */
package pcgen.gui.utils;

import pcgen.util.Logging;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

/**
 * This makes URLs load in a browser when clicked.
 *
 * @author     Greg Bingleman <byngl@hotmail.com>
 * @version    $Revision$
 */
public final class Hyperactive implements HyperlinkListener
{
	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
		{
			final JEditorPane pane = (JEditorPane) e.getSource();

			if (e instanceof HTMLFrameHyperlinkEvent)
			{
				final HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
				final HTMLDocument doc = (HTMLDocument) pane.getDocument();
				doc.processHTMLFrameHyperlinkEvent(evt);
			}
			else
			{
				try
				{
					Utility.viewInBrowser(e.getURL().toString());
				}
				catch (Throwable t)
				{
					Logging.errorPrint("Exception in Hyperactive::hyperlinkUpdate", t);
				}
			}
		}
	}
}
