package at.jku.se.calculator.operators;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * This test class performs tests for the {@link AddOperation} class.
 * 
 * @author Michael Vierhauser
 */
public class TestAddOperation {

	private AddOperation add;

	@Before
	public void setup() {
		add = new AddOperation();
		System.out.println("Add Operation initialized ");
	}

	/**
	 * This test case tests the calculate method in the AddOperation class.
	 * 
	 */
	@org.junit.Test
	public void testCalculate() {
		String result = add.caclulate("3+3");
		assertEquals(Integer.parseInt(result), 6);
	}

	/**
	 * This test case tests the calculate method in the AddOperation class.
	 * 
	 */
	@org.junit.Test
	public void testCalculate2() {
		String result = add.caclulate("000+3");
		assertEquals(Integer.parseInt(result), 3);
	}

	/**
	 * This test case tests an illegal input for an {@link AddOperation}. A String
	 * is entered instead of a number. An {@link IllegalArgumentException} should be
	 * thrown
	 * 
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCalculateException() {
		String result = add.caclulate("xyz+3");
		assertEquals(Integer.parseInt(result), 3);
	}

}
