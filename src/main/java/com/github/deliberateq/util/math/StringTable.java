package com.github.deliberateq.util.math;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.github.deliberateq.util.web.html.Html;
import com.github.deliberateq.util.xml.TaggedOutputStream;

public final class StringTable extends ArrayList<List<String>> implements Html {

    private static final long serialVersionUID = 4581287593900054692L;

    @Override
    public String toHtml() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TaggedOutputStream t = new TaggedOutputStream(bytes, true);
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
        return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
    }

    public void addRow() {
        this.add(new ArrayList<String>());
    }

    public void addEntry(String s) {
        get(size() - 1).add(s);
    }
}
