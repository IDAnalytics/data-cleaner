package com.idanalytics.datacleaner;

import java.util.Map;

import static org.apache.commons.lang3.StringUtils.trim;

public class DataCleanser {

    private AddressProcessor addressProcessor;
    private NameProcessor nameProcessor;

    /**
     * Constructs a DataCleanser with a default exclusion list. This constructor
     * will throw an exception if the default name exclusion file is removed from the resource folder.
     */
    public DataCleanser() throws Exception {
        this(new ExclusionList());
    }

    /**
     * Constructs a DataCleanser with provided name exclusion list.
     *
     * @param exclusionList
     */
    public DataCleanser(ExclusionList exclusionList) {
        nameProcessor = new NameProcessor(exclusionList);
        addressProcessor = new AddressProcessor();
    }

    /**
     * preProcess is responsible for preparing input for MelissaData call. This is intended to be called
     * before the MelissaData.
     * <p>
     * The Following keys are checked/accessed from the incoming record @see Constants.java:
     * Name:
     * IN_PREFIX
     * IN_SUFFIX
     * IN_FIRSTNAME
     * IN_MIDDLENAME
     * IN_LASTNAME
     * <p>
     * Address:
     * IN_ADDRESS
     * IN_CITY
     * IN_STATE
     * IN_ZIP
     *
     * An Exception will be thrown in the following cases:
     *
     * Name processing: first middle and last are empty or data map is null/empty
     * Address procesing: address, city, state, and zip are empty or data map is null/empty
     *
     * @param data
     */
    public void preProcess(Map<String, String> data) throws Exception {
        if (data == null || data.isEmpty()) {
            throw new Exception("data map must not be null or empty.");
        }

        // preProcess will throw an exception if data map is null or empty or if first, middle, and last are empty.
        Name name = nameProcessor.preProcess(data);

        // preProcess will throw an exception if address IN fields are missing or data map is null or empty.
        Address address = addressProcessor.preProcess(data);

        addNameToMap(data, name);
        addAddressToMap(data, address);
    }

    /**
     * postProcess is called after the call to MelissaData is complete. IN_NAME and IN_ADDRESS must be present
     * as it is our flag that preProcess was called. The Caller should expect an Exception as the name
     * and address are required for error processing and downstream library calls will expect access the tokenized data.
     *
     * The following OUT data map keys are required and should be set from a MD call from Constants.java:
     *
     * Name:
     * OUT_PREFIX
     * OUT_FIRSTNAME
     * OUT_MIDDLENAME
     * OUT_LASTNAME
     * OUT_SUFFIX
     *
     * Address (values are from MD call):
     * OUT_ADDR_RESULTS
     * OUT_ADDRESSTYPE
     * OUT_ADDRESS
     * OUT_SECONDARYADDRESS
     * OUT_CITY
     * OUT_STATE
     * OUT_ZIP9
     * OUT_ZIP5
     * OUT_ZIP4
     *
     * @param data
     * @param nameStatus
     * @param addressStatus
     */
    public void postProcess(Map<String, String> data, MelissaDataStatus nameStatus, MelissaDataStatus addressStatus)
            throws Exception {

        if (!data.containsKey(Constants.IN_NAME)) {
            throw new Exception("IN_NAME key not present.");
        }
        if (!data.containsKey(Constants.IN_ADDRESS)) {
            throw new Exception("IN_ADDRESS key not present.");
        }

        Name name = Name.fromMap(data);

        nameProcessor.postProcess(name);
        addCleanedNameToMap(data, name);

        Address addr = Address.fromMap(data);

        if (MelissaDataStatus.ERROR.equals(addressStatus)) {
            addressProcessor.errorProcess(addr);
        }

        addressProcessor.postProcess(addr, data);
        addCleanedAddressToMap(data, addr);
    }

    private void addNameToMap(Map<String, String> data, Name name) {
        data.put(Constants.IN_NAME, trim(name.getName()));
        data.put(Constants.IN_PREFIX, name.getPrefix());
        data.put(Constants.IN_SUFFIX, name.getSuffix());
        data.put(Constants.IN_FIRSTNAME, name.getFirst());
        data.put(Constants.IN_MIDDLENAME, name.getMiddle());
        data.put(Constants.IN_MIDDLEINITIAL, name.MiddleInitial());
        data.put(Constants.IN_LASTNAME, name.getLast());
    }

    private void addAddressToMap(Map<String, String> data, Address addr) {
        data.put(Constants.IN_ADDRESS, addr.getAddress());
        data.put(Constants.IN_CITY, addr.getCity());
        data.put(Constants.IN_STATE, addr.getState());
        data.put(Constants.IN_ZIP4, addr.getZip4());
        data.put(Constants.IN_ZIP5, addr.getZip5());
        data.put(Constants.IN_ZIP, addr.getZip9());
    }

    private void addCleanedNameToMap(Map<String, String> data, Name name) {
        data.put(Constants.OUT_NAME, name.getName());
        data.put(Constants.OUT_PREFIX, name.getPrefix());
        data.put(Constants.OUT_FIRSTNAME, name.getFirst());
        data.put(Constants.OUT_MIDDLENAME, name.getMiddle());
        data.put(Constants.OUT_LASTNAME, name.getLast());
        data.put(Constants.OUT_SUFFIX, name.getSuffix());
    }

    private void addCleanedAddressToMap(Map<String, String> data, Address addr) {
        data.put(Constants.OUT_ADDRESS, addr.getAddress());
        data.put(Constants.OUT_ZIP4, addr.getZip4());
        data.put(Constants.OUT_ZIP5, addr.getZip5());
        data.put(Constants.OUT_ZIP9, addr.getZip9());
        data.put(Constants.OUT_CITY, addr.getCity());
        data.put(Constants.OUT_STATE, addr.getState());
    }
}
