package tr.org.tspb.constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * <pre>
 * ISO standartlarına göre temel Latin alfabesi
 * Aa	Bb	Cc	Dd	Ee	Ff	Gg
 * Hh	Ii	Jj	Kk      Ll	Mm	Nn
 * Oo	Pp	Qq	Rr	Ss	Tt	Uu
 * Vv	Ww	Xx	Yy	Zz
 * </pre>
 */
/**
 *
 * @author Telman Şahbazoğlu
 */
public class ProjectConstants {

    public static final String REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_TYPE = "fms_code{{login_member_type}}";
    public static final String REPLACEABLE_KEY_WORD_FOR_FUNCTONS_LOGIN_MEMBER_ID = "fms_code{{login_member_id}}";
    public static final String REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_MEMBER = "fms_code{{filter_member}}";
    public static final String REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_PERIOD = "fms_code{{filter_period}}";
    public static final String REPLACEABLE_KEY_WORD_FOR_FUNCTONS_FILTER_TEMPLATE = "fms_code{{filter_template}}";
    public static final String REPLACEABLE_KEY_WORD_FOR_THIS_FORM = "fms_code{{this_form}}";

    public static final String JMS_EMAIL_QUEUE = "jms/fmsEmailQueue";
    public static final String JMS_EMAIL_QUEUE_CONN_FACTORY = "jms/fmsEmailQueueConnectionFactory";
    public static final String CUSTOM_RESOURCE_ENVIRONMENT = "fms/environment";
    public static final String CUSTOM_RESOURCE_MONGO_URL = "fms/mongourl";
    public static final String CUSTOM_RESOURCE_MONGO_ADMIN_PSWD = "fms/mongoadmin";

    public static final String ARCHITECT_ROLE = "architect";

    public static final DateFormat SIMPLE_DATE_FORMAT__0 = new SimpleDateFormat("dd.MM.yyyy");
    public static final DateFormat SIMPLE_DATE_FORMAT__1 = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    public static final DateFormat SIMPLE_DATE_FORMAT__2 = new SimpleDateFormat("dd MMMM yyyy ; yyyy.MM.dd", new Locale("tr", "TR"));
    public static final DateFormat SIMPLE_DATE_FORMAT__3 = new SimpleDateFormat("dd MMMM yyyy hh:mm:ss", new Locale("tr"));
    public static final DateFormat SIMPLE_DATE_FORMAT__4 = new SimpleDateFormat("yyyyMMddHHmm");
    public static final DateFormat SIMPLE_DATE_FORMAT__5 = new SimpleDateFormat("yyyyMMdd_hhmmss");
    public static final DateFormat SIMPLE_DATE_FORMAT__6 = new SimpleDateFormat("yyyyMMdd");
    public static final DateFormat SIMPLE_DATE_FORMAT__7 = new SimpleDateFormat("HH:mm");

    public static final String UYSDB = "uysdb";
    public static final String COMMON = "common";

    //A
    public static final String ADMIN_QUERY = "adminQuery";
    public static final String ADMIN_ROLE = "adminRole";
    public static final String ADMIN_METADATA = "adminMetadata";
    public static final String ACCEPT = "accept";
    public static final String ACCESS_CONTROL = "accesscontrol";
    public static final String ACTIVE = "active";
    public static final String ACTIVITY_STATUS = "activity_status";
    public static final String FORMACTIONS = "actions";
    public static final String ACTION = "action";
    public static final String AJAX_ACTION = "action";
    public static final String AJAX_UPDATE = "ajax_update";
    public static final String AJAX = "ajax";
    public static final String AJAX_EFFECTED_KEYS = "effected_keys";
    public static final String APPLY_TO_QUERY = "applyToQuery";
    public static final String APPLY_LIST_OF_OBJECTIDS = "applyListOfObjectIds";
    public static final String CHECK_AND_WRITE_CONTROL_RESULT_FUNCTION = "checkAndWriteControlResultFunc";
    public static final String EVENT_ENABLE = "event-enable";
    public static final String ATTACHMENTS = "ekler";
    public static final String ATTACHMENTS_1 = "ek1";
    public static final String ATTRIBUTES = "attributes";
    public static final String AUTOSET = "autoset";
    public static final String ACCESS_CONTROL_LEVEL_TWO = "accessControlLevelTwo";

