/*
 *  Initiative - A role playing utility to track turns
 *  Copyright (C) 2002 Devon D Jones
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
 *  CastSpell.java
 *
 */
package plugin.initiative.gui;

import gmgen.plugin.Spell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import plugin.initiative.SpellModel;

/**
 * <p>
 * Dialog which casts a spell, creating a durationed event in the
 * initiative tracker.
 * </p>
 */
@SuppressWarnings("serial")
public class CastSpell extends StartEvent
{
	protected JEditorPane descText;
	protected JPanel descPanel;
	protected JScrollPane descScroll;

	/**
	 *  Creates new form CastSpell - used when you do know who your frame is
	 *
	 *@param  parent      Parent form
	 *@param  modal       is modal?
	 *@param  initiative  Initiative panel
	 */
	public CastSpell(java.awt.Frame parent, boolean modal, Initiative initiative)
	{
		super(parent, modal, initiative);
	}

	/**
	 *  Constructor for the CastSpell object - used when you know who your frame is
	 *  and you want to cast the spell for a particular player
	 *
	 *@param  parent      Parent form
	 *@param  modal       is modal
	 *@param  initiative  Initiative panel
	 *@param  player      player name
	 *@param  init        player's initiative
	 */
	public CastSpell(java.awt.Frame parent, boolean modal,
		Initiative initiative, String player, int init)
	{
		super(parent, modal, initiative, player, init);
	}

	/**
	 * <p>
	 * Sets the spell model for the dialog.  Although the dialog
	 * functions fine without this, setting the spell model turns on
	 * some additional capabilities.
	 * </p>
	 *
	 * @param model A non-null spell model.
	 */
	public void setSpellModel(SpellModel model)
	{
		StringBuilder text = new StringBuilder();

		if (descPanel.getComponents().length == 0)
		{
			descText = new JEditorPane("text/html", "<html></html>");
			descScroll = new JScrollPane(descText);
			descPanel.add(descScroll, BorderLayout.CENTER);
		}

		descText.setBackground(getContentPane().getBackground());
		text.append("<html><body><font size='-2'>");
		text.append("<b>Duration: </b>" + model.getDuration() + " ");
		text.append("<b>Range: </b>" + model.getRange() + " ");
		text.append("<b>Save: </b>" + model.getSaveInfo() + " ");
		text.append("<b>Cast: </b>" + model.getCastingTime() + " ");
		text.append("<b>Target/Area: </b>" + model.getTarget() + " ");
		text.append("<b>Desc: </b>" + model.getDesc() + " ");
		text.append("</font></body></html>");
		descText.setText(text.toString());
		descPanel
			.setPreferredSize(new Dimension(mainPanel.getWidth() - 16, 75));
		descPanel.setMaximumSize(new Dimension(mainPanel.getWidth() - 16, 75));
		descPanel.setMinimumSize(new Dimension(mainPanel.getWidth() - 16, 75));
		pack();
		tEffect.setText(model.getDesc());
	}

	/**
	 *
	 * <p>Sets the spell name</p>
	 * @param spellName
	 */
	public void setSpellName(String spellName)
	{
		tName.setText(spellName);
	}

    @Override
	protected void save()
	{
		initiative.initList.add(new Spell(tName.getText(), tPlayer.getText(),
			tEffect.getText(), ((Integer) lDuration.getValue()).intValue(),
			((Integer) lInit.getValue()).intValue(), cbAlert.isSelected()));
		initiative.writeToCombatTabWithRound(tPlayer.getText() + " Cast "
			+ tName.getText());
		initiative.refreshTable();
		initiative.grabFocus();
		initiative.focusNextInit();
		setVisible(false);
		dispose();
	}

	/**
	 *
	 * <p>Initializes the components.</p>
	 *
	 */
    @Override
	protected void initComponents()
	{
		sTitle = "Cast Spell";
		sAlertLabel = "Alert when spell duration expires";

		basicSetup();

		initAllDefaultComponents();
		addDescriptionPanel();

		addButtons();

		finalizeSetup();

	}

	private void addDescriptionPanel()
	{
		GridBagConstraints gridBagConstraints = null;

		descPanel = new JPanel(new BorderLayout());

		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = gridBagRow;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.gridheight = 1;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.insets = new Insets(3, 3, 3, 3);
		mainPanel.add(descPanel, gridBagConstraints);

		gridBagRow++;
	}

}
