package com.americana.common.utils;

public class QRConstants {
    public static final int GPS_RESULT = 900;
    public static final int LOCATION_REQ_CODE = 901;
    public static final String PNG_EXTENTION = ".png";
    public static final String DELIMITER = "#";
    public static final String DEFAULT_TAG = "0";
    public static final float DEFAULT_MIN_ORDER_AMOUNT = 23.0f;
    public static final String DEFAULT_CUSTOMER_CARE_NUMBER = "987654321";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String REFRESH_TOKEN = "refreshToken";
    public static final String REFRESH_HOME = "refreshHome";
    public static final int APP_UPDATE_REQUEST_CODE = 999;
    public static final long IMAGE_REFRESH_TIME_MILLI_SECONDS = 2 * 60 * 60 * 1000; // by default time in milli seconds (2 hours).
    public static final String SUPPORT_EMAIL_SUB = "KFC SUPER APP FEEDBACK";
    public static final String HIDDEN_CATEGORY = "1";
    public static final int MAX_MENU_COUNT = 6;
    public static final String NO_RESULTS = "No Results";
    public static final String COMMON_UNDERSCORE = "_";
    public static final String COMMON_BACKSLASH = "/";
    public static final String PNG_EXTENSION = ".png";
    public static final String FLAG_START_NAME = "icon_countryflag";
    public static final int GUEST = 1;
    public static final String DATA = "data";
    public static final String ONBOARDING = "ONBOARDING";
    public static final String OTP_VERIFIED = "otp_verified";
    public static final String UPSELL_CATEGORY_CODE = "Upsell";
    public static final String ADDRESS_UPDATE_AFTER_CART = "address_update_after_cart";
    public static final int GOOGLE_PAY_ACTIVITY_RESULT = 1;
    public static final int DECIMAL_DIGITS_COUNT = 2;
    public static final String BACK_FROM_VIDEO = "BACK_FROM_VIDEO";
    public static String COUNTRY = "country";
    public static String PHONE_NUMBER = "phone_number";
    public static String COUNTRY_CODE = "country_code";
    public static String IS_NUMBER_ALREDY_EXIST = "IS_NUMBER_ALREDY_EXIST";
    public static String IS_FROM_ADD_NUMBER = "IS_FROM_ADD_NUMBER";
    public static String IS_FROM_CHECKOUT = "IS_FROM_CHECKOUT";
    public static String COUNTRY_TYPE = "country";
    public static String REORDER_SHORT_CUT_ID = "REORDER_SHORT_CUT_ID";
    public static String REORDER_DATA = "REORDER_DATA";
    public static String REORDER_ACTION = "REORDER_ACTION";
    public static String REORDER_PRICE = "REORDER_PRICE";
    public static String FOOD_INST = "FOOD_INST";
    public static int ONBOARDING_BACK_TO_CHECKOUT = 1122;


    public interface StoreFailureCode {
        int NOT_DELIVER_HERE = 409;
        int STORE_OFFLINE = 411;
    }

    public interface AmountAddedOrSubtract {
        String ADD = "add";
        String SUBTRACT = "subtract";
    }

    public interface Language {
        String ENGLISH = "En";
        String ARABIC = "Ar";
    }


    public interface HttpCode {
        int EMAIL_EXISTS = 216;
        int PHONE_EXISTS = 215;
    }

    public interface Ui {
        long CLICK_DEBOUNCE_MILLIS = 300;
    }

    public interface ERROR {
        String NO_INTERNET_ERROR = "NO_INTERNET_ERROR";
        String MENU_NOT_CHANGED = "MENU_NOT_CHANGED";
    }

    public interface Network {
        int ORDER_STATUS_FETCH_INTERVAL = 20;
        int ETAG_API_TYPE_HOME = 0;
        int ETAG_API_TYPE_MENU = 1;
        int ETAG_API_LOCATION = 2;
    }

    public interface ProgressBarConstants {
        int SHOW_PROGRESS = 1;
        int HIDE_PROGRESS = 2;
    }

    public interface AmountType {
        String TOTAL = "TOTAL";
        String SUB_TOTAL = "SUB_TOTAL";
        String FLAT = "FLAT";
        String TAX = "TAX";
        String DISCOUNT = "DISCOUNT";
        String SHIPPING = "SHIPPING";
    }

