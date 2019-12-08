/*
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
 * 
 */
package pcgen.gui2.tabs.bio;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import pcgen.cdom.base.Constants;
import pcgen.core.ChronicleEntry;
import pcgen.facade.core.CharacterFacade;
import pcgen.facade.core.DescriptionFacade;
import pcgen.gui2.tabs.CharacterInfoTab;
import pcgen.gui2.tabs.TabTitle;
import pcgen.gui2.tabs.models.TextFieldListener;
import pcgen.gui2.tools.Icons;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * The CampaignHistoryInfoPane displays a set of chronicles that the user can fill in for his
 * character.
 */
public class CampaignHistoryInfoPane extends JPanel implements CharacterInfoTab
{

	private static final String ADD_COMMAND = "ADD";
	private static final String ALL_COMMAND = "ALL";
	private static final String NONE_COMMAND = "NONE";
	private final TabTitle title = new TabTitle("Campaign History", null);
	private final JPanel chroniclesPane;
	private final JButton addButton;
	private final JButton allButton;
	private final JButton noneButton;

	public CampaignHistoryInfoPane()
	{
		this.addButton = new JButton();
		this.allButton = new JButton();
		this.noneButton = new JButton();
		this.chroniclesPane = new ChroniclesPane();
		initComponents();
	}

	private void initComponents()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		addButton.setText("Add Next Chronicle");
		addButton.setActionCommand(ADD_COMMAND);
		allButton.setText("All");
		allButton.setActionCommand(ALL_COMMAND);
		noneButton.setText("None");
		noneButton.setActionCommand(NONE_COMMAND);

		Box hbox = Box.createHorizontalBox();
		hbox.add(Box.createRigidArea(new Dimension(5, 0)));
		hbox.add(new JLabel("Check an item to include on your Character Sheet"));
		hbox.add(Box.createRigidArea(new Dimension(5, 0)));
		hbox.add(allButton);
		hbox.add(Box.createRigidArea(new Dimension(3, 0)));
		hbox.add(noneButton);
		hbox.add(Box.createHorizontalGlue());

