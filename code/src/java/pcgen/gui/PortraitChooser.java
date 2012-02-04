/*
 * PortraitChooser.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 * Created on April 18, 2002, 5:00 PM
 */
package pcgen.gui;

import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.SettingsHandler;
import pcgen.gui.utils.IconUtilitities;
import pcgen.gui.utils.Utility;
import pcgen.system.LanguageBundle;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;

/**
 * <code>PortraitChooser</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
public final class PortraitChooser extends JPanel
{
	static final long serialVersionUID = -2286034876554542232L;

	private static final boolean ALLOW_PNG = isJava141OrBetter();

	private static final JFileChooser fileChooser;
	private static Image default_portrait = null;

	/**
	 * <br>author: Thomas Behr 18-04-02
	 */
	static
	{
		fileChooser = ImagePreview.decorateWithImagePreview(new JFileChooser());
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileFilter(new FileFilter()
		{
			public boolean accept(final File f)
			{
				if (f.isDirectory())
				{
					return true;
				}

				final String fileName = f.getName().toLowerCase();
				boolean isImage = fileName.endsWith(".gif")
						|| fileName.endsWith(".jpg")
						|| fileName.endsWith(".jpeg");

				if (ALLOW_PNG)
				{
					isImage |= fileName.endsWith(".png");
				}

				return isImage;
			}

			public String getDescription()
			{
				return ALLOW_PNG
						? "Images (*.gif, *.jpg, *.jpeg, *.png)"
						: "Images (*.gif, *.jpg, *.jpeg)";
			}
		});
	}

	private File directory;
	private File portraitFile;
	private GridBagConstraints gbc;
	private GridBagLayout gbl;

	/**
	 * cache for already appropriately scaled images
	 */
	private ImageIcon cachedIcon;
	private JButton dirButton;
	private JButton refreshButton;
	private JButton removeButton;
	private JButton buyButton;
	private JLabel portraitLabel;
	private String title;

	/**
	 * Constructor
	 */
	public PortraitChooser()
	{
		this("Portrait");
	}

	/**
	 * Constructor
	 *
	 * @param title
	 */
	public PortraitChooser(String title)
	{
		super();

		this.title = title;
		this.directory = SettingsHandler.getPortraitsPath();

		if (default_portrait == null)
		{
			default_portrait = Toolkit.getDefaultToolkit().getImage(
					this.getClass().getResource(IconUtilitities.RESOURCE_URL
							+ "DefaultPortrait.gif"));
		}

		initComponents();
	}

	/**
	 * <br>author: Thomas Behr 20-04-02
	 *
	 * @param currPC
	 */
	public void refresh(PlayerCharacter currPC)
	{
		refresh(true, currPC);
	}

	/**
	 * Set the displayed portrait, automatically resize the image to fit the
	 * current display size
	 * 
	 * author: James Dempsey  02 Oct 2003
	 *
	 * @param portraitFile The portrait file to be set
	 * @param currPC
	 */
	private void setPortrait(File portraitFile, PlayerCharacter currPC)
	{
		Image image;
		String newPortraitPath;

		if (portraitFile == null)
		{
			image = default_portrait;
			newPortraitPath = "";
		}
		else
		{
			newPortraitPath = portraitFile.getAbsolutePath();
			image = Toolkit.getDefaultToolkit().getImage(newPortraitPath);
		}

		this.portraitFile = portraitFile;
		cachedIcon = createScaledImageIcon(image);
		portraitLabel.setIcon(cachedIcon);

		if (currPC != null && !newPortraitPath.equals(currPC.getPortraitPath()))
		{
			currPC.setPortraitPath(newPortraitPath);
			currPC.setDirty(true);
		}
	}

	/**
	 * convenience method
	 * <p/>
	 * <br>author: Thomas Behr 18-04-02
	 *
	 * @param comp
	 * @param row
	 * @param col
	 * @param width
	 * @param height
	 */
	private void add(JComponent comp, int row, int col, int width, int height)
	{
		gbc.gridx = col;
		gbc.gridy = row;
		gbc.gridwidth = width;
		gbc.gridheight = height;

		gbl.setConstraints(comp, gbc);
		super.add(comp);
	}

	/**
	 * create an ImageIcon instance with the specified image scaled to fit the
	 * current display size
	 * <p/>
	 * <br>author: Thomas Behr 18-04-02
	 *
	 * @param image the Image instance to scale
	 *
	 * @return an ImageIcon instance for an appropriately scaled instance of the
	 *         specified Image
	 */
	private ImageIcon createScaledImageIcon(Image image)
	{
		ImageIcon icon = new ImageIcon(image);

		int width = icon.getIconWidth();
		int height = icon.getIconHeight();

		double factorWidth = (width) / portraitLabel.getSize().getWidth();
		double factorHeight = (height) / portraitLabel.getSize().getHeight();

		if (factorWidth > factorHeight)
		{
			width = (int) ((width) / factorWidth);
			height = -1;
		}
		else
		{
			width = -1;
			height = (int) ((height) / factorHeight);
		}

		if ((width != 0) && (height != 0))
		{
			//Changed from SCALE_SMOOTH to fix bug #901800 (see sun bug http://developer.java.sun.com/developer/bugParade/bugs/4937376.html)
			icon = new ImageIcon(image.getScaledInstance(width, height,
					Image.SCALE_REPLICATE));
		}

		return icon;
	}

	/**
	 * initialize the gui stuff
	 * <p/>
	 * <br>author: Thomas Behr 18-04-02
	 */
	private void initComponents()
	{
		// basic layout
		gbl = new GridBagLayout();
		gbc = new GridBagConstraints();

		this.setLayout(gbl);

		if (title != null)
		{
			this.setBorder(BorderFactory.createTitledBorder(
					BorderFactory.createEtchedBorder(), title,
					TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
		}

		// label for displaying the portraits.
		portraitLabel = new JLabel();
		portraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
		portraitLabel.setVerticalAlignment(SwingConstants.CENTER);
		portraitLabel.setVerticalTextPosition(SwingConstants.CENTER);
		portraitLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		portraitLabel.setIcon(new ImageIcon(default_portrait));
		portraitLabel.setText("");
		portraitLabel.addComponentListener(new ComponentAdapter()
		{
			public void componentResized(ComponentEvent e)
			{
				resizePortrait();
			}
		});

		JPanel portraitPanel = new JPanel(new GridLayout(1, 1));
		portraitPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLoweredBevelBorder(),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		portraitPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(0, 0, 10, 0),
				portraitPanel.getBorder()));
		portraitPanel.add(portraitLabel);

		gbc.weightx = 10;
		gbc.weighty = 10;
		gbc.fill = GridBagConstraints.BOTH;
		this.add(portraitPanel, 0, 0, 3, 1);

		JPanel buttonPanel = new JPanel();

		// portrait refresh button
		refreshButton = Utility.createButton(null, null,
				LanguageBundle.getString("in_refreshTipString"),
				"Refresh16.gif", true);
		refreshButton.setMargin(new Insets(2, 2, 2, 2));
		refreshButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				refresh(false, PCGen_Frame1.getInst().getCurrentPC());
			}
		});
		buttonPanel.add(refreshButton);

		// Portrait chooser button
		dirButton = new JButton(LanguageBundle.getString("in_selectPortrait"));

		if (SettingsHandler.isToolTipTextShown())
		{
			Utility.setDescription(dirButton,
					LanguageBundle.getString("in_selectPortraitTipString"));
		}

		dirButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				fileChooser.setCurrentDirectory(directory);

				if (fileChooser.showOpenDialog(PortraitChooser.this)
						== JFileChooser.APPROVE_OPTION)
				{
					directory = fileChooser.getSelectedFile().getParentFile();
					setPortrait(fileChooser.getSelectedFile(),
							PCGen_Frame1.getInst().getCurrentPC());
				}
			}
		});
		buttonPanel.add(dirButton);

		// Portrait Remove button
		removeButton = new JButton(
				LanguageBundle.getString("in_removePortrait"));

		if (SettingsHandler.isToolTipTextShown())
		{
			Utility.setDescription(removeButton,
					LanguageBundle.getString("in_removePortraitTipString"));
		}

		removeButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				setPortrait(null, PCGen_Frame1.getInst().getCurrentPC());
			}
		});
		buttonPanel.add(removeButton);

		// Buy Portrait button
		buyButton = new JButton(
				LanguageBundle.getString("in_buyPortrait"));

		if (SettingsHandler.isToolTipTextShown())
		{
			Utility.setDescription(buyButton,
					LanguageBundle.getString("in_buyPortraitTipString"));
		}

		buyButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Utility.viewInBrowser("https://www.e-junkie.com/ecom/gb.php?cl=154598&c=ib&aff=154875");
			}
		});
		buttonPanel.add(buyButton);

		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.NONE;
		this.add(buttonPanel, 1, 2, 1, 1);

		// Now set the initial portrait
		initPortrait(true, PCGen_Frame1.getInst().getCurrentPC());
	}

	/**
	 * initialize the file stuff
	 * <p/>
	 * <br>author: Thomas Behr 19-04-02
	 *
	 * @param resetPortraitsPathToDefaultIfCharacterHasNone
	 * @param currPC
	 */
	private void initPortrait(
			boolean resetPortraitsPathToDefaultIfCharacterHasNone,
			PlayerCharacter currPC)
	{
		String portraitPath = "";
		if (currPC != null)
		{
			portraitPath = currPC.getPortraitPath();
		}

		if (portraitPath.length() > 0)
		{
			File aPortraitFile = new File(portraitPath);
			setPortrait(aPortraitFile, currPC);
		}
		else
		{
			if (resetPortraitsPathToDefaultIfCharacterHasNone)
			{
				directory = SettingsHandler.getPortraitsPath();
			}

			setPortrait(null, currPC);
		}
	}

	private void refresh(boolean resetPortraitsPathToDefaultIfCharacterHasNone,
			PlayerCharacter currPC)
	{
		initPortrait(resetPortraitsPathToDefaultIfCharacterHasNone, currPC);
	}

	/**
	 * resize the currently chosen image to fit the current display size; clear the
	 * cache since we probably need to resize previously cached images as well
	 * <p/>
	 * <br>author: Thomas Behr 18-04-02
	 */
	private void resizePortrait()
	{
		Image image;

		if (portraitFile != null)
		{
			image = Toolkit.getDefaultToolkit()
					.getImage(portraitFile.getAbsolutePath());
		}
		else
		{
			image = default_portrait;
		}

		cachedIcon = createScaledImageIcon(image);
		portraitLabel.setIcon(cachedIcon);
	}

	private static boolean isJava141OrBetter()
	{
		return ((Globals.javaVersionMajor > 1)
				|| ((Globals.javaVersionMajor == 1)
				&& (Globals.javaVersionMinor >= 4)));
	}
}
