/*
 *  GMGenSystem.java - main class for GMGen
 *  Copyright (C) 2002 Devon Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 */
package gmgen.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import pcgen.core.SettingsHandler;
import pcgen.gui2.dialog.AbstractPreferencesDialog;

import gmgen.GMGenSystem;

public class PreferencesDialog extends AbstractPreferencesDialog
{
	private static final String OPTION_NAME_DIVIDER = "PreferencesDialog.PrefsDividerLocation"; //$NON-NLS-1$
	private static final String OPTION_NAME_X = "PreferencesDialog.PrefsWindowX"; //$NON-NLS-1$
	private static final String OPTION_NAME_Y = "PreferencesDialog.PrefsWindowY"; //$NON-NLS-1$
	private static final String OPTION_NAME_WIDTH = "PreferencesDialog.PrefsWindowWidth"; //$NON-NLS-1$
	private static final String OPTION_NAME_HEIGHT = "PreferencesDialog.PrefsWindowHeight"; //$NON-NLS-1$

	private static final String EMPTY = PreferencesDialog.class.getName();

    private FlippingSplitPane jSplitPane1;
    private javax.swing.JTree prefsTree;
    private final PreferencesRootTreeNode root;
    private JPanel prefsPane;
    private CardLayout cardLayout;

    /**
     *  Creates new form PreferencesDialog
     *
     *@param  parent      Description of the Parameter
     *@param  modal       Description of the Parameter
     *@param root
     */
    public PreferencesDialog(final JFrame parent, final boolean modal, final PreferencesRootTreeNode root)
    {
        super(parent, GMGenSystem.APPLICATION_NAME, modal);
        this.root = root;
        prefsTree.setModel(new DefaultTreeModel(root));
		// TODO expand all leaf in prefsTree
        initLast();
        initPreferences();
    }

    /**
     * Apply the new preferences
     */
    public void applyPreferences()
    {
        root.getPanelList().forEach(PreferencesPanel::applyPreferences);
    }

    private void PrefsTreeActionPerformed()
    {
        Object obj = prefsTree.getLastSelectedPathComponent();

        if (obj instanceof DefaultMutableTreeNode)
        {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) obj;
            Object uobj = node.getUserObject();
            if(uobj instanceof PreferencesPanel)
            {
                cardLayout.show(prefsPane, uobj.toString());
            } else
            {
                cardLayout.show(prefsPane, PreferencesDialog.EMPTY);
            }
        }
    }

    @Override
    public void applyButtonActionPerformed()
    {
        applyPreferences();
    }

    /**
     *  Closes the dialog. Saves prefs before closing.
     */
    @Override
    protected void close()
    {
        SettingsHandler.setGMGenOption(OPTION_NAME_DIVIDER, jSplitPane1.getDividerLocation());

        SettingsHandler.setGMGenOption(OPTION_NAME_X, this.getX());
        SettingsHandler.setGMGenOption(OPTION_NAME_Y, this.getY());
        SettingsHandler.setGMGenOption(OPTION_NAME_WIDTH, this.getSize().width);
        SettingsHandler.setGMGenOption(OPTION_NAME_HEIGHT, this.getSize().height);
        super.close();
    }

    @Override
    protected JComponent getCenter()
    {
        jSplitPane1 = new FlippingSplitPane();
        prefsTree = new javax.swing.JTree();
        prefsTree.setRootVisible(false);
		prefsTree.setShowsRootHandles(true);
		cardLayout=new CardLayout();
		prefsPane = new JPanel(cardLayout);
		prefsPane.add(new JPanel(), EMPTY);

        addWindowListener(new java.awt.event.WindowAdapter()
            {
            @Override
                public void windowClosing(java.awt.event.WindowEvent e)
                {
                    close();
                }
            });


        jSplitPane1.setLeftComponent(new JScrollPane(prefsTree,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        prefsTree.addTreeSelectionListener(evt -> PrefsTreeActionPerformed());

        jSplitPane1.setRightComponent(prefsPane);

        return jSplitPane1;
    }

    /** Moves and resizes the preferences dialog based on your last opening of it */
    private void initLast()
    {
        int iDividerLocation = SettingsHandler.getGMGenOption(OPTION_NAME_DIVIDER, 118);
        jSplitPane1.setDividerLocation(iDividerLocation);

        int iWinX = SettingsHandler.getGMGenOption(OPTION_NAME_X, 0);
        int iWinY = SettingsHandler.getGMGenOption(OPTION_NAME_Y, 0);
        this.setLocation(iWinX, iWinY);

        int iWinWidth = SettingsHandler.getGMGenOption(OPTION_NAME_WIDTH, 550);
        int iWinHeight = SettingsHandler.getGMGenOption(OPTION_NAME_HEIGHT, 385);
        this.setSize(iWinWidth, iWinHeight);
    }

    /** Sets all the widgets to reflect the current preferences */
    private void initPreferences()
    {
        // add panel to card layout
        root.getPanelList().forEach(panel ->
        {
            panel.initPreferences();
            // add panel to card layout
            JPanel jp = new JPanel(new BorderLayout());
            JLabel comp = new JLabel(panel.toString());
            comp.setFont(UIManager.getFont("TitledBorder.font"));
            jp.add(comp, BorderLayout.NORTH);
            jp.add(panel, BorderLayout.CENTER);

            prefsPane.add(jp, panel.toString());
        });
    }
}
