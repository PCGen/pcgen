/*
 * Copyright 2003 (C) Devon Jones
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
package plugin.doomsdaybook;

import java.awt.Component;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import pcgen.core.SettingsHandler;
import pcgen.gui2.doomsdaybook.NameGenPanel;
import pcgen.gui2.tools.Utility;
import pcgen.pluginmgr.InteractivePlugin;
import pcgen.pluginmgr.PCGenMessage;
import pcgen.pluginmgr.PCGenMessageHandler;
import pcgen.pluginmgr.messages.FocusOrStateChangeOccurredMessage;
import pcgen.system.LanguageBundle;

public class RandomNamePlugin implements InteractivePlugin
{
    /**
     * Log name
     */
    private static final String LOG_NAME = "Random_Name_Generator";

    /**
     * The plugin menu item in the tools menu.
     */
    private final JMenuItem nameToolsItem = new JMenuItem();

    /**
     * The user interface that this class will be using.
     */
    private NameGenPanel theView;

    /**
     * The English name of the plugin.
     */
    private static final String NAME = "Random Names"; //$NON-NLS-1$
    /**
     * Key of plugin tab.
     */
    private static final String IN_NAME = "in_plugin_randomname_name"; //$NON-NLS-1$
    /**
     * Mnemonic in menu for {@link #IN_NAME}
     */
    private static final String IN_NAME_MN = "in_mn_plugin_randomname_name"; //$NON-NLS-1$

    /**
     * Starts the plugin, registering itself with the {@code TabAddMessage}.
     */
    @Override
    public void start(PCGenMessageHandler mh)
    {
        theView = new NameGenPanel(getDataDirectory());
        initMenus();
    }

    @Override
    public void stop()
    {
    }

    @Override
    public int getPriority()
    {
        return SettingsHandler.getGMGenOption(RandomNamePlugin.LOG_NAME + ".LoadOrder", 80);
    }

    /**
     * Accessor for name
     *
     * @return name
     */
    @Override
    public String getPluginName()
    {
        return RandomNamePlugin.NAME;
    }

    private static String getLocalizedName()
    {
        return LanguageBundle.getString(RandomNamePlugin.IN_NAME);
    }

    /**
     * Gets the view that this class is using.
     *
     * @return the view.
     */
    public Component getView()
    {
        return theView;
    }

    /**
     * listens to messages from the GMGen system, and handles them as needed
     *
     * @param message the source of the event from the system
     */
    @Override
    public void handleMessage(PCGenMessage message)
    {
        if (message instanceof FocusOrStateChangeOccurredMessage)
        {
            if (isActive())
            {
                nameToolsItem.setEnabled(false);
            } else
            {
                nameToolsItem.setEnabled(true);
            }
        }
    }

    /**
     * Returns true if this plugin is active
     *
     * @return true if this plugin is active
     */
    public boolean isActive()
    {
        JTabbedPane tp = Utility.getTabbedPaneFor(theView);
        return (tp != null) && JOptionPane.getFrameForComponent(tp).isFocused()
                && tp.getSelectedComponent().equals(theView);
    }

    /**
     * Initialize the menus
     */
    private void initMenus()
    {
        nameToolsItem.setMnemonic(LanguageBundle.getMnemonic(RandomNamePlugin.IN_NAME_MN));
        nameToolsItem.setText(RandomNamePlugin.getLocalizedName());
    }

    @Override
    public File getDataDirectory()
    {
        return new File(SettingsHandler.getGmgenPluginDir(), RandomNamePlugin.NAME);
    }
}