    //B
    //C
    public static final String CALCULATE_ON_LIST_VIEW = "on-list-view";
    public static final String CALCULATE_ON_CRUD_VIEW = "on-crud-view";
    public static final String CALCULATE_ON_POST = "on-post";
    public static final String CALCULATE_ON_SAVE = "on-save";
    public static final String CALCULATE_AFTER_SAVE = "after-save";
    public static final String CALCULATE_AFTER_DELETE = "after-delete";
    public static final String CALCULATE_ENGINE = "engine";
    public static final String CALCULATE_AREA = "calculateArea";
    public static final String CALCULATE_ENABLE = "enabled";
    public static final String CALCULATE = "calculate";
    public static final String CALCULATE_ACTION = "action";
    public static final String CALCULATE_ON_CLIENT = "on-client";
    public static final String CALCULATE_COLLECTION = "calculateCollection";
    public static final String CALCULATE_REF = "calculate-ref";
    public static final String CELL_VERSION = "cellVersion";
    public static final String CFG_TABLE_PROJECT = "project2";
    public static final String CHILDS = "childs";
    public static final String CODE = "code";
    public static final String COLLECTION = "collection";
    public static final String COLOR = "color";
    public static final String COLUMN_COUNT = "columnCount";
    public static final String COMMA = ",";
    public static final String COLLECTION_NAME = "collectionName";
    public static final String COMPONENTTYPE = "componentType";
    public static final String CONVERTER_PARAM = "converterParam";
    public static final String CONVERTER_INSTANCE = "converterInstance";
    public static final String CONVERTER_FORMAT = "converterFormat";
    public static final String CONVERTER_MIN_STR_LENGTH = "min-str-length";
    public static final String CONSTRAINTS = "constraints";
    public static final String CONTROL_FUNCTION = "controlFunction";
    public static final String CONTROL_RESULT = "controlResult";
    public static final String CONVERTER_TELMAN_STRING_CONVERTER = "TelmanStringConverter";
    public static final String CONVERTER_NUMBER_CONVERTER = "NumberConverter";
    public static final String CONVERTER_INTEGER_CONVERTER = "IntegerConverter";
    public static final String CONVERTER_MONEY_CONVERTER = "MoneyConverter";
    public static final String CONVERTER_SELECT_ONE_OBJECTID_CONVERTER = "SelectOneObjectIdConverter";
    public static final String CONVERTER_SELECT_ONE_STRING_CONVERTER = "SelectOneStringConverter";
    public static final String CONVERTER_JS_FUNCTION_CONVERTER = "JsFunctionConverter";
    public static final String CONVERTER_BSON_CONVERTER = "BsonConverter";
    public static final String CONVERTER = "converter";
    public static final String CONVERTER_VALUE = "converterValue";
    public static final String CONSTRAINT_ERROR = "ERROR";
    public static final String CONSTRAINT_WARNING = "WARNING";
    public static final String CONSTRAINT_ITEMS = "constraintItems";
    public static final String CONFIG_ATTR_ACTIONS = "actions";
    public static final String CONFIG_ATTR_CHECK_ALL = "checkAll";
    public static final String CONFIG_COLLECTIONS = "configCollections";
    public static final String CONFIG_DB = "configdb";
    public static final String CREATE_DATE = "createDate";
    public static final String CREATE_USER = "createUser";
    public static final String CREATE_SESSIONID = "createSessionId";
    public static final String CURRENT_RENDERED = "currentRendered";
    public static final String COLLECTION_SYSTEM_MESSAGES = "appmessages"; // https://support.tspb.org.tr/issues/4603
    public static final String CUSTOM_RESOURCE_PDFTOOL = "PDFTOOL";
    public static final String CONTROL_COLLECTION = "controlCollection";
    public static final String COLUMN_LIST = "columList";
    public static final String CRUD_OBJECT_ID = "crud_object_id";

