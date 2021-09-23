/*
 * Copyright 2014-8 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.base.formula.base;

import java.util.Optional;

import pcgen.base.formula.parse.Node;
import pcgen.base.formula.visitor.DependencyVisitor;
import pcgen.base.formula.visitor.EvaluateVisitor;
import pcgen.base.formula.visitor.SemanticsVisitor;
import pcgen.base.formula.visitor.StaticVisitor;
import pcgen.base.util.FormatManager;

/**
 * A FormulaFunction is part of a Formula that performs an operation. It can be
 * distinguished by a set of identifying characters surrounding zero or more arguments to
 * the function. (An example is the min function which might look like: "min(a, b)")
 * 
 * For ease of use, it is recommended that FormulaFunction names be considered
 * case-insensitive, but that is not a contractual requirement of this interface. Rather,
 * it is enforced by the system that manages Functions, see, for example, FunctionLibrary.
 * 
 * This interface represents the common behaviors of a function, including the ability to
 * validate the arguments, the ability to evaluate the value of the function, and
 * determine if the FormulaFunction will return a static value (does not depend on any
 * variables).
 * 
 * Note that it is a contractual obligation of the FormulaFunction interface that the
 * combination of allowArgs() and getDependencies() can identify all reasonable situations
 * that could cause evaluate() to fail. For example, the formula "age("Bob")" may be
 * passed by allowArgs() because the database to determine "Bob" being a legal name is not
 * present (and allowArgs is designed to be used as early as possible). If allowArgs
 * returns TRUE and has NOT validated the person "Bob" actually exists, then
 * getDependencies() MUST be able to load an appropriate manager with a DependencyManager
 * in order to indicate that "Bob" is a name upon which the formula is dependent.
 * 
 * Note as part of that contractual obligation, exceptions that can only be detected at
 * Runtime (such as division by zero in Java) don't need to be caught by the
 * combination of allowArgs() and getDependencies().
 */
public interface FormulaFunction
{

	/**
	 * Returns the name for this FormulaFunction. This is how it is called by a user in a
	 * formula.
	 * 
	 * For ease of use, it is recommended that the value returned by this method to be
	 * evaluated/matched by the formula system in a case-insensitive fashion, but that is
	 * not strictly required by this interface.
	 * 
	 * It is a contract of the FormulaFunction interface that this method must never
	 * return null.
	 * 
	 * @return The name for this FormulaFunction
	 */
	String getFunctionName();

	/**
	 * Checks if the given arguments are static using the given StaticVisitor.
	 * 
	 * Static is defined as producing a known value (meaning none of the arguments can
	 * ever change). This would allow, for example, the result of a Formula to be
	 * calculated once and cached if it is static. Any formula/function that contains a
	 * variable is by definition not static.
	 * 
	 * This method assumes the arguments are valid values in a formula (such as a number,
	 * variable, or another function).
	 * 
	 * The results of calling this method are not defined (exceptions may be thrown) if
	 * allowArgs returns a FormulaValidity that indicates the formula is not valid.
	 * allowArgs should always be called prior to calling this method.
	 * 
	 * Note that the static nature of variables or content within the FormulaFunction that
	 * can be evaluated as a sub formula (e.g. both "min(4,T)" and "Y" as part of
	 * "max(min(4,T),Y)") should be passed back into the provided StaticVisitor for
	 * further analysis. Therefore, the FormulaFunction makes no direct claims of whether
	 * a variable or subfunction is valid or not - that responsibility is entirely
	 * delegated back to the StaticVisitor.
	 * 
	 * The contract of the FormulaFunction interface requires that the arguments passed to
	 * this method are not null and the returned value must not be null. In addition, the
	 * contract specifies that the args array provided as a parameter has ownership
	 * transferred to the function. The StaticVisitor or other calling object should not
	 * reuse or otherwise share a reference to the array.
	 * 
	 * @param visitor
	 *            The StaticVisitor that visits portions of a Formula
	 * @param args
	 *            The arguments to this FormulaFunction within the Formula
	 * @return A non-null Boolean value indicating whether the value of this
	 *         FormulaFunction is static for the given arguments
	 */
	Boolean isStatic(StaticVisitor visitor, Node[] args);

