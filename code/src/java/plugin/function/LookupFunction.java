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
import java.util.Optional;

import pcgen.base.formatmanager.FormatUtilities;
import pcgen.base.formula.base.DependencyManager;
import pcgen.base.formula.base.EvaluationManager;
import pcgen.base.formula.base.FormulaFunction;
import pcgen.base.formula.base.FormulaManager;
import pcgen.base.formula.base.FormulaSemantics;
import pcgen.base.formula.exception.SemanticsFailureException;
import pcgen.base.formula.parse.ASTQuotString;
import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.ComparableManager;
import pcgen.base.util.FormatManager;
import pcgen.cdom.format.table.ColumnFormatManager;
import pcgen.cdom.format.table.DataTable;
import pcgen.cdom.format.table.DataTable.LookupType;
import pcgen.cdom.format.table.TableColumn;
import pcgen.cdom.format.table.TableFormatManager;
import pcgen.cdom.formula.ManagerKey;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;

/**
 * This is a Lookup function for finding items in a DataTable.
 * <p>
 * This function requires 3 arguments: (1) The Table (2) The Value to be looked up in the
 * first column (3) The Column of the result to be returned
 */
public class LookupFunction implements FormulaFunction
{

    /**
     * A constant referring to the TableColumn Class.
     */
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
    public FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args, FormulaSemantics semantics)
    {
        int argCount = args.length;
        if ((argCount < 3) || (argCount > 4))
        {
            throw new SemanticsFailureException("Function " + getFunctionName()
                    + " received incorrect # of arguments, expected: 3-4 got " + args.length + ' ' + Arrays.asList(args));
        }

        LoadContext context = semantics.get(ManagerKey.CONTEXT);
        AbstractReferenceContext refContext = context.getReferenceContext();
        //Table node (must be a DataTable)
        @SuppressWarnings("PMD.PrematureDeclaration")
        Object format = args[0].jjtAccept(visitor,
                semantics.getWith(FormulaSemantics.ASSERTED, Optional.of(refContext.getManufacturer(DATATABLE_CLASS))));
        if (!(format instanceof TableFormatManager))
        {
            throw new SemanticsFailureException("Parse Error: Invalid Object: " + format.getClass()
                    + " found in location requiring a TableFormatManager");
        }
        TableFormatManager tableFormatManager = (TableFormatManager) format;
        FormatManager<?> lookupFormat = tableFormatManager.getLookupFormat();

        //Lookup value (at this point we enforce based on the Table Format)
        @SuppressWarnings("PMD.PrematureDeclaration")
        FormatManager<?> luFormat = (FormatManager<?>) args[1].jjtAccept(visitor,
                semantics.getWith(FormulaSemantics.ASSERTED, Optional.of(lookupFormat)));
        if (!lookupFormat.equals(luFormat))
        {
            throw new SemanticsFailureException("Parse Error: Invalid Lookup Object: " + luFormat.getIdentifierType()
                    + " found in location the Table Format says is a " + lookupFormat.getIdentifierType());
        }

        //Result Column
        @SuppressWarnings("PMD.PrematureDeclaration")
        Object resultColumn = args[2].jjtAccept(visitor,
                semantics.getWith(FormulaSemantics.ASSERTED, Optional.of(refContext.getManufacturer(COLUMN_CLASS))));
        if (!(resultColumn instanceof ColumnFormatManager))
        {
            throw new SemanticsFailureException("Parse Error: Invalid Result Column Name: " + resultColumn.getClass()
                    + " found in location requiring a Column");
        }
        ColumnFormatManager<?> cf = (ColumnFormatManager<?>) resultColumn;
        if (argCount == 4)
        {
            if (!(args[3] instanceof ASTQuotString))
            {
                throw new SemanticsFailureException("Parse Error: Invalid lookup type argument: Must be a String");
            }
            ASTQuotString typeNode = (ASTQuotString) args[3];
            String lookupTypeName = typeNode.getText();
            try
            {
                LookupType lookupType = DataTable.LookupType.valueOf(lookupTypeName);
                if (lookupType.requiresSorting() && !(lookupFormat instanceof ComparableManager))
                {
                    throw new SemanticsFailureException(
                            "Parse Error: Lookup type: " + lookupTypeName + " (which requries comparison) was requested on "
                                    + "a format that is not Comparable: " + lookupFormat.getIdentifierType());
                }
            } catch (IllegalArgumentException e)
            {
                throw new SemanticsFailureException("Parse Error: Invalid lookup type: " + lookupTypeName, e);
            }
        }
        return cf.getComponentManager().get();
    }

    @Override
    public Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager manager)
    {
        LoadContext context = manager.get(ManagerKey.CONTEXT);
        AbstractReferenceContext refContext = context.getReferenceContext();
        DataTable dataTable = (DataTable) args[0].jjtAccept(visitor,
                manager.getWith(EvaluationManager.ASSERTED, Optional.of(refContext.getManufacturer(DATATABLE_CLASS))));

        FormatManager<?> lookupFormat = dataTable.getFormat(0);

        //Lookup value (format based on the table)
        @SuppressWarnings("PMD.PrematureDeclaration")
        Object lookupValue =
                args[1].jjtAccept(visitor, manager.getWith(EvaluationManager.ASSERTED, Optional.of(lookupFormat)));

        //Result Column
        TableColumn column = (TableColumn) args[2].jjtAccept(visitor,
                manager.getWith(EvaluationManager.ASSERTED, Optional.of(refContext.getManufacturer(COLUMN_CLASS))));

        String columnName = column.getName();
        if (!dataTable.isColumn(columnName))
        {
            FormatManager<?> fmt = column.getFormatManager();
            System.out.println("Lookup called on invalid column: '" + columnName + "' is not present on table '"
                    + dataTable.getName() + "' assuming default for " + fmt.getIdentifierType());
            FormulaManager fm = manager.get(EvaluationManager.FMANAGER);
            return fm.getDefault(fmt);
        }
        String lookupRule = "EXACT";
        if (args.length == 4)
        {
            lookupRule = (String) args[3].jjtAccept(visitor,
                    manager.getWith(EvaluationManager.ASSERTED, Optional.of(FormatUtilities.STRING_MANAGER)));
        }
        LookupType lookupType = DataTable.LookupType.valueOf(lookupRule);
        if (!dataTable.hasRow(lookupType, lookupValue))
        {
            FormatManager<?> fmt = column.getFormatManager();
            System.out.println(
                    "Lookup called on invalid item: '" + lookupValue + "' is not present in the first row of table '"
                            + dataTable.getName() + "' assuming default for " + fmt.getIdentifierType());
            FormulaManager fm = manager.get(EvaluationManager.FMANAGER);
            return fm.getDefault(fmt);
        }
        return dataTable.lookup(lookupType, lookupValue, columnName);
    }

    @Override
    public Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor, DependencyManager manager, Node[] args)
    {
        LoadContext context = manager.get(ManagerKey.CONTEXT);
        AbstractReferenceContext refContext = context.getReferenceContext();
        //Table name node (must be a Table)
        TableFormatManager tableFormat = (TableFormatManager) args[0].jjtAccept(visitor,
                manager.getWith(DependencyManager.ASSERTED, Optional.of(refContext.getManufacturer(DATATABLE_CLASS))));

        args[1].jjtAccept(visitor,
                manager.getWith(DependencyManager.ASSERTED, Optional.of(tableFormat.getLookupFormat())));

        /*
         * TODO Is there a way to check if the supplied column is part of this table? Not
         * really. If directly included (e.g. via get), it would show up in
         * ManagerKey.REFERENCES, but it could easily be a variable, so there are no
         * guarantees here, and right now, not sure of ROI.
         */
        ColumnFormatManager<?> columnFormat = (ColumnFormatManager<?>) args[2].jjtAccept(visitor,
                manager.getWith(DependencyManager.ASSERTED, Optional.of(refContext.getManufacturer(COLUMN_CLASS))));
        return columnFormat.getComponentManager();
    }

}