    //D
    public static final String DESCRIPTION = "description";
    public static final String POPUP_DESCRIPTION = "popupDesc";
    public static final String DEFAULT_SORT_FIELD = "defaultSortField";
    public static final String DEFAULT_VALUE = "defaultValue";
    public static final String DEFAULT_QUERY = "defaultQuery";
    public static final String DEBUG_MODE = "DEBUG_MODE";
    public static final String DELEGATE_DB_NAME = "DELEGATE_DB_NAME";
    public static final String DELEGATE_TABLE_NAME = "DELEGATE_TABLE_NAME";
    public static final String DELEGATING_FORM_FIELD_NAME = "DELEGATING_FORM_FIELD_NAME";
    public static final String DELEGATING_MEMBER_FIELD_NAME = "DELEGATING_MEMBER_FIELD_NAME";
    public static final String DELEGATED_MEMBER_FIELD_NAME = "DELEGATED_MEMBER_FIELD_NAME";
    public static final String FORM_DIMENSION = "DIMENSION";
    public static final String DIMENSION_LOWER_CASE = "dimension";
    public static final String DEFAULT_CURRENT_QUERY = "defaultCurrentQuery";
    public static final String DEFAULT_HISTORY_QUERY = "defaultHistoryQuery";
    public static final String DISABLED = "disabled";
    public static final String DOT = ".";
    public static final String FORM_DB = "db";
    public static final String DB_EIMZA = "eimzadb";
    public static final String DROOLS_VERTICAL = "droolsVertical";
    public static final String DROOLS_HORIZONTAL = "droolsHorizontal";
    public static final String DROOLS_RULE_COORDINATE = "droolsRuleCoordinate";
    public static final String DROOLS_MEASURE_KEY = "droolsMeasureKey";
    public static final String DATE_RANGE_CONTROL = "dateRangeControl";
    public static final String DATE_RANGE_BEGIN_KEY = "dateRangeBeginKey";
    public static final String DATE_RANGE_END_KEY = "dateRangeEndKey";
    public static final String DATE_RANGE_VALIDATE = "dateRangeValidate";
    public static final String DIVIDER = "divider";

    //E
    public static final String ERROR = "Hata";
    public static final String EDIT_RECORD_TYPE = "Kayıt Tipi Güncelleme";
    public static final String ELEMENT = "element";
    public static final String EDIT_NODE = "Node Güncelleme";
    public static final String EDIT_FIELD = "Alan Güncelleme";
    public static final String ENABLED = "enabled";
    public static final String ENABLE = "enable";
    public static final String ENGINE = "engine";
    public static final String EVENT_POST_DELETE = "eventPostDelete";
    public static final String EVENT_PRE_SAVE = "eventPreSave";
    public static final String EVENT_PRE_DELETE = "eventPreDelete";
    public static final String EVENT_POST_SAVE = "eventPostSave";
    public static final String EVENT_FORM_SELECTION = "eventFormSelection";
    public static final String ESIGN_EMAIL_TO_RECIPIENTS = "esignEmailToRecipients";
    public static final String ESIGN_EMAIL_BCC_RECIPIENTS = "esignEmailBccRecipients";
    public static final String EMAIL = "email";
    public static final String EMAIL_TO_ALL = "emailToAll";
    public static final String EIMZA = "eimza";
    public static final String ESIGN_PROVIDER = "esign_provider";
    public static final String EXPRESSION = "expression";
    public static final String EXCEL_FORMAT = "excelFormat";

