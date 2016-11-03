package moten.david.util.permutation;

import java.io.PrintStream;

public class Possibility {
    public static void doPossibility(int n, int range, final Processor p) {
        if (n < 1) {
            return;
        }
        if (n == 1) {
            int[] values = new int[1];
            for (int i = 1; i <= range; i++) {
                values[0] = i;
                p.processValues(values);
            }
        } else {
            final int[] vals = new int[n];
            for (int i = 1; i <= range; i++) {
                vals[0] = i;
                doPossibility(n - 1, range, new Processor() {
                    public void processValues(int[] values) {
                        for (int i = 0; i < values.length; i++) {
                            vals[(i + 1)] = values[i];
                        }
                        p.processValues(vals);
                    }
                });
            }
        }
    }

    public static void main(String[] args) {
        doPossibility(9, 3, new Processor() {
            public void processValues(int[] values) {
                for (int i = 0; i < values.length; i++) {
                    System.out.print(values[i] + "\t");
                }
                System.out.println();
            }
        });
    }
}
