/*
 * Copyright 2019 (C) Eitan Adler <lists@eitanadler.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.     See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package pcgen.gui3.dialog;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import pcgen.system.LoggingRecorder;
import pcgen.util.Logging;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;

/**
 * A dialog that shows logs and memory usage.
 * Over time we could add more debug and troubleshooting information into the pane.
 */
public class DebugDialogController
{

	private static final MemoryMXBean MEMORY_BEAN = ManagementFactory.getMemoryMXBean();

	/** Data rows shown: heap and non-heap. */
	private static final int MEMORY_ROWS = 2;

	/** Fixed row height; keeps the table height deterministic for the maxHeight cap. */
	private static final double ROW_HEIGHT = 24.0;

	/** Allowance for the table's top/bottom borders on top of header + rows. */
	private static final double HEADER_BORDER_PAD = 4.0;

	@FXML
	private TableView<Map<String, String>> memoryTable;

	private final ObservableList<Map<String, String>> memoryTableData = FXCollections.observableArrayList();
	@FXML
	private TextArea logText;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	@FXML
	void initialize()
	{
		memoryTable.setItems(memoryTableData);
		// Cap the table to its data rows plus the header (one row tall) so it
		// doesn't pad with empty rows. maxHeight is derived from the row height
		// rather than a fixed pixel total, so it adapts if the cell size changes.
		memoryTable.setFixedCellSize(ROW_HEIGHT);
		// Header + data rows + a small allowance for the table's borders, so the
		// content fits exactly without a scrollbar or trailing empty rows.
		memoryTable.maxHeightProperty()
				.bind(memoryTable.fixedCellSizeProperty().multiply(MEMORY_ROWS + 1).add(HEADER_BORDER_PAD));
		setMemoryTableData();
		logText.setText(LoggingRecorder.getLogs());
		Logging.registerHandler(new LogHandler());
	}

	private void setMemoryTableData()
	{
		// posible optimization: get the rows rather than clear and re-add
		memoryTableData.clear();
		for (int row = 0; row < MEMORY_ROWS; ++row)
		{
			Map<String, String> dataRow = new HashMap<>();
			for (int column = 0; column < memoryTable.getColumns().size(); column++)
			{
				String id = memoryTable.getColumns().get(column).getId();
				dataRow.put(id, getMemoryTableValue(row, column));
			}
			memoryTableData.add(dataRow);
		}

		memoryTable.setItems(memoryTableData);
		memoryTable.refresh();
	}

	private static String getMemoryTableValue(int rowIndex, int columnIndex)
	{
		MemoryUsage usage = (rowIndex == 0) ? MEMORY_BEAN.getHeapMemoryUsage() : MEMORY_BEAN.getNonHeapMemoryUsage();
		String label = (rowIndex == 0) ? "Heap" : "Non-Heap";
		return formatMemoryCell(columnIndex, label, usage);
	}

	/**
	 * Formats one memory-table cell. Package-private and free of any live
	 * {@link MemoryMXBean} access so the number handling can be unit-tested by
	 * passing synthetic {@link MemoryUsage} values.
	 *
	 * @param columnIndex the column (0 label, 1 init, 2 used, 3 committed, 4 max, 5 % used)
	 * @param label       the row label to render for column 0
	 * @param usage       the memory usage for the row
	 * @return the rendered cell text
	 */
	static String formatMemoryCell(int columnIndex, String label, MemoryUsage usage)
	{
		final long MEGABYTE = 1024 * 1024;
		final NumberFormat format = new DecimalFormat("###,###,###");
		// getMax() is -1 when the pool has no defined maximum (common for
		// non-heap); guard it so Max and % Used don't render as 0 / a huge
		// negative number.
		final long max = usage.getMax();
		return switch (columnIndex)
				{
					case 0 -> label;
					case 1 -> format.format(usage.getInit() / MEGABYTE);
					case 2 -> format.format(usage.getUsed() / MEGABYTE);
					case 3 -> format.format(usage.getCommitted() / MEGABYTE);
					case 4 -> (max < 0) ? "n/a" : format.format(max / MEGABYTE);
					case 5 -> (max <= 0) ? "n/a" : Math.round(100.0 * usage.getUsed() / max) + "%";
					default -> throw new IllegalStateException("Unexpected column index: " + columnIndex);
				};
	}

	@FXML
	private void clearLogs(final ActionEvent actionEvent)
	{
		LoggingRecorder.clearLogs();
		logText.setText(LoggingRecorder.getLogs());
	}

	void initTimer()
	{
		scheduler.scheduleAtFixedRate(this::setMemoryTableData, 0, 30, TimeUnit.SECONDS);
	}

	private final class LogHandler extends Handler implements Runnable
	{

		private LogHandler()
		{
			setLevel(Logging.DEBUG);
		}

		@Override
		public void publish(LogRecord record)
		{
		}

		@Override
		public void flush()
		{
		}

		@Override
		public void close()
		{
		}

		@Override
		public void run()
		{
			logText.setText(LoggingRecorder.getLogs());
		}
	}


	@FXML
	private void runGC(final ActionEvent actionEvent)
	{
		MEMORY_BEAN.gc();
		setMemoryTableData();
		final long megabyte = 1024 * 1024;
		Logging.log(Logging.INFO, MessageFormat.format("Memory used after manual GC, Heap: {0} MB, Non heap: {1} MB",
				MEMORY_BEAN.getHeapMemoryUsage().getUsed() / megabyte,
				MEMORY_BEAN.getNonHeapMemoryUsage().getUsed() / megabyte));
	}
	void shutdown()
	{
		scheduler.shutdown();
	}
}
