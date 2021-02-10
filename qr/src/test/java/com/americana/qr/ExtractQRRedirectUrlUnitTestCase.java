package com.americana.qr;

import android.net.Uri;



import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class ExtractQRRedirectUrlUnitTestCase {

    private String validSchema= "kfckwt";
    private String inValidSchema= "kfcPizza";
    private String currentValidCountry= "KWT";
    private String currentInvalidCountry= "BIT";

    @Test
    public void getMobApiRedirectValidUrlCase() {
        Uri uri = Uri.parse("http://5ec3.adj.st/pickupHome?type=fromStore&countryId=KWT&storeId=1041&kfc=test_kfc&extraDetails=QR_Pickup__Pavement&adj_t=yxfibh9&adj_deep_link=kfc.kwt%3A%2F%2F");
        String expected = "https://rebrand.ly/test_kfc";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertEquals(expected, extractQRRedirectUrl.getMobApiRedirectUrl(uri));
    }

    @Test
    public void getMobApiRedirectInValidUrlCase() {
        Uri uri = Uri.parse("http://5ec3.adj.st/pickupHome?type=fromStore&countryId=KWT&storeId=1041&extraDetails=QR_Pickup__Pavement&adj_t=yxfibh9&adj_deep_link=kfc.kwt%3A%2F%2F");
        String expected = "https://rebrand.ly/test_kfc";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertNotEquals(expected, extractQRRedirectUrl.getMobApiRedirectUrl(uri));
    }

    @Test
    public void getDeepLinkUrlWithBlankUrl() {
        String uri = "";
        String expected = "http://5ec3.adj.st/pickupHome?type=fromStore&countryId=KWT&storeId=1041&kfc=test_kfc&extraDetails=QR_Pickup__Pavement&adj_t=yxfibh9&adj_deep_link=kfc.kwt%3A%2F%2F";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertNotEquals(expected, extractQRRedirectUrl.getDeepLinkUrl(uri));
    }

    @Test
    public void getDeepLinkUrlWithValidUrl() {
        String uri = "https://app.adjust.com/jsr?url=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F";
        String expected = "http://5ec3.adj.st/pickupHome?type=fromStore&countryId=KWT&storeId=1041&extraDetails=QR_Pickup__Pavement&adj_t=yxfibh9&adj_deep_link=kfc.kwt%3A%2F%2F";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertEquals(expected, extractQRRedirectUrl.getDeepLinkUrl(uri));

    }

    @Test
    public void getDeepLinkUrlForPickupHome() {
        String uri = "https://app.adjust.com/jsr?pickupHome=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F";
        String expected = "http://5ec3.adj.st/pickupHome?type=fromStore&countryId=KWT&storeId=1041&extraDetails=QR_Pickup__Pavement&adj_t=yxfibh9&adj_deep_link=kfc.kwt%3A%2F%2F";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertEquals(expected, extractQRRedirectUrl.getDeepLinkUrl(uri));

    }

    @Test
    public void getDeepLinkUrlForAdjustUrl() {
        String uri = "https://app.adjust.com/jsr?adjust_deeplink=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F";
        String expected = "http://5ec3.adj.st/pickupHome?type=fromStore&countryId=KWT&storeId=1041&extraDetails=QR_Pickup__Pavement&adj_t=yxfibh9&adj_deep_link=kfc.kwt%3A%2F%2F";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertEquals(expected, extractQRRedirectUrl.getDeepLinkUrl(uri));

    }

    @Test
    public void getDeepLinkUrlForDeepLink() {
        String uri = "https://app.adjust.com/jsr?deeplink=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F";
        String expected = "http://5ec3.adj.st/pickupHome?type=fromStore&countryId=KWT&storeId=1041&extraDetails=QR_Pickup__Pavement&adj_t=yxfibh9&adj_deep_link=kfc.kwt%3A%2F%2F";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertEquals(expected, extractQRRedirectUrl.getDeepLinkUrl(uri));

    }

    @Test
    public void isBelongToKFC() {
        String uri = "https://app.adjust.com/jsr?url=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertTrue(extractQRRedirectUrl.isBelongToKFC(uri,validSchema,currentValidCountry));
    }

    @Test
    public void isNotBelongToKFC() {
        String uri = "https://app.adjust.com/jsr?url=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F";
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertFalse(extractQRRedirectUrl.isBelongToKFC(uri,inValidSchema,inValidSchema));
    }

    @Test
    public void parseUriWithInvalidDeeplinkSchema() {
        Uri uri = Uri.parse("https://app.adjust.com/jsr?url=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F");
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertFalse(extractQRRedirectUrl.parseUri(uri,inValidSchema,currentValidCountry));

    }

    @Test
    public void parseUriWithValidDeeplinkSchema() {
        Uri uri = Uri.parse("kfckwt://app.adjust.com/jsr?url=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F");
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertTrue(extractQRRedirectUrl.parseUri(uri,validSchema,currentValidCountry));

    }


    @Test
    public void parseUriWithInValidDeeplinkSchemaWithValidCountry() {
        Uri uri = Uri.parse("kfckwt://app.adjust.com/jsr?url=http%3A%2F%2F5ec3.adj.st%2FpickupHome%3Ftype%3DfromStore%26countryId%3DKWT%26storeId%3D1041%26extraDetails%3DQR_Pickup__Pavement%26adj_t%3Dyxfibh9%26adj_deep_link%3Dkfc.kwt%253A%252F%252F");
        ExtractQRRedirectUrl extractQRRedirectUrl = new ExtractQRRedirectUrl();
        assertFalse(extractQRRedirectUrl.parseUri(uri,inValidSchema,currentInvalidCountry));

    }
}
