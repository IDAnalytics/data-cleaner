# Data Cleanser Utils

This library contains methods needed to preProcess, errorProcess and postProcess data that goes in and out of 
Melissa Data. 

### Preferred Workflow:
1) preProcess PII
2) Process through Melissa Data
3) postProcess PII

### Methods:

#### _preProcess()_:

Method which is used to pre-process a map of Key-value pairs containing input information about name and address. 

*NOTE*: preProcess will throw an exception on the following conditions:

* Null or empty data Map
* Empty IN_FIRSTNAME and IN_LASTNAME keys
* Missing IN_ADDRESS, IN_CITY, IN_STATE, IN_ZIP keys

##### The following keys are required for Name and Address from Constants.java:

     Name:
     * IN_PREFIX
     * IN_SUFFIX
     * IN_FIRSTNAME
     * IN_MIDDLENAME
     * IN_LASTNAME

     Address:
     * IN_ADDRESS
     * IN_CITY
     * IN_STATE
     * IN_ZIP


Name:
* Extracts data from input for processing
* Checks to see that inputs are valid
* Checks to see that inputs do not include exclusions that were passed in as exceptions when calling this method.
* Trims the input to get rid of extra white spaces, and combines first/last name inputs to form full name when 
input is absent
* Extracts prefix or suffix from first or last name if they were entered together. For example, "Mr. John" as 
input first name.

Address:
* Trims and Romanizes inputs if input was not empty
* Makes sure that the input address is valid based on the length of the ZIP code
* Sets fields like city or state to empty if they were not part of the inputs

Populates output map with preprocessed results.

*Note*: Preprocess overwrites IN_* fields. These same fields should be fetched from data map and passed to MelissaData. 

#### _postProcess()_:

Postprocess is responsible for data cleanup after the call to MelissaData. The expectation is the OUT_* fields are set from the results of MelissaData before the call. In order properly handle errors, there are two MelissaDataStatus flags that are required to be set when calling the post process method to indicate if an error/exception occurred (see postprocess example #1)

*NOTE*: postProcess will throw an exception on the following conditions:
* Missing IN_NAME key
* Missing IN_ADDRESS key
     
##### The following OUT data map keys are required and should be set from a MD call (from Constants.java):
     
     Name:
     * OUT_PREFIX
     * OUT_FIRSTNAME
     * OUT_MIDDLENAME
     * OUT_LASTNAME
     * OUT_SUFFIX
     *
     Address (values come from MD call):
     * OUT_ADDR_RESULTS
     * OUT_ADDRESSTYPE
     * OUT_ADDRESS
     * OUT_CITY
     * OUT_STATE
     * OUT_SECONDARYADDRESS
     * OUT_ZIP9
     * OUT_ZIP5
     * OUT_ZIP4

Name: 
* Removes white spaces and apostrophes from name input fields
* Makes sure that the input fields are within bounds of required character length, trims extra when needed

Address:
* Cleans secondary components and removes spaces and apostrophes out of address-related fields
* Splits ZIP9 into ZIP5 and ZIP4, and makes sure that ZIP9 matches combined ZIP5 and ZIP4
* Forms an address that contains ZIP5

Populates address fields with postprocessed results.

### Preprocess Usage:
Example #1 Preprocess (Default exclusion list used):

```
Map<String, String> data = new HashMap<String, String>();
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
```

Example #2 Preprocess (exclusion list provided):

```
......
List<Pattern> exclusions = new ArrayList<>();
exclusions.add(Pattern.compile("\\bBIG APPLE COMPANY\\b"));

DataCleanser dc = new DataCleanser(new ExclusionList(exclusions));
dc.preProcess(data);
......
```
### PostProcess Usage Example #1 (After MD call):

```
MD Call -> 
....
data.put(Constants.OUT_PREFIX, "MR");
data.put(Constants.OUT_FIRSTNAME, "JOHN");
data.put(Constants.OUT_MIDDLENAME, "DOE");
data.put(Constants.OUT_LASTNAME, " DIAZ SMITH");
data.put(Constants.OUT_SUFFIX, "JR");

data.put(Constants.OUT_ADDRESSTYPE, Constants.UNASSIGNED_ADDRESS_TYPE);
data.put(Constants.OUT_ADDRESS, "1234 MAIN ST");
data.put(Constants.OUT_SECONDARYADDRESS, "SUITE 123");
data.put(Constants.OUT_ZIP9, "92128");

DataCleanser dc = new DataCleanser();
dc.postProcess(data, MelissaDataStatus.SUCCESS, MelissaDataStatus.SUCCESS);

String zip4 = data.get(Constants.OUT_ZIP4);
.....
```

### PostProcess Usage Example #1 - Failure (After MD call):

```
MD Call -> 
....
data.put(Constants.OUT_PREFIX, "MR");
data.put(Constants.OUT_FIRSTNAME, "JOHN");
data.put(Constants.OUT_MIDDLENAME, "DOE");
data.put(Constants.OUT_LASTNAME, " DIAZ SMITH");
data.put(Constants.OUT_SUFFIX, "JR");

data.put(Constants.OUT_ADDRESSTYPE, Constants.UNASSIGNED_ADDRESS_TYPE);
data.put(Constants.OUT_ADDRESS, "1234 MAIN ST");
data.put(Constants.OUT_SECONDARYADDRESS, "SUITE 123");
data.put(Constants.OUT_ZIP9, "92128");

DataCleanser dc = new DataCleanser();
dc.postProcess(data, MelissaDataStatus.ERROR, MelissaDataStatus.ERROR);

String zip4 = data.get(Constants.OUT_ZIP4);
.....
```

Afterwards, the data map should contain preProcessed and postProcessed results.
