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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
public class DebugDialog extends JDialog
{

	private static MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
	private final JTextArea debuggingText;
	private final MemoryPanel memoryPanel;

	public DebugDialog(PCGenFrame frame)
	{
		super(frame);
		setTitle("Debugging Console");
		debuggingText = new JTextArea();
		memoryPanel = new MemoryPanel();
		initComponents();
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
		//debuggingText.setLineWrap(true);
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
			memoryTable.setPreferredScrollableViewportSize(memoryTable.getPreferredSize());
			memoryTable.setDefaultRenderer(Long.class, new DefaultTableCellRenderer()
			{

				DecimalFormat format = new DecimalFormat("###,###,###");

				@Override
				protected void setValue(Object value)
				{
					setHorizontalAlignment(JLabel.RIGHT);
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

}
