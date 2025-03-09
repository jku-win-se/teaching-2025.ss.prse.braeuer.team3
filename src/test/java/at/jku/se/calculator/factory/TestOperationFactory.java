package at.jku.se.calculator.factory;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import at.jku.se.calculator.SimpleCalculator.CalcAction;
import at.jku.se.calculator.operators.AddOperation;

/**
 * This test class performs tests for the {@link OperationFactory} class.
 * 
 * @author Michael Vierhauser
 */
public class TestOperationFactory {

	/**
	 * This test case tests the getOperation Method of the OperationFactory. A
	 * method is requested for the add operation and the respective implementation
	 * should be returned.
	 * 
	 */
	@org.junit.Test
	public void testAdditionOperation() {
		ICalculationOperation operation = OperationFactory.getOperation(CalcAction.ADD);
		assertThat(operation, instanceOf(AddOperation.class));
	}
}