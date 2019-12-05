/*
 * Copyright James Dempsey, 2013
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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.EquipmentBuilderFacade;
import pcgen.gui2.equip.EquipCustomPanel;
import pcgen.gui2.tools.Utility;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.component.OKCloseButtonBar;
import pcgen.system.LanguageBundle;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;

/**
 * The Class {@code EquipCustomizerDialog} provides a pop-up dialog that allows
 * the user to build up custom equipment items by adding equipment modifiers and
 * setting the name, cost etc.
 */
public final class EquipCustomizerDialog extends JDialog
{
    private final EquipCustomPanel equipCustomPanel;
    private boolean purchase;
    private boolean cancelled;

    /**
     * Create a new instance of KitSelectionDialog
     *
     * @param frame     The parent frame we are displaying over.
     * @param character The character being displayed.
     */
    public EquipCustomizerDialog(JFrame frame, CharacterFacade character, EquipmentBuilderFacade builder)
    {
        super(frame, true);
        setTitle(LanguageBundle.getString("in_itemCustomizer"));

        this.equipCustomPanel = new EquipCustomPanel(character, builder);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        initComponents();
        pack();
    }

    private void initComponents()
    {
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        pane.add(equipCustomPanel, BorderLayout.CENTER);

        ButtonBar buttonBar = new OKCloseButtonBar(
                this::doOK,
                this::doCancel
        );

        Button buyButton = new Button(LanguageBundle.getString("in_buy"));
        buttonBar.getButtons().add(buyButton);

        pane.add(GuiUtility.wrapParentAsJFXPanel(buttonBar), BorderLayout.PAGE_END);
        Utility.installEscapeCloseOperation(this);
    }

    private void doOK(javafx.event.ActionEvent event)
    {
        purchase = false;
        cancelled = false;
        dispose();
    }

    private void doCancel(javafx.event.ActionEvent event)
    {
        purchase = false;
        cancelled = true;
        dispose();
    }

    private void doBuy(javafx.event.ActionEvent event)
    {
        purchase = true;
        cancelled = false;
        dispose();
    }

    /**
     * @return boolean
     */
    public boolean isPurchase()
    {
        return purchase;
    }

    /**
     * @return boolean
     */
    public boolean isCancelled()
    {
        return cancelled;
    }

}
