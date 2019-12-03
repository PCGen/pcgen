/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.rules.persistence;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import pcgen.base.text.ParsingSeparator;
import pcgen.base.util.FormatManager;
import pcgen.cdom.format.table.DataTable;
import pcgen.cdom.format.table.TableColumn;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstLineFileLoader;
import pcgen.rules.context.LoadContext;

/**
 * A TableLoader loads CSV-like files into Tables for PCGen.
 * 
 * The CSV files must conform to the CSV file format with some additional
 * limitations. Embedded newlines are not permitted in quotes. This should
 * generate an error from TableLoader.
 */
public class TableLoader extends LstLineFileLoader
{
	/**
	 * A pattern for empty lines. This helps the LineProcessors from having to
	 * deal with this situation.
	 */
	private static Pattern EMPTY = Pattern.compile("^[\\s,\\\"]+$");

	/**
	 * The active LineProcessor used to interpret the contents of the next
	 * loaded line in a Table file.
	 */
	private LineProcessor processor = new ExpectStartTable();

	@Override
	public void loadLstString(LoadContext context, URI uri, String aString) throws PersistenceLayerException
	{
		//Reset to ensure prior file corruption doesn't leak into a new file
		processor = new ExpectStartTable();
		super.loadLstString(context, uri, aString);
		if (!(processor instanceof ExpectStartTable))
		{
			throw new PersistenceLayerException("Did not find last ENDTABLE: entry in " + uri);
		}
	}

	@Override
	public void parseLine(LoadContext context, String lstLine, URI sourceURI) throws PersistenceLayerException
	{
		//ignore comments
		if (lstLine.startsWith("#") || lstLine.startsWith("\"#"))
		{
			return;
		}
		//Empty line (commas, whitespace, empty quotes)
		if (EMPTY.matcher(lstLine).find())
		{
			return;
		}
		processor = processor.parseLine(context, lstLine, sourceURI);
	}

	/**
	 * A LineProcessor interprets a line of a Table file and returns the
	 * LineProcessor that should be responsible for interpreting the next line
	 * of the file.
	 * 
	 * A LineProcessor is not expected to be able to understand/comprehend
	 * either blank lines or comment lines. Both of those should be ignored
	 * prior to the line being passed to a LineProcessor.
	 */
	@FunctionalInterface
	public interface LineProcessor
	{
		/**
		 * Processes the given line of a Table file (identified by the
		 * sourceURI).
		 * 
		 * @param context
		 *            The LoadContext to be used for interpretation of the Table
		 *            file
		 * @param lstLine
		 *            The line of the Table file to be processed
		 * @param sourceURI
		 *            The URI indicating the file being processed
		 * @return The LineProcessor that should be responsible for interpreting
		 *         the next line of the file
		 * @throws PersistenceLayerException
		 *             if there is an error during the loading of the given line
		 */
        LineProcessor parseLine(LoadContext context, String lstLine, URI sourceURI)
			throws PersistenceLayerException;
	}

	/**
	 * Unescapes a given entry. This performs whitespace padding removal both
	 * before and after the removal of the optional escaping quotes available in
	 * the CSV file format.
	 * 
	 * @param entry
	 *            The entry to be unescaped into its base state
	 * @return The unescaped entry (trimmed and with CSV quoting removed)
	 */
	private static String unescape(String entry)
	{
		String unescaped = entry.trim();
		if (unescaped.startsWith("\"") && unescaped.endsWith("\""))
		{
			unescaped = unescaped.substring(1, unescaped.length() - 1);
			unescaped = unescaped.replace("\"\"", "\"");
		}
		return unescaped.trim();
	}

	/**
	 * Ensures the rest of a given line is empty. This means the
	 * ParsingSeparator should only return entries that trim to length zero
	 * (after accounting for CSV escaping quotes)
	 * 
	 * @param line
	 *            The line being processed (for debugging purposes only)
	 * @param ps
	 *            The ParsingSeparator that should only return "blank" entries
	 * @throws PersistenceLayerException
	 *             if there is any non-blank content returned by the
	 *             ParsingSeparator
	 */
	private static void ensureEmpty(String line, ParsingSeparator ps) throws PersistenceLayerException
	{
		while (ps.hasNext())
		{
			String next = ps.next();
			if ((!next.isEmpty()) && (!unescape(next).isEmpty()))
			{
				throw new PersistenceLayerException("Expected Rest of Line to be empty: " + line);
			}
		}
	}

