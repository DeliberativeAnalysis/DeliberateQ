package au.edu.anu.delibdem.qsort;

import java.util.Set;

public class DataSelection {

	private String stage;
	private final Set<String> filter;

	public Set<String> getFilter() {
		return filter;
	}

	public DataSelection(Set<String> filter, String stage) {
		this.filter = filter;
		this.stage = stage;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	@Override
	public String toString() {
		return ("all".equalsIgnoreCase(stage) ? "All Stages" : stage);
	}
}