    //F
    public static final String FIRSTANME = "firstname";
    public static final String FIELD_NOTE = "fieldNote";
    public static final String FORM = "form";
    public static final String FORM_EVENTS = "events";
    public static final String FORM_TYPE = "formType";
    public static final String FORM_NAME = "formName";
    public static final String FORMFIELDS = "fields";
    public static final String FORM_CHILD_FIELDS = "child-fields";
    public static final String FIELDS_ROW = "fieldsRow";
    public static final String FIELD = "field";
    public static final String FIELD_KEY = "fieldKey";
    public static final String FORMS = "forms";
    public static final String FORMAT = "format";
    public static final String FUNC = "func";
    public static final String FUNC_NOTE = "funcNote";
    public static final String FILENAME = "filename";
    public static final String FILE_TYPE = "fileType";
    public static final String FILETYPE = "DOCX";
    public static final String FULL_TEXT_SEARCH_MISTAKE = "full_text_seacrh";
    public static final String FULL_TEXT_SEARCH = "full_text_search";
    public static final String FILE_ID = "fileID";
    public static final String FILE_NAME = "fileName";
    public static final String FILE_SIZE = "fileSize";
    public static final String FILE_STATE = "fileState";
    public static final String FILE_COLOR = "fileColor";
    public static final String FILE_LIMIT = "fileLimit";
    public static final String FILE_EXTENSION_PDF = ".pdf";
    public static final String FILE_EXTENSION_DOCX = ".docx";
    public static final String FILE_EXTENSION_JASPER = ".jasper";
    public static final String FAIL_MESSAGE = "failMessage";
    public static final String FILTER_PROJECTION = "filter-projection";

    //G
    public static final String GROUP = "group";
    public static final String GUI = "gui";
    public static final String GUI_X = "x";
    public static final String GUI_Y = "y";

    //H
    public static final String HAYIR = "HAYIR"; //
    public static final String HTTP_SCHEME = "http"; //
    public static final String HISTORY_QUERY = "historyQuery";
    public static final String HISTORY_POSITION = "historyPosition";
    public static final String HISTORY_RENDERED = "historyRendered";
    public static final int HTTP_DEFAULT_PORT = 80;
    public static final int HTTPS_DEFAULT_PORT = 443;
    public static final String HANDSON_ROW_HEADER_WIDTH = "handsonRowHeaderWidth";
    public static final String HANDSON_COL_WIDTH = "handsonColWidths";
    public static final String HTTP_SESSION_ATTR_MAP_REQURED_CONTROL = "mapRequiredControl";

    //I
    public static final String INPUT = "input";
    public static final String INPUT_FILE = "inputFile";
    public static final String IMMEDIATE = "immediate";
    public static final String ITEMS = "items";
    public static final String IS_AJAX = "isAjax";
    public static final String IMPORT_TEXT_FROMAT = "importTextFormat";
    public static final String INODE = "inode";
    public static final String INCLUDE = "include";

    //J
    public static final String JSF_ATTR_PATTERNNAME = "patternName";
    public static final String JEVAL_RETURN_TYPE = "jevalRetunType";

    //K
    public static final String FORM_KEY = "key";

