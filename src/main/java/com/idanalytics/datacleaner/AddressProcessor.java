package com.idanalytics.datacleaner;

import java.util.Arrays;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * AddressProcessor is responsible for performing address processing during the
 * PreProcess step. This is intended to be called before MelissaData.
 */
final class AddressProcessor {

    /**
     * preProcess cleans given address elements and expects the following keys to be
     * present in the data map:
     *
     * IN_ADDRESS 
     * IN_CITY 
     * IN_STATE 
     * IN_ZIP
     *
     * @param data
     * @return sanitized Address object
     */
    Address preProcess(Map<String, String> data) throws Exception {

        if (!data.containsKey(Constants.IN_ADDRESS) && !data.containsKey(Constants.IN_CITY)
                && !data.containsKey(Constants.IN_STATE) && !data.containsKey(Constants.IN_ZIP)) {
            throw new Exception("in_address, in_city, in_state, and in_zip must be present");
        }

        String address = Romanize.convert(trimToEmpty(data.get(Constants.IN_ADDRESS))).toUpperCase();
        String city = trimToEmpty(data.get(Constants.IN_CITY)).toUpperCase();
        String state = trimToEmpty(data.get(Constants.IN_STATE)).toUpperCase();
        String zip9 = trimToEmpty(data.get(Constants.IN_ZIP)).toUpperCase();
        String zip5 = Constants.EMPTY;
        String zip4 = Constants.FOUR_ZEROS;

        // If we don't have a proper zip9(5), return early here as we can't derive zip5
        // or zip4
        if (zip9.length() != 5 && zip9.length() != 9) {
            // set ZIP9 to empty string in this case.
            return new Address(false, address, Constants.EMPTY, zip5, zip4, city, state);
        }

        if (zip9.length() == 9) {
            zip5 = zip9.substring(0, 5);
            zip4 = zip9.substring(5, 9);
        } else {
            zip5 = zip9;
        }

        return new Address(true, address, zip9, zip5, zip4, city, state);
    }

    /**
     * errorProcess is responsible for basic data sanitization in response to an
     * error in the MelissaData service.
     * 
     * @param addr
     */
    void errorProcess(Address addr) {
        String cleanedAddr = AddressUtils
                .abbreviateStreetSuffixes(AddressUtils.cleanSecondaryComponent(addr.getAddress()));
        addr.setAddress(cleanedAddr);
        addr.setCity(Constants.EMPTY);
        addr.setState(Constants.EMPTY);
    }

    void postProcess(Address addr, Map<String, String> data) {
        String combinedAddr = combineAddress(addr, data);
        addr.setAddress(combinedAddr);
        
        String postCity = Utils.postProcessOutput(addr.getCity());
        String postState = Utils.postProcessOutput(addr.getState());

        String addressType = Utils.postProcessOutput(data.getOrDefault(Constants.OUT_ADDRESSTYPE, Constants.EMPTY));
        if (Constants.UNASSIGNED_ADDRESS_TYPE.equals(addressType)) {
            addr.setZip4(Constants.FOUR_ZEROS);
        }

        addr.setCity(postCity);
        addr.setState(postState);
    }

    private String combineAddress(Address addr, Map<String, String> data) {
        String resultsString = data.getOrDefault(Constants.OUT_ADDR_RESULTS, Constants.EMPTY);
		String[] results = resultsString.split(",");
		// If "AS23" is in the result code and it's of street type, don't use secondary address
		if (results.length == 0 || 
				!(Arrays.asList(results).contains("AS23")
						&& (data.get(Constants.OUT_ADDRESSTYPE).equals(Constants.STREET_RESIDENTIAL_ADDRESS_TYPE) 
								|| data.get(Constants.OUT_ADDRESSTYPE).equals(Constants.STREET_DEFAULT_ADDRESS_TYPE) )
			     )
			) {
            return join(new String[] {
                    addr.getAddress(),
                    data.get(Constants.OUT_SECONDARYADDRESS)
            }, Constants.SPACE).trim();
        }
        return addr.getAddress();
    }
}
