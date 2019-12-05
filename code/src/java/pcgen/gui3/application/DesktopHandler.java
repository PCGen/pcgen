/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
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

package pcgen.gui3.application;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.PreferencesEvent;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitResponse;

import pcgen.gui2.PCGenUIManager;

/**
 * DesktopHandler handles desktop-initiated events
 */
public final class DesktopHandler
{
    private static boolean initialized = false;

    private DesktopHandler()
    {
    }

    /**
     * Initialize the Mac-specific properties.
     * Create an ApplicationAdapter to listen for Help, Prefs, and Quit.
     */
    public static void initialize()
    {
        if (initialized)
        {
            return;
        }
        initialized = true;

        if (!Desktop.isDesktopSupported())
        {
            return;
        }

        Desktop theDesktop = Desktop.getDesktop();
        if (theDesktop.isSupported(Action.APP_ABOUT))
        {
            theDesktop.setAboutHandler(new AboutHandler());
        }
        if (theDesktop.isSupported(Action.APP_PREFERENCES))
        {
            theDesktop.setPreferencesHandler(new PreferencesHandler());
        }
        if (theDesktop.isSupported(Action.APP_QUIT_HANDLER))
        {
            theDesktop.setQuitHandler(new QuitHandler());
        }
    }

    private static class AboutHandler implements java.awt.desktop.AboutHandler
    {
        @Override
        public void handleAbout(final AboutEvent aboutEvent)
        {
            PCGenUIManager.displayAboutDialog();
        }
    }

    private static class PreferencesHandler implements java.awt.desktop.PreferencesHandler
    {
        @Override
        public void handlePreferences(final PreferencesEvent preferencesEvent)
        {
            PCGenUIManager.displayPreferencesDialog();
        }
    }

    private static class QuitHandler implements java.awt.desktop.QuitHandler
    {
        @Override
        public void handleQuitRequestWith(final QuitEvent quitEvent, final QuitResponse quitResponse)
        {
            PCGenUIManager.closePCGen();
        }
    }
}
