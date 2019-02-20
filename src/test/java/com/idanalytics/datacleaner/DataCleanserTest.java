package com.idanalytics.datacleaner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DataCleanserTest {

    private Map<String, String> data = new HashMap<>();

    @BeforeEach
    void init() {
        data.clear();
    }

    /////////////////////////////////////////
    // PREPROCESS NAME TESTS
    /////////////////////////////////////////

    @Test
    void preProcess_missingDataMap_throwsException() throws Exception {
        DataCleanser dc = new DataCleanser();
        Executable closure = () -> dc.preProcess(null);
        assertThrows(Exception.class, closure, "data map must not be null or empty.");
    }

    @Test
    void preProcess_cleanNameElements_FNMNLNCleaned() throws Exception {

        data.put(Constants.IN_PREFIX, "MR");
        data.put(Constants.IN_FIRSTNAME, "OR");
        data.put(Constants.IN_MIDDLENAME, "DO!E");
        data.put(Constants.IN_LASTNAME, "SMI!TH.");
        data.put(Constants.IN_SUFFIX, "JR");

        data.put(Constants.IN_ADDRESS, "1234 Main St # 24");
        data.put(Constants.IN_ZIP, "92128");

        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        String name = data.get(Constants.IN_NAME);
        String fn = data.get(Constants.IN_FIRSTNAME);
        String mn = data.get(Constants.IN_MIDDLENAME);
        String ln = data.get(Constants.IN_LASTNAME);
        String pf = data.get(Constants.IN_PREFIX);
        String sf = data.get(Constants.IN_SUFFIX);

        assertEquals("MR", pf);
        assertEquals("SMITH", ln);
        assertEquals("DOE", mn);
        assertEquals("", fn);
        assertEquals("DOE SMITH", name);
        assertEquals("JR", sf);
    }

    @Test
    void preProcess_combinedSuffixLNprefixFN() throws Exception {

        data.put(Constants.IN_FIRSTNAME, "MR JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "SMITH JR");

        data.put(Constants.IN_ADDRESS, "1234 Main St # 24");
        data.put(Constants.IN_ZIP, "92128");

        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        String name = data.get(Constants.IN_NAME);
        String fn = data.get(Constants.IN_FIRSTNAME);
        String mn = data.get(Constants.IN_MIDDLENAME);
        String ln = data.get(Constants.IN_LASTNAME);
        String pf = data.get(Constants.IN_PREFIX);
        String sf = data.get(Constants.IN_SUFFIX);

        assertEquals("MR", pf);
        assertEquals("SMITH", ln);
        assertEquals("DOE", mn);
        assertEquals("JOHN", fn);
        assertEquals("JOHN DOE SMITH", name);
        assertEquals("JR", sf);
    }

    @Test
    void preProcess_suffixAndPrefixWithDot() throws Exception {

        data.put(Constants.IN_PREFIX, "MR.");
        data.put(Constants.IN_FIRSTNAME, "JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "SMITH");
        data.put(Constants.IN_SUFFIX, "JR.");

        data.put(Constants.IN_ADDRESS, "1234 Main St # 24");
        data.put(Constants.IN_ZIP, "92128");

        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        String name = data.get(Constants.IN_NAME);
        String fn = data.get(Constants.IN_FIRSTNAME);
        String mn = data.get(Constants.IN_MIDDLENAME);
        String ln = data.get(Constants.IN_LASTNAME);
        String pf = data.get(Constants.IN_PREFIX);
        String sf = data.get(Constants.IN_SUFFIX);

        assertEquals("MR.", pf);
        assertEquals("SMITH", ln);
        assertEquals("DOE", mn);
        assertEquals("JOHN", fn);
        assertEquals("JOHN DOE SMITH", name);
        assertEquals("JR.", sf);
    }

    @Test
    void preProcess_prefixFNAndSuffixLN() throws Exception {

        data.put(Constants.IN_PREFIX, "");
        data.put(Constants.IN_FIRSTNAME, "JOHN JR.");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "MR. SMITH");
        data.put(Constants.IN_SUFFIX, "");

        data.put(Constants.IN_ADDRESS, "1234 Main St # 24");
        data.put(Constants.IN_ZIP, "92128");

        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        String name = data.get(Constants.IN_NAME);
        String fn = data.get(Constants.IN_FIRSTNAME);
        String mn = data.get(Constants.IN_MIDDLENAME);
        String ln = data.get(Constants.IN_LASTNAME);
        String pf = data.get(Constants.IN_PREFIX);
        String sf = data.get(Constants.IN_SUFFIX);

        assertEquals("MR", pf);
        assertEquals("SMITH", ln);
        assertEquals("DOE", mn);
        assertEquals("JOHN", fn);
        assertEquals("JOHN DOE SMITH", name);
        assertEquals("JR", sf);
    }

    @Test
    void preProcess_missingFirstAndLast_throwsException() throws Exception {
        data.put(Constants.IN_FIRSTNAME, "");
        data.put(Constants.IN_LASTNAME, "");

        DataCleanser dc = new DataCleanser();
        Executable closure = () -> dc.preProcess(data);
        assertThrows(Exception.class, closure, "first and last cannot be empty.");
    }

    @Test
    void preProcess_middleName_extractsMiddleInitial() throws Exception {
        data.put(Constants.IN_PREFIX, "MR");
        data.put(Constants.IN_FIRSTNAME, "JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "SMITH");
        data.put(Constants.IN_SUFFIX, "JR");

        data.put(Constants.IN_ADDRESS, "1234 Main St # 24");
        data.put(Constants.IN_ZIP, "92128");

        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        String mn = data.get(Constants.IN_MIDDLENAME);
        String mi = data.get(Constants.IN_MIDDLEINITIAL);

        assertEquals("DOE", mn);
        assertEquals("D", mi);
    }

    @Test
    void preProcess_exclusionList_exclusionExtraction() throws Exception {
        data.put(Constants.IN_PREFIX, "");
        data.put(Constants.IN_FIRSTNAME, "BIG");
        data.put(Constants.IN_MIDDLENAME, "APPLE");
        data.put(Constants.IN_LASTNAME, "COMPANY");
        data.put(Constants.IN_SUFFIX, "");

        data.put(Constants.IN_ADDRESS, "1234 Main St # 24");
        data.put(Constants.IN_ZIP, "92128");

        List<Pattern> exclusions = new ArrayList<>();
        exclusions.add(Pattern.compile("\\bBIG APPLE COMPANY\\b"));

        DataCleanser dc = new DataCleanser(new ExclusionList(exclusions));
        dc.preProcess(data);

        String name = data.get(Constants.IN_NAME);
        String fn = data.get(Constants.IN_FIRSTNAME);
        String mn = data.get(Constants.IN_MIDDLENAME);
        String mi = data.get(Constants.IN_MIDDLEINITIAL);
        String ln = data.get(Constants.IN_LASTNAME);
        String pf = data.get(Constants.IN_PREFIX);
        String sf = data.get(Constants.IN_SUFFIX);

        assertEquals("", pf);
        assertEquals("COMPANY", ln);
        assertEquals("A", mi);
        assertEquals("APPLE", mn);
        assertEquals("BIG", fn);
        assertEquals("BIG|COMPANY", name);
        assertEquals("", sf);
    }

    /////////////////////////////////////////
    // POSTPROCESS NAME TESTS
    /////////////////////////////////////////

    @Test
    void postProcess_nameElementsExceedsMax_trimsElements() throws Exception {
        data.put(Constants.IN_NAME, "JOHN DOE SMITH");
        data.put(Constants.IN_FIRSTNAME, "JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "SMITH");

        data.put(Constants.OUT_NAME, "JOHN DOE SMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITH");
        data.put(Constants.OUT_PREFIX, "MR");
        data.put(Constants.OUT_FIRSTNAME, "JOHN");
        data.put(Constants.OUT_MIDDLENAME, "DOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOEDOE");
        data.put(Constants.OUT_LASTNAME, "SMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITHSMITH");
        data.put(Constants.OUT_SUFFIX, "JR");

        data.put(Constants.IN_ADDRESS, "1234 MAIN ST # 24");
        data.put(Constants.IN_ZIP, "92128");

        data.put(Constants.OUT_ADDRESS, "1234 MAIN ST # 24");
        data.put(Constants.OUT_ZIP9, "921281234");

        DataCleanser dc = new DataCleanser();
        dc.postProcess(data, MelissaDataStatus.SUCCESS, MelissaDataStatus.SUCCESS);

        String name = data.get(Constants.OUT_NAME);
        String fn = data.get(Constants.OUT_FIRSTNAME);
        String mn = data.get(Constants.OUT_MIDDLENAME);
        String ln = data.get(Constants.OUT_LASTNAME);
        String pf = data.get(Constants.OUT_PREFIX);
        String sf = data.get(Constants.OUT_SUFFIX);

        assertEquals("MR", pf);
        assertEquals("SMITHSMITHSMITHSMITHSMI", ln);
        assertEquals("DOEDOEDOEDOEDOEDOEDOEDO", mn);
        assertEquals("JOHN", fn);
        assertEquals("JOHN DOE SMITHSMITHSMITHSMITHSMITHSMITH", name);
        assertEquals("JR", sf);
    }

    @Test
    void postProcess_apostropheInNameElements_removesApostrophe() throws Exception {
        data.put(Constants.IN_NAME, "JOHN DOE MCSMITH");
        data.put(Constants.IN_FIRSTNAME, "JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "MCSMITH");

        data.put(Constants.OUT_NAME, "JOHN DOE MC'SMI!TH");
        data.put(Constants.OUT_PREFIX, "MR");
        data.put(Constants.OUT_FIRSTNAME, "JOHN");
        data.put(Constants.OUT_MIDDLENAME, "DOE");
        data.put(Constants.OUT_LASTNAME, "MC'SMITH");
        data.put(Constants.OUT_SUFFIX, "JR");

        data.put(Constants.IN_ADDRESS, "1234 MAIN ST # 24");
        data.put(Constants.IN_ZIP, "92128");

        data.put(Constants.OUT_ADDRESS, "1234 MAIN ST # 24");
        data.put(Constants.OUT_ZIP9, "921281234");

        DataCleanser dc = new DataCleanser();
        dc.postProcess(data, MelissaDataStatus.SUCCESS, MelissaDataStatus.SUCCESS);

        String name = data.get(Constants.OUT_NAME);
        String fn = data.get(Constants.OUT_FIRSTNAME);
        String mn = data.get(Constants.OUT_MIDDLENAME);
        String ln = data.get(Constants.OUT_LASTNAME);
        String pf = data.get(Constants.OUT_PREFIX);
        String sf = data.get(Constants.OUT_SUFFIX);

        assertEquals("MR", pf);
        assertEquals("MCSMITH", ln);
        assertEquals("DOE", mn);
        assertEquals("JOHN", fn);
        assertEquals("JOHN DOE MCSMITH", name);
        assertEquals("JR", sf);
    }

    /////////////////////////////////////////
    // PREPROCESS ADDRESS TESTS
    /////////////////////////////////////////

    @Test
    void preProcess_nullMap() throws Exception {
        DataCleanser dc = new DataCleanser();
        Executable closure = () -> dc.preProcess(null);
        assertThrows(Exception.class, closure, "should return null with no map");
    }

    @Test
    void preProcess_missingAddressData() throws Exception {
        data.put(Constants.IN_PREFIX, "Mr");
        data.put(Constants.IN_FIRSTNAME, "John");
        data.put(Constants.IN_MIDDLENAME, "Quincy");
        data.put(Constants.IN_LASTNAME, "Public");
        data.put(Constants.IN_SUFFIX, "Jr");

        DataCleanser dc = new DataCleanser();
        Executable closure = () -> dc.preProcess(data);
        assertThrows(Exception.class, closure, "should return null with no map");
    }

    @Test
    void preProcess_addressHasData() throws Exception {

        data.put(Constants.IN_PREFIX, "Mr");
        data.put(Constants.IN_FIRSTNAME, "John");
        data.put(Constants.IN_MIDDLENAME, "Quincy");
        data.put(Constants.IN_LASTNAME, "Public");
        data.put(Constants.IN_SUFFIX, "Jr");

        data.put(Constants.IN_ADDRESS, "123 Hello Street");
        data.put(Constants.IN_ZIP, "123456789");
        data.put(Constants.IN_CITY, "San Diego");
        data.put(Constants.IN_STATE, "CA");


        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        assertEquals("123 HELLO STREET", data.get(Constants.IN_ADDRESS));
        assertEquals("123456789", data.get(Constants.IN_ZIP));
        assertEquals("12345", data.get(Constants.IN_ZIP5));
        assertEquals("6789", data.get(Constants.IN_ZIP4));
    }

    @Test
    void preProcess_ZIPInvalid() throws Exception {

        data.put(Constants.IN_PREFIX, "Mr");
        data.put(Constants.IN_FIRSTNAME, "John");
        data.put(Constants.IN_MIDDLENAME, "Quincy");
        data.put(Constants.IN_LASTNAME, "Public");
        data.put(Constants.IN_SUFFIX, "Jr");

        data.put(Constants.IN_ADDRESS, "1234 Maple St");
        data.put(Constants.IN_ZIP, "1234");
        data.put(Constants.IN_CITY, "San Diego");
        data.put(Constants.IN_STATE, "CA");

        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        assertEquals("", data.get(Constants.IN_ZIP));
        assertEquals("", data.get(Constants.IN_ZIP5));
        assertEquals("0000", data.get(Constants.IN_ZIP4));

    }

    @Test
    void preProcess_ZIPValid() throws Exception {

        data.put(Constants.IN_PREFIX, "Mr");
        data.put(Constants.IN_FIRSTNAME, "John");
        data.put(Constants.IN_MIDDLENAME, "Quincy");
        data.put(Constants.IN_LASTNAME, "Public");
        data.put(Constants.IN_SUFFIX, "Jr");

        data.put(Constants.IN_ADDRESS, "1234 Maple St");
        data.put(Constants.IN_ZIP, "12345");
        data.put(Constants.IN_CITY, "San Diego");
        data.put(Constants.IN_STATE, "CA");

        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        assertEquals("12345", data.get(Constants.IN_ZIP));
        assertEquals("12345", data.get(Constants.IN_ZIP5));
        assertEquals("0000", data.get(Constants.IN_ZIP4));

//        data.put(Constants.IN_ZIP, "1234567890");
//
//        tester = DataCleanserUtils.preProcessAddress(data);
//
//        assertEquals("INVALID", data.get(Constants.ZIP9));
//        assertEquals("INVALID", data.get(Constants.ZIP5));
//        assertEquals("0000", data.get(Constants.ZIP4));

    }

    @Test
    void preProcess_noCityOrState() throws Exception {

        data.put(Constants.IN_PREFIX, "Mr");
        data.put(Constants.IN_FIRSTNAME, "John");
        data.put(Constants.IN_MIDDLENAME, "Quincy");
        data.put(Constants.IN_LASTNAME, "Public");
        data.put(Constants.IN_SUFFIX, "Jr");

        data.put(Constants.IN_ADDRESS, "1234 Maple St");
        data.put(Constants.IN_ZIP, "12345");

        DataCleanser dc = new DataCleanser();
        dc.preProcess(data);

        assertEquals("", data.get(Constants.IN_CITY));
        assertEquals("", data.get(Constants.IN_STATE));
    }

    /////////////////////////////////////////
    // POSTPROCESS ADDRESS TESTS
    /////////////////////////////////////////

    @Test
    void postProcess_handlesAddressAndZip_cleanedAddressAndZip() throws Exception {
        data.put(Constants.IN_NAME, "JOHN DOE MCSMITH");
        data.put(Constants.IN_FIRSTNAME, "JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "MCSMITH");

        data.put(Constants.OUT_PREFIX, "MR");
        data.put(Constants.OUT_FIRSTNAME, "JOHN");
        data.put(Constants.OUT_MIDDLENAME, "DOE");
        data.put(Constants.OUT_LASTNAME, " DIAZ SMITH");
        data.put(Constants.OUT_SUFFIX, "JR");

        data.put(Constants.IN_ADDRESS, "1234 MAIN ST # 24");
        data.put(Constants.IN_ZIP, "92128");

        data.put(Constants.OUT_ADDRESS, "1234 MAIN ST # 24");
        data.put(Constants.OUT_ZIP9, "921281234");

        DataCleanser dc = new DataCleanser();
        dc.postProcess(data, MelissaDataStatus.SUCCESS, MelissaDataStatus.ERROR);


        String line1 = data.get(Constants.OUT_ADDRESS);
        String zip = data.get(Constants.OUT_ZIP9);
        String zip5 = data.get(Constants.OUT_ZIP5);
        String zip4 = data.get(Constants.OUT_ZIP4);

        assertEquals("1234 MAIN ST APT 24", line1);
        assertEquals("921281234", zip);
        assertEquals("92128", zip5);
        assertEquals("1234", zip4);
    }

    @Test
    void postProcess_handlesSecondaryAddress_appendsSecondaryAddress() throws Exception {
        data.put(Constants.IN_NAME, "JOHN DOE MCSMITH");
        data.put(Constants.IN_FIRSTNAME, "JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "MCSMITH");

        data.put(Constants.OUT_PREFIX, "MR");
        data.put(Constants.OUT_FIRSTNAME, "JOHN");
        data.put(Constants.OUT_MIDDLENAME, "DOE");
        data.put(Constants.OUT_LASTNAME, " DIAZ SMITH");
        data.put(Constants.OUT_SUFFIX, "JR");

        data.put(Constants.IN_ADDRESS, "1234 MAIN ST");
        data.put(Constants.IN_ZIP, "92128");

        data.put(Constants.OUT_ADDR_RESULTS, Constants.EMPTY);
        data.put(Constants.OUT_ADDRESSTYPE, Constants.STREET_RESIDENTIAL_ADDRESS_TYPE);
        data.put(Constants.OUT_ADDRESS, "1234 MAIN ST");
        data.put(Constants.OUT_SECONDARYADDRESS, "SUITE 123");
        data.put(Constants.OUT_ZIP9, "921281234");

        DataCleanser dc = new DataCleanser();
        dc.postProcess(data, MelissaDataStatus.SUCCESS, MelissaDataStatus.ERROR);


        String line1 = data.get(Constants.OUT_ADDRESS);
        String zip = data.get(Constants.OUT_ZIP9);
        String zip5 = data.get(Constants.OUT_ZIP5);
        String zip4 = data.get(Constants.OUT_ZIP4);

        assertEquals("1234 MAIN ST SUITE 123", line1);
        assertEquals("921281234", zip);
        assertEquals("92128", zip5);
        assertEquals("1234", zip4);
    }

    @Test
    void postProcess_handlesAS23_secAddressNotAppended() throws Exception {
        data.put(Constants.IN_NAME, "JOHN DOE MCSMITH");
        data.put(Constants.IN_FIRSTNAME, "JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "MCSMITH");

        data.put(Constants.OUT_PREFIX, "MR");
        data.put(Constants.OUT_FIRSTNAME, "JOHN");
        data.put(Constants.OUT_MIDDLENAME, "DOE");
        data.put(Constants.OUT_LASTNAME, " DIAZ SMITH");
        data.put(Constants.OUT_SUFFIX, "JR");

        data.put(Constants.IN_ADDRESS, "1234 MAIN ST");
        data.put(Constants.IN_ZIP, "92128");

        data.put(Constants.OUT_ADDR_RESULTS, "AS23");
        data.put(Constants.OUT_ADDRESSTYPE, Constants.STREET_RESIDENTIAL_ADDRESS_TYPE);
        data.put(Constants.OUT_ADDRESS, "1234 MAIN ST");
        data.put(Constants.OUT_SECONDARYADDRESS, "SUITE 123");
        data.put(Constants.OUT_ZIP9, "921281234");

        DataCleanser dc = new DataCleanser();
        dc.postProcess(data, MelissaDataStatus.SUCCESS, MelissaDataStatus.ERROR);


        String line1 = data.get(Constants.OUT_ADDRESS);
        String zip = data.get(Constants.OUT_ZIP9);
        String zip5 = data.get(Constants.OUT_ZIP5);
        String zip4 = data.get(Constants.OUT_ZIP4);

        assertEquals("1234 MAIN ST", line1);
        assertEquals("921281234", zip);
        assertEquals("92128", zip5);
        assertEquals("1234", zip4);
    }

    @Test
    void postProcess_handlesUnassignedAddress_setsZip4ToZeros() throws Exception {
        data.put(Constants.IN_NAME, "JOHN DOE MCSMITH");
        data.put(Constants.IN_FIRSTNAME, "JOHN");
        data.put(Constants.IN_MIDDLENAME, "DOE");
        data.put(Constants.IN_LASTNAME, "MCSMITH");

        data.put(Constants.OUT_PREFIX, "MR");
        data.put(Constants.OUT_FIRSTNAME, "JOHN");
        data.put(Constants.OUT_MIDDLENAME, "DOE");
        data.put(Constants.OUT_LASTNAME, " DIAZ SMITH");
        data.put(Constants.OUT_SUFFIX, "JR");

        data.put(Constants.IN_ADDRESS, "1234 MAIN ST");
        data.put(Constants.IN_ZIP, "92128");

        data.put(Constants.OUT_ADDRESSTYPE, Constants.UNASSIGNED_ADDRESS_TYPE);
        data.put(Constants.OUT_ADDRESS, "1234 MAIN ST");
        data.put(Constants.OUT_SECONDARYADDRESS, "SUITE 123");
        data.put(Constants.OUT_ZIP9, "92128");

        DataCleanser dc = new DataCleanser();
        dc.postProcess(data, MelissaDataStatus.SUCCESS, MelissaDataStatus.ERROR);

        String zip4 = data.get(Constants.OUT_ZIP4);
        assertEquals("0000", zip4);
    }

}