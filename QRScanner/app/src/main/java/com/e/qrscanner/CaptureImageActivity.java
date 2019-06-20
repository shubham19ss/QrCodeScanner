package com.e.qrscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CaptureImageActivity extends AppCompatActivity {
    private final int requestCode = 10;
    private ImageView imageviewer;
    private Button decodeButton;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image);
        decodeButton = (Button) findViewById(R.id.decodeButton);
        decodeButton.setEnabled(false);
    }

    public void launchCamera(View view) {
        Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(in, requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (this.requestCode == requestCode && resultCode == RESULT_OK) {
            imageviewer = (ImageView) findViewById(R.id.capturedImage);
            bitmap = (Bitmap) data.getExtras().get("data");
            imageviewer.setImageBitmap(bitmap);
            decodeButton.setEnabled(true);
        }
    }

    public void decodeQrcode(View view) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        scanBarcodes(image);
    }

    private void scanBarcodes(FirebaseVisionImage image) {
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                .setBarcodeFormats(
                        FirebaseVisionBarcode.FORMAT_ALL_FORMATS)

                .build();
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                .getVisionBarcodeDetector(options);
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        List<QrData> list = new ArrayList<QrData>();

                        for (FirebaseVisionBarcode barcode : barcodes) {
                            String Type = "";
                            String value = "";
                            String details = "";
                            Rect bounds = barcode.getBoundingBox();
                            Point[] corners = barcode.getCornerPoints();

                            String rawValue = barcode.getRawValue();

                            int valueType = barcode.getValueType();

                            switch (valueType) {
                                case FirebaseVisionBarcode.TYPE_WIFI:
                                    String ssid = barcode.getWifi().getSsid();
                                    String password = barcode.getWifi().getPassword();
                                    int type = barcode.getWifi().getEncryptionType();
                                    Type = "WIFI";
                                    value = "SSID :" + ssid + ", " + "Password: " + password;
                                    details = "<b>Type:<b> " + "WIFI" + "\n" + "<b>SSID: <b> " + ssid + "\n" + "<b>PASSWORD: <b>" + password + "\n" + "<b>ENCRYPTION: <b>" + String.valueOf(type);
                                    break;
                                case FirebaseVisionBarcode.TYPE_URL:
                                    String title = barcode.getUrl().getTitle();
                                    String url = barcode.getUrl().getUrl();
                                    Type = "URL";
                                    value = "URL: " + rawValue;
                                    details = "<b>Type:<b> " + "URL" + "\n" + "<b>URL: <b> " + rawValue + "\n" + "<b>TITLE: <b>" + title;
                                    break;
                                case FirebaseVisionBarcode.TYPE_PHONE:
                                    String number = barcode.getPhone().getNumber();
                                    int types = barcode.getPhone().getType();
                                    Type = "PHONE";
                                    value = "Phone Number: " + number;
                                    details = "<b>Type:<b> " + "PHONE" + "\n" + "<b>PHONE-TYPE: <b> " + String.valueOf(types) + "\n" + "<b>Number: <b>" + number;
                                    break;
                                case FirebaseVisionBarcode.TYPE_SMS:
                                    String message = barcode.getSms().getMessage();
                                    String phonenumber = barcode.getSms().getPhoneNumber();
                                    Type = "SMS";
                                    value = "Message: " + message;
                                    details = "<b>Type:<b> " + "SMS" + "\n" + "<b>Number: <b> " + phonenumber + "\n" + "<b>Message: <b>" + message;
                                    break;
                                case FirebaseVisionBarcode.TYPE_EMAIL:
                                    String address = barcode.getEmail().getAddress();
                                    String body = barcode.getEmail().getBody();
                                    String subject = barcode.getEmail().getSubject();
                                    Type = "EMAIL";
                                    value = "Message: " + body;
                                    details = "<b>Type:<b> " + "EMAIL" + "\n" + "<b>From Email: <b> " + address + "\n" + "<b>Subject: <b>" + subject + "\n" + "<b>Body: <b>" + body;
                                    break;
                                case FirebaseVisionBarcode.TYPE_TEXT:
                                    Type = "TEXT";
                                    value = "Text: " + rawValue;
                                    details = "<b>Type:<b> " + "TEXT" + "\n" + "<b>Data: <b> " + rawValue;
                                    break;
                                case FirebaseVisionBarcode.TYPE_GEO:
                                    double lat = barcode.getGeoPoint().getLat();
                                    double lon = barcode.getGeoPoint().getLng();
                                    Type = "GEO";
                                    value = "Lat: " + String.valueOf(lat) + " long: " + String.valueOf(lon);
                                    details = "<b>Type:<b> " + "GEO" + "\n" + "<b>Lattitude: <b> " + String.valueOf(lat) + "\n" + "<b>Longitude: <b>" + String.valueOf(lon);
                                    break;
                                default:
                                      Toast.makeText(getApplicationContext(), "TypeNotFound", Toast.LENGTH_SHORT).show();
                                    break;


                            }
                            list.add(new QrData(Type, value, details));
                        }





if(list.size()>0) {
    Intent in = new Intent(CaptureImageActivity.this, DisplayQrDataActivity.class);
    in.putExtra("qrlist", (Serializable) list);
    startActivity(in);
}
else
{
    Toast.makeText(getApplicationContext(),"No Codes found!!Recapture the image",Toast.LENGTH_SHORT).show();
}
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Toast.makeText(getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();

                    }
                });
    }


}

