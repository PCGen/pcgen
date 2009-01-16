/*
 * Copyright (c) 2006, 2009.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2.0 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 * 
 * Created on Mar 10, 2006
 */
package pcgen.gui.converter;

import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.inst.ObjectCache;
import pcgen.gui.converter.event.ProgressEvent;
import pcgen.gui.converter.panel.ConvertSubPanel;
import pcgen.gui.converter.panel.GameModePanel;
import pcgen.gui.converter.panel.MessagePanel;
import pcgen.gui.converter.panel.SourceSelectionPanel;
import pcgen.gui.converter.panel.StartupPanel;
import pcgen.gui.converter.panel.WriteDirectoryPanel;
import pcgen.gui.utils.AWTUtilities;
import pcgen.persistence.lst.LstSystemLoader;

public final class PCGenDataConvert extends JFrame
{

	private final JPanel contentPanel = new JPanel(new CardLayout());

	public void addNamedPanel(String name, JPanel panel)
	{
		contentPanel.add(panel, name);
	}

	private PCGenDataConvert()
	{
		super("PCGenDataConvert");
	}

	public static PCGenDataConvert getConverter(CDOMObject pc)
			throws InterruptedException
	{
		PCGenDataConvert frame = new PCGenDataConvert();
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		BlockingQueue<ConvertSubPanel> panels = new LinkedBlockingQueue<ConvertSubPanel>();
		LstSystemLoader loader = new LstSystemLoader();
		panels.put(new StartupPanel(loader));

		final ConvertPanel installPanel = new ConvertPanel(panels);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent wEvent)
			{
				installPanel.checkExit();
			}
		});

		panels.put(new SourceSelectionPanel());
		panels.put(new GameModePanel(loader));

		/*
		 * TODO Need to select Campaigns here...
		 */

		panels.put(new WriteDirectoryPanel());

		panels.put(new MessagePanel("PCGen Data Conversion Complete!",
				ProgressEvent.NOT_ALLOWED));

		frame.getContentPane().add(installPanel);
		frame.pack();
		frame.setLocation(AWTUtilities.computeWindowLocation(frame));
		return frame;
	}

	public static void main(String[] args) throws InterruptedException
	{
		getConverter(new ObjectCache()).setVisible(true);
	}
}