    public interface IntentKeys {
        String CATEGORY_NAME = "CATEGORY_NAME";
        String REDIRECTION_ACTION = "redirect_action";
        String CATEGORY_ID = "category_id";
        String PRODUCT_ID = "product_id";
        String LINK_TYPE = "linkType";
        String SOURCE = "source";
        String DATA = "data";
        String SCREEN = "screen";
        String FOR_EDIT = "forEdit";
        String IS_LOGIN = "is_login";
        String CURRENT_LANGUAGE = "current_language";
        String NOONPAY_URL = "noonpayUrl";
        String ORDER_ID = "orderId";
        String ORDER = "order";
        String NEW_CART_ID = "newcARTiD";
        String IS_PAYMENT_SUCCESS = "isPaymentSucess";
        String FROM_SIDE_MENU = "from_side_menu";
        String VIDEO_URL = "video_url";
        String ORDER_STATUS = "order_status";
        String ORDER_FOR = "order_for";
        String ORDER_FAILED = "order_failed";
        String RATING = "rating";
        String HIGHLIGHT_PRODUCT = "highlight_product";
        String WEB_LINK = "web_link";
        String SCROLL_TO_ADDON = "scroll_to_addon";
        String CART_PRICE = "CART_PRICE";

        String LATLNG = "latLng";
        String IS_LOCATION_VALID = "isLocValid";
        String IN_APP_LINK = "in_app_link";
        String COUPON_VALID = "COUPON_VALID";
    }

    public interface ProductType {
        String SIMPLE = "simple";
        String CONFIGURABLE = "configurable";
        String BUNDLE = "bundle";
        String BUNDLE_GROUP = "bundle_group";
    }

    public interface NavigatedFrom {
        int ONBOARDING = 1;
    }

    public interface LanguageConstants {
        String ARABIC = "Ar";
        String ENGLISH = "En";
    }


    public interface DealsOffersViewType {
        int DEALS_OFFERS_ITEM = 0;
        int LOADER_ITEM = 1;
    }

    public interface FavouriteConstants {
        int LIKED = 1;
        int NOT_LIKED = 0;
    }

    public interface AddressType {
        String PICKUP = "PICKUP";
        String DELIVERY = "DELIVERY";
        String DINE_IN = "DINEIN";
        String DRIVE_THRU = "DRIVETHRU";
    }

    public interface SubAddressType {
        String DELIVERY = "DELIVERY";
        String STORE = "STORE";
        String CARHOP = "CARHOP";
        String DINE_IN = "DINEIN";
        String DRIVE_THRU = "DRIVETHRU";
    }

    public interface SocialLoginMedium {
        String GOOGLE = "GOOGLE";
        String FB = "FB";
    }

    public interface ScreenNameForFirebaseEvents {
        String HOME_PAGE = "HomePage";
        String MENU_PAGE = "MenuPage";
        String ORDER_STATUS = "OrderStatus";
        String TRACK_ORDER = "TrackOrder";
        String CART_PAGE = "CartPage";
        String SIDE_MENU = "SideMenu";
    }

    public interface ProductDetailViewType {
        String RADIO = "radio";
        String CHECKBOX = "checkbox";
        String STEPPER = "stepper";
    }

    public interface CTActions {
        String MENU = "menu";
        String VIDEO = "video";
        String PRODUCT = "product";
        String CATEGORY = "category";

        String CART = "cart";
        String FAVOURITE = "favourite";
        String CARHOP = "pickup_carhop";
        String PICKUP = "pickup_store";
    }

    public interface MediaType {
        String IMAGE = "image";
        String GIF = "gif";
        String VIDEO = "video";

    }


    public interface AddressTagType {

        String HOME_EN = "home";
        String OFFICE_EN = "Office";
        String OTHER_EN = "Other";
        String HOTEL_EN = "Hotel";

        String HOME_AR = "المنزل";
        String OFFICE_AR = "المكتب";
        String OTHER_AR = "آخر";
        String HOTEL_AR = "الفندق";

    }


