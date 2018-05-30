package com.github.deliberateq.qsort;

import java.io.IOException;

import org.junit.Test;

import com.github.deliberateq.qsort.Data;

public class DataTest {

	@Test
	public void testLoadData() throws IOException {
		Data data = new Data(
				AnalysisTest.class
						.getResourceAsStream("/studies2/Bloomfield Track.txt"));
	}
}
