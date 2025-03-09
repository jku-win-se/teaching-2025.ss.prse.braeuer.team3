package at.jku.se.calculator.operators;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.jku.se.calculator.factory.ICalculationOperation;

public class AddOperation implements ICalculationOperation {

	private static final Logger LOGGER = LogManager.getLogger(AddOperation.class);

	@Override
	public String caclulate(String txt) {
		LOGGER.info("Add Operation executed: " + txt);
		String[] terms = txt.split("\\+");
		if (terms.length == 2) {
			if (!StringUtils.isNumeric(terms[0])) {
				LOGGER.error("Invalid Value");
				throw new IllegalArgumentException(String.format("%s is not a valid number", terms[0]));
			}
			if (!StringUtils.isNumeric(terms[1])) {
				LOGGER.error("Invalid Value");
				throw new IllegalArgumentException(String.format("%s is not a valid number", terms[1]));
			}

			return (Integer.parseInt(terms[0]) + Integer.parseInt(terms[1])) + "";
		} else {
			throw new IllegalArgumentException("Input not correct!");
		}

	}

}