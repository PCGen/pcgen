/*
 * Copyright 2012 Vincent Lhote
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
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.JDialog;

import pcgen.gui3.GuiUtility;
import pcgen.gui3.component.OKCloseButtonBar;
import pcgen.system.LanguageBundle;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;

/**
 * A dialog with a Ok, a cancel and apply button.
 */
public abstract class AbstractDialog extends JDialog
{
    protected static final int GAP = 12;

    protected AbstractDialog(Frame owner, String title, boolean modal)
    {
        super(owner, title, modal);
        initialize();
    }

    private void initialize()
    {
        OKCloseButtonBar buttonBar = new OKCloseButtonBar(
                evt -> okButtonActionPerformed(),
                evt -> dispose()
        );
        buttonBar.getOkButton().setText(LanguageBundle.getString(getOkKey()));

        if (includeApplyButton())
        {
            Button applyButton = new Button(LanguageBundle.getString("in_apply"));
            applyButton.setOnAction(evt -> applyButtonActionPerformed());
            ButtonBar.setButtonData(applyButton, ButtonBar.ButtonData.APPLY);
            buttonBar.getButtons().add(applyButton);
        }

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(getCenter(), BorderLayout.CENTER);
        getContentPane().add(GuiUtility.wrapParentAsJFXPanel(buttonBar), BorderLayout.PAGE_END);
    }

    protected String getOkKey()
    {
        return "in_ok";
    }

    protected abstract JComponent getCenter();

    protected boolean includeApplyButton()
    {
        return false;
    }

    /**
     * Defaults to calling apply and closing.
     */
    private void okButtonActionPerformed()
    {
        applyButtonActionPerformed();
        dispose();
    }

    /**
     * Defaults to hide and dispose.
     */
    protected void close()
    {
        setVisible(false);
        dispose();
    }

    /**
     * what to do if the ok button is pressed (beside closing the dialog)
     */
    protected abstract void applyButtonActionPerformed();

}
