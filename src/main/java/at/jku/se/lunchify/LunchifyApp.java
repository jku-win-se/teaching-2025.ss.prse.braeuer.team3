package at.jku.se.lunchify;

import view.LoginView;

import javax.swing.*;

public class LunchifyApp {

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		SwingUtilities.invokeLater(() -> new LoginView());
	}
}
