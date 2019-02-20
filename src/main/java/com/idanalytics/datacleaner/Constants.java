package com.idanalytics.datacleaner;

import java.util.regex.Pattern;

public interface Constants {

    Pattern FULL_NAME_PATTERN = Pattern.compile("[^A-Z \\-\\|]");
    Pattern NAME_PATTERN = Pattern.compile("[^A-Z \\-]");
    Pattern NON_NAME_WORDS = Pattern.compile("^(OR|AND)(?:\\s|$)");
    Pattern PREFIX_PATTERN = Pattern.compile("(?:\\s|^)(DR|MR|MS|MRS|SIR|SRTA|SRA)(?:\\s|$)");
    Pattern SUFFIX_PATTERN = Pattern.compile("(?:\\s|^)(JR|SR|I+V?|PHD|MD)(?:\\s|$)");

    char POUND = '#';
    String POUNDSTR = "#";
    String APOSTROPHE = "'";
    String EMPTY = "";
    String FOUR_ZEROS = "0000";

    String IN_NAME = "in_name";
    String IN_FIRSTNAME = "in_firstname";
    String IN_MIDDLENAME = "in_middlename";
    String IN_MIDDLEINITIAL = "in_middleinitial";
    String IN_LASTNAME = "in_lastname";
    String IN_PREFIX = "in_prefix";
    String IN_SUFFIX = "in_suffix";

    String OUT_NAME = "out_name";
    String OUT_PREFIX = "out_prefix";
    String OUT_FIRSTNAME = "out_firstname";
    String OUT_MIDDLENAME = "out_middlename";
    String OUT_LASTNAME = "out_lastname";
    String OUT_SUFFIX = "out_suffix";

    String IN_ADDRESS = "in_address";
    String IN_CITY = "in_city";
    String IN_STATE = "in_state";
    String IN_ZIP = "in_zip";
    String IN_ZIP4 = "in_zip4";
    String IN_ZIP5 = "in_zip5";

    // IDA Address Types
    String FIRM_ADDRESS_TYPE = "F";
    String GENERAL_DELIVERY_ADDRESS_TYPE = "G";
    String HIGH_RISE_APT_ADDRESS_TYPE = "H";
    String MILITARY_ADDRESS_TYPE = "M";
    String PO_BOX_ADDRESS_TYPE = "P";
    String RURAL_ROUTE_ADDRESS_TYPE = "R";
    String STREET_RESIDENTIAL_ADDRESS_TYPE = "S";
    String FIRM_DEFAULT_ADDRESS_TYPE = "FD";
    String GENERAL_DEFAULT_DELIVERY_ADDRESS_TYPE = "GD";
    String HIGH_RISE_APT_DEFAULT_ADDRESS_TYPE = "HD";
    String MILITARY_DEFAULT_ADDRESS_TYPE = "MD";
    String PO_BOX_DEFAULT_ADDRESS_TYPE = "PD";
    String RURAL_ROUTE_DEFAULT_ADDRESS_TYPE = "RD";
    String STREET_DEFAULT_ADDRESS_TYPE = "SD";
    String UNIQUE_DEFAULT_TYPE = "UD";
    String UNASSIGNED_ADDRESS_TYPE = "U";

    String OUT_ADDRESSTYPE = "out_addresstypecode";

    String OUT_ADDR_RESULTS = "out_a_results";
    String OUT_ADDRESS = "out_address";
    String OUT_SECONDARYADDRESS = "out_secondaryaddress";

    String OUT_CITY = "out_city";
    String OUT_STATE = "out_state";
    String OUT_ZIP9 = "out_zip9";
    String OUT_ZIP5 = "out_zip5";
    String OUT_ZIP4 = "out_zip4";

    String IN_PHONE = "in_phone";

    // Address related outputs
    String VALID_ADDRESS = "valid_address";
    String ADDRESS = "address";
    String ZIP9 = "zip9";
    String ZIP5 = "zip5";
    String ZIP4 = "zip4";
    String CITY = "city";
    String STATE = "State";

    // Name related outputs
    String VALID_NAME = "valid_name";
    String MATCHED_EXCLUSIONS = "matched_exclusions";

    String FULL_NAME = "full_name";
    String PREFIX = "prefix";
    String FIRST_NAME = "first_name";
    String MIDDLE_NAME = "middle_name";
    String LAST_NAME = "last_name";
    String SUFFIX = "suffix";
    String MIDDLE_INITIAL = "middle_initial";
    String NAME = "name";

    String TRUE = "true";
    String FALSE = "false";
    
    String PIPE = "|";
    String SPACE = " ";
}
