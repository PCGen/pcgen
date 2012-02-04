/*
 * PToolBar.java
 * Copyright 2001 (C) Mario Bonassin
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
 * Created on April 21, 2001, 2:15 PM
 * Modified June 5, 2001 by Bryan McRoberts (merton_monk@yahoo.com)
 *
 * Current Ver: $Revision$
 * Last Editor: $Author$
 * Last Edited: $Date$
 *
 */
package pcgen.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import pcgen.cdom.content.TabInfo;
import pcgen.core.Globals;
import pcgen.gui.filter.FilterConstants;
import pcgen.gui.filter.FilterDialogFactory;
import pcgen.gui.filter.Filterable;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.util.Logging;
import pcgen.system.LanguageBundle;
import pcgen.util.enumeration.Tab;

/**
 * @author  Mario Bonassin
 * @version $Revision$
 */
public class PToolBar extends JToolBar
{
	static final long serialVersionUID = 6970993764494046979L;

	// need this to get access from FilterDialog
	private static PToolBar currentInstance = null;
	private static JFrame helpFrame = new JFrame("Help");
	private static JEditorPane helpPane = new JEditorPane("text/html", "");
	JButton addKit;
	JButton closeItem;
	JButton gmgenItem;
	JButton newItem;
	JButton newNPCItem;
	JButton openItem;
	JButton printItem;
	JButton printPreviewItem;
	JButton saveItem;
	private FilterComponentListener fcl;
	private FilterToolTipButton openFilters;
	private JButton clearFilters;
	private JButton customFilters;
	private JButton editorFilters;
	private JButton helpItem;
	private JButton preferencesItem;

	/**
	 * Constructor
	 * @param main
	 */
	private PToolBar(PCGen_Frame1 main)
	{
		init(main);
		setFloatable(true);
		putClientProperty("JToolBar.isRollover", Boolean.TRUE);
		fcl = new FilterComponentListener();
	}

	public static void displayHelpPanel(boolean forceDisplay)
	{
		Component curPanel = PCGen_Frame1.getBaseTabbedPane().getSelectedComponent();

		if (curPanel == null)
		{
			return;
		}

		if (curPanel instanceof CharacterInfo)
		{
			curPanel = ((CharacterInfo) curPanel).getActivePane();
		}

		String panelName = "";

		if (curPanel != null)
		{
			panelName = curPanel.getName();
		}

		TabInfo ti = null;
		
		if ((panelName != null) && (panelName.length() > 0))
		{
			// Inventory tab has three subpanels
			if (panelName.equals(Tab.INVENTORY.toString()))
			{
				Component subPanel = ((JTabbedPane) curPanel).getSelectedComponent();
				panelName = subPanel.getName();
			}

			ti = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
					TabInfo.class, panelName);
		}

		if (ti == null)
		{
			return;
		}
		File helpFile = ti.getHelpContext();
		if (helpFile == null)
		{
			return;
		}

		try
		{
////////////
			//BufferedReader helpReader = new BufferedReader(new FileReader(helpFile));
			//
			// The following line causes the help files (which are html) to throw a
			// sun.io.MalformedInputException on the 1st character of the file (in most
			// cases the line is <html>)
			// Byngl--January 18, 2003
			//final BufferedReader helpReader = new BufferedReader(new InputStreamReader(new FileInputStream(helpFile), "UTF-8"));
			final BufferedReader helpReader = new BufferedReader(new FileReader(helpFile));

////////////
			int length = (int) helpFile.length();
			char[] inputLine = new char[length];
			helpReader.read(inputLine, 0, length);

//			int charCount = 0;
//			try
//			{
//				charCount = helpReader.read(inputLine, 0, length);
//			}
//			catch (sun.io.MalformedInputException exc)
//			{
//				Logging.errorPrint(Integer.toString(charCount) + " characters read before error in helpFile: " + helpFile);
//			}
			helpReader.close();

			String aString = new String(inputLine);

			int i = 0;
			boolean wholeFile = true;
			String rep = "";
			String other = ">";
			String search = "";

			for (int j = 0; j < 13; j++)
			{
				switch (j)
				{
					case 0:
						search = "<img";
						rep = " ";

						break;

					case 1:
						search = "<html ";
						rep = "<html>";

						break;

					case 2:
						search = "<body ";
						rep = "<body>";

						break;

					case 3:
						search = "<meta ";
						rep = " ";

						break;

					case 4:
						search = "<!--[";
						other = "<![endif]-->";

						break;

					case 5:
						search = "<xml>";
						other = "</xml>";

						break;

					case 6:
						search = "<![endif";
						other = ">";
						rep = " ";

						break;

					case 7:
						search = "![endif";
						other = ">";
						rep = " ";

						break;

					case 8:
						search = "<o:p";
						other = ">";
						rep = " ";

						break;

					case 9:
						search = "</o:p";
						other = ">";
						rep = " ";

						break;

					case 10:
						search = "<link ";
						other = ">";
						rep = " ";

						break;

					case 11:
						search = "<style";
						other = "</style>";
						rep = " ";

						break;

					case 12:
						search = "<li ";
						other = ">";
						rep = "<li>";

						break;

					default:
						Logging.errorPrint("Index " + j + " not handled in Ptoolbar.displayHelpPanel");

						break;
				}

				i = aString.indexOf(search);

				while (i >= 0)
				{
					int k = aString.substring(i).indexOf(other);
					String replacement = rep;

					if (k == -1)
					{
						replacement = " ";
					}

					aString = aString.substring(0, i) + replacement
							+ aString.substring(i + k + 1);
					i = aString.indexOf(search);
					wholeFile = false;
				}
			}

			if (!wholeFile)
			{
				helpPane.setText(aString);
			}
			else
			{
				URL aurl = helpFile.toURI().toURL();
				helpPane.setPage(aurl);
			}
		}
		catch (IOException e)
		{
			Logging.errorPrint("Something went wrong printing ", e);
		}

