package at.jku.se.calculator.factory;

/**
 * Calculation Operation interface.<br>
 * Each Operation provided by the {@link OperationFactory} has to implement this
 * interface.
 * 
 * @author Michael Vierhauser
 */
public interface ICalculationOperation {

	String caclulate(String input);
}
