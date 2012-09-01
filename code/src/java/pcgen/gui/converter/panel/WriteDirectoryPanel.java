/*
 * Copyright (c) 2009 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package pcgen.gui.converter.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.gui.converter.event.TaskStrategyMessage;

public class WriteDirectoryPanel extends ConvertSubPanel
{

	private File path = null;

	private SpringLayout layout = new SpringLayout();

	private final JLabel fileLabel;

	public WriteDirectoryPanel()
	{
		fileLabel = new JLabel();
	}

	public String getPath()
	{
		return path.getAbsolutePath();
	}

	@Override
	public boolean performAnalysis(CDOMObject pc)
	{
		TaskStrategyMessage.sendStatus(this, "Finding Data Directories");
		path = pc.get(ObjectKey.WRITE_DIRECTORY);
		if (path != null)
		{
			fileLabel.setText(path.getAbsolutePath());
		}
		else
		{
			path = new File(".");
		}
		pc.put(ObjectKey.WRITE_DIRECTORY, path);
		fireProgressEvent(ProgressEvent.ALLOWED);
		return true;
	}

	@Override
	public boolean autoAdvance(CDOMObject pc)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see pcgen.gui.converter.panel.ConvertSubPanel#returnAllowed()
	 */
	@Override
	public boolean returnAllowed()
	{
		return true;
	}
	
	@Override
	public void setupDisplay(JPanel panel, final CDOMObject pc)
	{
		panel.setLayout(layout);
		JLabel label = new JLabel(
				"Please select the Directory where Converted files should be written: ");
		JButton button = new JButton("Browse...");
		button.setMnemonic('r');
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setDialogType(JFileChooser.OPEN_DIALOG);
				chooser.setSelectedFile(path);
				while (true)
				{
					int open = chooser.showOpenDialog(null);
					if (open == JFileChooser.APPROVE_OPTION)
					{
						File fileToOpen = chooser.getSelectedFile();
						if (fileToOpen.isDirectory() && fileToOpen.canRead()
								&& fileToOpen.canWrite())
						{
							path = fileToOpen;
							pc.put(ObjectKey.WRITE_DIRECTORY, path);
							fileLabel.setText(path.getAbsolutePath());
							break;
						}
						JOptionPane.showMessageDialog(null,
								"Selection must be a valid "
										+ "(readable & writeable) Directory");
						chooser.setSelectedFile(path);
					}
					else if (open == JFileChooser.CANCEL_OPTION)
					{
						break;
					}
				}
			}
		});
		panel.add(label);
		panel.add(fileLabel);
		panel.add(button);
		layout.putConstraint(SpringLayout.NORTH, label, 50, SpringLayout.NORTH,
				panel);
		layout.putConstraint(SpringLayout.NORTH, fileLabel, 75 + label
				.getPreferredSize().height, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.NORTH, button, 75 + label
				.getPreferredSize().height, SpringLayout.NORTH, panel);
		layout.putConstraint(SpringLayout.WEST, label, 25, SpringLayout.WEST,
				panel);
		layout.putConstraint(SpringLayout.WEST, fileLabel, 25,
				SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, button, -50, SpringLayout.EAST,
				panel);
		fileLabel.setText(path.getAbsolutePath());
	}
}
