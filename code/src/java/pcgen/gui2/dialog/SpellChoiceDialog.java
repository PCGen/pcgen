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

import pcgen.facade.core.SpellBuilderFacade;
import pcgen.gui2.equip.SpellChoicePanel;
import pcgen.gui2.tools.Utility;
import pcgen.gui3.GuiUtility;
import pcgen.gui3.component.OKCloseButtonBar;
import pcgen.system.LanguageBundle;

import javafx.scene.control.ButtonBar;

/**
 * The Class {@code SpellChoiceDialog} provides a pop-up dialog that allows
 * the user to select a spell for inclusion in things like custom equipment
 * items.
 */
public final class SpellChoiceDialog extends JDialog
{
    private final SpellChoicePanel spellChoicePanel;
    private boolean cancelled;

    /**
     * Create a new instance of SpellChoiceDialog
     *
     * @param frame The parent frame we are displaying over.
     */
    public SpellChoiceDialog(JFrame frame, SpellBuilderFacade builder)
    {
        super(frame, true);
        setTitle(LanguageBundle.getString("in_csdChooseSpell"));
        this.spellChoicePanel = new SpellChoicePanel(builder);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        Utility.installEscapeCloseOperation(this);
        initComponents();
        pack();
    }

    private void initComponents()
    {
        Container pane = getContentPane();
        pane.setLayout(new BorderLayout());

        pane.add(spellChoicePanel, BorderLayout.CENTER);

        ButtonBar buttonBar = new OKCloseButtonBar(
                this::onOK,
                this::onCancel
        );

        pane.add(GuiUtility.wrapParentAsJFXPanel(buttonBar), BorderLayout.PAGE_END);
    }

    private void onOK(javafx.event.ActionEvent event)
    {
        cancelled = false;
        dispose();
    }

    private void onCancel(javafx.event.ActionEvent event)
    {
        cancelled = true;
        dispose();
    }

    public boolean isCancelled()
    {
        return cancelled;
    }

}
