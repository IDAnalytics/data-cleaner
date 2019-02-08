import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.Map;

class Name {
    private boolean valid;
    private String name;
    private String prefix;
    private String first;
    private String middle;
    private String last;
    private String suffix;

    Name(boolean valid, String name, String prefix,
         String first, String middle, String last, String suffix) {

        this.name = name;
        this.valid = valid;
        this.prefix = prefix;
        this.first = first;
        this.middle = middle;
        this.last = last;
        this.suffix = suffix;
    }

    static Name fromMap(Map<String, String> data) {
        String name = data.getOrDefault(Constants.OUT_NAME, Constants.EMPTY);
        String prefix = data.getOrDefault(Constants.OUT_PREFIX, Constants.EMPTY);
        String suffix = data.getOrDefault(Constants.OUT_SUFFIX, Constants.EMPTY);
        String first = data.getOrDefault(Constants.OUT_FIRSTNAME, Constants.EMPTY);
        String middle = data.getOrDefault(Constants.OUT_MIDDLENAME, Constants.EMPTY);
        String last = data.getOrDefault(Constants.OUT_LASTNAME, Constants.EMPTY);

        String inFirst = data.getOrDefault(Constants.IN_FIRSTNAME, Constants.EMPTY);
        String inMiddle = data.getOrDefault(Constants.IN_MIDDLENAME, Constants.EMPTY);
        String inLast = data.getOrDefault(Constants.IN_LASTNAME, Constants.EMPTY);

        // When constructing a Name from the output of MD, if the last name is present, but first name empty,
        // we must swap first and last name as MD has swapped them.

        if (isNotEmpty(inFirst) && isEmpty(inMiddle) && isEmpty(inLast)) {
            if (isNotEmpty(last) && isEmpty(first)) {
                return new Name(true, name, prefix, last, middle, inLast, suffix);
            }
        }
        return new Name(true, name, prefix, first, middle, last, suffix);
    }

    public String MiddleInitial() {
        if (isNotEmpty(middle)) {
            return middle.substring(0, 1);
        }
        return Constants.EMPTY;
    }

    public boolean isValid() {
        return valid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getMiddle() {
        return middle;
    }

    public void setMiddle(String middle) {
        this.middle = middle;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