    //L
    public static final String LDAP_UID = "uid";
    public static final String LABEL = "label";
    public static final String LIMIT = "limit";
    public static final String LIST_OF_FUNCTIONS = "listOfFunctions";
    public static final String LOCAL = "LOCAL";
    public static final String LOCALE = "locale";
    public static final String LOGGED_USER_ROLES = "loggedUserRoles";
    public static final String LOGGED_USER = "loggedUser";
    public static final String LABEL_STRING_FORMAT = "labelStringFormat";
    public static final String LIST_OF_VALUES = "listOfValues";
    public static final String LASTNAME = "lastname";
    public static final String LDAP_USERS_DN = "LDAP_USERS_DN";
    public static final String LDAP_ROLES_DN = "LDAP_ROLES_DN";
    public static final String LDAP_ADMIN = "LDAP_ADMIN";
    public static final String LDAP_WHATISIT = "LDAP_PASSWORD";
    public static final String LDAP_URL = "LDAP_URL";
    public static final String LOGIN_FK = "loginFkField";
    public static final String LABEL_STYLE = "labelStyle";
    //M
    public static final String MAIN_PROJECT_CODE = "MAIN_PROJECT_CODE";
    public static final String MASK = "mask";
    public static final String MAX_MONEY = "maxMoney";
    public static final String MAX_VALUE = "maxValue";
    public static final String MEMBER = "member";
    public static final String MEMBER_ID = "memberID";
    public static final String METADATA = "metadata";
    public static final String MENU_ORDER = "menuOrder";
    public static final String MEASURE = "MEASURE";
    public static final String MEMBER_TYPE = "memberType";
    public static final String MEMBER_NAME = "memberName";
    public static final String MEMBER_CODE = "memberCode";
    public static final String MESSAGE = "message";
    public static final String MESSAGE_DIALOG = "messageDialog";
    public static final String MONEY = "money";
    public static final String MONGO_LDAP_UID = "ldapUID";
    public static final String MODUL = "modul";
    public static final String MY_NAMED_QUERIES = "myNamedQueries";
    public static final String MY_FORM_GROUPS = "myFormGroups";
    public static final String MY_CONVERTER = "myconverter";
    public static final String MY_DATE_PATTERN = "datePattern";
    public static final String MULTIPLE = "MULTIPLE";
    public static final String MONGODB_MAIN_PROJECT_CODE = "MONGODB_LDAP_PROJECT_CODE";
    public static final String MIN_FRACTATION_DIGITIS = "minFractationDigits";
    public static final String MAX_FRACTATION_DIGITIS = "maxFractationDigits";
    public static final String MYACTION = "myaction";
    public static final String MY_ACTION_TYPE = "myActionType";
    public static final String JAVA_FUNC = "javaFunc";

    //N
    public static final String NAME = "name";
    public static final String NEW_RECORD_TYPE = "Yeni Kayıt Tipi";
    public static final String NEW_NODE = "Yeni Node";
    public static final String NEW_FIELD = "Yeni Alan";
    public static final String NODE = "node";
    public static final String NOTE = "note";
    public static final String ND_TYPE = "ndType";
    public static final String ND_AXIS = "ndAxis";
    public static final String ND_FUNCTION = "ndFunction";
    public static final String ND_FUNCTION_SET = "ndFunctionSet";
    public static final String NOT_ON_USER_ROLE = "notOnUserRole";//see ON_USER_ROLE

    //O
    public static final String OBSERVABLE = "observable";
    public static final String OBSERVER = "observer";
    public static final String ON_USER_ROLE = "onUserRole";//see NOT_ON_USER_ROLE
    public static final String ORDER = "order";
    public static final String OPERATOR_LDAP_UID = "operatorLdapUID";

    //P
    public static final String PPOLICY = "ppolicy";
    public static final String PARENT = "parent";
    public static final String PRODUCT = "PRODUCT";
    public static final String PERIOD = "period";
    public static final String PROJECT_KEY = "projectKey";
    public static final String PROJECT_CODE = "projectCode";
    public static final String PRIORITY = "priority";
    public static final String PATH_JASPER_JRXML = "/home/fms/myjaspers";
    public static final String PATH_UYS_EXPORT = "EXPORT_PATH";
    public static final String PATH_PDF_TOOLS = "/home/fms/pdftool/";
    public static final String PRESENT = "present";

    //Q
    public static final String QUERY = "query";
    public static final String QUERY_CODE = "queryCode";

    //R
    public static final String READONLY = "readonly";
    public static final String RELATIONS = "relations";
    public static final String ROLECHECK = "roleCheck";
    public static final String RENDERED = "rendered";
    public static final String REQUIRED = "required";
    public static final String REPORT_RENDERED = "reportRendered";
    public static final String REPORT_ORDER = "reportOrder";
    public static final String RESET = "reset";
    public static final String RESULT_TYPE = "resultType";
    public static final String RESULT = "result";
    public static final String READ_ONLY_NOTE = "readOnlyNote";
    public static final String RETVAL = "retval";
    public static final String RETURN_KEY = "returnKey";
    public static final String REF_COLLECTION = "refCollection";
    public static final String ROW_LIST = "rowList";
    public static final String REGISTRED_FUNC_NAME = "registred-func-name";
    public static final String RELATIONS_PRESENTATION = "relationsPresentation";

