package com.americana.common.qrcodereader;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Handler;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.americana.common.utils.QRConstants;
import com.americana.common.utils.TextUtils;

import java.net.URISyntaxException;

/**
 * Use of class
 * <<
 * This class is used to apply redirection of url received from qr code
 * Redirection will perform with the help of web view and events will handle in its override methods
 * This class used to get all the data from redirection url and pass it to required place
 * >>
 */

public class ExtractQRRedirectUrl {
    //interface instance to handle events
    private ExtractQRRedirectUrlCallbacks callbacks;
    //handler instance
    private Handler mHandler;
    //runnable instance
    private Runnable mRunnable;
    //constant variable used to apply delay time on handler to be trigger
    private final long WAIT_DURATION = 10000;
    //web view instance reference used in redirection of deep link url
    private WebView webView;
    //web view instance reference used in redirection of mob api
    private WebView mobWebView;
    // contant variable that contains rebrandly schema for mob api redirection
    private final String REBRANDLY_SCHEMA_FOR_MOB_API_REDIRECTION = "https://rebrand.ly/";
    // contant variable that contains mob api schema
    private final String MOB_API_SCHEMA = "mobapi.americana-food.com";
    // variable used to store DEEP link schema
    private String DEEP_LINK_SCHEMA;


    /**
     * Constructor
     *
     * @param callbacks interface instance to get call back
     */
    public ExtractQRRedirectUrl(ExtractQRRedirectUrlCallbacks callbacks) {

        this.callbacks = callbacks;
    }

    public ExtractQRRedirectUrl() {
    }


    /**
     * Use of interface
     * << This interface will use to trigger event of this class >>
     * { QRCodeReaderView} all the callback will handle in #QRCodeReaderView
     */

    public interface ExtractQRRedirectUrlCallbacks {
        void onGetExtractedQRData(Intent intent, boolean isFromGallery, String url);//url is actual deep link after successfull scanned from QR

        void onGetExtractQRError();

        void onUnableToExtractQR();

        void fireQREvents(String eventLabel, String eventCategory, boolean isNoOrderMode);

        void onfireQREvents(String eventLabel, String qrActions, String deeplink);

        void notBelongToKFC(boolean isFromGallery, String deepLinkUrl);
    }

    /**
     * Use of method
     * << This method will call to  get actual deeplink url >>
     *
     * @param redirectUrl    this is first parameter used to perform redirection to get actual deep link
     * @param context        this is second parameter used to access resources
     * @param isFromGallery  this is third parameter used to trigger firebase event
     * @param currentCountry this is used to identify the current selected country
     * @param deepLinkScheme this is used to identify the app schema
     */


    public void extractDeepLink(String redirectUrl, Context context, boolean isFromGallery, String currentCountry, String deepLinkScheme) {
        this.DEEP_LINK_SCHEMA = deepLinkScheme;
        if (TextUtils.isEmpty(redirectUrl)) {
            if (callbacks != null) {
                callbacks.notBelongToKFC(isFromGallery, redirectUrl);
            /*    if(isFromGallery)
                    callbacks.fireQREvents(AppConstants.FirebaseEvents.NO_DEEP_LINK, AppConstants.FirebaseEvents.QR_FROM_GALLERY,false);
                else
                    callbacks.fireQREvents(AppConstants.FirebaseEvents.NO_DEEP_LINK,null,false);
*/
            }
            return;
        } else if ((redirectUrl.startsWith("http") || redirectUrl.startsWith("https")) && !isBelongToKFC(redirectUrl, deepLinkScheme, currentCountry)) // load url in webview to get redirect url only if extracted url is web url
        {
            loadWebView(context, redirectUrl, isFromGallery, currentCountry);
        } else {
            readValueFromRedirectUrl(redirectUrl, isFromGallery, false, redirectUrl, currentCountry);
        }

    }

    /**
     * Use of method
     * << This method will call to load url in web view to get actual deeplink url or mob api url >>
     *
     * @param context        this is first parameter used to access resources
     * @param redirectUrl    this is second parameter used to perform redirection to get actual deep link
     * @param isFromGallery  this is third parameter used to trigger firebase event
     * @param currentCountry this is used to identify the current selected country
     */

