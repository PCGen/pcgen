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
		setMemoryTableData();
		logText.setText(LoggingRecorder.getLogs());
		Logging.registerHandler(new LogHandler());
	}

	private void setMemoryTableData()
	{
		// posible optimization: get the rows rather than clear and re-add
		memoryTableData.clear();
		for (int row = 0; row < 2; ++row)
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
		final long MEGABYTE = 1024 * 1024;

		MemoryUsage usage;
		if (rowIndex == 0)
		{
			usage = MEMORY_BEAN.getHeapMemoryUsage();
		}
		else
		{
			usage = MEMORY_BEAN.getNonHeapMemoryUsage();
		}
		final NumberFormat format = new DecimalFormat("###,###,###");
		return switch (columnIndex)
				{
					case 0 -> (rowIndex == 0) ? "Heap" : "Non-Heap";
					case 1 -> format.format(usage.getInit() / MEGABYTE);
					case 2 -> format.format(usage.getUsed() / MEGABYTE);
					case 3 -> format.format(usage.getCommitted() / MEGABYTE);
					case 4 -> format.format(usage.getMax() / MEGABYTE);
					case 5 -> String.valueOf(100 * (usage.getUsed() / usage.getMax()));
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
		Logging.log(Logging.INFO, MessageFormat.format("Memory used after manual GC, Heap: {0}, Non heap: {1}",
				MEMORY_BEAN.getHeapMemoryUsage().getUsed(), MEMORY_BEAN.getNonHeapMemoryUsage().getUsed()));
	}
	void shutdown()
	{
		scheduler.shutdown();
	}
}
