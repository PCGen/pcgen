/*
 * Copyright 2009 (C) James Dempsey
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
package pcgen.gui2.converter.panel;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import pcgen.gui2.tools.Utility;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

/**
 * The Class {@code ConversionChoiceDialog} is responsible for
 * displaying choices the user needs to make as part of the conversion
 * of some data items.
 */
@SuppressWarnings("serial")
public class ConversionInputDialog extends JDialog implements ActionListener
{

    private final String introText;

    private String result;

    private JTextField field;

    /**
     * Instantiates a new decision dialog for the data converter.
     *
     * @param parent    the parent frame
     * @param introText the intro text to explain the dialogs purpose to the user.
     */
    ConversionInputDialog(Frame parent, String introText)
    {
        super(parent, "PCGenDataConvert", true);

        this.introText = introText;

        initComponents();
        setLocationRelativeTo(parent);
    }

    /**
     * @return the result
     */
    public String getResult()
    {
        return result;
    }

    /**
     * Initialises the user interface.
     */
    private void initComponents()
    {
        setLayout(new GridBagLayout());

        JLabel introLabel = new JLabel(introText);
        GridBagConstraints gbc = new GridBagConstraints();
        Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 1.0);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 5, 10);
        add(introLabel, gbc);

        field = new JTextField(20);
        Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, 1, 1.0, 0, GridBagConstraints.HORIZONTAL,
                GridBagConstraints.WEST);
        gbc.insets = new Insets(5, 10, 5, 10);
        add(field, gbc);

        JLabel dummy = new JLabel(" ");
        Utility.buildRelativeConstraints(gbc, 1, 1, 1.0, 0.0, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        add(dummy, gbc);

        JButton okButton = new JButton(LanguageBundle.getString("in_ok"));
        okButton.addActionListener(this);
        getRootPane().setDefaultButton(okButton);
        Utility.buildRelativeConstraints(gbc, GridBagConstraints.REMAINDER, GridBagConstraints.REMAINDER, 0, 0,
                GridBagConstraints.NONE, GridBagConstraints.EAST);
        gbc.insets = new Insets(5, 5, 10, 10);
        add(okButton, gbc);

        pack();

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                result = field.getText();
                setVisible(false);
                logInput();
            }
        });

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        result = field.getText();
        setVisible(false);
        logInput();
    }

    private void logInput()
    {
        Logging.log(Logging.INFO, "Decision required: " + introText + "\nValue entered: " + result);
    }
}
