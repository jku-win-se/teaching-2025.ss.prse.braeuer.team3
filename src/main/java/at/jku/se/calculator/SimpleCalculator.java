package at.jku.se.calculator;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.jku.se.calculator.factory.OperationFactory;

public class SimpleCalculator extends JFrame {
//abc
	public enum CalcAction {

		ADD("+"), SUB("-"), MULT("x"), DIV("/"), POW("^");

		CalcAction(String symbol) {
			this.symbol = symbol;
		}

		String symbol;

		public String getSymbol() {
			return symbol;
		}
	}

	/**
	 * UI Components
	 */
	private static final long serialVersionUID = -8989515696863631010L;
	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					SimpleCalculator frame = new SimpleCalculator();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SimpleCalculator() {
		setTitle("Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(0, 2, 2, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new GridLayout(0, 3, 0, 0));

		JButton btnNumber1 = new JButton("1");

		panel.add(btnNumber1);

		JButton btnNumber2 = new JButton("2");

		panel.add(btnNumber2);

		JButton btnNumber3 = new JButton("3");
		panel.add(btnNumber3);

		JButton btnNumber4 = new JButton("4");
		panel.add(btnNumber4);

		JButton btnNumber5 = new JButton("5");
		panel.add(btnNumber5);

		JButton btnNumber6 = new JButton("6");
		panel.add(btnNumber6);

		JButton btnNumber7 = new JButton("7");
		panel.add(btnNumber7);

		JButton btnNumber8 = new JButton("8");
		panel.add(btnNumber8);

		JButton btnNumber9 = new JButton("9");
		panel.add(btnNumber9);

		JButton btnNumber0 = new JButton("0");
		panel.add(btnNumber0);

		JPanel mainPanel = new JPanel();
		contentPane.add(mainPanel);
		mainPanel.setLayout(new BorderLayout(0, 0));

		textField = new JTextField();
		textField.setFont(new Font("Consolas", Font.PLAIN, 25));
		mainPanel.add(textField, BorderLayout.NORTH);
		textField.setColumns(10);
		textField.setText("0");
		JButton btnSum = new JButton("=");

		mainPanel.add(btnSum, BorderLayout.EAST);

		JPanel centerPanel = new JPanel();
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new GridLayout(0, 2, 0, 0));

		JButton btnPlus = new JButton("+");
		centerPanel.add(btnPlus);

		JButton btnMinus = new JButton("-");
		centerPanel.add(btnMinus);

		JButton btnTimes = new JButton("x");
		centerPanel.add(btnTimes);

		JButton btnDiv = new JButton("/");
		centerPanel.add(btnDiv);

		JButton btnPow = new JButton("x^y");
		btnPow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		centerPanel.add(btnPow);

		JButton btnCE = new JButton("CE");
		centerPanel.add(btnCE);

		btnCE.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textField.setText("0");
			}
		});

		btnPlus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkAndAppend(CalcAction.ADD);
			}
		});
		btnMinus.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkAndAppend(CalcAction.SUB);
			}
		});
		btnDiv.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkAndAppend(CalcAction.DIV);
			}
		});
		btnTimes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkAndAppend(CalcAction.MULT);
			}
		});
		btnPow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkAndAppend(CalcAction.POW);
			}
		});

		btnSum.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				checkAndAppend(null);
			}
		});

		btnNumber1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(1);
			}
		});
		btnNumber2.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(2);
			}
		});
		btnNumber3.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(3);
			}
		});
		btnNumber4.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(4);
			}
		});
		btnNumber5.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(5);
			}
		});
		btnNumber6.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(6);
			}
		});
		btnNumber7.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(7);
			}
		});
		btnNumber8.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(8);
			}
		});
		btnNumber9.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(9);
			}
		});
		btnNumber0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addText(0);
			}
		});
	}

	protected void checkAndAppend(CalcAction action) {
		String txt = textField.getText();
		try {
		if (txt.contains("-")) {
			txt = OperationFactory.getOperation(CalcAction.SUB).caclulate(txt);
		} else if (txt.contains("+")) {
			txt = OperationFactory.getOperation(CalcAction.ADD).caclulate(txt);
		} else if (txt.contains("/")) {
			txt = OperationFactory.getOperation(CalcAction.DIV).caclulate(txt);
		} else if (txt.contains("x")) {
			txt = OperationFactory.getOperation(CalcAction.MULT).caclulate(txt);
		} else if (txt.contains("^")) {
			txt = OperationFactory.getOperation(CalcAction.POW).caclulate(txt);
		}
		try {
			txt = new Double(txt).intValue() + "";
		} catch (NumberFormatException ex) {
		}
		}catch(IllegalArgumentException e) {
			txt = "Error";
		}
		textField.setText(txt + (action == null ? "" : action.getSymbol()));
	}

	protected void addText(int i) {
		String txt = textField.getText();
		if (txt != null) {
			textField.setText(txt + i);
		} else {
			textField.setText("" + i);
		}
	}
}