    private void loadWebView(Context context, String redirectUrl, boolean isFromGallery, String currentCountry) {
        if (webView == null)
            webView = new WebView(context);
        registerHandler(isFromGallery, redirectUrl);
        webView.loadUrl(redirectUrl);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                removeHandlerCallBack();
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                removeHandlerCallBack();
                if (callbacks == null) {
                    if (webView != null)
                        webView.setWebViewClient(null);
                    return true;
                }
                if (webView != null)
                    webView.setWebViewClient(null);
                Uri uri = Uri.parse(url);
                if (uri != null && uri.getHost() != null && uri.getHost().equals(MOB_API_SCHEMA) && uri.getQueryParameterNames() != null && uri.getQueryParameterNames().contains("kfc")) {
                    stopWebViewLoading();
                    String mobRedirectUrl = getMobApiRedirectUrl(uri);
                    redirectMobUrlInWebView(context, mobRedirectUrl, isFromGallery, currentCountry);
                } else {
                    readValueFromRedirectUrl(url, isFromGallery, true, redirectUrl, currentCountry);
                    stopWebViewLoading();
                }
                return true;
            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                removeHandlerCallBack();
                super.onReceivedError(view, request, error);
                stopWebViewLoading();
                unableToExtrackQR(isFromGallery, false, redirectUrl);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                removeHandlerCallBack();
                super.onReceivedHttpError(view, request, errorResponse);
                stopWebViewLoading();
                unableToExtrackQR(isFromGallery, false, redirectUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                removeHandlerCallBack();
                super.onReceivedSslError(view, handler, error);
                stopWebViewLoading();
                unableToExtrackQR(isFromGallery, false, redirectUrl);
            }

        });

    }

    /**
     * Use of method
     * <<
     * This method will return redirection url from mobapi url
     * to get this url append rebrandly schema as prefix and append kfc parameter from uri
     * >>
     *
     * @param uri this is first parameter used to create url to perform redirection of mob api
     * @return It will return string url to apply redirection for mob api
     */

    public String getMobApiRedirectUrl(Uri uri) {

        return REBRANDLY_SCHEMA_FOR_MOB_API_REDIRECTION + uri.getQueryParameter("kfc");
    }


    /**
     * Method will call to load url in web view to get redirect url
     *
     * @param context        This is first parameter used to access resources
     * @param redirectUrl    This is second url used to perform redirection by using web view
     * @param isFromGallery  This is third parameter that used to trigger firebase event
     * @param currentCountry this is used to identify the current selected country
     */

    private void redirectMobUrlInWebView(Context context, String redirectUrl, boolean isFromGallery, String currentCountry) {

        if (mobWebView == null)
            mobWebView = new WebView(context);
        registerHandler(isFromGallery, redirectUrl);
        mobWebView.loadUrl(redirectUrl);
        WebSettings webSettings = mobWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mobWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                removeHandlerCallBack();
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                removeHandlerCallBack();
                if (callbacks == null) {
                    if (mobWebView != null)
                        mobWebView.setWebViewClient(null);
                    return true;
                }
                if (mobWebView != null)
                    mobWebView.setWebViewClient(null);
                readValueFromRedirectUrl(url, isFromGallery, true, redirectUrl, currentCountry);
                stopMobWebViewLoading();
                return true;
            }


            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                removeHandlerCallBack();
                super.onReceivedError(view, request, error);
                stopMobWebViewLoading();
                unableToExtrackQR(isFromGallery, false, redirectUrl);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                removeHandlerCallBack();
                super.onReceivedHttpError(view, request, errorResponse);
                stopMobWebViewLoading();
                unableToExtrackQR(isFromGallery, false, redirectUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                removeHandlerCallBack();
                super.onReceivedSslError(view, handler, error);
                stopMobWebViewLoading();
                unableToExtrackQR(isFromGallery, false, redirectUrl);
            }

        });

    }

    /**
     * Use of method
     * << This method will call to stop web view loading for mob api redirection url>>
     */

    private void stopMobWebViewLoading() {
        if (mobWebView != null)
            mobWebView.stopLoading();

    }

    /**
     * Use of method
     * << This method will call to trigger firebase event and update UI  when no deep link is found or not belong to kfc  >>
     *
     * @param isFromGallery This is first parameter used to trigger firebase event
     * @param isNoOrderMode This is second parameter used to trigger firebase event
     * @param redirectUrl   This is third parameter used to check url belong to rebrand.ly or not
     */

    private void unableToExtrackQR(boolean isFromGallery, boolean isNoOrderMode, String redirectUrl) {
        if (callbacks != null) {
            Uri uri = Uri.parse(redirectUrl);
            if (uri.getHost() != null && uri.getHost() != null && uri.getHost().equals("rebrand.ly")) {
                noDeepLink(isFromGallery, isNoOrderMode);
            } else {
                callbacks.notBelongToKFC(isFromGallery, redirectUrl);
            }
      /*      if(isFromGallery)
                callbacks.fireQREvents(AppConstants.FirebaseEvents.NO_ADJUST_URL,AppConstants.FirebaseEvents.QR_FROM_GALLERY,isNoOrderMode);
            else
                callbacks.fireQREvents(AppConstants.FirebaseEvents.NO_ADJUST_URL,null,isNoOrderMode);
      */  //    callbacks.notBelongToKFC(isFromGallery);
        }

    }

    /**
     * Use of method
     * << This method will call to trigger firebase event and update UI  when no deep link is found from qr code  >>
     *
     * @param isFromGallery This is first parameter used to trigger firebase event
     * @param isNoOrderMode This is second parameter used to trigger firebase event
     */

    private void noDeepLink(boolean isFromGallery, boolean isNoOrderMode) {
        if (callbacks != null) {
            if (isFromGallery)
                callbacks.fireQREvents(QRConstants.FirebaseEvents.NO_DEEP_LINK, QRConstants.FirebaseEvents.QR_FROM_GALLERY, isNoOrderMode);
            else
                callbacks.fireQREvents(QRConstants.FirebaseEvents.NO_DEEP_LINK, null, isNoOrderMode);
            callbacks.onUnableToExtractQR();
        }

    }

    /**
     * Use of method
     * << This method will use to read value from actual deep link (i.e kfc link ) and pass it to requires place with the help of listener
     * This method is also handle error case
     * >>
     *
     * @param url            This is first url that contains deep link
     * @param isFromGallery  This is second parameter used to check url received from gallery image or camera
     * @param redirectionUrl This is third parameter that contains actual link received from scanner .It will use in firebase event
     * @param currentCountry this is used to identify the current selected country
     */

    private void readValueFromRedirectUrl(String url, boolean isFromGallery, boolean isFromWebView, String redirectionUrl, String currentCountry) {
        if (isBelongToKFC(url, DEEP_LINK_SCHEMA, currentCountry)) {
            url = getDeepLinkUrl(url);
            Uri uri = null;
            uri = Uri.parse(url);
            fireEventForAdjustUrl(uri, isFromGallery);
            String countryId = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_COUNTRY_ID);
            if (uri.getScheme() != null && !uri.getScheme().equals(DEEP_LINK_SCHEMA)) {
                if (TextUtils.isEmpty(countryId)) {
                    noDeepLink(isFromGallery, false);
                    return;
                }
            }
            Intent intent = getLinkType(uri, isFromGallery, countryId, redirectionUrl);
            if (callbacks != null)
                callbacks.onGetExtractedQRData(intent, isFromGallery, url);

        } else {
            unableToExtrackQR(isFromGallery, false, redirectionUrl);
        }
    }


    /**
     * Use of method
     * << This method will use to trigger firebase events for adjust url >>
     *
     * @param uri           This is first parameter that contains uri received from scanner
     * @param isFromGallery This is second parameter used to check this url belong to gallery image or camera
     */

    private void fireEventForAdjustUrl(Uri uri, boolean isFromGallery) {
        if (uri != null && uri.getScheme() != null && (!uri.getScheme().equals("app.adjust.com") && !uri.getScheme().equals("n8du.adj.st"))) {
            if (callbacks != null) {
                if (isFromGallery)
                    callbacks.fireQREvents(QRConstants.FirebaseEvents.NO_ADJUST_URL, QRConstants.FirebaseEvents.QR_FROM_GALLERY, false);
                else
                    callbacks.fireQREvents(QRConstants.FirebaseEvents.NO_ADJUST_URL, null, false);
            }
        }
    }

    /**
     * Method will return actual deep link url from redirect url
     *
     * @param url This is first parameter that contains information about deep link
     * @return It will return child deep link from the url
     * <p>
     * {@code logic}
     * << As per our requirment there is no any specific link format to get the child line or redirection link from the the url got from scanner
     * so in this method, we are trying to get child url from some specific hard coded ke keys
     * >>
     */

    public String getDeepLinkUrl(String url) {
        if (url.startsWith("intent://")) {// when deep link does't have rebrandly url
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                if (intent != null) {
                    String fallbackUrl = intent.getStringExtra("browser_fallback_url");//getting redirect url
                    if (fallbackUrl != null) {
                        Uri fallbackUri = Uri.parse(fallbackUrl);
                        if (fallbackUri.getQueryParameter("url") != null && fallbackUri.getQueryParameter("url").length() > 0)//actual deep link with data
                            return fallbackUri.getQueryParameter("url");
                    }

                }
            } catch (URISyntaxException e) {
                Log.e("ExtractQr", "unable to parse uri");
                e.printStackTrace();
            }
        } else {
            Uri uri = Uri.parse(url);
            if (uri != null && uri.getQueryParameterNames() != null && uri.getQueryParameterNames().size() > 0) {
                if (uri.getQueryParameter("url") != null && uri.getQueryParameter("url").length() > 0) {
                    url = uri.getQueryParameter("url");
                } else if (!TextUtils.isEmpty(uri.getQueryParameter("deep_link"))) {
                    url = uri.getQueryParameter("deep_link");

                } else if (!TextUtils.isEmpty(uri.getQueryParameter("deeplink"))) {
                    url = uri.getQueryParameter("deeplink");

                } else if (!TextUtils.isEmpty(uri.getQueryParameter("pickupHome"))) {
                    url = uri.getQueryParameter("pickupHome");
                } else if (!TextUtils.isEmpty(uri.getQueryParameter("adjust_deeplink"))) {
                    url = uri.getQueryParameter("adjust_deeplink");
                }
            }
        }
        return url;
    }

    /**
     * Use of method
     * << Method will call to initialize handler , which will trigger in case when web view does not load redirect url >>
     *
     * @param isFromGallery this is first parameter, that defines redirection url fetched from gallery image or by camera
     * @param redirectUrl   this is second parameter that contains information of deep link
     */

    private void registerHandler(boolean isFromGallery, String redirectUrl) {
        if (mHandler == null) {
            mHandler = new Handler();
            mRunnable = () -> {
                stopWebViewLoading();
                unableToExtrackQR(isFromGallery, false, redirectUrl);
            };
            mHandler.postDelayed(mRunnable, WAIT_DURATION);
        } else {
            mHandler.postDelayed(mRunnable, WAIT_DURATION);
        }
    }

    /**
     * Use of method
     * << This method will call to remove redirection url wait time handler >>
     */

    private void removeHandlerCallBack() {
        if (mRunnable != null && mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    /**
     * Use of method
     * << This method will call to stop web view to load redirect url >>
     */

    private void stopWebViewLoading() {
        if (webView != null)
            webView.stopLoading();

    }

    /**
     * Use of method
     * << This method will used to check deep link url does belong to kfc or not >>
     *
     * @param url This is first parameter contains url information received from qr code or after redirection
     * @return It will return true if deep link belongs to kfc oterwise false
     */

    public boolean isBelongToKFC(String url, String deepLinkSchema, String currentCountry) {
        try {
            url = getDeepLinkUrl(url);
            return parseUri(Uri.parse(url), deepLinkSchema, currentCountry);

        } catch (UnsupportedOperationException e) {
            return false;
        }
    }

    /**
     * Use of method
     * << This method will call to parse the uri got from redirection url or qr code >>
     *
     * @param uri            this is first parameter that contains uri received from qr code or redirection url
     * @param currentCountry this is used to identify the current selected country
     * @return It will return true if uri belong to kfc otherwise false
     * <p>
     * {@code logic}
     * @implNote << logic is like , if we have {@link QRConstants.DeepLinkConstants#DEEP_LINK_COUNTRY_ID} as a key in uri
     * and the value of this key will match with current selected country id then its belongs to kfc otherwise not
     * >>
     */

    public boolean parseUri(Uri uri, String deepLinkSchema, String currentCountry) {
        if (uri != null && !uri.isOpaque()) {
            if (uri.getScheme() != null && uri.getScheme().equals(deepLinkSchema)) {
                if (uri.getQueryParameterNames().contains(QRConstants.DeepLinkConstants.DEEP_LINK_COUNTRY_ID)) {
                    String countryId = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_COUNTRY_ID);
                    if (countryId != null && countryId.equalsIgnoreCase(currentCountry)/*Constants.Country.DEFAULT_COUNTRY_NAME)*/) {
                        return true;
                    } else
                        return false;
                }

                return true;
            } else if (!TextUtils.isEmpty(uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_COUNTRY_ID))) {
                String countryId = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_COUNTRY_ID);
                if (countryId != null && countryId.equalsIgnoreCase(currentCountry)/*Constants.Country.DEFAULT_COUNTRY_NAME)*/) {
                    return true;
                }
            }
        }
        return false;

    }


    /**
     * Use of method
     * << This method will use to clear objects uses when activity or fragment destroyed >>
     **/

    public void onClearData() {
        callbacks = null;
        stopWebViewLoading();
        removeHandlerCallBack();
        webView = null;
        mHandler = null;
        mRunnable = null;
    }


    /**
     * Use of method
     * << This method will use to create bundle data on the basis of kfc deep link and pass it to required screen with the help of intent. >>
     *
     * @param uri           This is first parameter that contains actual deep link uri ie kfc deep link
     * @param isFromGallery This is second parameter used to identify , uri source was gallery image or camera
     * @param countryId     This is third parameter that contains country id got from qr deep link
     * @param redirectUri   this is fourth parameter that contains uri got from QR code
     * @return It will return intent that contains all the data fetched from qr code actual link i.e kfc deep link
     */

    public Intent getLinkType(Uri uri, boolean isFromGallery, String countryId, String redirectUri) {
        Intent intent = new Intent();

        String type = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_TYPE);
        String storeId = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_STORE_ID);
        String autoApplyCoupon = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_AUTO_APPLY_COUPON);
        String extraDetails = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_EXTRA_DETAILS);
        String sdmStoreno = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_STORE_NO_);
        String sdmStoreNo = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_LINK_STORE_NO);
        String categoryId = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_SUB_MENU_ID);
        String productId = uri.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_PRODUCT_ID);

        if (!TextUtils.isEmpty(categoryId)) {
            intent.putExtra(QRConstants.DeepLinkConstants.DEEP_SUB_MENU_ID, categoryId);
        }
        intent.putExtra(QRConstants.DeepLinkConstants.DEEP_LINK_COUNTRY_ID, countryId);
        intent.putExtra(QRConstants.DeepLinkConstants.DEEP_LINK_TYPE, type);
        intent.putExtra(QRConstants.DeepLinkConstants.DEEP_LINK_STORE_ID, storeId);
        intent.putExtra(QRConstants.DeepLinkConstants.DEEP_LINK_AUTO_APPLY_COUPON, autoApplyCoupon);
        intent.putExtra(QRConstants.DeepLinkConstants.DEEP_LINK_EXTRA_DETAILS, extraDetails);
        intent.putExtra(QRConstants.DeepLinkConstants.DEEP_LINK_STORE_NO_, sdmStoreno);
        intent.putExtra(QRConstants.DeepLinkConstants.DEEP_LINK_STORE_NO, sdmStoreNo);
        if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_CART)) {
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.CART);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_HOME)) {
            if (!TextUtils.isEmpty(autoApplyCoupon)) {
                intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.HOME_APPLY_COUPON);
            } else
                intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.HOME);

        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_ORDER_HISTORY)) {
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.ORDER_HISTORY);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_TRACK_ORDER)) {
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.TRACK_ORDER);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_OFFERS)) {
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.OFFERS);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_CHECKOUT)) {
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.CHECKOUT);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_ADD_TO_CART)) {
            if (TextUtils.isEmpty(productId)) {
                Uri uriProduct = Uri.parse(redirectUri);
                productId = uriProduct.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_PRODUCT_ID);
            }
            int pId = 0;
            if (!TextUtils.isEmpty(productId))
                pId = Integer.parseInt(productId);
            intent.putExtra(QRConstants.IntentKeys.CATEGORY_ID, 0);
            intent.putExtra(QRConstants.IntentKeys.PRODUCT_ID, pId);
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.ADD_TO_CART);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_CATEGORY)) {
            int id = 0;
            if (!TextUtils.isEmpty(categoryId))
                id = Integer.parseInt(categoryId);
            intent.putExtra(QRConstants.IntentKeys.CATEGORY_ID, id);
            intent.putExtra(QRConstants.IntentKeys.PRODUCT_ID, 1);
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.CATEGORY);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_PRODUCT)) {
            int id = 0, pId = 0;
            if (TextUtils.isEmpty(productId)) {
                Uri uriProduct = Uri.parse(redirectUri);
                productId = uriProduct.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_PRODUCT_ID);
            }
            if (!TextUtils.isEmpty(categoryId))
                id = Integer.parseInt(categoryId);
            if (!TextUtils.isEmpty(productId))
                pId = Integer.parseInt(productId);
            intent.putExtra(QRConstants.IntentKeys.CATEGORY_ID, id);
            intent.putExtra(QRConstants.IntentKeys.PRODUCT_ID, pId);
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.PRODUCT);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_CUSTOMIZE_PRODUCT)) {
            int id = 0, pId = 0;
            if (TextUtils.isEmpty(productId)) {
                Uri uriProduct = Uri.parse(redirectUri);
                productId = uriProduct.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_PRODUCT_ID);
            }
            if (!TextUtils.isEmpty(categoryId))
                id = Integer.parseInt(categoryId);
            if (!TextUtils.isEmpty(productId))
                pId = Integer.parseInt(productId);
            intent.putExtra(QRConstants.IntentKeys.CATEGORY_ID, id);
            intent.putExtra(QRConstants.IntentKeys.PRODUCT_ID, pId);
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.CUSTOMIZE);
        } else if (uri.toString().contains(DEEP_LINK_SCHEMA + QRConstants.IDeepLink.DEEP_HIGHLIGHT)) {
            int id = 0, pId = 0;
            if (TextUtils.isEmpty(productId)) {
                Uri uriProduct = Uri.parse(redirectUri);
                productId = uriProduct.getQueryParameter(QRConstants.DeepLinkConstants.DEEP_PRODUCT_ID);
            }
            if (!TextUtils.isEmpty(categoryId))
                id = Integer.parseInt(categoryId);
            if (!TextUtils.isEmpty(productId))
                pId = Integer.parseInt(productId);
            intent.putExtra(QRConstants.IntentKeys.CATEGORY_ID, id);
            intent.putExtra(QRConstants.IntentKeys.PRODUCT_ID, pId);
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.HIGHLIGHT_PRODUCT);
        } else {
            intent.putExtra(QRConstants.IntentKeys.LINK_TYPE, QRConstants.IDeepLink.DEEP_LINK_SCANNER_PICK_UP_HOME);
        }

        if (callbacks != null && TextUtils.isEmpty(type)) {
            if (isFromGallery)
                callbacks.fireQREvents(QRConstants.FirebaseEvents.VALID_QR, QRConstants.FirebaseEvents.QR_FROM_GALLERY, true);
            else
                callbacks.fireQREvents(QRConstants.FirebaseEvents.VALID_QR, null, true);
        }

        return intent;
    }
/*
    public void getRedirectUrl(String url) {
        AsyncTask.execute(() -> {
            URL urlTmp = null;
            String redUrl = null;
            HttpURLConnection connection = null;

            try {
                urlTmp = new URL(url);
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            try {
                connection = (HttpURLConnection) urlTmp.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                connection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }

            redUrl = connection.getURL().toString();
            connection.disconnect();


        });

    }
*/
}


