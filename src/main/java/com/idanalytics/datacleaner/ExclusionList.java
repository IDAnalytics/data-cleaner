package com.idanalytics.datacleaner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ExclusionList maintains patterns of name exclusions
 */
public class ExclusionList {

    private static final String EXCLUSION_FILENAME = "name.exclusion.file";
    private List<Pattern> exclusions;

    /**
     * Constructs an ExclusionList with default provided exclusion list file
     * @throws IOException
     */
    public ExclusionList() throws Exception {
        URL excusionFileURL = this.getClass(). getResource(EXCLUSION_FILENAME);
        exclusions = readExclusionList(Paths.get(excusionFileURL.toURI()));
    }

    /**
     * Constructs an ExclusionList with exclusion patterns provided. This constructor
     * expects the following pattern format:
     * SINGLEWORD
     * @param exclusions
     */
    public ExclusionList(List<Pattern> exclusions) {
        this.exclusions = new ArrayList<>(exclusions);
    }

    public List<Pattern> getExclusions() {
        return Collections.unmodifiableList(exclusions);
    }

    private List<Pattern> readExclusionList(Path exclusionFile) throws IOException {
        List<Pattern> exclusions = new ArrayList<>();
        try (Stream<String> stream = Files.lines(exclusionFile)) {
            List<Pattern> patterns = stream.map(pattern ->
                    Pattern.compile("\\b" + pattern.trim().toUpperCase() + "\\b")).collect(Collectors.toList()
            );
            exclusions.addAll(patterns);
        }
        return exclusions;
    }
}
