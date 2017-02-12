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
package plugin.function;

import java.util.Arrays;

import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.Function;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;
import pcgen.cdom.format.table.ColumnFormatManager;
import pcgen.cdom.format.table.DataTable;
import pcgen.cdom.format.table.TableColumn;
import pcgen.cdom.format.table.TableFormatManager;

/**
 * This is a Lookup function for finding items in a DataTable.
 * 
 * This function requires 3 arguments: (1) The Table Name (2) The Value to be
 * looked up in the first column (3) The Column name of the result to be
 * returned
 */
public class LookupFunction implements Function
{

	/**
	 * A constant referring to the TableColumn Class.
	 */
	@SuppressWarnings("rawtypes")
	private static final Class<TableColumn> COLUMN_CLASS = TableColumn.class;

	/**
	 * A constant referring to the Table Class.
	 */
	private static final Class<DataTable> DATATABLE_CLASS = DataTable.class;

	@Override
	public String getFunctionName()
	{
		return "Lookup";
	}

	@Override
	public Boolean isStatic(StaticVisitor visitor, Node[] args)
	{
		return false;
	}

	@Override
	public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics)
	{
		int argCount = args.length;
		if (argCount != 3)
		{
			semantics.setInvalid("Function " + getFunctionName()
				+ " received incorrect # of arguments, expected: 3 got "
				+ args.length + " " + Arrays.asList(args));
			return null;
		}

		//Table name node (must be a DataTable)
		@SuppressWarnings("PMD.PrematureDeclaration")
		Object tableFormat = args[0].jjtAccept(visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, DATATABLE_CLASS));
		if (!semantics.isValid())
		{
			return null;
		}
		if (!(tableFormat instanceof TableFormatManager))
		{
			semantics.setInvalid(
				"Parse Error: Invalid Object: " + tableFormat.getClass()
					+ " found in location requiring a " + "TableFormatManager");
			return null;
		}
		@SuppressWarnings("unchecked")
		TableFormatManager tableFormatManager =
				(TableFormatManager) tableFormat;

		//Lookup value (at this point we don't know the format - only at runtime)
		FormatManager<?> lookupFormat = tableFormatManager.getLookupFormat();
		args[1].jjtAccept(visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, lookupFormat.getManagedClass()));
		if (!semantics.isValid())
		{
			return null;
		}

		//Result Column Name (must be a String)
		@SuppressWarnings("PMD.PrematureDeclaration")
		Object resultColumn = args[2].jjtAccept(visitor,
			semantics.getWith(FormulaSemantics.ASSERTED, COLUMN_CLASS));
		if (!semantics.isValid())
		{
			return null;
		}
		if (!(resultColumn instanceof ColumnFormatManager))
		{
			semantics.setInvalid("Parse Error: Invalid Result Column Name: "
				+ resultColumn.getClass()
				+ " found in location requiring a Column");
			return null;
		}
		ColumnFormatManager<?> cf = (ColumnFormatManager<?>) resultColumn;
		FormatManager<?> rf = tableFormatManager.getResultFormat();
		if (!rf.equals(cf.getComponentManager()))
		{
			semantics.setInvalid("Parse Error: Invalid Result Column Type: "
				+ resultColumn.getClass()
				+ " found in table that does not contain that type");
			return null;
		}
		return rf;
	}

	@Override
	public Object evaluate(EvaluateVisitor visitor, Node[] args,
		EvaluationManager manager)
	{
		//Table name node (must be a Table)
		DataTable dataTable = (DataTable) args[0].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, DATATABLE_CLASS));

		FormatManager<?> lookupFormat = dataTable.getFormat(0);

		//Lookup value (format based on the table)
		@SuppressWarnings("PMD.PrematureDeclaration")
		Object lookupValue = args[1].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, lookupFormat.getManagedClass()));

		//Result Column Name (must be a tableColumn)
		TableColumn column = (TableColumn) args[2].jjtAccept(visitor,
			manager.getWith(EvaluationManager.ASSERTED, COLUMN_CLASS));

		String columnName = column.getName();
		if (!dataTable.isColumn(columnName))
		{
			FormatManager<?> fmt = column.getFormatManager();
			System.out.println("Lookup called on invalid column: '" + columnName
				+ "' is not present on table '" + dataTable.getName()
				+ "' assuming default for " + fmt.getIdentifierType());
			FormulaManager fm = manager.get(EvaluationManager.FMANAGER);
			return fm.getDefault(fmt.getManagedClass());
		}
		if (!dataTable.hasRow(lookupValue))
		{
			FormatManager<?> fmt = column.getFormatManager();
			System.out.println("Lookup called on invalid item: '" + lookupValue
				+ "' is not present in the first row of table '"
				+ dataTable.getName() + "' assuming default for "
				+ fmt.getIdentifierType());
			FormulaManager fm = manager.get(EvaluationManager.FMANAGER);
			return fm.getDefault(fmt.getManagedClass());
		}
		return dataTable.lookupExact(lookupValue, columnName);
	}

	@Override
	public void getDependencies(DependencyVisitor visitor,
		DependencyManager manager, Node[] args)
	{
		args[0].jjtAccept(visitor,
			manager.getWith(DependencyManager.ASSERTED, DATATABLE_CLASS));

		//TODO a Semantics Check can tell what this is
		args[1].jjtAccept(visitor, manager.getWith(DependencyManager.ASSERTED, null));

		args[2].jjtAccept(visitor,
			manager.getWith(DependencyManager.ASSERTED, COLUMN_CLASS));
	}

}