    //S
    public static final String ACTION_CHECK_ALL = "checkAll";
    public static final String ACTION_NEW = "new";
    public static final String ACTION_DOWNLOAD = "download";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_SEND_FROMS = "sendForms";
    public static final String ACTION_NORECORD = "norecord";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_SAVE_AS = "saveAs";
    public static final String SELECT_PLEASE = "Lütfen Seçiniz";
    public static final String SELECT_ALL = "Tümü";
    public static final String SELF = "self";
    public static final String SELECTED_FORM_KEY = "selectedFormKey";
    public static final String SELECT_FORM_KEY = "selectFormKey";
    public static final String SELECTED_FIELD_KEY = "selectedFieldKey";
    public static final String SEARCH_FORM = "searchForm";
    public static final String FORM_FILTER = "filter";
    public static final String QUICK_FILTER = "quickFilter";
    public static final String SEARCH_ACCESS = "search-access";
    public static final String SESSION_ATTR_SELECTED_FORM = "session_attr_selected_form";
    public static final String SHORT_NAME = "shortName";
    public static final String PAGE_NAME = "pageName";
    public static final String SHOW_HISTORY = "showHistory";
    public static final String SHOULD_CHECK_NEGOTIF = "shouldCheckNegative";
    public static final String SNAPSHOT_COLLECTION = "snapshotCollection";
    public static final String SORT = "sort";
    public static final String SPLITTER = "splitter";
    public static final String STYLE = "style";
    public static final String STYLE_CLASS = "styleClass";
    public static final String SUB_GROUP = "subGroup";
    public static final String SUB_GROUPS = "subGroups";
    public static final String SUCCESS_MESSAGE = "successMessage";
    public static final String STATE = "state";
    public static final String SHARE_AMOUNT = "shareAmount";
    //T
    public static final String TAMAM = "TAMAM";
    public static final String HATA_VAR = "HATA VAR";
    public static final String TEST = "TEST";
    public static final String TDUB = "TDUB";
    public static final String TEMPLATE = "template";
    public static final String TREQUIRED = "trequired";
    public static final String TO_BE_CALCULATED_COORDINATES = "toBeCalculatedCoordinates";
    public static final String TRANSFER_ORDER = "transferOrder";
    public static final String TC_NO = "tcno";
    public static final String TELEPHONE = "telefon";
    public static final String TITLE = "TITLE";
    public static final String TITLE_ACRONYM = "TITLE_ACRONYM";
    public static final String TYPE = "type";
    //U
    public static final String UPPER_NODES = "upperNodes";
    public static final String USER_NOTE = "userNote";
    public static final String USER_ROLE = "userRole";
    public static final String UNIQUE = "UNIQUE";
    public static final String UNIQUE_INDEX_LIST = "uniqueIndexList";
    public static final String UYSFORMAT = "uysformat";
    public static final String USER_CONSTANT_NOTE = "user-constant-note";
    public static final String USER_CONSTANT_NOTE_LIST = "userConstantNoteList";
    public static final String UPLOAD_DATE = "uploadDate";
    public static final String UPLOAD_MERGE_FIELDS = "uploadMergeFields";
    public static final String UPLOAD_CONFIG = "upload-config";
    public static final String UPDATE_DATE = "updateDate";
    public static final String UPDATE_USER = "updateUser";
    public static final String UPSERT_DATE = "upsertDate";
    public static final String UYS_EASY_FIND_KEY = "uysEasyFindKey";
    //V
    public static final String VALUES = "values";
    public static final String VALUE = "value";
    public static final String VALUE_KEY = "valueKey";
    public static final String VALUE_CHANGE_LISTENER_ACTION = "valueChangeListenerAction";
    public static final String VALUE_TYPE = "valueType";
    public static final String VARIABLES = "variables";
    public static final String VIEW = "v";
    public static final String VISIBLE = "visible";
    public static final String VERSION_HISTORY = "versionHistory";
    public static final String VERSION = "version";
    public static final String V_AXIS = "V_AXIS";//VALUE AXIS
    public static final String VIEWER_ROLE = "viewerRole";
    //W
    public static final String WARNING = "Uyarı";
    public static final String WELCOME_PAGE = "welcomePage";
    public static final String WORKFLOW = "workflow";
    //X
    public static final String X_IKS = "X-IKS";
    public static final String X_CODE = "xCode";
    //Y
    public static final String Y_CODE = "yCode";
    public static final String Y_IGREK = "Y-IGREK";
    //Z
    public static final String Z_ZET = "Z-ZET";
    public static final String ZET_DIMENSION = "zetDimension";
    //PUNCTATION
    public static final String DIEZ = "#";
    public static final String DOLAR = "$";
    public static final String DOLAR_EXISTS = "$exists";
    public static final String DOLAR_GTE = "$gte";
    public static final String DOLAR_LTE = "$lte";
    public static final String DOLAR_IN = "$in";
    public static final String DOLAR_NIN = "$nin";
    public static final String DOLAR_NE = "$ne";
    public static final String DOLAR_NOT = "$not";
    public static final String DOLAR_ADDTOSET = "$addToSet";
    public static final String DOLAR_PULL = "$pull";
    public static final String DOLAR_PUSH = "$push";
    public static final String DOLAR_SET = "$set";
    public static final String DOLAR_UNSET = "$unset";
    public static final String DOLAR_REGEX = "$regex";
    public static final String DOLAR_OPTIONS = "$options";
    public static final String DOLAR_TEXT = "$text";
    public static final String DOLAR_SEARCH = "$search";
    public static final String DOLAR_LANGUAGE = "$language";
    public static final String MONGO_ID = "_id";
    public static final String MONGO_UNIQUE = "unique";
    public final static String UYS_SELECTED_FROM = "uysSelectedForm";
    public final static String UYS_DOWNLOAD_READER = "uysDownloadReader";
    public final static String EXCEL_FORMAT_PATH = "excelFormatPath";
    public final static String EXCEL_LIBRARY_POI = "excelLibraryPoi";
    public final static String UYS_ND_ROW_SIZE = "uysNdRowSize";
    public final static String UYS_DOWNLOAD_ROLES = "uys_download_roles";

