package moten.david.util;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {
    private StringBuffer s;

    public StringOutputStream() {
        this.s = new StringBuffer();
    }

    public void write(int b) throws IOException {
        this.s.append((char) b);
    }

    public String toString() {
        return this.s.toString();
    }
}