    public interface FirebaseEvents {
        String UPDATE = "Update";
        String Menu = "Menu";
        String FAILURE = "Failure";
        String DRIVE_THRU = "DriveThru";
        String DINE_IN = "DineIn";
        String INVALID_STORE_ID = "InvalidStoreID";
        String STORE_ID_CARHOP = "StoreIDScanOnCarhop";
        String STORE_ID_PICKUP = "StoreIDOnSelfPickup";
        String STORE_ID_DINE_IN = "StoreIDScanOnDineIn";
        String INVALID_QR_SCAN = "Invalid QR Scan";
        String NO_ADJUST_URL = "AdjustURLNotPresent";
        String NO_DEEP_LINK = "NoDeeplink";
        String VALID_QR = "valid QR Scan";
        String QR_FROM_GALLERY = "UploadFromGallery";
        String SCAN_QR = "ScanQR";
        String QR_ALIGNMENT = "ImproperQRAlignment";
        String QR_SCAN = "QRScan";
        String QR_ON_CARHOP = "Carhop";
        String QR_ON_DINEIN = "DineIn";
        String QR_ON_PICKUP = "SelfPickup";
        String QR_ON_DRIVE_THRU = "DriveThru";
    }


    public class DeepLinkConstants {
        public static final String DEEP_LINK_TYPE = "type";
        public static final String DEEP_LINK_COUNTRY_ID = "countryId";
        public static final String DEEP_LINK_STORE_ID = "storeId";
        public static final String DEEP_LINK_STORE_NO = "sdmStoreNo";
        public static final String DEEP_LINK_STORE_NO_ = "sdmStoreno";
        public static final String DEEP_LINK_AUTO_APPLY_COUPON = "autoApplyCoupon";
        public static final String DEEP_LINK_EXTRA_DETAILS = "extraDetails";
        public static final String DEEP_LINK_DATA = "deepLinkData";
        public static final String DEEP_PICK_UP_HOME = "deepPickUpHome";
        public static final String DEEP_SUB_MENU_ID = "submenuId";
        public static final String DEEP_PRODUCT_ID = "productId";


        public static final String TYPE_CARHOP = "carhop";
        public static final String TYPE_FROM_STORE = "fromStore";
        public static final int ENABLE_QR_CODE = 1;

        public static final String EVENT_ACTION_COUNTRY_NAME = "CountryName";
        public static final String EVENT_ACTION_STORE_ID = "StoreID";
        public static final String EVENT_LABEL_DEFERRED_DEEPLINK = "Deferred deeplink";
        public static final String EVENT_LABEL_DEEPLINK = "deeplink";
        public static final String TYPE_DINE_IN = "DineIn";
        public static final String TYPE_DRIVE_THRU = "driveThru";
    }


    public interface QREventCategory {
        String QR_ON_HOME = "OnHome";
    }

    public interface QREventAction {
        String CLICK = "Click";
        String EnterStoreID = "EnterStoreID";
        String QR_SCAN = "QRScan";
        String UPLOAD_IMAGE = "UploadImage";
    }

    public interface QREventLabel {
        String VALID_STORE_ID = "ValidStoreID";
        String INVALID_STORE_ID = "InValidStoreID";
        String VALID_QR = "ValidQR";
        String INVALID_QR = "InValidQR";
    }

    public interface MenuConstants {
        String CART = "CART";
    }

    public interface IDeepLink {
        int HOME = 1, CATEGORY = 2, PRODUCT = 3, CART = 4, CUSTOMIZE = 5, ORDER_HISTORY = 6, TRACK_ORDER = 7, OFFERS = 8, HOME_APPLY_COUPON = 9, CHECKOUT = 10, ADD_TO_CART = 11, HIGHLIGHT_PRODUCT = 12, DEEP_PICK_UP_HOME = 13, DEEP_LINK_SCANNER_PICK_UP_HOME = 14, SCAN_QR = 15;
//        String DEEP_LINK_SCHEMA = "kfcbh";
        String DEEP_HOME = "://home", DEEP_CATEGORY = "://submenu?submenuId",
                DEEP_PRODUCT = "://product?submenuId", DEEP_CART = "://cart",
                DEEP_CUSTOMIZE_PRODUCT = "kfcbh://customize", DEEP_ORDER_HISTORY = "://orderhistory",
                DEEP_TRACK_ORDER = "://trackorder", DEEP_OFFERS = "://offers",
                DEEP_CHECKOUT = "://checkout", DEEP_ADD_TO_CART = "://addToCart",
                DEEP_HIGHLIGHT = "://highlight", PICK_UP_HOME = "://pickupHome", DEEP_SCAN_QR = "://qrscanner";
    }

    public class TextConstants {
        public static final String MIN_CART_VALUE = "${minCartValue}";
        public static final String SUBTOTAL = " ${cartValue}";
        public static final String SERVICE_TYPE = "{service_type}";
        public static final String ADDRESS_TYPE = "${serviceType}";
    }
}