public class Utils {

    static String postProcessOutput(String output) {
        if (output != null) {
            output = removeMultipleWhitespaces(output).toUpperCase();
            // remove all apostrophes
            output = output.replaceAll(Constants.APOSTROPHE, Constants.EMPTY);
            return output.trim();
        }
        return Constants.EMPTY;
    }

    static String appendDotToSingleCharName(String name) {
        if (name != null && name.length() == 1
                && Character.isLetter(name.charAt(0))) {
            name = name + Constants.APOSTROPHE;
        }
        return name;
    }

    /**
     * Removes multiple whitespace characters
     *
     * @param s
     * @return modified string if given string is not null, else returns null
     */
    private static String removeMultipleWhitespaces(String s) {
        String str = s;
        if (str != null) {
            str = str.replaceAll("\\s{2,}", Constants.SPACE);
        }
        return str;
    }
}
