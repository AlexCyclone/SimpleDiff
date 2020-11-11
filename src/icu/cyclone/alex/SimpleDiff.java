package icu.cyclone.alex;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Aleksey Babanin
 * @since 2020/11/11
 */
public class SimpleDiff<T> {
    private final List<T> a;
    private final List<T> b;
    private final Comparator<T> comp;
    private int[][] fullLcsMatrix;
    private LinkedList<DiffObject<T>> diffs;
    final boolean secondWay;

    public enum Status {
        SAME,
        ADDED,
        REMOVED
    }

    public static class DiffObject<T> {
        private final T object;
        private final Status status;

        public DiffObject(T object, Status status) {
            this.object = object;
            this.status = status;
        }

        public T getObject() {
            return object;
        }

        public Status getStatus() {
            return status;
        }

        @Override
        public String toString() {
            return status + " : " + object;
        }
    }

    public SimpleDiff(List<T> a, List<T> b, Comparator<T> comparator) {
        this(a, b, comparator, false);
    }

    public SimpleDiff(List<T> a, List<T> b, Comparator<T> comparator, boolean secondWay) {
        this.a = a;
        this.b = b;
        comp = comparator;
        this.secondWay = secondWay;
        initFullLcsMatrix();
    }

    private void initFullLcsMatrix() {
        if (fullLcsMatrix == null) {
            int n = a.size() + 1;
            int m = b.size() + 1;
            fullLcsMatrix = new int[n][m];

            for (int i = 1; i < n; i++) {
                for (int j = 1; j < m; j++) {
                    if (comp.compare(a.get(i - 1), b.get(j - 1)) == 0) {
                        fullLcsMatrix[i][j] = fullLcsMatrix[i - 1][j - 1] + 1;
                    } else {
                        fullLcsMatrix[i][j] = Integer.max(fullLcsMatrix[i - 1][j], fullLcsMatrix[i][j - 1]);
                    }
                }
            }
        }
    }


    public List<DiffObject<T>> getDiffs() {
        if (diffs == null) {
            int i = fullLcsMatrix.length - 1;
            int j = fullLcsMatrix[i].length - 1;

            diffs = new LinkedList<>();
            while (i > 0 && j > 0) {
                if (comp.compare(a.get(i - 1), b.get(j - 1)) == 0) {
                    diffs.addFirst(new DiffObject<>(a.get(--i), Status.SAME));
                    j -= 1;
                } else {
                    if (secondWay) {
                        if (fullLcsMatrix[i][j - 1] == fullLcsMatrix[i][j]) {
                            diffs.addFirst(new DiffObject<>(b.get(--j), Status.ADDED));
                        } else {
                            diffs.addFirst(new DiffObject<>(a.get(--i), Status.REMOVED));
                        }
                    } else {
                        if (fullLcsMatrix[i - 1][j] == fullLcsMatrix[i][j]) {
                            diffs.addFirst(new DiffObject<>(a.get(--i), Status.REMOVED));
                        } else {
                            diffs.addFirst(new DiffObject<>(b.get(--j), Status.ADDED));
                        }
                    }
                }
            }
            while (i > 0) {
                diffs.addFirst(new DiffObject<>(a.get(--i), Status.REMOVED));
            }
            while (j > 0) {
                diffs.addFirst(new DiffObject<>(b.get(--j), Status.ADDED));
            }
        }
        return diffs;
    }

    // unchanged object count
    public int getLcsLength() {
        return fullLcsMatrix[a.size()][b.size()];
    }

    // unchanged objects
    public List<T> getLcs() {
        return getDiffs().stream()
                .filter(d -> d.getStatus() == Status.SAME)
                .map(DiffObject::getObject)
                .collect(Collectors.toList());
    }

    public void printCompare(PrintStream printStream) {
        List<DiffObject<T>> diffs = getDiffs();
        diffs.forEach(printStream::println);
    }

    public void printLcsInfo(PrintStream printStream) {
        printStream.printf("LCS Length: %d%n", getLcsLength());
        printStream.println("LCS:");
        getLcs().forEach(printStream::println);
    }
}
