package com.developer.skylight.certificateinstalldemo.activity;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.security.KeyChain;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.developer.skylight.certificateinstalldemo.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                installCertificate();
            }
        });
    }

    private void installCertificate() {

        String CERT_FILE = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/charles-proxy-ssl-proxying-certificate.pem";

        File file = new File(CERT_FILE);


        Log.d(TAG, "installCertificate: CERT_FILE path  : "+CERT_FILE +"  status : "+file.exists());


        Intent intent = KeyChain.createInstallIntent();
        try {

            X509Certificate x509 = getCertFromFile(CERT_FILE);
            if(x509 == null){
                return;
            }
            intent.putExtra(KeyChain.EXTRA_CERTIFICATE, x509.getEncoded());
            startActivityForResult(intent, 0);  // this works but shows UI
            sendBroadcast(intent);  // this doesn't install cert
        } catch (IOException e) {
            Log.e(TAG, "getCertFromFile: IOException : "+e.getMessage() );

            e.printStackTrace();
        } catch (CertificateEncodingException e) {
            Log.e(TAG, "getCertFromFile: CertificateEncodingException : "+e.getMessage() );

            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG, "getCertFromFile: Exception : "+e.getMessage() );

            e.printStackTrace();
        }
    }

    private X509Certificate getCertFromFile(String path) throws Exception {
        AssetManager assetManager = getResources().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("charles-proxy-ssl-proxying-certificate.pem");
        } catch (IOException e) {
            Log.e(TAG, "getCertFromFile: IOException : "+e.getMessage() );
            e.printStackTrace();
            return null;
        }
        InputStream caInput = new BufferedInputStream(inputStream);
        X509Certificate cert = null;
        CertificateFactory cf = CertificateFactory.getInstance("X509");
        cert = (X509Certificate) cf.generateCertificate(caInput);
        cert.getSerialNumber();
        return cert;
    }

}
