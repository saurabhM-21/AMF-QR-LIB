//package com.americana.common.qrcodereader;
//
//import android.content.Context;
//import android.content.Intent;
//import android.util.Log;
//
//import androidx.test.platform.app.InstrumentationRegistry;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import static org.junit.Assert.*;
//
//public class QRCodeReaderViewTest {
//    Context context;
//    private String deepLink="https://app.adjust.com/jsr?url=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F";
//
//    @Before
//    public void setUp() throws Exception {
//        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void extractDeepLink() {
//        ExtractQRRedirectUrl extractQRInstance = new ExtractQRRedirectUrl(new ExtractQRRedirectUrl.ExtractQRRedirectUrlCallbacks() {
//            @Override
//            public void onGetExtractedQRData(Intent intent, boolean isFromGallery, String url) {
//                Log.d("methode","onGetExtractedQRData");
//            }
//
//            @Override
//            public void onGetExtractQRError() {
//                Log.d("methode","onGetExtractedQRData");
//
//            }
//
//            @Override
//            public void onUnableToExtractQR() {
//                Log.d("methode","onUnableToExtractQR");
//
//            }
//
//            @Override
//            public void fireQREvents(String eventLabel, String eventCategory, boolean isNoOrderMode) {
//                Log.d("methode","fireQREvents");
//
//            }
//
//            @Override
//            public void onfireQREvents(String eventLabel, String qrActions, String deeplink) {
//                Log.d("methode","onfireQREvents");
//            }
//
//            @Override
//            public void notBelongToKFC(boolean isFromGallery, String deepLinkUrl) {
//                Log.d("methode","notBelongToKFC");
//
//            }
//        });
//
//        QRCodeReaderView qrCodeReaderView=new QRCodeReaderView(context);
//        qrCodeReaderView.extractDeepLink(deepLink,false,"KWT","kfckwt");
//
//    }
//}