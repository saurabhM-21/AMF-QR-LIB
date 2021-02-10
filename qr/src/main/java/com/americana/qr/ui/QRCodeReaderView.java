package com.americana.qr.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;

import com.americana.qr.ExtractQRRedirectUrl;
import com.americana.qr.QRCodeAnimUtil;
import com.americana.qr.QRConfigErrors;
import com.americana.qr.R;
import com.americana.qr.ScannerOverlay;
import com.americana.qr.camera.CameraSource;
import com.americana.qr.camera.CameraSourcePreview;
import com.americana.qr.camera.CustomQRCodeDetector;
import com.americana.qr.utils.PermissionUtils;
import com.americana.qr.utils.QRConstants;
import com.americana.qr.utils.TextUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.io.IOException;
import java.util.List;


/**
 * use of class
 * <<
 * This class is a custom view class which will use to show QR scanner views and its camera permission views
 * (i.e UI for QR scanner and UI in a case when no permission was granted to use camera)
 * All the variations of QR scanner UI are managed in this class
 * This class also contains business logics (i.e. scan QR and handle result from camera ,
 * scan QR from gallery and handel its result and all the validations related to scan QR)
 * This class should be accessed via activity or fragments layout xml file where we need to show QR scan UI
 * >>
 */
public class QRCodeReaderView extends RelativeLayout implements View.OnClickListener, ExtractQRRedirectUrl.ExtractQRRedirectUrlCallbacks {
    // intent request code to pick image from gallery
    private final int PICK_GALLERY_IMAGE = 3;
    //constant value to identify qr error with no deep link
    private final int QR_ERROR_NO_DEEP_LINK = 1;
    //constant value to identify qr error with expired deep link
    private final int QR_ERROR_DEEP_LINK_EXPIRE = 2;
    //constant value to identify qr error with nothing to read from qr code
    private final int QR_ERROR_NOTHING_TO_READ = 3;
    //variable to define alignment error delay time , should be updated with config value
    private long ALIGHTMENT_ERROR_DELAY_TIME = 10000;
    //variable to define alignment error hide time , should be updated with config value
    private long ALIGHTMENT_ERROR_HIDE_TIME = 2500;
    //constant variable to define qr square alignment view width
    private final int QR_BOARDER_WIDTH = 13;
    // intent request code to handle updating play services if needed.
    private final int RC_HANDLE_GMS = 9001;
    //variable that show camera preview
    private CameraSourcePreview mPreview;
    //variable that contains view reference
    private FrameLayout flBar;
    //variable that contains view reference
    private View bar;
    //variable that contains view reference
    private ImageView ivTourch;
    //variable that contains view reference
    private ImageView ivGallery;
    //variable that contains view reference
    private View viewRectErrorr;
    //variable that contains view reference
    private ConstraintLayout consRootCameraPermission;
    //variable that contains view reference
    private Button btnEnablePermission;
    //variable that contains view reference
    private LottieAnimationView ivLoader;
    //variable that have camera source reference to capture view from camera
    private CameraSource mCameraSource;
    //variable that have reference of ml kit bar code detector
    private BarcodeDetector barcodeDetector;
    //variable that have listener reference to perform events callback
    private QRCodeDetectorCallBacks callBacks;
    //variable that have handler reference
    private Handler qrAlighHandler;
    //variable that have runnable reference attached with #qrAlighHandler
    private Runnable qrAlignRunnable;
    //variable that contains view reference
    private ScannerOverlay qrOverlay;
    //variable that contains view reference
    private ImageView ivQrSquare;
    //variable that initialize with default hight of transparent square area
    private int heightRect = dpToPx(210);
    //variable that initialize with default width of transparent square area
    private int widthRect = dpToPx(210);
    //variable that contains reference of custom class that extends detector
    private CustomQRCodeDetector boxDetector;
    //boolean variable used to check weather alignment error handler initialize or not
    private boolean isQRAlighmentHandlerSet;
    //object that contains reference of #ExtractQRRedirectUrl class to access its methods and variables
    private ExtractQRRedirectUrl extractQRInstance;
    //boolean variable used to check weather QR is processing or not
    private boolean isProcessingQR = false;
    //object that contains reference of #QRCodeAnimUtil class to access it variables and methods
    private QRCodeAnimUtil animUtil;
    //object that contains refrence of #Toast
    private Toast toastOne;
    //boolean variable used to check weather any alert dialog is showing on screen or not
    private boolean isAlertDialogIsShowing;
    //boolean variable used to check user requested for permission first time or not
    private boolean isAskForFirstTime = true;
    //object that contains reference of #QRConfigErrors class to access its public methos
    private QRConfigErrors qrConfigErrors;
    //boolean variable used to check transparent square area position updated or not
    private boolean isUpdateSqareArea = false;
    //variable that contains informaion of current selected service time
    private int eventCategoryType = QREventCategoryType.CATEGORY_CAR_HOP;
    //boolean variable used to check weather we need to show qr alignment error or not
    private boolean isShowAlighmentError = false;

    private String currentCountry;
    private String deepLinkSchema;

    /**
     * Constructor
     *
     * @param context This is first parameter that will use to get some value from resources
     */
    public QRCodeReaderView(Context context) {
        super(context);
        initViews(context);
    }

