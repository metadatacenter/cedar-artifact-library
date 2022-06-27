package org.metadatacenter.redcap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class REDCapConstants
{
  public static final String VARIABLE_NAME_COLUMN_NAME = "Variable/Field Name";
  public static final String FORM_NAME_COLUMN_NAME = "Form Name";
  public static final String SECTION_HEADER_COLUMN_NAME = "Section Header";
  public static final String FIELD_TYPE_COLUMN_NAME = "Field Type";
  public static final String FIELD_LABEL_COLUMN_NAME = "Field Label";
  public static final String CHOICES_CALCULATIONS_OR_SLIDER_LABELS_COLUMN_NAME = "Choices, Calculations OR Slider Labels";
  public static final String FIELD_NOTES_COLUMN_NAME = "Field Notes";
  public static final String TEXT_VALIDATION_TYPE_OR_SHOW_SLIDER_NUMBER_COLUMN_NAME = "Text Validation Type OR Show Slider Number";
  public static final String TEXT_VALIDATION_MIN_MAX_COLUMN_NAME = "Text Validation Min/Max";
  public static final String IDENTIFIERS_COLUMN_NAME = "Identifiers";
  public static final String BRANCHING_LOGIC_COLUMN_NAME = "Branching Logic";
  public static final String REQUIRED_FIELD_COLUMN_NAME = "Required Field";
  public static final String CUSTOM_ALIGNMENT_COLUMN_NAME = "Custom Alignment";
  public static final String QUESTION_NUMBER_COLUMN_NAME = "Question Number (surveys only)";

  public static final int VARIABLE_NAME_COLUMN_INDEX = 0;
  public static final int FORM_NAME_COLUMN_INDEX = 1;
  public static final int SECTION_HEADER_COLUMN_INDEX = 2;
  public static final int FIELD_TYPE_COLUMN_INDEX = 3;
  public static final int FIELD_LABEL_COLUMN_INDEX = 4;
  public static final int CHOICES_CALCULATIONS_OR_SLIDER_LABELS_COLUMN_INDEX = 5;
  public static final int FIELD_NOTES_COLUMN_INDEX = 6;
  public static final int TEXT_VALIDATION_TYPE_OR_SHOW_SLIDER_NUMBER_COLUMN_INDEX = 7;
  public static final int TEXT_VALIDATION_MIN_MAX_COLUMN_INDEX = 8;
  public static final int IDENTIFIERS_COLUMN_INDEX = 9;
  public static final int BRANCHING_LOGIC_COLUMN_INDEX = 10;
  public static final int REQUIRED_FIELD_COLUMN_INDEX = 11;
  public static final int CUSTOM_ALIGNMENT_COLUMN_INDEX = 12;
  public static final int QUESTION_NUMBER_COLUMN_INDEX = 13;

  public static final Set<String> COLUMN_NAMES = new HashSet<>(Arrays
    .asList(VARIABLE_NAME_COLUMN_NAME, FORM_NAME_COLUMN_NAME, SECTION_HEADER_COLUMN_NAME, FIELD_TYPE_COLUMN_NAME,
      FIELD_LABEL_COLUMN_NAME, CHOICES_CALCULATIONS_OR_SLIDER_LABELS_COLUMN_NAME, FIELD_NOTES_COLUMN_NAME,
      TEXT_VALIDATION_TYPE_OR_SHOW_SLIDER_NUMBER_COLUMN_NAME, TEXT_VALIDATION_MIN_MAX_COLUMN_NAME,
      IDENTIFIERS_COLUMN_NAME, BRANCHING_LOGIC_COLUMN_NAME, REQUIRED_FIELD_COLUMN_NAME, CUSTOM_ALIGNMENT_COLUMN_NAME,
      QUESTION_NUMBER_COLUMN_NAME));

  public static final int HEADER_ROW_NUMBER = 0;

  public static final String TEXT_FIELD_TYPE = "TEXT";
  public static final String NOTES_FIELD_TYPE = "NOTES";
  public static final String RADIO_FIELD_TYPE = "RADIO";
  public static final String DROPDOWN_FIELD_TYPE = "DROPDOWN";
  public static final String CHECKBOX_FIELD_TYPE = "CHECKBOX";
  public static final String CALC_FIELD_TYPE = "CACL";
  public static final String FILE_FIELD_TYPE = "FILE";
  public static final String YESNO_FIELD_TYPE = "YESNO";
  public static final String TRUEFALSE_FIELD_TYPE = "TRUEFALSE";
  public static final String DESCRIPTIVE_FIELD_TYPE = "DESCRIPTIVE";
  public static final String SLIDER_FIELD_TYPE = "SLIDER";

  public static final String DATE_YMD_TEXTFIELD_VALIDATION = "DATE_YMD";
  public static final String DATE_MDY_TEXTFIELD_VALIDATION = "DATE_MDY";
  public static final String DATE_DMY_TEXTFIELD_VALIDATION = "DATE_DMY";
  public static final String TIME_TEXTFIELD_VALIDATION = "TIME";
  public static final String DATETIME_YMD_FIELD_VALIDATION = "DATETIME_YMD";
  public static final String DATETIME_MDY_FIELD_VALIDATION = "DATETIME_MDY";
  public static final String DATETIME_DMY_TEXTFIELD_VALIDATION = "DATETIME_DMY";
  public static final String DATETIME_SECONDS_Y_TEXTFIELD_VALIDATION = "DATETIME_SECONDS_Y";
  public static final String MD_TEXTFIELD_VALIDATION = "MD";
  public static final String DATETIME_SECONDS_M_TEXTFIELD_VALIDATION = "DATETIME_SECONDS_M";
  public static final String DY_TEXTFIELD_VALIDATION = "DY";
  public static final String DATETIME_SECONDS_D_TEXTFIELD_VALIDATION = "DATETIME_SECONDS_D";
  public static final String MY_TEXTFIELD_VALIDATION = "MY";
  public static final String TIME_MM_SS_TEXTFIELD_VALIDATION = "Time (MM:SS)";
  public static final String PHONE_TEXTFIELD_VALIDATION = "PHONE";
  public static final String PHONE_AUSTRALIA_TEXTFIELD_VALIDATION = "Phone (Australia)";
  public static final String EMAIL_TEXTFIELD_VALIDATION = "EMAIL";
  public static final String POSTAL_CODE_AUSTRALIA_TEXTFIELD_VALIDATION = "Postal Code (Australia)";
  public static final String POSTAL_CODE_CANADA_TEXTFIELD_VALIDATION = "Postal Code (Canada)";
  public static final String SOCIAL_SECURITY_NUMBER_US_TEXTFIELD_VALIDATION = "Social Security Number (US)";
  public static final String LETTERS_ONLY_TEXTFIELD_VALIDATION = "Letters only";
  public static final String SQL_TEXTFIELD_VALIDATION = "SQL";
  public static final String MRN_TEXTFIELD_VALIDATION = "MRN";
  public static final String ZIPCODE_TEXTFIELD_VALIDATION = "ZIPCODE";
  public static final String INTEGER_TEXTFIELD_VALIDATION = "INTEGER";
  public static final String NUMBER_TEXTFIELD_VALIDATION = "NUMBER";
  public static final String NUMBER_1_DECIMAL_PLACE_TEXTFIELD_VALIDATION = "Number (1 decimal place)";
  public static final String NUMBER_2_DECIMAL_PLACE_TEXTFIELD_VALIDATION = "Number (2 decimal places)";
  public static final String NUMBER_3_DECIMAL_PLACE_TEXTFIELD_VALIDATION = "Number (3 decimal places)";
  public static final String NUMBER_4_DECIMAL_PLACE_TEXTFIELD_VALIDATION = "Number (4 decimal places)";

  public static final Set<String> FIELD_TYPES = new HashSet<>(Arrays
    .asList(TEXT_FIELD_TYPE, NOTES_FIELD_TYPE, RADIO_FIELD_TYPE, DROPDOWN_FIELD_TYPE, CHECKBOX_FIELD_TYPE,
      CALC_FIELD_TYPE, FILE_FIELD_TYPE, YESNO_FIELD_TYPE, TRUEFALSE_FIELD_TYPE, DESCRIPTIVE_FIELD_TYPE,
      SLIDER_FIELD_TYPE));

  public static final Set<String> TEXTFIELD_VALIDATIONS = new HashSet<>(Arrays
    .asList(DATE_YMD_TEXTFIELD_VALIDATION, DATE_MDY_TEXTFIELD_VALIDATION, DATE_DMY_TEXTFIELD_VALIDATION,
      TIME_TEXTFIELD_VALIDATION, DATETIME_YMD_FIELD_VALIDATION, DATETIME_MDY_FIELD_VALIDATION,
      DATETIME_DMY_TEXTFIELD_VALIDATION, DATETIME_SECONDS_Y_TEXTFIELD_VALIDATION, MD_TEXTFIELD_VALIDATION,
      DATETIME_SECONDS_M_TEXTFIELD_VALIDATION, DY_TEXTFIELD_VALIDATION, DATETIME_SECONDS_D_TEXTFIELD_VALIDATION,
      MY_TEXTFIELD_VALIDATION, TIME_MM_SS_TEXTFIELD_VALIDATION, PHONE_TEXTFIELD_VALIDATION,
      PHONE_AUSTRALIA_TEXTFIELD_VALIDATION, EMAIL_TEXTFIELD_VALIDATION, INTEGER_TEXTFIELD_VALIDATION,
      NUMBER_TEXTFIELD_VALIDATION, NUMBER_1_DECIMAL_PLACE_TEXTFIELD_VALIDATION,
      NUMBER_2_DECIMAL_PLACE_TEXTFIELD_VALIDATION, NUMBER_3_DECIMAL_PLACE_TEXTFIELD_VALIDATION,
      NUMBER_4_DECIMAL_PLACE_TEXTFIELD_VALIDATION, POSTAL_CODE_AUSTRALIA_TEXTFIELD_VALIDATION,
      POSTAL_CODE_CANADA_TEXTFIELD_VALIDATION, SOCIAL_SECURITY_NUMBER_US_TEXTFIELD_VALIDATION,
      LETTERS_ONLY_TEXTFIELD_VALIDATION, SQL_TEXTFIELD_VALIDATION, MRN_TEXTFIELD_VALIDATION,
      ZIPCODE_TEXTFIELD_VALIDATION));
}