	/**
	 * Checks if the given arguments are valid using the given SemanticsVisitor, loading
	 * any necessary information into the given FormulaSemantics.
	 * 
	 * This must check the entire set of arguments to the FormulaFunction for validity, as
	 * best can be done (assuming no items are resolvable to actual values).
	 * 
	 * For example: A function such as: classlevel("Fighter","APPLIEDAS=NonEpic") should:
	 * (a) Ensure that two arguments is a legal form (b) Check that the "APPLIEDAS="
	 * string is a valid prefix to the second argument (c) Check that "NonEpic" is a valid
	 * suffix for the second argument.
	 * 
	 * Optionally, some processing to ensure that "Fighter" is a legal first argument may
	 * be possible. This will be domain-specific.
	 * 
	 * This optional situation is a recognition that allowArgs is not required to catch
	 * situations it cannot reasonably predict: Specifically an unconstructed object which
	 * should be present at the time of evaluation. allowArgs is thus checking mostly for
	 * semantic legality, not necessarily content legality. However, allowArgs is part of
	 * a contractual obligation to eventually provide content legality.
	 * 
	 * Specifically, an example may be "age("Bob")". Since it is intended for allowArgs to
	 * be used early in a program life-cycle, it is not reasonable to assume the database
	 * indicating legal names is present. Some checking, such as ensuring the name doesn't
	 * have illegal symbols ("*" or "$") may optionally be reported in allowArgs.
	 * 
	 * However, if the database indicating that "Bob" is a legal name is not present when
	 * allowArgs is (designed to be) called, then it is a contractual obligation of the
	 * FormulaFunction interface that getDependencies indicate that "Bob" is a required
	 * name for the Formula to be evaluated.
	 * 
	 * Note also that the legality of variables or content within the FormulaFunction that
	 * can be evaluated as a sub formula (e.g. both "min(4,T)" and "Y" as part of
	 * "max(min(4,T),Y)") should be passed back into the provided SemanticsVisitor for
	 * further analysis. Therefore, the FormulaFunction makes no direct claims of whether
	 * a variable or subfunction is valid or not - that responsibility is entirely
	 * delegated back to the SemanticsVisitor.
	 * 
	 * The contract of the FormulaFunction interface requires that the arguments passed to
	 * this method are not null and the returned value must not be null. In addition, the
	 * contract specifies that the args array provided as a parameter has ownership
	 * transferred to the function. The SemanticsVisitor or other calling object should
	 * not reuse or otherwise share a reference to the array. The given FormulaSemantics
	 * object will be modified as necessary.
	 * 
	 * @param visitor
	 *            The SemanticsVisitor that visits portions of a Formula
	 * @param args
	 *            The arguments to this FormulaFunction within the Formula
	 * @param semantics
	 *            The FormulaSemantics object that is used to capture semantic information
	 *            about this FormulaFunction
	 * @return a FormatManager indicating the format of the value returned by this
	 *         FormulaFunction
	 */
	FormatManager<?> allowArgs(SemanticsVisitor visitor, Node[] args,
		FormulaSemantics semantics);

