package au.edu.anu.delibdem.qsort;

import java.io.IOException;

import org.junit.Test;

public class DataTest {

	@Test
	public void testLoadData() throws IOException {
		Data data = new Data(
				AnalysisTest.class
						.getResourceAsStream("/studies2/Bloomfield Track.txt"));
	}
}
