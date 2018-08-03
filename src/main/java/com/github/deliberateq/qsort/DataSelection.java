package com.github.deliberateq.qsort;

import java.util.Set;

public class DataSelection {

	private String stage;
	private final Set<String> participantFilter;

	public Set<String> getParticipantFilter() {
		return participantFilter;
	}

	public DataSelection(Set<String> participantFilter, String stage) {
		this.participantFilter = participantFilter;
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
		return "all".equalsIgnoreCase(stage) ? "All Stages" : stage;
	}
}
