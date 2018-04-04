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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import pcgen.base.util.FormatManager;
import pcgen.base.util.Indirect;
import pcgen.cdom.format.table.ColumnFormatFactory;
import pcgen.cdom.format.table.DataTable;
import pcgen.cdom.format.table.TableColumn;
import pcgen.cdom.format.table.TableFormatFactory;
import pcgen.cdom.formula.ManagerKey;
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
		getFunctionLibrary().addFunction(new GetFunction());
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

	public DataTable doNumberTableSetup()
	{
		DataTable dt = new DataTable();
		dt.setName("B");

		TableColumn c1 = new TableColumn();
		c1.setName("Strength");
		c1.setFormatManager(numberManager);
		TableColumn c2 = new TableColumn();
		c2.setName("Square");
		c2.setFormatManager(numberManager);
		dt.addColumn(c1);
		dt.addColumn(c2);

		List<Object> row = new ArrayList<>();
		row.add(1);
		row.add(1);
		dt.addRow(row);
		row.clear();

		row.add(2);
		row.add(4);
		dt.addRow(row);
		row.clear();

		row.add(3);
		row.add(9);
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
		finder.map.put("Value", buildColumn("Value", numberManager));
		finder.map.put("Result", buildColumn("Result", stringManager));

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
		TableFinder tablefinder = new TableFinder();
		DataTable dt = doTableSetup();
		tablefinder.map.put("A", dt);
		finder.map.put("Value", buildColumn("Value", numberManager));
		finder.map.put("Result", buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(tablefinder);
		FormatManager<?> tableMgr = fac.build("STRING", formatLibrary);
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
		TableFinder tablefinder = new TableFinder();
		DataTable dt = doTableSetup();
		tablefinder.map.put("A", dt);

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(tablefinder);
		FormatManager<?> tableMgr = fac.build("STRING", formatLibrary);
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
		finder.map.put("Value", buildColumn("Value", numberManager));
		finder.map.put("Result", buildColumn("Result", stringManager));

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
		TableFinder tablefinder = new TableFinder();
		DataTable dt = doTableSetup();
		tablefinder.map.put("A", dt);
		finder.map.put("Value", buildColumn("Value", numberManager));
		finder.map.put("Result", buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(tablefinder);
		FormatManager<?> tableMgr = fac.build("STRING", formatLibrary);
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
		TableFinder tablefinder = new TableFinder();
		DataTable dt = doTableSetup();
		tablefinder.map.put("A", dt);
		finder.map.put("Value", buildColumn("Value", numberManager));
		finder.map.put("Result", buildColumn("Value", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(tablefinder);
		FormatManager<?> tableMgr = fac.build("STRING", formatLibrary);
		vl.assertLegalVariableID("TableA", getGlobalScope(), tableMgr);

		VariableID tableID = vl.getVariableID(getGlobalScopeInst(), "TableA");
		vs.put(tableID, tableMgr.convert("A"));

		String formula = "lookup(TableA,\"That\",badf())";
		SimpleNode node = TestUtilities.doParse(formula);
		isNotValid(formula, node, numberManager, null);
	}

	@Test
	public void testBasic()
	{
		Finder finder = new Finder();
		TableFinder tablefinder = new TableFinder();
		DataTable dt = doTableSetup();
		tablefinder.map.put("A", dt);
		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(tablefinder);
		FormatManager<?> tableMgr = fac.build("STRING", formatLibrary);
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
		assertEquals(formula, rv.toString());
	}


	@Test
	public void testInvalidFormatDirect()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		context.getReferenceContext().importObject(dt);
		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(\"TABLE[NUMBER]\",\"A\",\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(numberManager);
		semantics = semantics.getWith(ManagerKey.CONTEXT, context);
		semanticsVisitor.visit(node, semantics);
		if (semantics.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula);
		}
	}

	@Test
	public void testInvalidTableFormatDirect()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		context.getReferenceContext().importObject(dt);
		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(\"NUMBER\",\"A\",\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(numberManager);
		semantics = semantics.getWith(ManagerKey.CONTEXT, context);
		semanticsVisitor.visit(node, semantics);
		if (semantics.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula);
		}
	}

	@Test
	public void testInvalidNameDirect()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		context.getReferenceContext().importObject(dt);
		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(\"TABLE[STRING]\",55,\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(numberManager);
		semantics = semantics.getWith(ManagerKey.CONTEXT, context);
		semanticsVisitor.visit(node, semantics);
		if (semantics.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula);
		}
	}

	@Test
	public void testDirect()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		context.getReferenceContext().importObject(dt);
		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(get(\"TABLE[STRING]\",\"A\"),\"That\",ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(numberManager);
		semantics = semantics.getWith(ManagerKey.CONTEXT, context);
		semanticsVisitor.visit(node, semantics);
		if (!semantics.isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: " + semantics.getReport());
		}
		isStatic(formula, node, false);
		evaluatesTo(formula, node, 2);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
	}

	@Test
	public void testInDirectColumn()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		context.getReferenceContext().importObject(dt);
		for (int i = 0; i < dt.getColumnCount(); i++)
		{
			context.getReferenceContext().importObject(dt.getColumn(i));
		}

		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		VariableLibrary vl = getVariableLibrary();
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		String formula = "lookup(get(\"TABLE[STRING]\",\"A\"),\"That\",get(\"COLUMN[NUMBER]\",\"Value\"))";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(numberManager);
		semantics = semantics.getWith(ManagerKey.CONTEXT, context);
		semanticsVisitor.visit(node, semantics);
		if (!semantics.isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: " + semantics.getReport());
		}
		isStatic(formula, node, false);
		evaluatesTo(formula, node, 2);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());
	}

	@Test
	public void testInvalidExtra()
	{
		Finder finder = new Finder();
		DataTable dt = doTableSetup();
		context.getReferenceContext().importObject(dt);
		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Value"));

		String formula = "lookup(\"TABLE[STRING]\",\"A\",\"That\",ResultColumn,\"TooMuch\")";
		SimpleNode node = TestUtilities.doParse(formula);
		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(numberManager);
		semantics = semantics.getWith(ManagerKey.CONTEXT, context);
		semanticsVisitor.visit(node, semantics);
		if (semantics.isValid())
		{
			TestCase.fail("Expected Invalid Formula: " + formula);
		}
	}

	@Test
	public void testNoColumn()
	{
		Finder finder = new Finder();
		TableFinder tablefinder = new TableFinder();
		DataTable dt = doTableSetup();
		tablefinder.map.put("A", dt);
		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));
		finder.map.put("Result", buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(tablefinder);
		FormatManager<?> tableMgr = fac.build("STRING", formatLibrary);
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
		TableFinder tablefinder = new TableFinder();
		DataTable dt = doTableSetup();
		tablefinder.map.put("A", dt);
		finder.map.put("Name", buildColumn("Name", stringManager));
		finder.map.put("Value", buildColumn("Value", numberManager));
		finder.map.put("Result", buildColumn("Result", stringManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		TableFormatFactory fac = new TableFormatFactory(tablefinder);
		FormatManager<?> tableMgr = fac.build("STRING", formatLibrary);
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

	private TableColumn buildColumn(String string, FormatManager<?> manager)
	{
		TableColumn tc = new TableColumn();
		tc.setName(string);
		tc.setFormatManager(manager);
		return tc;
	}

	private static class Finder implements FormatManager<TableColumn>
	{
		Map<String, TableColumn> map = new HashMap<>();

		@Override
		public TableColumn convert(String inputStr)
		{
			return map.get(inputStr);
		}

		@Override
		public Indirect<TableColumn> convertIndirect(String inputStr)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isDirect()
		{
			return true;
		}

		@Override
		public String unconvert(TableColumn obj)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<TableColumn> getManagedClass()
		{
			return TableColumn.class;
		}

		@Override
		public String getIdentifierType()
		{
			return "COLUMN";
		}

		@Override
		public FormatManager<?> getComponentManager()
		{
			return null;
		}
	}

	private static class TableFinder implements FormatManager<DataTable>
	{
		Map<String, DataTable> map = new HashMap<>();

		@Override
		public DataTable convert(String inputStr)
		{
			return map.get(inputStr);
		}

		@Override
		public Indirect<DataTable> convertIndirect(String inputStr)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isDirect()
		{
			return true;
		}

		@Override
		public String unconvert(DataTable obj)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public Class<DataTable> getManagedClass()
		{
			return DataTable.class;
		}

		@Override
		public String getIdentifierType()
		{
			return "TABLE";
		}

		@Override
		public FormatManager<?> getComponentManager()
		{
			return null;
		}
	}

	@Override
	public EvaluationManager generateManager()
	{
		EvaluationManager em = super.generateManager();
		return em.getWith(ManagerKey.CONTEXT, context);
	}
	
	public void testLessThan()
	{
		Finder finder = new Finder();
		DataTable dt = doNumberTableSetup();
		context.getReferenceContext().importObject(dt);
		finder.map.put("Strength", buildColumn("Strength", numberManager));
		finder.map.put("Square", buildColumn("Square", numberManager));

		VariableLibrary vl = getVariableLibrary();
		WriteableVariableStore vs = getVariableStore();

		ColumnFormatFactory cfac = new ColumnFormatFactory(finder);
		FormatManager<?> columnMgr = cfac.build("NUMBER", formatLibrary);
		vl.assertLegalVariableID("ResultColumn", getGlobalScope(), columnMgr);

		VariableID columnID =
				vl.getVariableID(getGlobalScopeInst(), "ResultColumn");
		vs.put(columnID, columnMgr.convert("Square"));

		String formula = "lookup(get(\"TABLE[NUMBER]\",\"B\"),2,ResultColumn)";
		SimpleNode node = TestUtilities.doParse(formula);
		isStatic(formula, node, false);
		Object rv =
				new ReconstructionVisitor().visit(node, new StringBuilder());
		assertEquals(formula, rv.toString());

		SemanticsVisitor semanticsVisitor = new SemanticsVisitor();
		FormulaSemantics semantics = generateFormulaSemantics(numberManager);
		semantics = semantics.getWith(ManagerKey.CONTEXT, context);
		semanticsVisitor.visit(node, semantics);
		if (!semantics.isValid())
		{
			TestCase.fail("Expected Valid Formula: " + formula
				+ " but was told: " + semantics.getReport());
		}
		evaluatesTo(formula, node, 4);

		formula = "lookup(get(\"TABLE[NUMBER]\",\"B\"),3,ResultColumn)";
		node = TestUtilities.doParse(formula);
		evaluatesTo(formula, node, 9);
		formula = "lookup(get(\"TABLE[NUMBER]\",\"B\"),1,ResultColumn,\"EXACT\")";
		node = TestUtilities.doParse(formula);
		evaluatesTo(formula, node, 1);
		formula = "lookup(get(\"TABLE[NUMBER]\",\"B\"),3,ResultColumn,\"LASTLTEQ\")";
		node = TestUtilities.doParse(formula);
		evaluatesTo(formula, node, 9);
		formula = "lookup(get(\"TABLE[NUMBER]\",\"B\"),3.5,ResultColumn,\"LASTLTEQ\")";
		node = TestUtilities.doParse(formula);
		evaluatesTo(formula, node, 9);
		formula = "lookup(get(\"TABLE[NUMBER]\",\"B\"),3.5,ResultColumn,\"EXACT\")";
		node = TestUtilities.doParse(formula);
		evaluatesTo(formula, node, 0);
	}

}
