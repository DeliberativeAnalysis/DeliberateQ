package com.github.deliberateq.qsort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class QSort implements Serializable {
	private static final long serialVersionUID = -7796050822543154325L;
	private String stage;
	private List<Double> rankings = new ArrayList<Double>();
	private List<Double> qResults = new ArrayList<Double>();
	private Participant participant;

	@Override
	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("stage=" + stage);
		s.append(",participantId=" + participant.getId());
		s.append(",rankings=" + rankings);
		s.append(",qResults=" + qResults);
		return s.toString();
	}

	public Participant getParticipant() {
		return participant;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public List<Double> getRankings() {
		return rankings;
	}

	public void setRankings(List<Double> rankings) {
		this.rankings = rankings;
	}

	public List<Double> getQResults() {
		return qResults;
	}

	public void setQResults(List<Double> results) {
		qResults = results;
	}

	public QSort copy() {
		QSort q = new QSort();
		q.setParticipant(participant);
		q.setQResults(new ArrayList<Double>(this.qResults));
		q.setRankings(new ArrayList<Double>(rankings));
		q.setStage(stage);
		return q;
	}

	public void setParticipant(Participant participant) {
		this.participant = participant;
	}
}