    public final static String JAVALANG_STRING = "java.lang.String";
    public final static String JAVALANG_DATE = "java.lang.Date";
    public final static String JAVAUTIL_DATE = "java.util.Date";

    // CONCATS
    public static final String METADATA_SELECTED_FORM_KEY = METADATA.concat(DOT).concat(SELECT_FORM_KEY);
    public static final String METADATA__SELECTED_FORM_KEY = METADATA.concat(DOT).concat(SELECTED_FORM_KEY);
    public static final String METADATA_CRUD_OBJECT_ID = METADATA.concat(DOT).concat(CRUD_OBJECT_ID);
    public static final String METADATA_USERNAME = METADATA.concat(DOT).concat("username");
    public static final String METADATA__MEMBER_NAME = METADATA.concat(DOT).concat(MEMBER_NAME);
    public static final String METADATA__SOURCE_TEXT = METADATA.concat(DOT).concat("sort_text");
    public static final String MSG_FORMAT_CONTROL_FORMULA = "".concat("<table>")
            .concat("<tr><td>kontrol kodu</td><td>:</td><td>{0}</td></tr>")
            .concat("<tr><td>ad</td><td>:</td><td>{1}</td></tr>")
            .concat("<tr><td>işlemci</td><td>:</td><td>{2}</td></tr>")
            .concat("</table>");

}
