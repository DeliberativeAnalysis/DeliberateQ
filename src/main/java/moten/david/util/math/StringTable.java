package moten.david.util.math;

import java.util.ArrayList;
import java.util.List;

import moten.david.util.StringOutputStream;
import moten.david.util.web.html.Html;
import moten.david.util.xml.TaggedOutputStream;

public class StringTable extends ArrayList<List<String>> implements Html {

	private static final long serialVersionUID = 4581287593900054692L;

	@Override
	public String toHtml() {
		StringOutputStream sos = new StringOutputStream();
		TaggedOutputStream t = new TaggedOutputStream(sos, true);
		t.startTag("table");
		t.addAttribute("class", "matrix");
		for (List<String> row : this) {
			t.startTag("tr");
			for (String entry : row) {
				t.startTag("td");
				t.addAttribute("class", "matrix");
				t.append(entry);
				t.closeTag();
			}
			t.closeTag();
		}
		t.closeTag();
		t.close();
		return sos.toString();
	}

	public void addRow() {
		this.add(new ArrayList<String>());
	}

	public void addEntry(String s) {
		get(size() - 1).add(s);
	}
}