	/**
	 * Evaluates the given arguments using the given EvaluateVisitor.
	 * 
	 * This method assumes the arguments are valid values.
	 * 
	 * The results of calling this method are not defined (exceptions may be thrown) if
	 * allowArgs returns a FormulaValidity that indicates the formula is not valid.
	 * allowArgs should always be called prior to calling this method.
	 * 
	 * Note that the evaluation of variables or content within the FormulaFunction that
	 * can be evaluated as a sub formula (e.g. both "min(4,T)" and "Y" as part of
	 * "max(min(4,T),Y)") should be passed back into the provided EvaluateVisitor for
	 * resolution. Therefore, the FormulaFunction makes no direct evaluation a variable or
	 * subfunction - that responsibility is entirely delegated back to the
	 * EvaluateVisitor.
	 * 
	 * The contract of the FormulaFunction interface requires that the arguments passed to
	 * this method are not null and the returned value must not be null. In addition, the
	 * contract specifies that the args array provided as a parameter has ownership
	 * transferred to the function. The EvaluateVisitor or other calling object should not
	 * reuse or otherwise share a reference to the array.
	 * 
	 * Note that this returns Object, since we do not know whether the FormulaFunction
	 * returns a Boolean or a Double value (or anything else). The semantic rules of what
	 * is returned must have been clearly provided in allowArgs, again, reinforcing the
	 * importance of running allowArgs before evaluate.
	 * 
	 * @param visitor
	 *            The EvaluateVisitor that visits portions of a Formula
	 * @param args
	 *            The arguments to this FormulaFunction within the Formula
	 * @param manager
	 *            The EvaluationManager for the context of the FormulaFunction
	 * @return A non-null object that is the result of performing the calculation defined
	 *         by this FormulaFunction
	 */
	Object evaluate(EvaluateVisitor visitor, Node[] args, EvaluationManager manager);

	/**
	 * Captures dependencies of this function. This may include Variables (in the form of
	 * VariableIDs), but is not limited to those as the only possible dependency.
	 * 
	 * This must be applied recursively for any contents within this FormulaFunction (if
	 * this FormulaFunction calls another function, etc. all variables in the tree below
	 * this FormulaFunction are included)
	 * 
	 * The results of calling this method are not defined if allowArgs returns a
	 * FormulaSemantics that indicates the formula is not valid. allowArgs should always be
	 * called prior to calling this method.
	 * 
	 * Note also that the legality of variables or content within the FormulaFunction that
	 * can be evaluated as a sub formula (e.g. both "min(4,T)" and "Y" as part of
	 * "max(min(4,T),Y)") should be passed back into the provided DependencyVisitor for
	 * further analysis. Therefore, the FormulaFunction directly makes no claims of
	 * whether a variable or subfunction has any dependencies - that is entirely the
	 * responsibility delegated back to DependencyVisitor.
	 * 
	 * The contract of the FormulaFunction interface requires that the arguments passed to
	 * this method are not null and the returned value must not be null. The provided
	 * DependencyManager may be altered in this method (that's kind of the idea :P ). In
	 * addition, the contract specifies that the args array provided as a parameter has
	 * ownership transferred to the function. The DependencyVisitor or other calling
	 * object should not reuse or otherwise share a reference to the array.
	 * 
	 * @param visitor
	 *            The DependencyVisitor that visits portions of a Formula
	 * @param manager
	 *            The DependencyManager used to support analysis of the FormulaFunction
	 * @param args
	 *            The arguments to this FormulaFunction within the Formula
	 * @return An Optional FormatManager indicating the format of the value returned by this
	 *         FormulaFunction
	 */
	Optional<FormatManager<?>> getDependencies(DependencyVisitor visitor, DependencyManager manager,
		Node[] args);

	/*
	 * Note: The non return of the managers are intentional, even though at first glance
	 * it seems inconsistent with the visitor pattern most of the methods in
	 * FormulaFunction are used with. The reason for this design is due to the polymorphic
	 * behavior of those classes (like DependencyManager).
	 * 
	 * In the case of "isStatic" or "evaluate", both of those return a definitive class
	 * (Boolean or some form of Number). Those are concrete and unambiguous behaviors. In
	 * the case of DependencyManager, it is *expected* that the behavior will be enhanced
	 * as domain-specific methods need to define domain-specific dependencies.
	 * 
	 * As a result, full replacement of the "active" the DependencyManager is prohibited
	 * for a Function. The entire ownership of the "active" DependencyManager is contained
	 * within the DependencyVisitor (which presumably only returns the DependencyManager
	 * that was originally provided in the visit call to the root node).
	 * 
	 * So generally, this is restricting behavior in order to protect the dependency
	 * analysis system from a FormulaFunction that was designed for a specific domain but
	 * was used in a more complex situation (of which it is not, and should not, be
	 * aware).
	 */

}
