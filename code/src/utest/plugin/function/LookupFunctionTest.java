/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;
import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formatmanager.SimpleFormatManagerLibrary;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.base.VariableID;
import pcgen.base.formula.base.VariableLibrary;
import pcgen.base.formula.base.WriteableVariableStore;
import pcgen.base.formula.operator.number.NumberMinus;
import pcgen.base.formula.parse.SimpleNode;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.ReconstructionVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.util.DoubleKeyMap;
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.base.util.ObjectDatabase;
import pcgen.cdom.base.Loadable;
import pcgen.cdom.format.table.ColumnFormatFactory;
import pcgen.cdom.format.table.DataTable;
import pcgen.cdom.format.table.TableColumn;
import pcgen.cdom.format.table.TableFormatFactory;
import plugin.function.testsupport.AbstractFormulaTestCase;
import plugin.function.testsupport.TestUtilities;

public class LookupFunctionTest extends AbstractFormulaTestCase
{

	private SimpleFormatManagerLibrary formatLibrary;

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		formatLibrary = new SimpleFormatManagerLibrary();
		FormatUtilities.loadDefaultFormats(formatLibrary);
		getFunctionLibrary().addFunction(new LookupFunction());
		getOperatorLibrary().addAction(new NumberMinus());
	}

	public DataTable doTableSetup()
	{
		DataTable dt = new DataTable();
		dt.setName("A");

		TableColumn c1 = new TableColumn();
		c1.setName("Name");
		c1.setFormatManager(stringManager);
		TableColumn c2 = new TableColumn();
		c2.setName("Value");
		c2.setFormatManager(numberManager);
		dt.addColumn(c1);
		dt.addColumn(c2);

		List<Object> row = new ArrayList<>();
		row.add("This");
		row.add(1);
		dt.addRow(row);
		row.clear();

		row.add("That");
		row.add(2);
		dt.addRow(row);
		row.clear();

		row.add("The \"Other\"");
		row.add(3);
		dt.addRow(row);
		row.clear();

		return dt;
	}

	@Test
	public void testInvalidWrongArg()
	{
		String formula = "lookup(2)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
		formula = "lookup(2, 3, 4, 5)";
		node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat1()
	{
		Finder finder = new Finder();
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));
		finder.map.put(TableColumn.class, "Result",
			buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();
		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("STRING", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(3,\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat2()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		finder.map.put(DataTable.class, "A", dt);
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));
		finder.map.put(TableColumn.class, "Result",
			buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(finder);
		FormatManager<?> tableMgr = fac.build("STRING,NUMBER", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("STRING", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(TableA,3,ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidWrongFormat3()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		finder.map.put(DataTable.class, "A", dt);

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(finder);
		FormatManager<?> tableMgr = fac.build("STRING,NUMBER", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		String formula = "lookup(TableA,\"That\",3)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidBadSemantics1()
	{
		Finder finder = new Finder();
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));
		finder.map.put(TableColumn.class, "Result",
			buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("STRING", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(badf(),\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidBadSemantics2()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		finder.map.put(DataTable.class, "A", dt);
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));
		finder.map.put(TableColumn.class, "Result",
			buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(finder);
		FormatManager<?> tableMgr = fac.build("STRING,NUMBER", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("STRING", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(TableA,badf(),ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testInvalidBadSemantics3()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		finder.map.put(DataTable.class, "A", dt);
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));
		finder.map.put(TableColumn.class, "Result",
			buildColumn("Value", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(finder);
		FormatManager<?> tableMgr = fac.build("STRING,NUMBER", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		String formula = "lookup(TableA,\"That\",badf())";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testBadResultColumnFormat()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		finder.map.put(DataTable.class, "A", dt);
		finder.map.put(TableColumn.class, "Name",
			buildColumn("Name", stringManager));
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));
		finder.map.put(TableColumn.class, "Result",
			buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(finder);
		FormatManager<?> tableMgr = fac.build("STRING,NUMBER", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("STRING", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Result"));

		String formula = "lookup(TableA,\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);

		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = getManagerFactory()
			.generateFormulaSemantics(getFormulaManager(), getGlobalScope(), null);
		semanticsVisitor.visit(node, semantics);
		if (semantics.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula);
		}
	}

	@Test
	public void testBasic()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		finder.map.put(DataTable.class, "A", dt);
		finder.map.put(TableColumn.class, "Name",
			buildColumn("Name", stringManager));
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(finder);
		FormatManager<?> tableMgr = fac.build("STRING,NUMBER", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(TableA,\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, numberManager, null);
		isStatic(formula, node, false);
		evaluatesTo(formula, node, 2);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertTrue(rv.toString().equals(formula));
	}

	@Test
	public void testNoColumn()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		finder.map.put(DataTable.class, "A", dt);
		finder.map.put(TableColumn.class, "Name",
			buildColumn("Name", stringManager));
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));
		finder.map.put(TableColumn.class, "Result",
			buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(finder);
		FormatManager<?> tableMgr = fac.build("STRING,NUMBER", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Result"));

		String formula = "lookup(TableA,\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, numberManager, null);
		isStatic(formula, node, false);
		EvaluationManager manager = generateManager();
		Object result = new EvaluateVisitor().visit(node, manager);
		if (result instanceof Number)
		{
			TestCase.fail(
				"Expected Invalid result, should have been a string due to invalid column: "
					+ result);
		}
	}

	@Test
	public void testNoLookup()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		finder.map.put(DataTable.class, "A", dt);
		finder.map.put(TableColumn.class, "Name",
			buildColumn("Name", stringManager));
		finder.map.put(TableColumn.class, "Value",
			buildColumn("Value", numberManager));
		finder.map.put(TableColumn.class, "Result",
			buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(finder);
		FormatManager<?> tableMgr = fac.build("STRING,NUMBER", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(TableA,\"Oh No\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		isValid(formula, node, numberManager, null);
		isStatic(formula, node, false);
		EvaluationManager manager = generateManager();
		Object result = new EvaluateVisitor().visit(node, manager);
		if (!result.equals(0))
		{
			TestCase.fail(
				"Expected Invalid result, should have been zero due to invalid column: "
					+ result);
		}
	}

	private Object buildColumn(String string, FormatManager<?> manager)
	{
		TableColumn tc = new TableColumn();
		tc.setName(string);
		tc.setFormatManager(manager);
		return tc;
	}

	private class Finder implements ObjectDatabase
	{
		DoubleKeyMap<Class<?>, String, Object> map = new DoubleKeyMap<>();

		@Override
		public <T extends Loadable> T get(Class<T> cl, String name)
		{
			return (T) map.get(cl, name);
		}

		@Override
		public <T extends Loadable> Indirect<T> getIndirect(Class<T> cl, String name)
		{
			throw new UnsupportedOperationException();
		}
	}

}