    /**
     * Constructor
     *
     * @param context This is first parameter that will use to get some value from resources
     * @param attrs   This is second parameter that will use to get some value from attributes
     */
    public QRCodeReaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    /**
     * Constructor
     *
     * @param context      This is first parameter that will use to get some value from resources
     * @param attrs        This is second parameter that will use to get some value from attributes
     * @param defStyleAttr This is third parameter that will use to apply style on this custom UI
     */
    public QRCodeReaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    /**
     * Constructor
     *
     * @param context      This is first parameter that will use to get some value from resources
     * @param attrs        This is second parameter that will use to get some value from attributes
     * @param defStyleAttr This is third parameter that will use to apply style on this custom UI
     * @param defStyleRes  This is fourth parameter that will use to get style resources
     */
    public QRCodeReaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(context);
    }

    /**
     * Use of Method
     * <<
     * This Method will use to register listener to get call backs of events that we triggered by this custom class to our activity or fragment
     * This method must be called from activity or fragment where QR scanner UI will integrated
     * >>>
     *
     * @param callBacks         This is first parameter that will use to instantiate listener call back
     * @param eventCategoryType This is second parameter that will use to identify from which screen
     *                          it is going to  attached , This parameter will be use in firebase events log
     */
    public void registerQRCodeDetectorCallback(QRCodeDetectorCallBacks callBacks, int eventCategoryType) {
        this.callBacks = callBacks;
        this.eventCategoryType = eventCategoryType;
    }

    /**
     * Use of Method
     * <<
     * This method will use to inflate QR scanner layout xml , find views ids and attaching listener on view
     * This method will also instantiate object of #ExtractQRRedirectUrl and #QRCodeAnimUtil
     * >>
     *
     * @param context This is first parameter that will use in case where we need context to fetch some value from resources
     */
    private void initViews(Context context) {
        View view = inflate(getContext(), R.layout.qr_code_reader_view, null);
        addView(view);
        findViews(view);
        setClickListners();
        extractQRInstance = new ExtractQRRedirectUrl(this);
        animUtil = new QRCodeAnimUtil();
    }

    /**
     * Use of Method
     * <<
     * This method will use to start animation on QR scanner UI
     * This animation basically represents vertically moving view from top to bottom
     * and vice-versa within white transparent square box in QR scanner UI
     * >>
     *
     * @param activity This first parameter that will use to access resource values
     */
    public void startScannerAnimation(Activity activity) {
        if (animUtil == null)
            animUtil = new QRCodeAnimUtil();
        animUtil.setQRCodeFrameAnimation(activity, flBar, bar, widthRect, heightRect, QR_BOARDER_WIDTH);
    }

    /**
     * Use of Method
     * <<
     * This method will use to update square overlay height (white transparent area in QR scanner UI) according to qr border
     * This method will also use to update alignment error square view width and height according to qr border
     * This method will also use to update loader(Lottie animation view) view  width and height according to qr border
     * >>
     * <p>
     * {@code logic}
     *
     * @param activity This first parameter that will use to access resource values
     * @implNote <<
     * The logic is like we are updating above mentioned views measurments on the basis of cornered square image width , height and its position on the screen
     * We are calculating desire width and height when #ivQrSquare is in idel state (i.e. rendered on screen)
     * >>
     */
    private void updateOverlayHeight(final Activity activity) {
        if (ivQrSquare != null) {
            ivQrSquare.post(new Runnable() {
                @Override
                public void run() {
                    if (callBacks == null)
                        return;
                    heightRect = ivQrSquare.getMeasuredHeight();
                    widthRect = ivQrSquare.getMeasuredWidth();

                    if (widthRect != 0 && heightRect != 0 && QRCodeReaderView.this.isUpdateInPreviousSquarePosition()) {
                        isUpdateSqareArea = true;
                        qrOverlay.changeSquarePosition(ivQrSquare.getLeft(), ivQrSquare.getRight(), ivQrSquare.getTop(), ivQrSquare.getBottom(), QR_BOARDER_WIDTH);
                        QRCodeReaderView.this.setAlignmentErrorMeasurment();
                        QRCodeReaderView.this.setLoaderViewWidthAndHeight();
                        QRCodeReaderView.this.startScannerAnimation(activity);
                    }

                }
            });
        }
    }

    /**
     * Use of Method
     * <<
     * This method will use to update measurment of alignment error view
     * >>
     */
    private void setAlignmentErrorMeasurment() {
        RelativeLayout.LayoutParams params = (LayoutParams) viewRectErrorr.getLayoutParams();
        params.width = widthRect;
        params.height = heightRect;
        viewRectErrorr.setLayoutParams(params);
    }


    /*
     * Use of method
     * <<
     * This method will used to check any update on the position of #ivQrSquare view to
     * update square overlay position on the screen
     * >>
     * @return This will return true if any update on ivQrSquare position else false
     */
    private boolean isUpdateInPreviousSquarePosition() {
        if (ivQrSquare.getLeft() != qrOverlay.getLeftPos()
                || ivQrSquare.getTop() != qrOverlay.getTopPos()
                || ivQrSquare.getBottom() != qrOverlay.getBottomPos()
                || ivQrSquare.getRight() != qrOverlay.getRightPos())
            return true;
        return false;
    }

    /**
     * use of method
     * <<
     * This method will use to update width and height of lottie loder view
     * >>
     */
    private void setLoaderViewWidthAndHeight() {
        RelativeLayout.LayoutParams params = (LayoutParams) ivLoader.getLayoutParams();
        params.width = widthRect;
        params.height = heightRect;
        ivLoader.setLayoutParams(params);
    }

    /**
     * Use of method
     * << This method will call to convert dp value to px value >>
     *
     * @param dp this is first parameter , that contains value in dp
     * @return this will return dp value to px
     */
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Use of method
     * << This method will used to find ids of view used in its xml file >>
     *
     * @param view this is first parameter , that contains view reference to find its child view ids
     */
    private void findViews(View view) {
        mPreview = view.findViewById(R.id.preview);
        flBar = view.findViewById(R.id.fl_bar);
        ivQrSquare = view.findViewById(R.id.iv_qr_square);
        ivTourch = view.findViewById(R.id.iv_qr_tourch);
        ivGallery = view.findViewById(R.id.iv_qr_gallery);
        bar = view.findViewById(R.id.bar);
        viewRectErrorr = view.findViewById(R.id.view_rect_rrror);
        qrOverlay = view.findViewById(R.id.qr_overlay);
        ivLoader = view.findViewById(R.id.iv_loader);
        btnEnablePermission = view.findViewById(R.id.btn_enable_permission);
        consRootCameraPermission = view.findViewById(R.id.cons_root_camera_permission);
    }

    /*
     * Use of method
     * << This method will used to add click event listener on views >>
     */
    private void setClickListners() {
        ivTourch.setOnClickListener(this);
        ivGallery.setOnClickListener(this);
        btnEnablePermission.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //override methos to handle view click event
        int id = v.getId();
        if (id == R.id.iv_qr_tourch) {
            turnOnOffTourch();
        } else if (id == R.id.btn_enable_permission) {
            if (callBacks != null)
                callBacks.takeCameraPermission(false);
        } else if (id == R.id.iv_qr_gallery) {
            if (callBacks != null && !isProcessingQR)
                callBacks.onQRGalleryIconClicked(PICK_GALLERY_IMAGE, getGalleryChooserIntent());
        }

    }

    /**
     * Use of method
     * <<
     * This method will call when we need to show scanner view , generally we can call it from onResume() or in a condiation where we need to show scanner view
     * Method will show scanner view if all the required permission was granted otherwise user will see take camera permission screen
     * >>
     *
     * @param activity         this is first parameter that used to access resources
     * @param isDontAskClicked this is second parameter that is use to identify user has denied camera permission with don't ask again check
     */
    public void showQRScannerView(Activity activity, boolean isDontAskClicked, String currentCountry, String deepLinkSchema) {
        this.currentCountry = currentCountry;
        this.deepLinkSchema = deepLinkSchema;
        updateOverlayHeight(activity);
        if (isUpdateSqareArea)
            startScannerAnimation(activity);
        if (isDontAskClicked)
            isAskForFirstTime = false;

        if (isCameraPermissionGranted(activity)) {
            isShowAlighmentError = true;
            consRootCameraPermission.setVisibility(View.GONE);
            createCameraSource(activity);
            startCameraSource(activity);
            isAskForFirstTime = false;
        } else {
            consRootCameraPermission.setVisibility(View.VISIBLE);
            stopPreview();
            if (isAskForFirstTime) {
                isAskForFirstTime = false;
                if (callBacks != null)
                    callBacks.takeCameraPermission(false);
            }
        }
    }

    /* Use of method
     * <<
     * This method will use to check user have camera permission or not
     * >>
     * @param activity This is first parameter that will use to access resource value
     * @return this will return true if user have camera permission otherwise false
     */
    private boolean isCameraPermissionGranted(Activity activity) {
        return PermissionUtils.checkCameraPermissions(activity);
    }


    /**
     * Use of method
     * <<
     * This method will use to get request intent to fetch image from gallery
     * >>
     *
     * @return this will return request intent to fetch image from gallery
     */
    private Intent getGalleryChooserIntent() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        //used to open all gallery app using android native chooser
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        return getIntent;
    }

    /**
     * Use of method
     * << This method will call to turn on /off flash light >>
     */
    private void turnOnOffTourch() {
        if (ivTourch.getTag() == null) {
            if (mCameraSource != null)
                mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            ivTourch.setTag(1);
            ivTourch.setImageResource(R.drawable.ic_qr_torch_on);
        } else {
            if (mCameraSource != null)
                mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            ivTourch.setTag(null);
            ivTourch.setImageResource(R.drawable.ic_qr_torch);
        }
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     * <p>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(Activity activity) {
        Context context = getContext();
        if (barcodeDetector != null)
            return;
        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        barcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE).build();
        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = activity.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                //todo show error
            }
        }


        boxDetector = new CustomQRCodeDetector(barcodeDetector);

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(activity, boxDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1200)
                .setRequestedFps(15.0f);

        builder = builder.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        mCameraSource = builder.build();

        setBarcodeDetectorListner(currentCountry, deepLinkSchema);
    }


    /**
     * Use of method
     * << This method will call to add qr code detector listener >>
     * <p>
     * {@code logic}
     *
     * @implNote <<
     * As bar code detected successfully , we are trying to get the deep link from them on the basis of some validation
     * We will process , deep link url if and only if we have only one valid url . If we detect more than one deep link then it will show error pop up
     * Whenever this method will call we also start a handle to show alignment error when qr will not detect in a specific time (this should be manage via config)
     * >>
     */
    private void setBarcodeDetectorListner(final String currentCountry, final String deeplinkSchema) {
        boxDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Log.d("TAG", "release: " + "To prevent memory leaks barcode scanner has been stopped");
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (isProcessingQR)//already processing qr
                    return;
                if (barcodes.size() != 0) {

                    isProcessingQR = true;
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (callBacks == null) {
                                isProcessingQR = false;
                                return;
                            }
                            Barcode barcode = getKfcBarCode(barcodes);
                            if (barcode == null) {
                                //if we don't get any deep link or more then one link is present
                                if (barcodes.size() > 1)
                                    showCustomToast(getContext(), getMultipleQRError());
                                else
                                    showCustomToast(getContext(), getInvlidQRError());
                                onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.QREventAction.QR_SCAN, "");
                                isProcessingQR = false;
                                return;
                            }
                            if (callBacks != null) {
                                if (barcode.url == null || barcode.url.url == null) {
                                    //if we got only one bar code but its don't have any deeplink link value on url parameter
                                    onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.QREventAction.QR_SCAN, "");
                                    showCustomToast(getContext(), getInvlidQRError());
                                    isQRAlighmentHandlerSet = false;
                                    isProcessingQR = false;
                                    return;
                                }
                                extractDeepLink(barcode.url.url, false, currentCountry, deeplinkSchema);

                            } else
                                isProcessingQR = false;

                        }
                    }, 1000);


                } else {
                    if (!isQRAlighmentHandlerSet)
                        startQRAlignmentHandler();
                }

            }

        });

    }

    /**
     * Use of method
     * << This method will call to get first barcode object from the barcode detected from ml kit lib   >>
     *
     * @param barcodes this is first parameter that contains list of barcodes detected from camera
     * @return this will return barcode object of first position otherwise null
     */
    private Barcode getKfcBarCode(SparseArray<Barcode> barcodes) {
        if (barcodes.size() == 1) {
            return barcodes.valueAt(0);
        }
        return null;
    }

    /**
     * Use of method
     * <p>
     * <<
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     * >>
     *
     * @param activity this is first parameter used to access resources
     */
    private void startCameraSource(Activity activity) throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                activity);
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(activity, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);

            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * Use of method
     * <<
     * This method will use to start handler to show alignment error after specific time
     * Its run method will trigger only when there is not bar code detected in given time
     * >>
     */
    private void startQRAlignmentHandler() {
        isQRAlighmentHandlerSet = true;
        if (qrAlighHandler == null) {
            qrAlighHandler = new Handler(Looper.getMainLooper());
            qrAlignRunnable = new Runnable() {
                @Override
                public void run() {
                    QRCodeReaderView.this.showAlingmentError(true);
                }
            };
        }
        if (qrAlignRunnable != null)
            qrAlighHandler.postDelayed(qrAlignRunnable, ALIGHTMENT_ERROR_DELAY_TIME);


    }

    /**
     * Use of method
     * <p>
     * <<
     * This method will used to show qr alignment error view and toast
     * Alignment error view also hide after given time , so that is also manage in this method
     * >>
     *
     * @param isShow this is first parameter that used to show / hide alighment error view
     */
    private void showAlingmentError(boolean isShow) {
        if (isShow && !isProcessingQR) {
            if (!isShowAlighmentError) {
                removeHandlerCallback();
                return;
            }
            if (viewRectErrorr != null)
                viewRectErrorr.setVisibility(View.VISIBLE);
            if (callBacks != null) {
                fireQREvents(QRConstants.FirebaseEvents.QR_ALIGNMENT, null, false);
                showCustomToast(getContext(), getAlignmentQRError());
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (viewRectErrorr != null && viewRectErrorr.getVisibility() == VISIBLE) {
                        viewRectErrorr.setVisibility(View.GONE);
                    }
                    if (viewRectErrorr != null) {
                        if (qrAlignRunnable != null)
                            qrAlighHandler.postDelayed(qrAlignRunnable, ALIGHTMENT_ERROR_DELAY_TIME);
                    }
                }
            }, ALIGHTMENT_ERROR_HIDE_TIME);
        } else {
            if (viewRectErrorr != null)
                viewRectErrorr.setVisibility(View.GONE);
        }
    }

    @Override
    public void onfireQREvents(String eventLabel, String qrAction, String deeplink) {
        //override method , trigger when need to fire firebase event
        String eventCategory = "";
        switch (eventCategoryType) {
            case QREventCategoryType.CATEGORY_CAR_HOP:
                eventCategory = QRConstants.FirebaseEvents.QR_ON_CARHOP;
                break;
            case QREventCategoryType.CATEGORY_DINE_IN:
                eventCategory = QRConstants.FirebaseEvents.QR_ON_DINEIN;
                break;
            case QREventCategoryType.CATEGORY_DRIVE_THRU:
                eventCategory = QRConstants.FirebaseEvents.QR_ON_DRIVE_THRU;
                break;
            case QREventCategoryType.CATEGORY_SELF_PICK_UP:
                eventCategory = QRConstants.FirebaseEvents.QR_ON_PICKUP;
                break;
        }
        if (callBacks != null)
            callBacks.onFireQREvents(eventCategory, eventLabel, qrAction, deeplink);

    }

    @Override
    public void fireQREvents(String eventLabel, String eventCategory, boolean isFromNoOrder) {
        //ignore
    }

    /**
     * Use of method
     * << This method will call to remove qr alignment error handler and to hide alignment error view>>
     */
    private void removeHandlerCallback() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                QRCodeReaderView.this.showAlingmentError(false);
                if (qrAlignRunnable != null && qrAlighHandler != null)
                    qrAlighHandler.removeCallbacks(qrAlignRunnable);
            }
        });

    }

    /**
     * Use of method
     * << This method will call to stop camera preview to detect QR when activity or fragment is in background >>
     */
    public void stopPreview() {
        isShowAlighmentError = false;
        if (animUtil != null)
            animUtil.clearAnimation();
        if (mPreview != null) {
            mPreview.stop();
            removeHandlerCallback();
            isQRAlighmentHandlerSet = false;
        }
    }

    /**
     * Use of method
     * << This method will call to release camera preview, triggre when activity or fragment has destroyed >>
     */
    public void releasePreview() {
        if (mPreview != null) {
            mPreview.release();
        }
    }

    /**
     * Use of method
     * << This method will get call to handle activity result when user choose to read qr from gallery image >>
     *
     * @param requestCode this is first parameter that refers request code
     * @param resultCode  this is second parameter that refers result code
     * @param data        this is third parameter that contains data information
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_GALLERY_IMAGE) {
            if (data != null) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    isProcessingQR = true;
                    scanQrCodeFromUri(imageUri);
                } else {
                    onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.QREventAction.UPLOAD_IMAGE, "");
                    showCustomToast(getContext(), getInvlidQRError());
                }
            }
        }

    }

    @Override
    public void onGetExtractedQRData(Intent intent, boolean isFromGallery, String url) {
        //override method, will trigger when deep link url received from qr code after performing its redirection
        hideLoader();
        if (callBacks != null) {
            if (isFromGallery)
                onfireQREvents(QRConstants.QREventLabel.VALID_QR, QRConstants.QREventAction.UPLOAD_IMAGE, url);
            else
                onfireQREvents(QRConstants.QREventLabel.VALID_QR, QRConstants.QREventAction.QR_SCAN, url);

            callBacks.onDetectQRCode(intent, url);

        }
        isProcessingQR = false;
    }

    /**
     * Use of method
     * << This method will call to hide loader from the QR scanner UI >>
     */
    private void hideLoader() {
        if (ivLoader != null)
            ivLoader.setVisibility(View.GONE);
    }

    @Override
    public void onGetExtractQRError() {
        //override method, will trigger when there is some error during extracting qr url
        hideLoader();
        showCustomToast(getContext(), getInvlidQRError());
        isProcessingQR = false;
    }

    @Override
    public void onUnableToExtractQR() {
        //override method, will trigger when we are unable to extract actual deep link from qr url
        hideLoader();
        showUnableToReadError();
    }

    /**
     * Use of method
     * <<
     * This method will use to update qr alignment error wait time and hide time
     * these time will be manageble from config
     * >>
     *
     * @param duration this is first parameter that contains both value (i.e alignment error wait time and its view hide time) separated by "-"
     *                 <p>
     *                 <p>
     *                 {@code logic}
     * @implNote << first we split that string by '-' and considering on the first position contain wait time and second position contains hide time >>
     */
    public void updateAlignmentErrorTime(String duration) {
        if (duration != null && duration.contains("-")) {
            String[] split = duration.split("-");
            if (split.length > 1) {
                ALIGHTMENT_ERROR_DELAY_TIME = Long.parseLong(split[0]);
                ALIGHTMENT_ERROR_HIDE_TIME = Long.parseLong(split[1]);
            }
        }
    }

    /**
     * Use of method
     * << This method will use to update QR error pojo object with its config value>>
     *
     * @param errors
     */
    public void qrConfigErrors(QRConfigErrors errors) {
        qrConfigErrors = errors;
    }

    /**
     * Use of method
     * << Method should be call from Activity or Fragment when QR Scanner
     * view is visible to calculate actual position of transparent square box >>
     *
     * @param activity this first parameter used to access resource
     */
    public void updateSquarePosition(final Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!activity.isFinishing() && callBacks != null && ivQrSquare != null)
                    QRCodeReaderView.this.updateOverlayHeight(activity);
            }
        }, 200);
    }

    /**
     * Use of method
     * << This method will get call to create InputImage from uri picked from galley image to read qr >>
     *
     * @param uri this is first parameter that contains uri path of image that user has picked from gallery
     */
    public void scanQrCodeFromUri(Uri uri) {
        InputImage image;
        try {
            image = InputImage.fromFilePath(getContext(), uri);
            scanQrcodes(image);
        } catch (IOException e) {
            isProcessingQR = false;
            e.printStackTrace();
            onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.FirebaseEvents.QR_FROM_GALLERY, "");
            showCustomToast(getContext(), getInvlidQRError());

        }
    }

    /**
     * Use of method
     * << This method will call to show custom toast messages >>
     *
     * @param context this is first parameter used to access resource
     * @param message this is second parameter that contains message string to show on toast view
     */
    public void showCustomToast(final Context context, final String message) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (callBacks != null) {
                    if (toastOne != null) {
                        try {
                            if (toastOne.getView().isShown())     // true if visible
                            {
                                toastOne.setText(message);
                                toastOne.show();
                            } else {
                                QRCodeReaderView.this.inflateToastLayout(message, context);
                            }
                        } catch (Exception e) {// invisible if exception
                            QRCodeReaderView.this.inflateToastLayout(message, context);
                        }
                    } else {
                        QRCodeReaderView.this.inflateToastLayout(message, context);
                    }
                }
            }
        });

    }

    /**
     * Use of method
     * << This method will use to infalte and show custom toast view with information >>
     *
     * @param message This is first parameter , use to show message on toast
     * @param context This is second parameter , use to access resources
     */
    private void inflateToastLayout(String message, Context context) {
        if (toastOne != null)
            toastOne.cancel();
        toastOne = null;
        toastOne = new Toast(context);
        toastOne.setDuration(Toast.LENGTH_SHORT);
        toastOne.setGravity(Gravity.CENTER_VERTICAL, 0, context.getResources().getDimensionPixelSize(R.dimen._130dp));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_custom_toast, null);
        TextView tvToast = view.findViewById(R.id.tv_toast);
        tvToast.setText(message);
        toastOne.setView(view);
        toastOne.show();

    }

    /**
     * Use of method
     * <p>
     * <<
     * This method will call to process InputImage to get redirect url from gallery QR image
     * This method also perform validation after getting redirection url from QR gallery image
     * >>
     * <p>
     * {@code logic}
     *
     * @param image This is first parameter that contains InputImage object , to get redirect url from  gallery QR image
     * @implNote <<
     * As bar code detected successfully , we are trying to get the deep link from them on the basis of some validation
     * We will process , deep link url if and only if we have only one valid url . If we detect more than one deep link then it will show error pop up
     * Whenever this method will call we also start a handle to show alignment error when qr will not detect in a specific time (this should be manage via config)
     * >>
     */
    private void scanQrcodes(InputImage image) {
        BarcodeScannerOptions options =
                new BarcodeScannerOptions.Builder()
                        .setBarcodeFormats(
                                com.google.mlkit.vision.barcode.Barcode.FORMAT_QR_CODE)
                        .build();

        BarcodeScanner scanner = BarcodeScanning.getClient(options);
        Task<List<com.google.mlkit.vision.barcode.Barcode>> result = scanner.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<com.google.mlkit.vision.barcode.Barcode>>() {
                    @Override
                    public void onSuccess(List<com.google.mlkit.vision.barcode.Barcode> barcodes) {
                        // Task completed successfully
                        com.google.mlkit.vision.barcode.Barcode barcode = QRCodeReaderView.this.getKfcBarCodeFromGalleryImage(barcodes);
                        if (barcode == null) {
                            if (barcodes.size() > 1)
                                QRCodeReaderView.this.showCustomToast(QRCodeReaderView.this.getContext(), QRCodeReaderView.this.getMultipleQRError());
                            else
                                QRCodeReaderView.this.showCustomToast(QRCodeReaderView.this.getContext(), QRCodeReaderView.this.getInvlidQRError());
                            QRCodeReaderView.this.onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.QREventAction.UPLOAD_IMAGE, "");
                            isProcessingQR = false;
                            return;
                        } else {
                            if (barcode.getUrl() == null || TextUtils.isEmpty(barcode.getUrl().getUrl())) {
                                QRCodeReaderView.this.onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.QREventAction.UPLOAD_IMAGE, "");
                                QRCodeReaderView.this.showCustomToast(QRCodeReaderView.this.getContext(), QRCodeReaderView.this.getInvlidQRError());
                                return;
                            }
                            String url = barcode.getUrl().getUrl();
                            QRCodeReaderView.this.extractDeepLink(url, true, currentCountry, deepLinkSchema);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        if (callBacks != null)
                            QRCodeReaderView.this.showCustomToast(QRCodeReaderView.this.getContext(), QRCodeReaderView.this.getInvlidQRError());
                        QRCodeReaderView.this.onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.QREventAction.UPLOAD_IMAGE, "");
                        isProcessingQR = false;
                    }
                });
    }


    @Override
    public void notBelongToKFC(boolean isFromGallery, String deepLinkUrl) {
        //override method, that triggered when deep link url not belongs to kfc
        hideLoader();
        showCustomToast(getContext(), getInvlidQRError());
        if (isFromGallery)
            onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.QREventAction.UPLOAD_IMAGE, "");
        else
            onfireQREvents(QRConstants.QREventLabel.INVALID_QR, QRConstants.QREventAction.QR_SCAN, "");
        isProcessingQR = false;
    }


    /**
     * Use of method
     * << This method will use to get barcode object from the list of barcodes returned from the ml kit >>
     *
     * @param barcodes list of barcode read from gallery image
     * @return This method will return barcode object of mlkit if we get it from gallery image otherwise it will return null
     */
    private com.google.mlkit.vision.barcode.Barcode getKfcBarCodeFromGalleryImage(List<com.google.mlkit.vision.barcode.Barcode> barcodes) {
        if (barcodes != null && barcodes.size() == 1) {
            return barcodes.get(0);
        }
        return null;
    }

    /**
     * Use of method
     * <<
     * This method will call when we are unable to read qr from camera or gallery to show the alert pop up
     * >>
     */
    private void showUnableToReadError() {
        openQRErrorAlertDialog(QR_ERROR_NOTHING_TO_READ);
    }


    /**
     * Use of method
     * <<
     * This method will use to  show QR error alert pop
     * >>
     *
     * @param errorType This is first parameter that will used to identify which type of error occurred  during reading QR
     */
    private void openQRErrorAlertDialog(final int errorType) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (isAlertDialogIsShowing)
                    return;
                isAlertDialogIsShowing = true;
                String title = "";
                String msg = "";
                String negativeBtnText = QRCodeReaderView.this.getUnableToReadQRAlertNegativeBtnText();
                String postiveBtnText = QRCodeReaderView.this.getUnableToReadQRAlertPositiveBtnText();

                switch (errorType) {
                    case QR_ERROR_NOTHING_TO_READ:
                    case QR_ERROR_NO_DEEP_LINK:
                    case QR_ERROR_DEEP_LINK_EXPIRE:
                        msg = QRCodeReaderView.this.getUnableToReadQRAlertDescription();
                        title = QRCodeReaderView.this.getUnableToReadQRAlertTitle();
                        break;
                }

                callBacks.showQRErrorAlertDialog(title, msg, negativeBtnText, postiveBtnText, new DialogClickListener() {
                    @Override
                    public void onPositiveBtnClick() {
                        if (callBacks != null)
                            callBacks.onSelecteStoreButtonClick();
                        isProcessingQR = false;
                        isAlertDialogIsShowing = false;
                    }

                    @Override
                    public void onNegativeBtnClick() {
                        isAlertDialogIsShowing = false;
                        isProcessingQR = false;
                    }
                });


            }
        });
    }


    /**
     * Use of method
     * <<
     * This method will use to  show alert pop up when selected store belongs to another service type or address type
     * >>
     *
     * @param addressType This is second parameter that will used to show current selected address type (i.e Dine in, Carhop etc)
     */
    public void showStoreInOtherAddressTypePopup(int isCarHop, int isDriveThru, int isDinin, int isTak, String addressType, String currentCountry, String deepLinkSchema) {
        this.currentCountry = currentCountry;
        this.deepLinkSchema = deepLinkSchema;
        String title = String.format("%s %s", getContext().getString(R.string.store_not_serving), addressType);
        StringBuilder msg = new StringBuilder();
        if (isCarHop == 1)
            msg.append(getContext().getString(R.string.carhop));
        if (isDriveThru == 1) {
            if (msg.length() > 0)
                msg.append(", ");
            msg.append(getContext().getString(R.string.drive_through));
        }
        if (isDinin == 1) {
            if (msg.length() > 0)
                msg.append(", ");
            msg.append(getContext().getString(R.string.dine_in));
        }
        if (isTak == 1) {
            if (msg.length() > 0)
                msg.append(", ");
            msg.append(getContext().getString(R.string.pickup));
        }
        callBacks.showStoreInOtherAddressType(title, msg.toString());

    }


    /**
     * Use of method
     * <<
     * This method will use to  show invalid store id alert pop up
     * >>
     *
     * @param currentSelectedAddressType This is first parameter that will used to show current selected address type (i.e Dine in, Carhop etc)
     */
    public void showInvalidStoreIdAlertPopUp(String currentSelectedAddressType) {
        String negativeBtnText = getInvalidStoreAlertNegativeBtnText();
        String postiveBtnText = getInvalidStoreAlertPositiveBtnText();
        String msg = getInvalidStoreAlertDescription(currentSelectedAddressType);
        String title = getInvalidStoreAlertTitle();
        callBacks.showQRErrorAlertDialog(title, msg, negativeBtnText, postiveBtnText, new DialogClickListener() {
            @Override
            public void onPositiveBtnClick() {
                if (callBacks != null)
                    callBacks.onSelecteStoreFromListClick();
            }

            @Override
            public void onNegativeBtnClick() {

            }
        });
    }

    /**
     * Use of method
     * <<
     * Used in  Alert dialog for Invalid Store
     * This method will use to get alert dialog title string from config data
     * >>
     *
     * @return This will return alert dialog title string
     */

    private String getInvalidStoreAlertTitle() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getAlertInvalidStoreHeader()))
            return qrConfigErrors.getAlertInvalidStoreHeader();
        else return getContext().getString(R.string.s_store_id_invalid);

    }

    /**
     * Use of method
     * <<
     * Used in  Alert dialog for Invalid Store
     * This method will use to get alert dialog description string from config data
     * >>
     *
     * @return This will return alert dialog description string
     */

    private String getInvalidStoreAlertDescription(String currentSelectedAddressType) {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getAlertInvalidStoreDescription())) {
            if (currentSelectedAddressType != null)
                return qrConfigErrors.getAlertInvalidStoreDescription().replace(QRConstants.TextConstants.ADDRESS_TYPE, currentSelectedAddressType);
            else
                return qrConfigErrors.getAlertInvalidStoreDescription().replace(QRConstants.TextConstants.ADDRESS_TYPE, "");
        } else return getContext().getString(R.string.s_no_store_id_exist);
    }

    /**
     * Use of method
     * <<
     * Used in  Alert dialog for Invalid Store
     * This method will use to get alert dialog positive button string from config data
     * >>
     *
     * @return This will return positive button string used in alert dialog
     */

    private String getInvalidStoreAlertPositiveBtnText() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getAlertInvalidStorePosBtn()))
            return qrConfigErrors.getAlertInvalidStorePosBtn();
        else return getContext().getString(R.string.s_select_store);

    }

    /**
     * Use of method
     * <<
     * Used in  Alert dialog for Invalid Store
     * This method will use to get alert dialog negative button string from config data
     * >>
     *
     * @return This will return negative button string used in alert dialog
     */

    private String getInvalidStoreAlertNegativeBtnText() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getAlertInvalidStoreNegBtn()))
            return qrConfigErrors.getAlertInvalidStoreNegBtn();
        else return getContext().getString(R.string.cancel);

    }

    /**
     * Use of method
     * <<
     * Used in  Alert dialog for Unable to read QR
     * This method will use to get alert dialog negative button string from config data
     * >>
     *
     * @return This will return negative button string used in alert dialog
     */

    private String getUnableToReadQRAlertNegativeBtnText() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getAlertUnableToReadQRNegBtn()))
            return qrConfigErrors.getAlertUnableToReadQRNegBtn();
        else return getContext().getString(R.string.cancel);
    }

    /**
     * Use of method
     * <<
     * Used in  Alert dialog for Unable to read QR
     * This method will use to get alert dialog positive button string from config data
     * >>
     *
     * @return This will return positive button string used in alert dialog
     */
    private String getUnableToReadQRAlertPositiveBtnText() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getAlertUnableToReadQRPosBtn()))
            return qrConfigErrors.getAlertUnableToReadQRPosBtn();
        else return getContext().getString(R.string.s_select_store);
    }


    /**
     * Use of method
     * <<
     * Used in  Alert dialog for Unable to read QR
     * This method will use to get alert dialog title string from config data
     * >>
     *
     * @return This will return title string used in alert dialog
     */
    private String getUnableToReadQRAlertTitle() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getAlertUnableTOReadQRHeader()))
            return qrConfigErrors.getAlertUnableTOReadQRHeader();
        else return getContext().getString(R.string.uh_oh);
    }

    /**
     * Use of method
     * <<
     * Used in  Alert dialog for Unable to read QR
     * This method will use to get alert dialog description string fetched in config data
     * >>
     *
     * @return This will return description string used in alert dialog
     */
    private String getUnableToReadQRAlertDescription() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getAlertUnableToReadQRDes()))
            return qrConfigErrors.getAlertUnableToReadQRDes();
        else return getContext().getString(R.string.s_we_are_facing_to_read_qr);
    }


    /**
     * Use of method
     * <<
     * This method will use to get invalid QR error string fetched in config data
     * >>
     *
     * @return This will return invalid QR error string
     */

    private String getInvlidQRError() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getInvalidQR()))
            return qrConfigErrors.getInvalidQR();
        else
            return getContext().getString(R.string.s_invalid_qr_Code);

    }

    /**
     * Use of method
     * <<
     * This method will use to get  error string when multiple QR detects by the ML kit lib
     * >>
     *
     * @return This will return string to show multiple qr read error
     */

    private String getMultipleQRError() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getUnableTOReadQR()))
            return qrConfigErrors.getUnableTOReadQR();
        else
            return getContext().getString(R.string.s_not_able_to_read_qr);
    }

    /**
     * Use of method
     * <<
     * This method will use to get alignment error string fetched in config data
     * >>
     *
     * @return This will return string to show qr alignment error
     */
    private String getAlignmentQRError() {
        if (qrConfigErrors != null && !TextUtils.isEmpty(qrConfigErrors.getQrAlignmentError()))
            return qrConfigErrors.getQrAlignmentError();
        else
            return getContext().getString(R.string.s_please_align_qr);
    }

    /**
     * Use of method
     * <<
     * This method will use to apply url redirection when get deep link url from the scanned QR through ML kit lib
     * >>
     *
     * @param deepLinkUrl   This is first parameter , that contains deep link fetched from qr code
     * @param isFromGallery This is second parameter, true if deeplink got from galley image , else from camera
     */
    public void extractDeepLink(final String deepLinkUrl, final boolean isFromGallery, final String currentCountry, final String deepLinkScheme) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (callBacks != null && extractQRInstance != null) {
                    if (ivLoader != null) {
                        ivLoader.setVisibility(VISIBLE);
                        ivLoader.animate();
                    }
                    extractQRInstance.extractDeepLink(deepLinkUrl, QRCodeReaderView.this.getContext(), isFromGallery, currentCountry, deepLinkScheme);
                } else {
                    isProcessingQR = false;
                }
            }
        });
    }

    /*
     * Use of method
     * <<
     * This method should be call when activity or fragment will destroy
     * This method will release all the object
     * >>
     */
    public void onClearData() {
        callBacks = null;
        removeHandlerCallback();
        releasePreview();
        animUtil.clearAnimation();
        if (toastOne != null)
            toastOne.cancel();
        toastOne = null;
        animUtil = null;
        qrAlighHandler = null;
        qrAlignRunnable = null;
        extractQRInstance.onClearData();
        extractQRInstance = null;
        boxDetector = null;
        mPreview = null;
        mCameraSource = null;
        barcodeDetector = null;
    }


    /**
     * Use of interface
     * <<
     * This interface will use to identify service type , these type will use in firebase events
     * >>
     */
    public interface QREventCategoryType {
        int CATEGORY_CAR_HOP = 1;
        int CATEGORY_DINE_IN = 2;
        int CATEGORY_DRIVE_THRU = 3;
        int CATEGORY_SELF_PICK_UP = 4;
    }

    public interface DialogClickListener {

        void onPositiveBtnClick();

        void onNegativeBtnClick();

    }

    /**
     * Use of interface
     * <<
     * This interface will use to handle event action performed by this class
     * The interface method will be override in activity or fragment where thsi interface will register
     * >>
     */
    public interface QRCodeDetectorCallBacks {
        void onDetectQRCode(Intent intent, String url);

        void onQRGalleryIconClicked(int requestCode, Intent chooserIntent);

        void onSelecteStoreButtonClick();

        void showProgressDialog(boolean isShow);

        void takeCameraPermission(boolean isShowRational);

        void onSelecteStoreFromListClick();

        void onFireQREvents(String eventCategory, String eventLabel, String eventAction, String deeplink);

        void showQRErrorAlertDialog(String title, String msg, String negativeBtnText, String postiveBtnText, DialogClickListener listener);

        void showStoreInOtherAddressType(String title, String addressType);
    }

}
