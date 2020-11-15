package nouni.elbachir.mit.dp;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.*;

public class TextJustification {

    public static void main(String[] args) throws Exception {
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(TextJustification.class.getResourceAsStream("data.in.txt"))
                )
        );) {
            final String[] words = in.lines()
                    .flatMap(line -> stream(line.split(" ")))
                    .collect(Collectors.toList())
                    .toArray(new String[]{});
            final int nbrWords = words.length;
            final int pageWidth = 70;
            final Map<Integer, Integer> parentPointer = new HashMap<>();//<i,argmin(j)>
            final Map<Integer, Badness> justifyDPMemo = new HashMap<>();
            final Map<String, Badness> badnessMemo = new HashMap<>();
            final Map<String, Integer> totalWithMemo = new HashMap<>();
            System.out.println(Arrays.toString(words));
            System.out.println(nbrWords + " words");
            System.out.println(pageWidth + " per line");
            System.out.println((nbrWords * nbrWords) + " as time complexity");

            Badness badnessMin = justify(words,
                    nbrWords,
                    pageWidth,
                    parentPointer,
                    justifyDPMemo,
                    badnessMemo,
                    totalWithMemo);
            System.out.println(badnessMin + " as min badness");
            System.out.println("Parent pointers" + parentPointer);

            final List<String> justifiedLines = buildLines(words, nbrWords, pageWidth, parentPointer);
            try (BufferedWriter out = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(new File("out/data.out.txt"), false)
                    )
            );) {
                for (String line : justifiedLines) {
                    out.append(line);
                    out.newLine();
                }
            }
        }
    }

    private static List<String> buildLines(String[] words,
                                           int nbrWords,
                                           int pageWidth,
                                           Map<Integer, Integer> parentPointer) {
        List<String> lines = new ArrayList<>();
        int key = 0;
        while (key < nbrWords) {
            Integer nextKey = parentPointer.get(key);
            lines.add(String.join(" ", copyOfRange(words, key, nextKey)));
            key = nextKey;
        }
        return lines;
    }

    private static Badness justify(String[] words,
                                   int nbrWords,
                                   int pageWidth,
                                   Map<Integer, Integer> parentPointer,
                                   Map<Integer, Badness> justifyDPMemo,
                                   Map<String, Badness> badnessMemo,
                                   Map<String, Integer> totalWithMemo) {
        return justifyDP(words, nbrWords, pageWidth, 0, parentPointer, justifyDPMemo, badnessMemo, totalWithMemo);
    }

    private static Badness justifyDP(String[] words,
                                     int nbrWords,
                                     int pageWidth,
                                     int k,
                                     Map<Integer, Integer> parentPointer,
                                     Map<Integer, Badness> memo,
                                     Map<String, Badness> badnessMemo,
                                     Map<String, Integer> totalWithMemo) {
        if (memo.containsKey(k))
            return memo.get(k);
        Map.Entry<Integer, Badness> res = IntStream.rangeClosed(k + 1, nbrWords)
                .mapToObj(j -> {
                    Badness bdness = badness(words, k, j, pageWidth, badnessMemo, totalWithMemo)
                            .plus(justifyDP(words, nbrWords, pageWidth, j, parentPointer, memo, badnessMemo, totalWithMemo));
                    return Map.entry(j, bdness);
                })
                .min(Map.Entry.comparingByValue())
                .orElse(Map.entry(k, Badness.ZERO));
        parentPointer.put(k, res.getKey());
        memo.put(k, res.getValue());
        return res.getValue();
    }

    private static Badness badness(String[] words,
                                   int startIndex,
                                   int endIndex,
                                   int pageWidth,
                                   Map<String, Badness> memo,
                                   Map<String, Integer> totalWithMemo) {
        String key = startIndex + "_" + endIndex;
        if (memo.containsKey(key))
            return memo.get(key);
        int totalWidth = totalWidth(words, startIndex, endIndex, totalWithMemo);//one space between each word
        if (endIndex > startIndex)
            totalWidth += endIndex - startIndex - 1;
        Badness res;
        if (totalWidth > pageWidth) {
            res = Badness.INFINITY;
        } else {
            final int diff = (int) Math.pow(pageWidth - totalWidth, 3);
            res = new Badness(diff);
        }
        memo.put(key, res);
        return res;
    }

    //[startIndex:endIndex] python slice
    private static int totalWidth(String[] words, int startIndex, int endIndex, Map<String, Integer> memo) {
        String key = startIndex + "_" + endIndex;
        if (memo.containsKey(key))
            return memo.get(key);
        int sum = 0;
        for (int i = startIndex; i < endIndex; i++)
            sum += words[i].length();
        memo.put(key, sum);
        return sum;
    }

    static class Badness implements Comparable<Badness> {
        public boolean infinity;
        public int value;

        public static Badness INFINITY = new Badness(true, -1);
        public static Badness ZERO = new Badness(0);

        public Badness(boolean isInfinity, int v) {
            this.infinity = isInfinity;
            this.value = v;
        }

        public Badness(int v) {
            this(false, v);
        }

        @Override
        public String toString() {
            return "Badness{" +
                    "infinity=" + infinity +
                    ", value=" + value +
                    '}';
        }

        public Badness plus(Badness dpj) {
            if (infinity || dpj.infinity) return INFINITY;
            else {
                return new Badness(value + dpj.value);
            }
        }

        @Override
        public int compareTo(Badness o) {
            return infinity ? 1 : (o.infinity ? -1 : (value - o.value));
        }
    }
}
