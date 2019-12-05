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

import javax.swing.JDialog;
import javax.swing.JFrame;

import pcgen.gui2.prefs.PCGenPrefsPanel;
import pcgen.gui2.tools.Utility;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.component.OKCloseButtonBar;

import javafx.event.ActionEvent;
import javafx.scene.control.ButtonBar;

/**
 * The Class {@code SinglePrefDialog} displays a single
 * preference panel to the user.
 */
public final class SinglePrefDialog extends JDialog
{
    private final PCGenPrefsPanel prefsPanel;

    /**
     * Create a new modal SinglePrefDialog to display a particular panel.
     *
     * @param parent     The parent frame, used for positioning and to be modal
     * @param prefsPanel The panel to be displayed.
     */
    public SinglePrefDialog(JFrame parent, PCGenPrefsPanel prefsPanel)
    {
        super(parent, prefsPanel.getTitle(), true);

        this.prefsPanel = prefsPanel;

        ButtonBar controlPanel = new OKCloseButtonBar(
                this::okButtonActionPerformed,
                this::cancelButtonActionPerformed
        );

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(prefsPanel, BorderLayout.CENTER);
        this.getContentPane().add(GuiUtility.wrapParentAsJFXPanel(controlPanel), BorderLayout.PAGE_END);
        prefsPanel.applyOptionValuesToControls();
        pack();
        Utility.installEscapeCloseOperation(this);
    }

    private void cancelButtonActionPerformed(final ActionEvent actionEvent)
    {
        setVisible(false);
        this.dispose();
    }

    private void okButtonActionPerformed(final ActionEvent actionEvent)
    {
        prefsPanel.setOptionsBasedOnControls();
        setVisible(false);

        this.dispose();
    }

}