	/**
	 * Generates a new "naive" CSV separator. This is not formally CSV compliant
	 * because it ignores "embedded" new lines. For purposes of PCGen this is
	 * acceptable.
	 * 
	 * @param lstLine
	 *            The line to be processed by a CSV-like ParsingSeparator
	 * @return A ParsingSeparator for the given line
	 */
	private static ParsingSeparator generateCSVSeparator(String lstLine)
	{
		ParsingSeparator ps = new ParsingSeparator(lstLine, ',');
		ps.addGroupingPair('"', '"');
		return ps;
	}

	/**
	 * ExpectStartTable is the LineProcessor that waits for a "STARTTABLE:"
	 * entry in a Table file. If any other content is encountered, a
	 * PersistenceLayerException is thrown.
	 */
	private static class ExpectStartTable implements LineProcessor
	{

		@Override
		public LineProcessor parseLine(LoadContext context, String lstLine, URI sourceURI)
			throws PersistenceLayerException
		{
			ParsingSeparator ps = generateCSVSeparator(lstLine);
			String first = unescape(ps.next());
			if (first.startsWith("STARTTABLE:"))
			{
				ensureEmpty(lstLine, ps);
				DataTable table =
						context.getReferenceContext().constructCDOMObject(DataTable.class, first.substring(11));
				return new ImportColumnNames(table);
			}
			throw new PersistenceLayerException(
				"Expected STARTTABLE: entry, but found: " + lstLine + " in " + sourceURI);
		}

	}

	/**
	 * ImportColumnNames is the LineProcessor that reads in the column names in
	 * a Table file.
	 */
	private static class ImportColumnNames implements LineProcessor
	{

		/**
		 * The underlying Table to which the column names should be assigned
		 */
		private final DataTable t;

		/**
		 * Constructs a new ImportColumnNames with the given underlying Table.
		 * 
		 * @param table
		 *            The underlying Table to which the column names should be
		 *            assigned
		 */
		public ImportColumnNames(DataTable table)
		{
			t = table;
		}

		@Override
		public LineProcessor parseLine(LoadContext context, String lstLine, URI sourceURI)
			throws PersistenceLayerException
		{
			ParsingSeparator ps = generateCSVSeparator(lstLine);
			List<String> columnNames = new ArrayList<>();
			boolean first = true;
			boolean foundEmpty = false;
			while (ps.hasNext())
			{
				String columnName = unescape(ps.next());
				if (columnName.isEmpty())
				{
					foundEmpty = true;
					continue;
				}
				//Once an empty item was reached, nothing later on can have content
				if (foundEmpty)
				{
					throw new PersistenceLayerException("Encountered blank Column Name entry in " + lstLine + " for "
						+ t.getDisplayName() + " in " + sourceURI);
				}
				if (first && columnName.startsWith("STARTTABLE:"))
				{
					throw new PersistenceLayerException(
						"Encountered STARTTABLE: entry while expecting Column Names for " + t.getDisplayName() + " in "
							+ sourceURI);
				}
				if (first && columnName.startsWith("ENDTABLE:"))
				{
					throw new PersistenceLayerException("Encountered ENDTABLE: entry while expecting Column Names for "
						+ t.getDisplayName() + " in " + sourceURI);
				}
				columnNames.add(columnName);
				first = false;
			}
			return new ImportColumnFormats(t, columnNames);
		}

	}

	/**
	 * ImportColumnFormats is the LineProcessor that reads in the column formats
	 * in a Table file.
	 * 
	 * The number of Format lines in a table must match the number of column
	 * names in the table.
	 */
	private static class ImportColumnFormats implements LineProcessor
	{

		/**
		 * The underlying Table to which the formats should be assigned.
		 */
		private final DataTable t;

		private final List<String> columnNames;

