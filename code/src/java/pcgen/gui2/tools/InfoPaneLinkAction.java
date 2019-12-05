/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 */
package pcgen.gui2.tools;

import java.io.IOException;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import pcgen.cdom.base.Constants;
import pcgen.core.utils.MessageType;
import pcgen.core.utils.ShowMessageDelegate;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * The Class {@code InfoPaneLinkAction} acts on the user clicking on hyperlinks
 * in an info pane such as the source info pane.
 */
public class InfoPaneLinkAction implements HyperlinkListener
{

    private InfoPane infoPane;

    /**
     * Create a new instance.
     *
     * @param infoPane the infopane that this will listen to
     */
    public InfoPaneLinkAction(InfoPane infoPane)
    {
        this.infoPane = infoPane;
    }

    /**
     * Attach the handler to the on-screen field.
     */
    public void install()
    {
        infoPane.addHyperlinkListener(this);
    }

    /**
     * Detach the handler from the on-screen field.
     */
    public void uninstall()
    {
        infoPane.removeHyperlinkListener(this);
    }

    @Override
    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            try
            {
                DesktopBrowserLauncher.viewInBrowser(e.getURL());
            } catch (IOException e1)
            {
                Logging.errorPrint("Failed to open URL " //$NON-NLS-1$
                        + e.getURL() + " due to ", e1); //$NON-NLS-1$
                ShowMessageDelegate.showMessageDialog(
                        LanguageBundle.getFormattedString("in_Src_browser", e //$NON-NLS-1$
                                .getURL().toString()), Constants.APPLICATION_NAME, MessageType.ERROR);
            }
        }
    }

}
