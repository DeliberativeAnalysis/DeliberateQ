package com.github.deliberateq.qsort;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.github.deliberateq.util.math.CorrelationCoefficient;
import com.github.deliberateq.util.math.Matrix;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class Data implements Serializable {

	private static Logger log = Logger.getLogger(Data.class.getName());

	private static final long serialVersionUID = -8216642174736641063L;
	private static final String TAB = "\t";

	private final SortedMap<Integer, String> statements = new TreeMap<>();

	private final Map<String, Participant> participants = new HashMap<>();
	private final Set<String> participantFilter = new TreeSet<>();
	private final Set<Integer> statementFilter = new TreeSet<>();
	private final Set<String> stageFilter = new TreeSet<>();

	private List<QSort> qSorts;
	private String title = "Untitled";

	public Data(InputStream is) throws IOException {
		log.info("loading");
		load(is);
	}

	public Set<String> getParticipantIds() {
		TreeSet<String> set = new TreeSet<String>();
		for (QSort q : qSorts) {
			set.add(q.getParticipant().getId());
		}
		return set;
	}

	public Set<String> getParticipantIds(String participantType) {
		TreeSet<String> set = new TreeSet<String>();
		for (QSort q : qSorts) {
			if (q.getParticipant().getTypes().contains(participantType))
				set.add(q.getParticipant().getId());
		}
		return set;
	}

	public Set<String> getStageTypes() {
		Set<String> set = new TreeSet<String>();
		for (QSort q : qSorts) {
			set.add(q.getStage());
		}
		return set;
	}

	public Set<String> getParticipantTypes() {
		Set<String> set = new TreeSet<String>();
		for (QSort q : qSorts) {
			set.addAll(participants.get(q.getParticipant().getId()).getTypes());
		}
		return set;
	}

	public void load(InputStream is) throws IOException {
		log.info("loading data..");
		InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		LineCountingReader br = new LineCountingReader(new BufferedReader(isr));

		String line;
		this.qSorts = new ArrayList<QSort>();

		Marker marker = Marker.STARTED;
		int numParticipants = 0;
		int numParticipantsRead = 0;
		int numQStatements = 0;
		int numPStatements = 0;
		int numVariables = 0;
		while ((line = nextLine(br)) != null) {
			log.info(line);

			if (marker.equals(Marker.STARTED) && isTitle(line)) {
				// is title
				title = getValue(line, 1);
				marker = Marker.TITLE_READ;
			} else if (marker.equals(Marker.TITLE_READ) && isParticipants(line)) {
				// is num participants
				numParticipants = Integer.parseInt(getValue(line, 1));
				marker = Marker.NUM_PARTICIPANTS_READ;
			} else if (marker.equals(Marker.NUM_PARTICIPANTS_READ)
					&& isVariables(line)) {
				// is num variables
				numVariables = Integer.parseInt(getValue(line, 1));
				marker = Marker.NUM_VARIABLES_READ;
			} else if (marker.equals(Marker.NUM_VARIABLES_READ)
					&& !isCommand(line)) {
				// is participant data
				numParticipantsRead++;
				String[] items = line.split(TAB);
				Participant participant = new Participant(items[0]);
				participants.put(participant.getId(), participant);
				for (int i = 0; i < numVariables; i++) {
					String value = items[i + 1].trim();
					participant.getTypes().add(value);
				}
			} else if (marker.equals(Marker.NUM_VARIABLES_READ)
					&& isQStatements(line)) {
				// is num q statements
				if (numParticipantsRead != numParticipants)
					throw new RuntimeException(
							"Number of participants doesn't match the declared value on the :Participants line (declared="
									+ numParticipants
									+ ",actual="
									+ numParticipantsRead);
				numQStatements = Integer.parseInt(getValue(line, 1));
				marker = Marker.NUM_Q_STATEMENTS_READ;
			} else if (marker.equals(Marker.NUM_Q_STATEMENTS_READ)
					&& isPStatements(line)) {
				// is num p statements
				numPStatements = Integer.parseInt(getValue(line, 1));
				marker = Marker.NUM_P_STATEMENTS_READ;
			} else if (marker.equals(Marker.NUM_P_STATEMENTS_READ)
					&& !isCommand(line)) {
				// is qsort data
				processQSortLine(line, numQStatements, numPStatements);
			} else if (marker.equals(Marker.NUM_P_STATEMENTS_READ)
					&& isStatements(line)) {
				marker = Marker.STATEMENTS_READ;
			} else if (marker.equals(Marker.STATEMENTS_READ)
					&& !isCommand(line)) {
				// is statement data
				String[] items = line.split(TAB);
				statements.put(Integer.parseInt(items[0]), items[1].trim());
			} else
				throw new RuntimeException(
						"Line "
								+ br.getLinesRead()
								+ " was unexpected. Please compare your file to the example DeliberateQ input file. Perhaps the lines are not in the right order?\n"
								+ line);
		}
		br.close();
		isr.close();
		is.close();
		participantFilter.addAll(getParticipantIds());
		statementFilter.addAll(getStatements().keySet());
		stageFilter.addAll(getStageTypes());
		log.info("loaded");
	}

	private enum Marker {
		STARTED, TITLE_READ, NUM_PARTICIPANTS_READ, NUM_VARIABLES_READ, PARTICIPANT_DATA_READ, NUM_Q_STATEMENTS_READ, NUM_P_STATEMENTS_READ, DATA_READ, STATEMENTS_READ, STATEMENTS_DATA_READ;
	}

	private boolean isCommand(String line) {
		return line.startsWith(":");
	}

	private boolean isTitle(String line) {
		return line.startsWith(":Title");
	}

	private boolean isStatements(String line) {
		return line.startsWith(":Statements");
	}

	private boolean isPStatements(String line) {
		return line.startsWith(":P Statements");
	}

	private boolean isQStatements(String line) {
		return line.startsWith(":Q Statements");
	}

	private boolean isVariables(String line) {
		return line.startsWith(":Variables");
	}

	private boolean isParticipants(String line) {
		return line.startsWith(":Participants");
	}

	private void processQSortLine(String line, int numQStatements,
			int numPStatements) {

		String[] items = line.split(TAB);
		if (items.length < numQStatements + numPStatements + 2)
			throw new RuntimeException("not enough columns: " + items.length
					+ "\n" + line);
		QSort q = new QSort();
		Participant participant = participants.get(items[0].trim());
		if (participant == null)
			throw new RuntimeException(
					"Participant "
							+ items[0]
							+ " not found on qsort line. Have you declared it in the participants section?");
		q.setParticipant(participant);
		q.setStage(items[1].trim());
		for (int j = 2; j < 2 + numQStatements; j++) {
			q.getQResults().add(new QResult(j - 1, getDouble(items[j])));
		}
		for (int j = 2 + numQStatements; j < 2 + numQStatements
				+ numPStatements; j++)
			q.getRankings().add(getDouble(items[j]));
		qSorts.add(q);
	}

	/**
	 * Returns the ith value (O based) from the line based on a tab delimiter
	 * 
	 * @param line
	 * @param i
	 * @return
	 */
	private String getValue(String line, int i) {
		String[] items = line.split(TAB);
		if (i >= items.length)
			throw new RuntimeException("Could not read the " + (i + 1)
					+ "th value from the line=" + line
					+ ". Perhaps there is a value missing on this line?");
		return items[i].trim();
	}

	private static class LineCountingReader {
		private final BufferedReader br;

		public int getLinesRead() {
			return linesRead;
		}

		private int linesRead = 0;

		public LineCountingReader(BufferedReader br) {
			this.br = br;
		}

		public String readLine() throws IOException {
			String line = br.readLine();
			if (line != null)
				linesRead++;
			return line;
		}

		public void close() throws IOException {
			br.close();
		}

	}

	private String nextLine(LineCountingReader br) throws IOException {
		String line = br.readLine();
		while (line != null
				&& (line.startsWith("#") || line.trim().length() == 0))
			line = br.readLine();
		return line;
	}

	private Double getDouble(String s) {
		if (s == null || s.trim().equals(""))
			return null;
		else
			return Double.parseDouble(s);
	}

	private Integer getInt(String s) {
		if (s == null || s.trim().equals(""))
			return null;
		else
			return Integer.parseInt(s);
	}

	private int getTerminatingInteger(String s) {
		StringBuffer num = new StringBuffer();
		int i = s.length() - 1;
		while (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
			num.insert(0, s.charAt(i));
			i--;
		}
		Integer result = Integer.parseInt(num.toString());
		return result;
	}

	public Map<Integer, Integer> getOrdered(String columns[], String[] items,
			int starti, String label) {
		Map<Integer, Integer> ordered = new LinkedHashMap<Integer, Integer>();
		int i = starti;
		while (columns[i].startsWith(label)) {
			int n = getTerminatingInteger(columns[i]);
			ordered.put(n, getInt(items[i++]));
		}
		return ordered;
	}

	public List<QSort> getQSorts() {
		List<QSort> list = new ArrayList<QSort>(qSorts);
		for (int i = list.size() - 1; i >= 0; i--)
			if (!participantFilter.contains(list.get(i).getParticipant().getId())) {
				list.remove(i);
			}
		return list;
	}


	public DataComponents getDataComponents(String stage,
			Set<String> participantFilter, CorrelationCoefficient cc) {
		List<QSort> subList = restrictList(stage, participantFilter);
		return buildMatrix(subList, participantFilter, cc);

	}

	public List<QSort> restrictList(String stage, Set<String> participantFilter) {
		// read data
		List<QSort> list = getQSorts();

		List<QSort> subList = new ArrayList<QSort>();
		for (QSort q : list) {
			if ((stage.equalsIgnoreCase("all") || q.getStage().trim()
					.equalsIgnoreCase(stage))
					&& (participantFilter == null || participantFilter
							.contains(q.getParticipant().getId()))) {

				boolean alreadyGotIt = false;
				for (QSort q2 : subList) {
					if (q2.getParticipant().getId()
							.equals(q.getParticipant().getId())
							&& q2.getStage().equals(q.getStage())) {
						alreadyGotIt = true;
						break;
					}
				}
				if (!alreadyGotIt)
					subList.add(q);
			}
		}
		return subList;
	}

	public static class DataComponents {
		public List<QSort> list;
		public Matrix qSorts;
		public Matrix rankings;
		public Matrix correlations;
		public List<String> participants1;
		public List<String> participants2;
	}

	public DataComponents buildMatrix(List<QSort> list, Set<String> participantFilter, CorrelationCoefficient cc) {
		if (list == null)
			return null;
		list = Lists.newArrayList(list);

		Set<String> stages = Sets.newHashSet();
		for (QSort q : list) {
			stages.add(q.getStage());
		}

		boolean singleStage = isSingleStage(list);

		// remove from the list all QSort objects that are missing a qResult or
		// a ranking value
		Set<QSort> removeThese = new HashSet<QSort>();
		for (QSort q : list) {
			if (q.getQResults().size() == 0) {
				removeThese.add(q);
			} else {
				for (QResult v : q.getQResults()) {
					if (v == null)
						removeThese.add(q);
				}
			}
			if (q.getRankings().size() == 0)
				removeThese.add(q);
			else {
				for (Double v : q.getRankings()) {
					if (v == null)
						removeThese.add(q);
				}
			}
		}

		list.removeAll(removeThese);

		if (list.size() == 0) {
			return null;
		}
		// make the matrix of the qResults
        Matrix qSorts = new Matrix(list.size(), list.get(0).getQResults().size());
        for (int i = 0; i < list.size(); i++) {
            QSort q = list.get(i);
            qSorts.setRowLabel(i + 1, getParticipantLabel(singleStage, q));
            int j = 0;
            for (QResult r : q.getQResults()) {
                if (statementFilter.contains(r.statementNo())) {
                    qSorts.setValue(i + 1, j + 1, r.value());
                    qSorts.setColumnLabel(j + 1, "Q" + (r.statementNo()));
                    j++;
                }
            }
        }

		// make the matrix of rankings
		Matrix rankings = new Matrix(list.size(), list.get(0).getRankings()
				.size());
		for (int i = 0; i < list.size(); i++) {
			QSort q = list.get(i);
			rankings.setRowLabel(i + 1, getParticipantLabel(singleStage, q));
			for (int j = 0; j < q.getRankings().size(); j++) {
				rankings.setValue(i + 1, j + 1, q.getRankings().get(j));
				rankings.setColumnLabel(j + 1, "R" + (j + 1));
			}
		}

		// perform correlations
		Matrix qSortsCorrelated = qSorts.transpose()
				.getCorrelationMatrix(cc);
		Matrix rankingsCorrelated = rankings.transpose()
				.getCorrelationMatrix(cc);

		// compare rankings and qSorts
		List<String> participants1 = new ArrayList<String>();
		List<String> participants2 = new ArrayList<String>();
		Matrix m = new Matrix(1, 2);
		for (int i = 1; i <= qSortsCorrelated.rowCount(); i++) {
			for (int j = i + 1; j <= qSortsCorrelated.columnCount(); j++) {
				boolean includeIt = participantFilter == null || participantFilter.size() == 0
						|| participantFilter.contains(qSortsCorrelated.getRowLabel(i))
						|| participantFilter.contains(qSortsCorrelated.getRowLabel(j));
				if (includeIt) {
					if (i != 1 || j != 2)
						m = m.addRow();
					participants1.add(qSortsCorrelated.getRowLabel(i));
					participants2.add(qSortsCorrelated.getRowLabel(j));
					m.setValue(m.rowCount(), 1, qSortsCorrelated.getValue(i, j));
					m.setValue(m.rowCount(), 2,
							rankingsCorrelated.getValue(i, j));
					m.setRowLabel(
							m.rowCount(),
							combineLabels(qSortsCorrelated.getRowLabel(i),
									qSortsCorrelated.getRowLabel(j)));
				}
			}
		}
		m.setColumnLabel(1, "qSort");
		m.setColumnLabel(2, "ranking");

		DataComponents dataComponents = new DataComponents();
		dataComponents.list = list;
		dataComponents.qSorts = qSorts;
		dataComponents.rankings = rankings;
		dataComponents.participants1 = participants1;
		dataComponents.participants2 = participants2;
		dataComponents.correlations = m;
		return dataComponents;
	}

	private String getParticipantLabel(boolean singleStage, QSort q) {
		String rowLabel;
		if (singleStage)
			rowLabel = q.getParticipant().getId();
		else
			rowLabel = q.getStage() + "-" + q.getParticipant().getId();
		return rowLabel;
	}

	private static String combineLabels(String a, String b) {
		return a + ":" + b;
	}

	public static String[] separateLabels(String s) {
		return s.split(":");
	}

	public void writeMatrix(Matrix m, OutputStream os) throws IOException {
		os.write(m.getDelimited(TAB, true).getBytes(StandardCharsets.UTF_8));
		os.flush();
	}

	public Matrix getRawData(DataSelection dataSelection,
			Set<Integer> exclusions, int dataSet) {
		return getRawData(dataSelection.getStage(), dataSelection.getParticipantFilter(),
				dataSet);
	}

	private Matrix getRawData(String stage, Set<String> filter, int dataSet) {
		List<QSort> subList = restrictList(stage, filter);

		if (subList.size() == 0) {
			return null;
		}

		int numRows = subList.get(0).getQResults().size();
		if (dataSet == 2)
			numRows = subList.get(0).getRankings().size();
		if (numRows == 0)
			return null;
		Matrix m = new Matrix(numRows, subList.size());
		boolean singleStage = isSingleStage(subList);

		int col = 1;
		for (QSort q : subList) {
			int row = 1;
			List<Double> items = q.getQResults().stream().map(QResult::value).collect(Collectors.toList());
			if (dataSet == 2)
				items = q.getRankings();
			for (double value : items) {
				m.setValue(row, col, value);
				row++;
			}
			m.setColumnLabel(col, getParticipantLabel(singleStage, q));
			col++;
		}
		if (dataSet == 1)
			m.setRowLabelPattern("Stmt<index>");
		else
			m.setRowLabelPattern("Pref<index>");

		// m.writeToFile(new File("/matrix.txt"), false);
		m = m.removeColumnsWithNoStandardDeviation();
		return m;
	}

	private static boolean isSingleStage(List<QSort> list) {
		Set<String> set = Sets.newHashSet();
		for (QSort q : list)
			set.add(q.getStage());
		return set.size() == 1;
	}

	public Set<String> getParticipantFilter() {
		return participantFilter;
	}

	public SortedMap<Integer, String> getStatements() {
		return statements;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<String> getStageFilter() {
		return stageFilter;
	}

    public Set<Integer> getStatementFilter() {
        return statementFilter;
    }

}