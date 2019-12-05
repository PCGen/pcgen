/*
 * Copyright James Dempsey, 2010
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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import pcgen.core.SettingsHandler;
import pcgen.gui2.doomsdaybook.NameGenPanel;
import pcgen.gui2.tools.Utility;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.component.OKCloseButtonBar;
import pcgen.system.LanguageBundle;

import javafx.scene.control.ButtonBar;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class {@code RandomNameDialog} is a dialog in which the user can
 * generate a random name for their character.
 */
public final class RandomNameDialog extends JDialog
{
    private final NameGenPanel nameGenPanel;
    private boolean cancelled;

    /**
     * Create a new Random Name Dialog
     *
     * @param frame  The parent frame. The dialog will be centred on this frame
     * @param gender The current gender of the character.
     */
    public RandomNameDialog(JFrame frame, String gender)
    {
        super(frame, LanguageBundle.getString("in_rndNameTitle"), true); //$NON-NLS-1$
        nameGenPanel = new NameGenPanel(new File(getDataDir()));
        nameGenPanel.setGender(gender);
        initUserInterface();
        pack();
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.setLocationRelativeTo(frame);
        cancelled = false;

        Utility.installEscapeCloseOperation(this);
    }

    private void initUserInterface()
    {
        getContentPane().setLayout(new BorderLayout());

        getContentPane().add(nameGenPanel, BorderLayout.CENTER);

        ButtonBar buttonBar = new OKCloseButtonBar(
                evt -> okButtonActionPerformed(),
                evt -> cancelButtonActionPerformed()
        );

        getContentPane().add(GuiUtility.wrapParentAsJFXPanel(buttonBar), BorderLayout.PAGE_END);
    }

    private void okButtonActionPerformed()
    {
        setVisible(false);
    }

    private void cancelButtonActionPerformed()
    {
        cancelled = true;
        setVisible(false);
    }

    /**
     * @return The directory where the random name data is held
     */
    private String getDataDir()
    {
        String pluginDirectory = SettingsHandler.getGmgenPluginDir().toString();

        return pluginDirectory + File.separator + "Random Names";
    }

    /**
     * @return The name the user generated.
     */
    public String getChosenName()
    {
        if (cancelled)
        {
            return StringUtils.EMPTY;
        }
        return nameGenPanel.getChosenName();
    }

    /**
     * @return the gender
     */
    public String getGender()
    {
        if (cancelled)
        {
            return StringUtils.EMPTY;
        }
        return nameGenPanel.getGender();
    }

}
