package com.github.deliberateq.qsort.gui;

public class Preferences {

	public static final String EIGENVALUE_THRESHOLD = "Eigenvalue Threshold";
	public static final String EIGENVALUE_THRESHOLD_DEFAULT = "1.0";
	public static final String VENN_MAX_STANDARD_ERRORS = "Venn Diagram Max SE";
	public static final String MAX_PRINCIPAL_FACTORS = "Max Principal Factors";
	public static final String MAX_PRINCIPAL_FACTORS_DEFAULT = "8";
	public static final String SYSTEM_LOOK_AND_FEEL = "System Look and Feel";
	public static final String SYSTEM_LOOK_AND_FEEL_DEFAULT = "false";

	private static Preferences instance;

	public synchronized static Preferences getInstance() {
		if (instance == null)
			instance = new Preferences();
		return instance;
	}

	private final java.util.prefs.Preferences prefs;

	public Preferences() {
		// props = new Properties();
		// props.setProperty(EIGENVALUE_THRESHOLD, "1");
		prefs = java.util.prefs.Preferences.userNodeForPackage(this.getClass());
	}

	public String getProperty(String key, String defaultValue) {
		return prefs.get(key, defaultValue);
	}

	public void setProperty(String key, String value) {
		prefs.put(key, value);
	}

	public double getDouble(String key, Double defaultValue) {
		return Double.parseDouble(getProperty(key, defaultValue.toString()));
	}

	public float getFloat(String key, Float defaultValue) {
		return Float.parseFloat(getProperty(key, defaultValue.toString()));
	}

	public double getMinEigenvalue() {
		return getDouble(Preferences.EIGENVALUE_THRESHOLD,
				Double.parseDouble(Preferences.EIGENVALUE_THRESHOLD_DEFAULT));
	}

	public int getMaxFactors() {
		return getInteger(Preferences.MAX_PRINCIPAL_FACTORS,
				Integer.parseInt(Preferences.MAX_PRINCIPAL_FACTORS_DEFAULT));
	}

	private int getInteger(String key, Integer defaultValue) {
		return Integer.parseInt(getProperty(key, defaultValue.toString()));
	}

	public boolean isSystemLookAndFeel() {
		return "true".equals(getProperty(SYSTEM_LOOK_AND_FEEL,
				SYSTEM_LOOK_AND_FEEL_DEFAULT));
	}

	public void setSystemLookAndFeel(boolean value) {
		setProperty(SYSTEM_LOOK_AND_FEEL, (value ? "true" : "false"));
	}

}
