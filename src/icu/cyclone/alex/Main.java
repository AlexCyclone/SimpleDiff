package icu.cyclone.alex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        String baseFilePath = ConsoleUtils.requestString("Base file:", (s)-> Files.isRegularFile(Path.of(s)));
        List<String> baseData = readLines(baseFilePath);

        String changedFilePath = ConsoleUtils.requestString("Changed file:", (s)-> Files.isRegularFile(Path.of(s)));
        List<String> changedData = readLines(changedFilePath);

        SimpleDiff<String> diff = new SimpleDiff<>(baseData, changedData, String::compareTo);

        diff.printCompare(System.out);
        diff.printLcsInfo(System.out);
    }

    private static List<String> readLines(String path) throws IOException {
        try (BufferedReader readerBase = new BufferedReader(new FileReader(path))) {
            return readerBase.lines().collect(Collectors.toList());
        }
    }

}
