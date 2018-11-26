/*
 * Copyright 2010 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import pcgen.gui2.PCGenFrame;
import pcgen.gui2.tools.DesktopBrowserLauncher;
import pcgen.gui2.tools.Icons;
import pcgen.gui2.tools.Utility;
import pcgen.gui2.util.GridBoxLayout;
import pcgen.system.LanguageBundle;
import pcgen.system.PCGenPropBundle;
import pcgen.util.Logging;

import org.apache.commons.lang3.StringUtils;

public class AboutDialog extends JDialog
{

	public AboutDialog(PCGenFrame frame)
	{
		super(frame, LanguageBundle.getString("in_abt_title"), true); //$NON-NLS-1$
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new MainAbout(), BorderLayout.CENTER);
		pack();
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Utility.setComponentRelativeLocation(frame, this);
		Utility.installEscapeCloseOperation(this);
	}

}

/**
 * Create a simple panel to identify the program and those who contributed
 * to it.
 *
 * Modified 4/8/02 by W Robert Reed III (Mynex)
 * Adds List Monkeys Display area
 * Cleaned up naming schema
 */
final class MainAbout extends JPanel
{

	static final long serialVersionUID = -423796320641536943L;
	private JButton mailingList;
	private JButton wwwSite;

	/** Creates new form MainAbout */
	MainAbout()
	{
		initComponents();
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents()
	{
		JTabbedPane mainPane = new JTabbedPane();
		mainPane.add(LanguageBundle.getString("in_abt_credits"), buildCreditsPanel()); //$NON-NLS-1$
		mainPane.add(LanguageBundle.getString("in_abt_libraries"), buildIncludesPanel()); //$NON-NLS-1$
		mainPane.add(LanguageBundle.getString("in_abt_license"), buildLicensePanel()); //$NON-NLS-1$
		mainPane.add(LanguageBundle.getString("in_abt_awards"), buildAwardsPanel()); //$NON-NLS-1$

		setLayout(new BorderLayout());

		add(mainPane, BorderLayout.CENTER);
		mainPane.setPreferredSize(new Dimension(640, 480));
	}

	/**
	 * Construct the credits panel. This panel shows basic details
	 * about PCGen and lists all involved in it's creation.
	 *
	 * @return The credits panel.
	 */
	private JPanel buildCreditsPanel()
	{

		JLabel versionLabel = new JLabel();
		JLabel dateLabel = new JLabel();
		JLabel javaVersionLabel = new JLabel();
		JLabel leaderLabel = new JLabel();
		JLabel helperLabel = new JLabel();
		JLabel wwwLink = new JLabel();
		JLabel emailLabel = new JLabel();
		JTextField version = new JTextField();
		JTextField releaseDate = new JTextField();
		JTextField javaVersion = new JTextField();
		JTextField projectLead = new JTextField();
		wwwSite = new JButton();
		mailingList = new JButton();
		JTabbedPane monkeyTabPane = new JTabbedPane();

		JPanel aCreditsPanel = new JPanel();
		aCreditsPanel.setLayout(new GridBagLayout());

		// Labels

		versionLabel.setText(LanguageBundle.getString("in_abt_version")); //$NON-NLS-1$
		GridBagConstraints gridBagConstraints1 = buildConstraints(0, 0, GridBagConstraints.WEST);
		gridBagConstraints1.weightx = 0.2;
		aCreditsPanel.add(versionLabel, gridBagConstraints1);

		dateLabel.setText(LanguageBundle.getString("in_abt_release_date")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 1, GridBagConstraints.WEST);
		aCreditsPanel.add(dateLabel, gridBagConstraints1);

		javaVersionLabel.setText(LanguageBundle.getString("in_abt_java_version")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 2, GridBagConstraints.WEST);
		aCreditsPanel.add(javaVersionLabel, gridBagConstraints1);

		leaderLabel.setText(LanguageBundle.getString("in_abt_BD")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 3, GridBagConstraints.WEST);
		aCreditsPanel.add(leaderLabel, gridBagConstraints1);

		wwwLink.setText(LanguageBundle.getString("in_abt_web")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 4, GridBagConstraints.WEST);
		aCreditsPanel.add(wwwLink, gridBagConstraints1);

		emailLabel.setText(LanguageBundle.getString("in_abt_email")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 5, GridBagConstraints.WEST);
		aCreditsPanel.add(emailLabel, gridBagConstraints1);

		helperLabel.setText(LanguageBundle.getString("in_abt_monkeys")); //$NON-NLS-1$
		gridBagConstraints1 = buildConstraints(0, 6, GridBagConstraints.NORTHWEST);
		aCreditsPanel.add(helperLabel, gridBagConstraints1);

		// Info

		version.setEditable(false);
		String versionNum = PCGenPropBundle.getVersionNumber();
		if (StringUtils.isNotBlank(PCGenPropBundle.getAutobuildNumber()))
		{
			versionNum += " autobuild #" + PCGenPropBundle.getAutobuildNumber();
		}
		version.setText(versionNum);
		version.setBorder(null);
		version.setOpaque(false);

		gridBagConstraints1 = buildConstraints(1, 0, GridBagConstraints.WEST);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.weightx = 1.0;
		aCreditsPanel.add(version, gridBagConstraints1);

		releaseDate.setEditable(false);
		String releaseDateStr = PCGenPropBundle.getReleaseDate();
		if (StringUtils.isNotBlank(PCGenPropBundle.getAutobuildDate()))
		{
			releaseDateStr = PCGenPropBundle.getAutobuildDate();
		}
		releaseDate.setText(releaseDateStr);
		releaseDate.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		releaseDate.setOpaque(false);

		gridBagConstraints1 = buildConstraints(1, 1, GridBagConstraints.WEST);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		aCreditsPanel.add(releaseDate, gridBagConstraints1);

		javaVersion.setEditable(false);
		javaVersion
			.setText(System.getProperty("java.runtime.version") + " (" + System.getProperty("java.vm.vendor") + ")");
		javaVersion.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		javaVersion.setOpaque(false);

		gridBagConstraints1 = buildConstraints(1, 2, GridBagConstraints.WEST);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		aCreditsPanel.add(javaVersion, gridBagConstraints1);

		projectLead.setEditable(false);
		projectLead.setText(PCGenPropBundle.getHeadCodeMonkey());
		projectLead.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		projectLead.setOpaque(false);

		gridBagConstraints1 = buildConstraints(1, 3, GridBagConstraints.WEST);
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		aCreditsPanel.add(projectLead, gridBagConstraints1);

		// Web site button
		wwwSite.setText(PCGenPropBundle.getWWWHome());
		wwwSite.addActionListener(event -> {
			try
			{
				DesktopBrowserLauncher.viewInBrowser(new URL(wwwSite.getText()));
			}
			catch (IOException ioe)
			{
				Logging.errorPrint(LanguageBundle.getString("in_abt_browser_err"), ioe); //$NON-NLS-1$
			}
		});
		gridBagConstraints1 = buildConstraints(1, 4, GridBagConstraints.WEST);
		aCreditsPanel.add(wwwSite, gridBagConstraints1);

		// Mailing list button
		mailingList.setText(PCGenPropBundle.getMailingList());
		mailingList.addActionListener(event -> {
			try
			{
				DesktopBrowserLauncher.viewInBrowser(new URL(mailingList.getText()));
			}
			catch (IOException ioe)
			{
				Logging.errorPrint(LanguageBundle.getString("in_err_browser_err"), ioe); //$NON-NLS-1$
			}
		});
		gridBagConstraints1 = buildConstraints(1, 5, GridBagConstraints.WEST);
		aCreditsPanel.add(mailingList, gridBagConstraints1);

		// Monkey tabbed pane
		gridBagConstraints1 = buildConstraints(1, 6, GridBagConstraints.WEST);
		gridBagConstraints1.gridwidth = 2;
		gridBagConstraints1.weighty = 1.0;
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		aCreditsPanel.add(monkeyTabPane, gridBagConstraints1);

		monkeyTabPane.add(LanguageBundle.getString("in_abt_code_mky"), //$NON-NLS-1$
			buildMonkeyList(PCGenPropBundle.getCodeMonkeys()));
		monkeyTabPane.add(LanguageBundle.getString("in_abt_list_mky"), //$NON-NLS-1$
			buildMonkeyList(PCGenPropBundle.getListMonkeys()));
		monkeyTabPane.add(LanguageBundle.getString("in_abt_test_mky"), //$NON-NLS-1$
			buildMonkeyList(PCGenPropBundle.getTestMonkeys()));
		monkeyTabPane.add(LanguageBundle.getString("in_abt_eng_mky"), //$NON-NLS-1$
			buildMonkeyList(PCGenPropBundle.getEngineeringMonkeys()));

		// because there isn't one
		monkeyTabPane.setToolTipTextAt(2, LanguageBundle.getString("in_abt_easter_egg")); //$NON-NLS-1$

		return aCreditsPanel;
	}

	/**
	 * Build up a scrollable list of monkeys, given the monkey names.
	 * @param monkeys The names of the monkeys
	 * @return A JScrollPane to display the monkeys.
	 */
	private JScrollPane buildMonkeyList(String monkeys)
	{
		JTextArea textArea = new JTextArea();
		JScrollPane scroller = new JScrollPane();

		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setText(monkeys);
		scroller.setViewportView(textArea);
		textArea.setCaretPosition(0);

		return scroller;
	}

	/**
	 * Construct a GridBagConstraints record using defaults and
	 * some basic supplied details.
	 *
	 * @param xPos The column the field should appear in.
	 * @param yPos The row the field should appear in.
	 * @param anchor Where the field should be positioned.
	 * @return A GridBagConstraints object.
	 */
	private GridBagConstraints buildConstraints(int xPos, int yPos, int anchor)
	{
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = xPos;
		constraints.gridy = yPos;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = anchor;
		constraints.insets = new Insets(5, 0, 5, 10);
		return constraints;
	}

	/**
	 * Construct the includes panel. This panel shows details
	 * and licencing statrements about any libraries distributed
	 * with PCGen.
	 *
	 * @return The includes panel.
	 */
	private JPanel buildIncludesPanel()
	{
		JPanel iPanel = new JPanel();

		JTextArea otherLibrariesField = new JTextArea();

		iPanel.setLayout(new BorderLayout());

		String s = LanguageBundle.getString("in_abt_lib_apache"); //$NON-NLS-1$
		s += LanguageBundle.getString("in_abt_lib_jdom"); //$NON-NLS-1$
		s += LanguageBundle.getString("in_abt_lib_l2f"); //$NON-NLS-1$
		otherLibrariesField.setText(s);
		otherLibrariesField.setWrapStyleWord(true);
		otherLibrariesField.setLineWrap(true);
		otherLibrariesField.setEditable(false);
		otherLibrariesField.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		iPanel.add(otherLibrariesField, BorderLayout.CENTER);

		return iPanel;
	}

	/**
	 * Construct the awards panel. This panel shows each award
	 * the pcgen project has been awarded
	 *
	 * @return The awards panel.
	 */
	private JPanel buildAwardsPanel()
	{
		JScrollPane sp = new JScrollPane();
		JPanel panel = new JPanel();

		JPanel aPanel = new JPanel();
		aPanel.setLayout(new GridBoxLayout(2, 2));
		aPanel.setBackground(Color.WHITE);
		Icon goldIcon = Icons.ennie_award_2005.getImageIcon();
		if (goldIcon != null)
		{
			JLabel e2005 = new JLabel(goldIcon);
			aPanel.add(e2005);

			JTextArea title = new JTextArea();
			title.setLineWrap(true);
			title.setWrapStyleWord(true);
			title.setEditable(false);
			title.setText(LanguageBundle.getString("in_abt_awards_2005_ennie"));
			aPanel.add(title);
		}

		Icon bronzeIcon = Icons.ennie_award_2003.getImageIcon();;
		if (bronzeIcon != null)
		{
			JLabel e2003 = new JLabel(bronzeIcon);
			aPanel.add(e2003);

			JTextArea title = new JTextArea();
			title.setLineWrap(true);
			title.setWrapStyleWord(true);
			title.setEditable(false);
			title.setText(LanguageBundle.getString("in_abt_awards_2003_ennie"));
			aPanel.add(title);
		}

		sp.setViewportView(aPanel);
		panel.add(sp, BorderLayout.CENTER);
		return panel;
	}

	/**
	 * Construct the license panel. This panel shows the full
	 * text of the license under which PCGen is distributed.
	 *
	 * @return The license panel.
	 */
	private JPanel buildLicensePanel()
	{
		JPanel lPanel = new JPanel();

		JScrollPane license = new JScrollPane();
		JTextArea lgplArea = new JTextArea();

		lPanel.setLayout(new BorderLayout());

		lgplArea.setEditable(false);

		InputStream lgpl = ClassLoader.getSystemResourceAsStream("LICENSE"); //$NON-NLS-1$

		if (lgpl != null)
		{
			try
			{
				lgplArea.read(new InputStreamReader(lgpl), "LICENSE"); //$NON-NLS-1$
			}
			catch (IOException ioe)
			{
				lgplArea.setText(LanguageBundle.getString("in_abt_license_read_err1")); //$NON-NLS-1$
			}
		}
		else
		{
			lgplArea.setText(LanguageBundle.getString("in_abt_license_read_err2")); //$NON-NLS-1$
		}

		license.setViewportView(lgplArea);
		lPanel.add(license, BorderLayout.CENTER);

		return lPanel;
	}

}
