/*
 * DebugDialog.java
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
 * Created on Mar 14, 2011, 3:45:35 PM
 */
package pcgen.gui2.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import pcgen.gui2.PCGenFrame;
import pcgen.gui2.tools.Utility;
import pcgen.system.LoggingRecorder;
import pcgen.util.Logging;

/**
 * 
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class DebugDialog extends JDialog implements MouseListener
{

	private static MemoryMXBean memoryBean = ManagementFactory
		.getMemoryMXBean();
	private final JTextArea debuggingText;
	private final MemoryPanel memoryPanel;

	public DebugDialog(PCGenFrame frame)
	{
		super(frame);
		setTitle("Log & Memory Use");
		debuggingText = new JTextArea();
		memoryPanel = new MemoryPanel();
		initComponents();
		debuggingText.addMouseListener(this);
		pack();
		setSize(700, 500);

		Utility.installEscapeCloseOperation(this);
	}

	private void initComponents()
	{
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		initDebugLog();
		contentPane.add(new JScrollPane(debuggingText), BorderLayout.CENTER);
		contentPane.add(memoryPanel, BorderLayout.SOUTH);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
	}

	private void initDebugLog()
	{
		// debuggingText.setLineWrap(true);
		debuggingText.setEditable(false);
		debuggingText.setText(LoggingRecorder.getLogs());
		Logging.registerHandler(new LogHandler());
	}

	@Override
	public void dispose()
	{
		super.dispose();
		memoryPanel.dispose();
	}

	private class LogHandler extends Handler implements Runnable
	{

		public LogHandler()
		{
			setLevel(Logging.DEBUG);
		}

		@Override
		public void publish(LogRecord record)
		{
			SwingUtilities.invokeLater(this);
		}

		@Override
		public void flush()
		{
		}

		@Override
		public void close() throws SecurityException
		{
		}

		@Override
		public void run()
		{
			debuggingText.setText(LoggingRecorder.getLogs());
		}

	}

	private static class MemoryPanel extends JPanel implements ActionListener
	{

		private final Timer timer;
		private final JButton gcButton;
		private final JTable memoryTable;

		public MemoryPanel()
		{
			timer = new Timer(1000, this);
			gcButton = new JButton("Run Garbage Collection");
			memoryTable = new JTable(new MemoryTableModel());
			initComponents();
			timer.start();
		}

		private void initComponents()
		{
			setBorder(BorderFactory.createTitledBorder("Memory Usage"));
			setLayout(new BorderLayout());
			add(new JScrollPane(memoryTable)
			{

				@Override
				public Dimension getMaximumSize()
				{
					return super.getPreferredSize();
				}

			}, BorderLayout.CENTER);
			memoryTable.setFocusable(false);
			memoryTable.setRowSelectionAllowed(false);
			memoryTable.setPreferredScrollableViewportSize(memoryTable
				.getPreferredSize());
			memoryTable.setDefaultRenderer(Long.class,
				new DefaultTableCellRenderer()
				{

					DecimalFormat format = new DecimalFormat("###,###,###");

					@Override
					protected void setValue(Object value)
					{
						setHorizontalAlignment(SwingConstants.RIGHT);
						setText(format.format(value));
					}

				});

			gcButton.setActionCommand("COLLECT");
			gcButton.addActionListener(this);
			add(gcButton, BorderLayout.SOUTH);
		}

		public void dispose()
		{
			timer.stop();
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if ("COLLECT".equals(e.getActionCommand()))
			{
				memoryBean.gc();
				Logging.log(Logging.INFO, MessageFormat.format(
					"Memory used after manual GC, Heap: {0}, Non heap: {1}",
					memoryBean.getHeapMemoryUsage().getUsed(), memoryBean
						.getNonHeapMemoryUsage().getUsed()));
			}
			else
			{
				memoryTable.repaint();
			}
		}

	}

	private static class MemoryTableModel extends AbstractTableModel
	{

		private static long megaByte = 1024 * 1024;

		@Override
		public int getRowCount()
		{
			return 2;
		}

		@Override
		public int getColumnCount()
		{
			return 5;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			if (columnIndex == 0)
			{
				return String.class;
			}
			else
			{
				return Long.class;
			}
		}

		@Override
		public String getColumnName(int column)
		{
			switch (column)
			{
				case 0:
					return "";
				case 1:
					return "Init";
				case 2:
					return "Used";
				case 3:
					return "Committed";
				case 4:
					return "Max";
				default:
					return super.getColumnName(column);
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			MemoryUsage usage;
			if (rowIndex == 0)
			{
				usage = memoryBean.getHeapMemoryUsage();
			}
			else
			{
				usage = memoryBean.getNonHeapMemoryUsage();
			}
			switch (columnIndex)
			{
				case 0:
					return (rowIndex == 0) ? "Heap" : "Non-Heap";
				case 1:
					return usage.getInit();// / megaByte;
				case 2:
					return usage.getUsed();// / megaByte;
				case 3:
					return usage.getCommitted();// / megaByte;
				case 4:
					return usage.getMax();// / megaByte;
				default:
					return 0;
			}
		}

	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		int caretPos = determineCaretPosition(e);
		String line = getCurrentIndexedLine(caretPos);
		File file = extractFileFromLine(line);
		if (file != null)
		{
			try
			{
				openFile(file);
			}
			catch (IOException e1)
			{
				Logging.log(Level.WARNING,
					"Unable to open the requested file: " + file.getName(), e1);
			}
		}
		else
		{
			Logging.log(Level.FINER, "No file in current line.");
		}
	}

	private void openFile(File file) throws IOException
	{
		if (Desktop.isDesktopSupported())
		{
			if (System.getProperty("os.name").toLowerCase().contains("windows"))
			{
				String cmd =
						"rundll32 url.dll,FileProtocolHandler "
							+ file.getCanonicalPath();
				Runtime.getRuntime().exec(cmd);
			}
			else
			{
				Desktop.getDesktop().open(file);
			}
		}
	}

	protected File extractFileFromLine(String line)
	{
		File file = null;
		String[] parts = line.split("\"");
		for (String part : parts)
		{
			file = convertURItoFileIfPossible(part);
			if (file == null)
			{
				String[] subParts = part.split(" ");
				for (String subPart : subParts)
				{
					file = convertURItoFileIfPossible(subPart);
					if (file != null)
					{
						return file;
					}
				}
			}
			else
			{
				return file;
			}
		}
		return file;
	}

	private File convertURItoFileIfPossible(String filePart)
	{
		File file = null;
		URI fileURI;
		try
		{
			fileURI = extractFileURIFromLinePart(filePart);
			file = new File(fileURI);
			if (!file.exists())
			{
				file = null;
			}
		}
		catch (Exception e)
		{
			// This part is NOT a valid URI. No harm done.
		}
		return file;
	}

	private URI extractFileURIFromLinePart(String part)
		throws URISyntaxException
	{
		String filePart = part;
		if (part.indexOf(':') < 0)
		{
			filePart = "file://" + part;
		}
		return new URI(filePart);
	}

	private String getCurrentIndexedLine(int index)
	{
		int startIndex = debuggingText.getText().lastIndexOf("\n", index) + 1;
		int endIndex = debuggingText.getText().indexOf("\n", index);
		String line = "";
		if (startIndex >= 0 && endIndex >= startIndex)
		{
			line = debuggingText.getText().substring(startIndex, endIndex);
		}
		return line;
	}

	private int determineCaretPosition(MouseEvent e)
	{
		debuggingText.setCaretPosition(debuggingText.viewToModel(e.getPoint()));
		int caretPos = debuggingText.getCaretPosition();
		return caretPos;
	}

	@Override
	public void mouseEntered(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0)
	{
		// TODO Auto-generated method stub

	}

}
