module pcgen.formula {
    requires transitive pcgen.base;

    exports pcgen.base.formula;
    exports pcgen.base.formula.analysis;
    exports pcgen.base.formula.base;
    exports pcgen.base.formula.exception;
    exports pcgen.base.formula.factory;
    exports pcgen.base.formula.function;
    exports pcgen.base.formula.inst;
    exports pcgen.base.formula.library;
    exports pcgen.base.formula.operator.array;
    exports pcgen.base.formula.operator.bool;
    exports pcgen.base.formula.operator.generic;
    exports pcgen.base.formula.operator.number;
    exports pcgen.base.formula.operator.string;
    exports pcgen.base.formula.parse;
    exports pcgen.base.formula.visitor;
    exports pcgen.base.solver;
}