		add(Box.createVerticalStrut(5));
		add(hbox);
		add(Box.createVerticalStrut(10));
		JScrollPane pane = new JScrollPane(chroniclesPane)
		{

			@Override
			public Dimension getMaximumSize()
			{
				Dimension size = getPreferredSize();
				size.width = Integer.MAX_VALUE;
				return size;
			}

			@Override
			public boolean isValidateRoot()
			{
				return false;
			}

		};
		pane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(pane);
		add(Box.createVerticalStrut(10));
		addButton.setAlignmentX(0.5f);
		add(addButton);
		add(Box.createVerticalStrut(5));
		add(Box.createVerticalGlue());
	}

	@Override
	public ModelMap createModels(CharacterFacade character)
	{
		ModelMap models = new ModelMap();
		models.put(ChronicleHandler.class, new ChronicleHandler(character));
		return models;
	}

	@Override
	public void restoreModels(ModelMap models)
	{
		models.get(ChronicleHandler.class).install();
	}

	@Override
	public void storeModels(ModelMap models)
	{
		models.get(ChronicleHandler.class).uninstall();
	}

	@Override
	public TabTitle getTabTitle()
	{
		return title;
	}

	/**
	 * A ChronicleHandler manages the set of chronicles for a character and handles the
	 * addButton, allButton, and noneButton button presses.
	 */
	private class ChronicleHandler implements ActionListener
	{

		private final List<ChroniclePane> chronicles;
		private final DescriptionFacade descFacade;

		public ChronicleHandler(CharacterFacade character)
		{
			descFacade = character.getDescriptionFacade();
			chronicles = new ArrayList<>();
			for (ChronicleEntry entry : descFacade.getChronicleEntries())
			{
				chronicles.add(new ChroniclePane(this, entry));
			}
		}

		/**
		 * Creates a new chronicle entry for the character.
		 */
		private ChroniclePane createNewChronicleEntry()
		{
			ChronicleEntry entry = descFacade.createChronicleEntry();
			ChroniclePane pane = new ChroniclePane(this, entry);
			return pane;
		}

		/**
		 * Installs this ChronicleHandler by attaching itself to the buttons and changes
		 * the components of the chroniclesPane to the ones contained in this handler.
		 */
		public void install()
		{
			addButton.addActionListener(this);
			allButton.addActionListener(this);
			noneButton.addActionListener(this);

			chroniclesPane.removeAll();
			for (ChroniclePane chroniclePane : chronicles)
			{
				//since these components are not part of the UI tree make sure that they
				//use the current LAF
				SwingUtilities.updateComponentTreeUI(chroniclePane);
				chroniclesPane.add(chroniclePane);
			}
			updateChroniclesPane();
		}

		/**
		 * Uninstalls this ChronicleHandler by removing its listeners from the buttons.
		 */
		public void uninstall()
		{
			addButton.removeActionListener(this);
			allButton.removeActionListener(this);
			noneButton.removeActionListener(this);
		}

		private void updateChroniclesPane()
		{
			boolean notempty = !chronicles.isEmpty();
			allButton.setEnabled(notempty);
			noneButton.setEnabled(notempty);
			chroniclesPane.revalidate();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (ADD_COMMAND.equals(e.getActionCommand()))
			{
				ChroniclePane pane = createNewChronicleEntry();
				chronicles.add(pane);
				chroniclesPane.add(pane);
				updateChroniclesPane();
			}
			else if (ALL_COMMAND.equals(e.getActionCommand()))
			{
				for (ChroniclePane chroniclePane : chronicles)
				{
					chroniclePane.setSelected(true);
				}
			}
			else if (NONE_COMMAND.equals(e.getActionCommand()))
			{
				for (ChroniclePane chroniclePane : chronicles)
				{
					chroniclePane.setSelected(false);
				}
			}
		}

		/**
		 * Deletes a chronicle from this character and updates the display
		 */
		public void deleteChroniclePane(ChroniclePane pane, ChronicleEntry entry)
		{
			descFacade.removeChronicleEntry(entry);
			chroniclesPane.remove(pane);
			chronicles.remove(pane);
			updateChroniclesPane();
		}

	}

	/**
	 * The ChroniclePane is a JPanel which displays and handles a single chronicle entry.
	 */
	private static class ChroniclePane extends JPanel implements ActionListener
	{

		private final JCheckBox checkBox = new JCheckBox();
		private final JButton deleteButton = new JButton();
		private final JTextField campaignField = new JTextField();
		private final JTextField adventureField = new JTextField();
		private final JTextField partyField = new JTextField();
		private final JTextField dateField = new JTextField();
		private final JFormattedTextField xpField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		private final JTextField gmField = new JTextField();
		private final JTextArea textArea = new JTextArea()
		{
			@Override
			public Dimension getMinimumSize()
			{
				Dimension minSize = super.getMinimumSize();
				Dimension prefSize = getPreferredSize();
				minSize.height = prefSize.height;
				return minSize;
			}

		};
		private ChronicleHandler handler;
		private ChronicleEntry entry;

		/**
		 * Creates a bare ChroniclePane that initializes the layout of its components. This is meant
		 * only for components that want to know the size of a ChroniclePane without fully
		 * initializing it.
		 */
		public ChroniclePane()
		{
			super(new GridBagLayout());
			setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 5));
			initComponents();
		}

		public ChroniclePane(ChronicleHandler handler, final ChronicleEntry entry)
		{
			this();
			this.handler = handler;
			this.entry = entry;
			populateComponents();
			textArea.setLineWrap(true);
			textArea.setWrapStyleWord(true);
			textArea.setRows(5);

			partyField.setColumns(10);
			dateField.setColumns(6);
			xpField.setColumns(6);
			gmField.setColumns(10);

			deleteButton.setMinimumSize(new Dimension(20, 20));
			deleteButton.setPreferredSize(new Dimension(20, 20));
			deleteButton.setFocusable(false);
			deleteButton.setMargin(new Insets(0, 0, 0, 0));
			deleteButton.setIcon(Icons.CloseX9.getImageIcon());
			deleteButton.addActionListener(this);
		}

		public void setSelected(boolean select)
		{
			checkBox.setSelected(select);
			entry.setOutputEntry(select);
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (fieldsNotBlank())
			{
				int ret = JOptionPane.showConfirmDialog(this,
					"<html>This chronicle has been written in." + "<br>Are you sure you want to delete it?</html>",
					Constants.APPLICATION_NAME, JOptionPane.YES_NO_OPTION);
				if (ret != JOptionPane.YES_OPTION)
				{//The user has not agreed so exit out
					return;
				}
			}
			handler.deleteChroniclePane(this, entry);
		}

		/**
		 * Checks all the fields to see if the user has entered any information into this
		 * chronicle entry.
		 * @return true if the fields are empty, false otherwise
		 */
		private boolean fieldsNotBlank()
		{
			return StringUtils.isNotBlank(campaignField.getText()) || StringUtils.isNotBlank(adventureField.getText())
				|| StringUtils.isNotBlank(partyField.getText()) || StringUtils.isNotBlank(dateField.getText())
				|| !NumberUtils.INTEGER_ZERO.equals(xpField.getValue()) || StringUtils.isNotBlank(gmField.getText())
				|| StringUtils.isNotBlank(textArea.getText());
		}

		private void initComponents()
		{
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0, 4, 0, 0);
			gbc.gridheight = 1;
			gbc.anchor = GridBagConstraints.BASELINE;
			add(checkBox, gbc);

			gbc.anchor = GridBagConstraints.EAST;
			gbc.insets = new Insets(0, 10, 0, 0);

			GridBagConstraints gbc2 = new GridBagConstraints();
			gbc2.fill = GridBagConstraints.HORIZONTAL;
			gbc2.gridwidth = 3;
			gbc2.insets = new Insets(1, 4, 1, 0);

			add(new JLabel("Campaign:"), gbc);
			add(campaignField, gbc2);
			add(new JLabel("Adventure:"), gbc);
			gbc2.gridwidth = GridBagConstraints.REMAINDER;
			add(adventureField, gbc2);

			add(new JLabel(), gbc);
			add(new JLabel("Party Name:"), gbc);
			gbc2.gridwidth = 1;
			gbc2.weightx = 0.35;
			add(partyField, gbc2);
			add(new JLabel("Date:"), gbc);
			gbc2.weightx = 0.15;
			add(dateField, gbc2);
			add(new JLabel("XP Gained:"), gbc);
			gbc2.weightx = 0.05;
			add(xpField, gbc2);
			add(new JLabel("GM:"), gbc);

			gbc2.gridwidth = GridBagConstraints.REMAINDER;
			gbc2.weightx = 0.45;
			add(gmField, gbc2);

			gbc.fill = GridBagConstraints.VERTICAL;
			gbc.insets = new Insets(0, 0, 2, 0);
			add(deleteButton, gbc);

			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.fill = GridBagConstraints.BOTH;
			gbc.insets = new Insets(0, 10, 0, 0);
			add(textArea, gbc);
		}

		/**
		 * Place the values from the entry into the fields.
		 */
		private void populateComponents()
		{
			checkBox.setSelected(entry.isOutputEntry());
			campaignField.setText(entry.getCampaign());
			adventureField.setText(entry.getAdventure());
			partyField.setText(entry.getParty());
			dateField.setText(entry.getDate());
			xpField.setValue(entry.getXpField());
			gmField.setText(entry.getGmField());
			textArea.setText(entry.getChronicle());

			// Listeners to write any entered values back to the character
			ActionListener actionListener = actionEvent -> entry.setOutputEntry(checkBox.getModel().isSelected());
			checkBox.addActionListener(actionListener);
			campaignField.getDocument().addDocumentListener(new TextFieldListener(campaignField)
			{

				@Override
				protected void textChanged(String text)
				{
					entry.setCampaign(text);
				}

			});
			adventureField.getDocument().addDocumentListener(new TextFieldListener(adventureField)
			{

				@Override
				protected void textChanged(String text)
				{
					entry.setAdventure(text);
				}

			});
			partyField.getDocument().addDocumentListener(new TextFieldListener(partyField)
			{

				@Override
				protected void textChanged(String text)
				{
					entry.setParty(text);
				}

			});
			dateField.getDocument().addDocumentListener(new TextFieldListener(dateField)
			{

				@Override
				protected void textChanged(String text)
				{
					entry.setDate(text);
				}

			});
			xpField.addPropertyChangeListener("value", evt -> entry.setXpField(((Number) xpField.getValue()).intValue()));
			gmField.getDocument().addDocumentListener(new TextFieldListener(gmField)
			{

				@Override
				protected void textChanged(String text)
				{
					entry.setGmField(text);
				}

			});
			textArea.getDocument().addDocumentListener(new TextFieldListener(textArea)
			{

				@Override
				protected void textChanged(String text)
				{
					entry.setChronicle(text);
				}

			});
		}

	}

	/**
	 * The ChroniclesPane is just a JPanel that implements Scrollable. This controls the ammount
	 * that this component is scrolled within its parent JScrollPane. By default we set this scroll
	 * distance to a single ChroniclePane's height such that one tick of the mouse scroll will
	 * scroll a single chronicle entry.
	 */
	private static class ChroniclesPane extends JPanel implements Scrollable
	{

		private final ChroniclePane dummyPane = new ChroniclePane();

		public ChroniclesPane()
		{
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		}

		@Override
		public Dimension getPreferredScrollableViewportSize()
		{
			return getPreferredSize();
		}

		@Override
		public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return 100;
		}

		@Override
		public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
		{
			return dummyPane.getPreferredSize().height + 10;
		}

		@Override
		public boolean getScrollableTracksViewportWidth()
		{
			return true;
		}

		@Override
		public boolean getScrollableTracksViewportHeight()
		{
			return false;
		}

	}

}
