/*
 * Copyright James Dempsey, 2012
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
import java.awt.Container;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import pcgen.facade.core.CharacterFacade;
import pcgen.gui2.kits.KitPanel;
import pcgen.gui2.tools.Utility;
import pcgen.gui3.GuiUtility;
import pcgen.system.LanguageBundle;

import javafx.scene.control.Button;

/**
 * The Class {@code KitSelectionDialog} provides a pop-up dialog that allows
 * the user to add kits to a character. Kits are prepared groups of equipment and
 * other rules items.
 */
public final class KitSelectionDialog extends JDialog
{
    private final KitPanel kitPanel;

    /**
     * Create a new instance of KitSelectionDialog
     *
     * @param frame     The parent frame we are displaying over.
     * @param character The character being displayed.
     */
    public KitSelectionDialog(JFrame frame, CharacterFacade character)
    {
        super(frame, true);
        setTitle(LanguageBundle.getString("in_mnuEditAddKit")); //$NON-NLS-1$
        this.kitPanel = new KitPanel(character);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        initComponents();
        pack();
    }

    private void initComponents()
    {
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        pane.add(kitPanel, BorderLayout.CENTER);

        Button closeButton = new Button(LanguageBundle.getString("in_close"));
        closeButton.setOnAction(this::onClose);

        Box buttons = Box.createHorizontalBox();
        buttons.add(GuiUtility.wrapParentAsJFXPanel(closeButton));
        pane.add(buttons, BorderLayout.PAGE_END);

        Utility.installEscapeCloseOperation(this);
    }

    private void onClose(final javafx.event.ActionEvent actionEvent)
    {
        setVisible(true);
        dispose();
    }


}
