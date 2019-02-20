package com.idanalytics.datacleaner;

public class StrippedField {
    private String remainder;
    private String matchedPart;

    StrippedField(String remainder, String matchedPart) {
        this.remainder = remainder;
        this.matchedPart = matchedPart;
    }

    public String getRemainder() {
        return remainder;
    }

    public String getMatchedPart() {
        return matchedPart;
    }
}