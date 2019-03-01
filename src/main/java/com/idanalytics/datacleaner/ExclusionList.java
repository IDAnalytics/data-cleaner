package com.idanalytics.datacleaner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ExclusionList maintains patterns of name exclusions
 */
public class ExclusionList {

    private static final List<String> exclusionList = Collections.unmodifiableList(Arrays.asList(
            "ESTATE",
            "CO",
            "COMP",
            "DBA",
            "CHEMICAL",
            "ASSOC",
            "ASSOCIATE",
            "INC",
            "INCORPORATED",
            "TEMPLE",
            "CENTER",
            "SCHOOL",
            "ATT",
            "ATTN",
            "SYSTEMS",
            "SHOP",
            "OR",
            "AND",
            "CAPITAL",
            "CORP",
            "CORPORATION",
            "AUTO",
            "SALES",
            "ADVISOR",
            "COLLEGE",
            "APPLICATION",
            "FRAUD",
            "TESTCASE",
            "SAMPLE",
            "TEST",
            "CARE OF",
            "CHURCH",
            "STORE",
            "HOSPITAL"
    ));

    private List<Pattern> exclusions;

    /**
     * Constructs an ExclusionList with default provided exclusion list file
     * @throws IOException
     */
    public ExclusionList() {
        exclusions = createExclusionList();
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

    private static List<Pattern> createExclusionList() {
        return exclusionList.stream()
                .map(ExclusionList::compilePattern)
                .collect(Collectors.toList());
    }

    private static Pattern compilePattern(String pattern) {
        return Pattern.compile("\\b" + pattern.trim().toUpperCase() + "\\b");
    }
}