		/**
		 * Constructs a new ImportColumnFormats with the given underlying Table.
		 * 
		 * @param table
		 *            The underlying Table to which the formats should be
		 *            assigned
		 * @param columnNames
		 */
		public ImportColumnFormats(DataTable table, List<String> columnNames)
		{
			t = table;
			this.columnNames = columnNames;
		}

		@Override
		public LineProcessor parseLine(LoadContext context, String lstLine, URI sourceURI)
			throws PersistenceLayerException
		{
			ParsingSeparator ps = generateCSVSeparator(lstLine);
			boolean first = true;
			boolean foundEmpty = false;
			int i = 0;
			while (ps.hasNext())
			{
				String formatName = unescape(ps.next());
				if (formatName.isEmpty())
				{
					foundEmpty = true;
					continue;
				}
				//Once an empty item was reached, nothing later on can have content
				if (foundEmpty)
				{
					throw new PersistenceLayerException("Encountered blank FORMAT entry in " + lstLine + " for "
						+ t.getDisplayName() + " in " + sourceURI);
				}
				if (first && formatName.startsWith("STARTTABLE:"))
				{
					throw new PersistenceLayerException("Encountered STARTTABLE: entry while expecting Formats for "
						+ t.getDisplayName() + " in " + sourceURI);
				}
				if (first && formatName.startsWith("ENDTABLE:"))
				{
					throw new PersistenceLayerException("Encountered ENDTABLE: entry while expecting Formats for "
						+ t.getDisplayName() + " in " + sourceURI);
				}
				if (columnNames.size() <= i)
				{
					throw new PersistenceLayerException("Encountered FORMAT " + i + " but no such column was named for "
						+ t.getDisplayName() + " in " + sourceURI);
				}
				String name = columnNames.get(i++);

				TableColumn column = context.getReferenceContext().constructNowIfNecessary(TableColumn.class, name);
				FormatManager<?> format = context.getReferenceContext().getFormatManager(formatName);
				if (column.getFormatManager() == null)
				{
					column.setFormatManager(format);
					column.setSourceURI(sourceURI);
				}
				else if (!column.getFormatManager().equals(format))
				{
					throw new PersistenceLayerException("Table column " + name + " in table " + t.getDisplayName()
						+ " in " + sourceURI + " had different format than previous column format: " + format + " in "
						+ column.getSourceURI());
				}

				t.addColumn(column);
				first = false;
			}
			if (t.getColumnCount() != columnNames.size())
			{
				throw new PersistenceLayerException("Table " + t.getDisplayName()
					+ " had different quantity of column names and formats " + " in " + sourceURI);
			}
			return new ImportData(t);
		}
	}

	/**
	 * ImportData is the LineProcessor that reads in the row data in a Table
	 * file. Each entry must conform to the format defined in the format row of
	 * the Table. There may not be more columns in a data row than there were
	 * formats.
	 */
	private static class ImportData implements LineProcessor
	{

		/**
		 * The underlying Table to which the data will be loaded.
		 */
		private final DataTable t;

		/**
		 * Constructs a new ImportData with the given underlying Table.
		 * 
		 * @param table
		 *            The underlying Table to which the data will be loaded
		 */
		public ImportData(DataTable table)
		{
			t = table;
		}

		@Override
		public LineProcessor parseLine(LoadContext context, String lstLine, URI sourceURI)
			throws PersistenceLayerException
		{
			ParsingSeparator ps = generateCSVSeparator(lstLine);
			int i = 0;
			List<Object> rowContents = new ArrayList<>();
			while (ps.hasNext())
			{
				String content = unescape(ps.next());
				if (i == 0)
				{
					if (content.startsWith("STARTTABLE:"))
					{
						throw new PersistenceLayerException(
							"Encountered STARTTABLE: entry before reaching ENDTABLE for " + t.getDisplayName() + " in "
								+ sourceURI);
					}
					if (content.startsWith("ENDTABLE:"))
					{
						ensureEmpty(lstLine, ps);
						if (t.getRowCount() == 0)
						{
							throw new PersistenceLayerException(
								"Table " + t.getDisplayName() + " had no data in " + sourceURI);
						}
						t.trim();
						return new ExpectStartTable();
					}
				}
				rowContents.add(t.getFormat(i++).convert(content));
			}
			t.addRow(rowContents);
			return this;
		}
	}
}
