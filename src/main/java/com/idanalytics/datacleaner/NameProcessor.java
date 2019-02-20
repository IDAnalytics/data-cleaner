package com.idanalytics.datacleaner;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.*;

/**
 * NameProcessor is responsible for performing name processing during the
 * PreProcess step. This is intended to be called before MelissaData.
 */
final class NameProcessor {

    private static final int maxNameLength = 39;
    private static final int maxLastnameLength = 23;
    private static final int maxMiddlenameLength = maxLastnameLength;
    private ExclusionList exclusionList;

    NameProcessor(ExclusionList exclusionList) {
        this.exclusionList = exclusionList;
    }

    /**
     * Process cleans given name elements and expects the following keys to be
     * present in the data map:
     * <p>
     * IN_PREFIX 
     * IN_SUFFIX 
     * IN_FIRSTNAME 
     * IN_MIDDLENAME 
     * IN_LASTNAME 
     * IN_FULLNAME
     *
     * @param data
     * @return
     */
    Name preProcess(Map<String, String> data) throws Exception {

        String prefix = trimToEmpty(data.getOrDefault(Constants.IN_PREFIX, Constants.EMPTY)).toUpperCase();
        String first = Romanize
                .convert(trimToEmpty(data.getOrDefault(Constants.IN_FIRSTNAME, Constants.EMPTY)).toUpperCase());
        String middle = Romanize
                .convert(trimToEmpty(data.getOrDefault(Constants.IN_MIDDLENAME, Constants.EMPTY)).toUpperCase());
        String last = Romanize
                .convert(trimToEmpty(data.getOrDefault(Constants.IN_LASTNAME, Constants.EMPTY)).toUpperCase());
        String suffix = trimToEmpty(data.getOrDefault(Constants.IN_SUFFIX, Constants.EMPTY)).toUpperCase();

        // The assumption is empty first and last constitutes and invalid name. Middle
        // is not checked as it might be possible there is no middle name for a given individual.
        if (isEmpty(first) && isEmpty(last)) {
            throw new Exception("first and last cannot be empty.");
        }

        first = cleanName(first);
        last = cleanName(last);
        middle = cleanName(middle);

        String rawName = trim(join(new String[] { first, middle, last }, Constants.SPACE));

        if (matchesExclusion(rawName)) {
            String name = cleanFullName(join(new String[] { first, last }, Constants.PIPE));

            prefix = cleanName(prefix);
            suffix = cleanName(suffix);
            return new Name(true, name, prefix, first, middle, last, suffix);
        }

        // if prefix is empty prefix will be populated by stripping first name followed
        // by last in priority.
        if (isEmpty(prefix)) {
            StrippedField sf = stripName(Constants.PREFIX_PATTERN, first);
            if (sf != null) {
                first = sf.getRemainder();
                prefix = sf.getMatchedPart();
            }

            if (isEmpty(prefix)) {
                // if prefix is still empty, use lastname
                sf = stripName(Constants.PREFIX_PATTERN, last);
                if (sf != null) {
                    last = sf.getRemainder();
                    prefix = sf.getMatchedPart();
                }
            }
        }
        // next is suffix with LAST as priority, then first
        if (isEmpty(suffix)) {
            StrippedField sf = stripName(Constants.SUFFIX_PATTERN, last);
            if (sf != null) {
                last = sf.getRemainder();
                suffix = sf.getMatchedPart();
            }
            if (isEmpty(suffix)) {
                sf = stripName(Constants.SUFFIX_PATTERN, first);
                if (sf != null) {
                    first = sf.getRemainder();
                    suffix = sf.getMatchedPart();
                }
            }
        }

        String name = join(new String[] { first, middle, last }, Constants.SPACE).replaceAll("\\s+", Constants.SPACE);
        return new Name(true, name, prefix.toUpperCase(), first, middle, last, suffix.toUpperCase());
    }

    void postProcess(Name name) {

        String nameStr = cleanFullName(Utils.postProcessOutput(name.getName()));
        String first = Utils.appendDotToSingleCharName(Utils.postProcessOutput(name.getFirst()));
        String last = Utils.postProcessOutput(name.getLast());
        String middle = Utils.appendDotToSingleCharName(Utils.postProcessOutput(name.getMiddle()));
        String prefix = Utils.postProcessOutput(name.getPrefix());
        String suffix = Utils.postProcessOutput(name.getSuffix());

        if (nameStr.length() > maxNameLength) {
            nameStr = nameStr.substring(0, maxNameLength).trim();
        }

        if (last.length() > maxLastnameLength) {
            last = last.substring(0, maxLastnameLength).trim();
        }

        if (middle.length() > maxMiddlenameLength) {
            middle = middle.substring(0, maxMiddlenameLength).trim();
        }

        name.setName(nameStr);
        name.setFirst(first);
        name.setLast(last);
        name.setMiddle(middle);
        name.setPrefix(prefix);
        name.setSuffix(suffix);
    }

    private StrippedField stripName(Pattern pattern, String name) {
        Matcher m = pattern.matcher(name);
        if (m.find()) {
            String matchedPart = m.group().trim();
            String remainder = m.replaceAll(Constants.SPACE).trim();
            return new StrippedField(remainder, matchedPart);
        } else {
            return null;
        }
    }

    private String cleanName(String name) {
        if (name == null)
            return Constants.EMPTY;
        name = name.trim().toUpperCase();
        name = name.replaceAll("\\s*&\\s*", " AND ");
        name = Constants.NAME_PATTERN.matcher(name).replaceAll(Constants.EMPTY);
        name = name.replaceAll("\\s+", Constants.SPACE);
        Matcher m = Constants.NON_NAME_WORDS.matcher(name);
        if (m.find()) {
            name = m.replaceFirst(Constants.SPACE).trim();
        }
        return name;
    }

    private String cleanFullName(String name) {
        if (isNotEmpty(name)) {
            return Constants.FULL_NAME_PATTERN.matcher(name.toUpperCase()).replaceAll(Constants.EMPTY).trim();
        }
        return name.trim();
    }

    private boolean matchesExclusion(String rawName) {
        for (Pattern p : exclusionList.getExclusions()) {
            Matcher m = p.matcher(rawName);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }

}
