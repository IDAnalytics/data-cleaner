import java.util.Map;

class Address {
    private boolean valid;
    private String address;
    private String zip9;
    private String zip5;
    private String zip4;
    private String city;
    private String state;

    Address(boolean valid, String address, String zip9, String zip5, String zip4, String city, String state) {

        this.valid = valid;
        this.address = address;
        this.zip9 = zip9;
        this.zip5 = zip5;
        this.zip4 = zip4;
        this.city = city;
        this.state = state;
    }

    static Address fromMap(Map<String, String> data) {
        String address = data.getOrDefault(Constants.OUT_ADDRESS, Constants.EMPTY);
        String city = data.getOrDefault(Constants.OUT_CITY, Constants.EMPTY);
        String state = data.getOrDefault(Constants.OUT_STATE, Constants.EMPTY);
        String zip9 = data.getOrDefault(Constants.OUT_ZIP9, Constants.EMPTY);
        String zip5 = populateZip5(zip9);
        String zip4 = populateZip4(zip9, data);

        return new Address(true, address, zip9, zip5, zip4, city, state);
    }

    public boolean isValid() {
        return valid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZip9() {
        return zip9;
    }


    public String getZip5() {
        return zip5;
    }


    public String getZip4() {
        return zip4;
    }

    public void setZip4(String zip4) {
        this.zip4 = zip4;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    private static String populateZip5(String zip9) {
        if (zip9.length() >= 5) {
            return zip9.substring(0, 5);
        }
        return Constants.EMPTY;
    }

    private static String populateZip4(String zip9, Map<String, String> data) {
        // see if we can get zip4 from zip9
        if (zip9.length() == 9) {
            return zip9.substring(5, 9);
        }
        return data.getOrDefault(Constants.OUT_ZIP4, Constants.EMPTY);
    }
}