		if (helpFrame.isVisible() || forceDisplay)
		{
			helpFrame.setVisible(true);
		}

		helpPane.setCaretPosition(0);
	}

	/**
	 * @return the component event listener associated to this PToolBar instance
	 */
	public ComponentListener getComponentListener()
	{
		return fcl;
	}

	/**
	 * this method allows us to get the current instance of PToolBar
	 * we will use this in FilterDialog to get the appropriate instance
	 *
	 * @return the currently set PToolBar instance
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public static PToolBar getCurrentInstance()
	{
		return currentInstance;
	}

	/**
	 * switches the icon for the open filter button:
	 * set to "There are selected filters for the selected tab" icon
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public void setFilterActive()
	{
		openFilters.setActiveIcon();
	}

	/**
	 * switches the icon for the open filter button:
	 * set to "There are no selected filters for the selected tab" icon
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public void setFilterInactive()
	{
		openFilters.setInactiveIcon();
	}

	/**
	 * create a new PToolBar and registers it as current instance
	 * @param main
	 *
	 * @return the PToolBar instance
	 *
	 * author: Thomas Behr 11-02-02
	 */
	public static PToolBar createToolBar(PCGen_Frame1 main)
	{
		if (currentInstance == null)
		{
			currentInstance = new PToolBar(main);
		}

		return currentInstance;
	}

	/**
	 * switches the icon for the open filter button:
	 * set to "There are no selected filters for the selected tab" icon
	 * Then the open filter and the clear filter button are disabled
	 * We use this for tabs like "Misc" which are no instance of Filterable
	 *
	 * author: Thomas Behr 11-02-02
	 */
	private void disableFilterButtons()
	{
		setFilterInactive();
		filterButtonsSetEnabled(false);
	}

	private void filterButtonsSetEnabled(boolean b)
	{
		openFilters.setEnabled(b);
		clearFilters.setEnabled(b);
		customFilters.setEnabled(b);
		editorFilters.setEnabled(b);
	}

	private void init(PCGen_Frame1 main)
	{
		newItem = Utility.createButton(main.frameActionListener.newActionListener, "file.new",
				LanguageBundle.getString("in_mnuFileNewTip"), "New16.gif", false);
		add(newItem);

		newNPCItem = Utility.createButton(main.frameActionListener.newNPCActionListener, "file.newNPC",
				LanguageBundle.getString("in_mnuFileNewNPCTip"), "NewNPC16.gif", false);
		add(newNPCItem);

		openItem = Utility.createButton(main.frameActionListener.openActionListener, "file.open",
				LanguageBundle.getString("in_mnuFileOpenTip"), "Open16.gif", true);
		add(openItem);

		closeItem = Utility.createButton(main.frameActionListener.closeActionListener, "file.close",
				LanguageBundle.getString("in_mnuFileCloseTip"), "Close16.gif", false);
		add(closeItem);

		saveItem = Utility.createButton(main.frameActionListener.saveActionListener, "file.save",
				LanguageBundle.getString("in_mnuFileSaveTip"), "Save16.gif", false);
		add(saveItem);

		addSeparator();

		printPreviewItem = Utility.createButton(main.frameActionListener.printPreviewActionListener,
				"file.printpreview", LanguageBundle.getString("in_mnuFilePrintPreviewTip"), "PrintPreview16.gif", false);
		add(printPreviewItem);

		printItem = Utility.createButton(main.frameActionListener.printActionListener, "file.print",
				LanguageBundle.getString("in_mnuFilePrintTip"), "Print16.gif", false);
		add(printItem);

		addSeparator();

		openFilters = new FilterToolTipButton(IconUtilitities.getImageIcon("Zoom16.gif"),
				IconUtilitities.getImageIcon("ZoomHighlightBlue16.gif"));
		openFilters.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					FilterDialogFactory.showHideFilterSelectDialog();
				}
			});

		add(openFilters);

		clearFilters = Utility.createButton(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						FilterDialogFactory.clearSelectedFiltersForSelectedFilterable();
					}
				}, "filter.clear", LanguageBundle.getString("in_mnuToolsFiltersClearTip"), "RemoveZoom16.gif", false);
		add(clearFilters);

		customFilters = Utility.createButton(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						FilterDialogFactory.showHideFilterCustomDialog();
					}
				}, "filter.custom", LanguageBundle.getString("in_mnuToolsFiltersCustomTip"), "CustomZoom16.gif", false);
		add(customFilters);

		editorFilters = Utility.createButton(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						FilterDialogFactory.showHideFilterEditorDialog();
					}
				}, "filter.editor", LanguageBundle.getString("in_mnuToolsFiltersEditTip"), "EditZoom16.gif", false);
		add(editorFilters);

		addSeparator();

		addKit = Utility.createButton(main.frameActionListener.addKitActionListener, "assign.kit",
				LanguageBundle.getString("in_mnuFileAddKitTip"), "Information16.gif", false);
		add(addKit);

		addSeparator();

		preferencesItem = Utility.createButton(main.frameActionListener.preferencesActionListener,
				"settings.preferences", LanguageBundle.getString("in_mnuSettingsPreferencesTip"), "Preferences16.gif",
				true);
		add(preferencesItem);

		addSeparator();
		gmgenItem = Utility.createButton(main.frameActionListener.gmgenActionListener, "gmgen.load",
				LanguageBundle.getString("in_launchGMGen"), "gmgen_icon.png", true);
		add(gmgenItem);
		addSeparator();

		helpItem = Utility.createButton(new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						displayHelpPanel(true);
					}
				}, "help.context", LanguageBundle.getString("in_mnuHelpContext"), "ContextualHelp16.gif", true);
		add(helpItem);

		helpFrame.setSize(new Dimension(400, 400));
		helpFrame.getContentPane().add(new JScrollPane(helpPane));
		helpFrame.setLocation(100, 100);
		helpPane.setEditable(false);
	}

	private class FilterComponentListener extends ComponentAdapter
	{
		public void componentShown(ComponentEvent e)
		{
			PToolBar.displayHelpPanel(false);

			Component c = e.getComponent();

			// A component on a JTabbedPane will fire a componentShown when it is set
			// to be the top item on the tabbed pane, even if the tabbed pane itself
			// is not visible. So we check that the component's parent is visible
			// before believing that the supplied component is actually shown.
			if (c.getParent() instanceof JTabbedPane)
			{
				JTabbedPane tabPane = (JTabbedPane) c.getParent();

				if (!tabPane.isVisible())
				{
					return;
				}
			}

			if (c instanceof Filterable)
			{
				handleFilterableShown((Filterable) c);
			}
			else if (c instanceof CharacterInfo)
			{
				handleFilterableShown(((CharacterInfo) c).getSelectedFilterable());
			}
			else
			{
				disableFilterButtons();
				PCGen_Frame1.getInst().getPcgenMenuBar().getFiltersMenu().setEnabled(false);
			}
		}

		private void handleFilterableShown(Filterable f)
		{
			if ((f != null) && (f.getSelectionMode() != FilterConstants.DISABLED_MODE))
			{
				filterButtonsSetEnabled(true);

				if (f.getSelectedFilters().size() > 0)
				{
					setFilterActive();
				}
				else
				{
					setFilterInactive();
				}

				PCGen_Frame1.getInst().getPcgenMenuBar().getFiltersMenu().setEnabled(true);
			}
			else
			{
				disableFilterButtons();
				PCGen_Frame1.getInst().getPcgenMenuBar().getFiltersMenu().setEnabled(false);
			}
		}
	}

	private static class FilterIconButton extends JButton
	{
		private Icon activeIcon;
		private Icon inactiveIcon;

		public FilterIconButton(Icon inactiveIcon, Icon activeIcon)
		{
			super(inactiveIcon);
			this.activeIcon = activeIcon;
			this.inactiveIcon = inactiveIcon;

			// Work around old JDK bug on Windows
			this.setMargin(new Insets(0, 0, 0, 0));
		}

		public void setActiveIcon()
		{
			setIcon(activeIcon);
		}

		public void setInactiveIcon()
		{
			setIcon(inactiveIcon);
		}
	}

	private class FilterToolTipButton extends FilterIconButton
	{
		public FilterToolTipButton(Icon inactiveIcon, Icon activeIcon)
		{
			super(inactiveIcon, activeIcon);
			this.setToolTipText(LanguageBundle.getString("in_filterIcon"));
		}

		public String getToolTipText(MouseEvent event)
		{
			return FilterDialogFactory.getSelectedFiltersToolTipText();
		}
	}
}
