package at.jku.se.calculator.operators;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.jku.se.calculator.factory.ICalculationOperation;

public class NullOperation implements ICalculationOperation {

	private static final Logger LOGGER = LogManager.getLogger(NullOperation.class);
	
	@Override
	public String caclulate(String txt) {
		LOGGER.warn("This Calculation Operation is not implemented!");
		return "0";

	}

}