package com.americana.common.qrcodereader;

import android.os.Parcel;
import android.os.Parcelable;

public class QRConfigErrors implements Parcelable {
    private String invalidQR;
    private String unableTOReadQR;
    private String qrAlignmentError;
    private String alertInvalidStoreHeader;
    private String alertInvalidStoreDescription;
    private String alertInvalidStoreNegBtn;//Negative button
    private String alertInvalidStorePosBtn;//positive button
    private String alertUnableTOReadQRHeader;
    private String alertUnableToReadQRDes;//description text
    private String alertUnableToReadQRPosBtn;//positive button
     private String alertUnableToReadQRNegBtn;//negative button

    public QRConfigErrors()
    {

    }
    public QRConfigErrors(Parcel in) {
        invalidQR = in.readString();
        unableTOReadQR = in.readString();
        qrAlignmentError = in.readString();
        alertInvalidStoreHeader = in.readString();
        alertInvalidStoreDescription = in.readString();
        alertInvalidStoreNegBtn = in.readString();
        alertInvalidStorePosBtn = in.readString();
        alertUnableTOReadQRHeader = in.readString();
        alertUnableToReadQRDes = in.readString();
        alertUnableToReadQRPosBtn = in.readString();
        alertUnableToReadQRNegBtn = in.readString();
    }

    public static final Creator<QRConfigErrors> CREATOR = new Creator<QRConfigErrors>() {
        @Override
        public QRConfigErrors createFromParcel(Parcel in) {
            return new QRConfigErrors(in);
        }

        @Override
        public QRConfigErrors[] newArray(int size) {
            return new QRConfigErrors[size];
        }
    };

    public String getInvalidQR() {
        return invalidQR;
    }

    public void setInvalidQR(String invalidQR) {
        this.invalidQR = invalidQR;
    }

    public String getUnableTOReadQR() {
        return unableTOReadQR;
    }

    public void setUnableTOReadQR(String unableTOReadQR) {
        this.unableTOReadQR = unableTOReadQR;
    }

    public String getQrAlignmentError() {
        return qrAlignmentError;
    }

    public void setQrAlignmentError(String qrAlignmentError) {
        this.qrAlignmentError = qrAlignmentError;
    }

    public String getAlertInvalidStoreHeader() {
        return alertInvalidStoreHeader;
    }

    public void setAlertInvalidStoreHeader(String alertInvalidStoreHeader) {
        this.alertInvalidStoreHeader = alertInvalidStoreHeader;
    }

    public String getAlertInvalidStoreDescription() {
        return alertInvalidStoreDescription;
    }

    public void setAlertInvalidStoreDescription(String alertInvalidStoreDescription) {
        this.alertInvalidStoreDescription = alertInvalidStoreDescription;
    }

    public String getAlertInvalidStoreNegBtn() {
        return alertInvalidStoreNegBtn;
    }

    public void setAlertInvalidStoreNegBtn(String alertInvalidStoreNegBtn) {
        this.alertInvalidStoreNegBtn = alertInvalidStoreNegBtn;
    }

    public String getAlertInvalidStorePosBtn() {
        return alertInvalidStorePosBtn;
    }

    public void setAlertInvalidStorePosBtn(String alertInvalidStorePosBtn) {
        this.alertInvalidStorePosBtn = alertInvalidStorePosBtn;
    }

    public String getAlertUnableTOReadQRHeader() {
        return alertUnableTOReadQRHeader;
    }

    public void setAlertUnableTOReadQRHeader(String alertUnableTOReadQRHeader) {
        this.alertUnableTOReadQRHeader = alertUnableTOReadQRHeader;
    }

    public String getAlertUnableToReadQRDes() {
        return alertUnableToReadQRDes;
    }

    public void setAlertUnableToReadQRDes(String alertUnableToReadQRDes) {
        this.alertUnableToReadQRDes = alertUnableToReadQRDes;
    }

    public String getAlertUnableToReadQRPosBtn() {
        return alertUnableToReadQRPosBtn;
    }

    public void setAlertUnableToReadQRPosBtn(String alertUnableToReadQRPosBtn) {
        this.alertUnableToReadQRPosBtn = alertUnableToReadQRPosBtn;
    }

    public String getAlertUnableToReadQRNegBtn() {
        return alertUnableToReadQRNegBtn;
    }

    public void setAlertUnableToReadQRNegBtn(String alertUnableToReadQRNegBtn) {
        this.alertUnableToReadQRNegBtn = alertUnableToReadQRNegBtn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(invalidQR);
        dest.writeString(unableTOReadQR);
        dest.writeString(qrAlignmentError);
        dest.writeString(alertInvalidStoreHeader);
        dest.writeString(alertInvalidStoreDescription);
        dest.writeString(alertInvalidStoreNegBtn);
        dest.writeString(alertInvalidStorePosBtn);
        dest.writeString(alertUnableTOReadQRHeader);
        dest.writeString(alertUnableToReadQRDes);
        dest.writeString(alertUnableToReadQRPosBtn);
        dest.writeString(alertUnableToReadQRNegBtn);
    }
}
