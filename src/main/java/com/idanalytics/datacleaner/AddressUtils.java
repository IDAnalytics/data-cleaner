package com.idanalytics.datacleaner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.isBlank;

final class AddressUtils {

    private static final Pattern MULTIPLE_WHITESPACES = Pattern.compile("[ ]{2,}");
    private static final Pattern SECONDARY_UNIT_DESIGNATORS = Pattern
            .compile("( APT )|( APARTMENT )|( STE )|( SUITE )|( UNIT )");
    private static final Pattern STREET_SUFFIXES = Pattern
            .compile("(?:\\s|^)(ROAD|STREET|AVENUE|COURT|DRIVE|CIRCLE|PARKWAY|LANE|BOULEVARD|HIGHWAY|PLACE)(?:\\s(OF)*|$)");
    private static final Map<String, String> STREET_SUFFIXES_ABBREVS = Collections.unmodifiableMap(new HashMap<String, String>() {
        {
            put("ROAD", "RD");
            put("STREET", "ST");
            put("AVENUE", "AVE");
            put("COURT", "CT");
            put("DRIVE", "DR");
            put("CIRCLE", "CIR");
            put("PARKWAY", "PKWY");
            put("LANE", "LN");
            put("BOULEVARD", "BLVD");
            put("HIGHWAY", "HWY");
            put("PLACE", "PL");
        }
    });

    static String cleanSecondaryComponent(String addr) {
        
        if (isBlank(addr) || !contains(addr, Constants.POUND)) {
            return addr;
        }

        StringBuilder sb = new StringBuilder(64);
        boolean begin = true;
        boolean last = false;
        for (int i = addr.length() - 1; i >= 0; i--) {
            char c = addr.charAt(i);
            if (c == '.') {
                sb.append(Constants.SPACE);
                continue;
            }
            if (c == Constants.POUND) {
                if (!begin) {
                    if (!last) {
                        sb.append(Constants.SPACE);
                        sb.append(c);
                        sb.append(Constants.SPACE);
                        last = true;
                    }
                }
            } else {
                if (begin) {
                    if (!Character.isWhitespace(c)) {
                        begin = false;
                        sb.append(c);
                    }
                } else {
                    sb.append(c);
                }
            }
        }
        addr = sb.reverse().toString().trim();
        addr = MULTIPLE_WHITESPACES.matcher(addr).replaceAll(Constants.SPACE);
        addr = SECONDARY_UNIT_DESIGNATORS.matcher(addr).replaceAll(Constants.SPACE);
        // there should not be more than one pounds any more
        addr = addr.replaceFirst(Constants.POUNDSTR, "APT");

        return addr;
    }

    static String abbreviateStreetSuffixes(String addr) {
        if (isBlank(addr)) {
            return addr;
        }

        Matcher m = STREET_SUFFIXES.matcher(addr);
        if (!m.find()) {
            return addr;
        }

        String streetSuffix = m.group(0);
        if (streetSuffix != null
                && STREET_SUFFIXES_ABBREVS.containsKey(streetSuffix
                .trim())) {
            boolean startSpace = Character.isWhitespace(streetSuffix
                    .charAt(0));
            boolean endSpace = Character.isWhitespace(streetSuffix
                    .charAt(streetSuffix.length() - 1));
            StringBuilder sb = new StringBuilder(64);
            if (startSpace) {
                sb.append(Constants.SPACE);
            }
            sb.append(STREET_SUFFIXES_ABBREVS.get(streetSuffix.trim()));
            if (endSpace) {
                sb.append(Constants.SPACE);
            }
            addr = m.replaceFirst(sb.toString());
        }

        return addr;
    }
}
