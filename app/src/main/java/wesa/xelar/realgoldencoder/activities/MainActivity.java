package wesa.xelar.realgoldencoder.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import wesa.xelar.realgoldencoder.R;
import wesa.xelar.realgoldencoder.encodedecode.Alice;
import wesa.xelar.realgoldencoder.encodedecode.AliceContext;
import wesa.xelar.realgoldencoder.encodedecode.AliceContextBuilder;
import wesa.xelar.realgoldencoder.model.UserData;
import wesa.xelar.realgoldencoder.network.APIHelper;
import wesa.xelar.realgoldencoder.network.APIService;
import wesa.xelar.realgoldencoder.network.Is_Online;
import wesa.xelar.realgoldencoder.network.RestClient;
import wesa.xelar.realgoldencoder.utils.Dp_to_Px;
import wesa.xelar.wesapermissions.PermissionHandler;
import wesa.xelar.wesapermissions.Permissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private LottieAnimationView loading;
    TextInputEditText idNumber;
    Button requestData;
    LinearLayout tapCardLayout;

    ActivityResultLauncher<Intent> attemptWriteIntent;

    private NfcAdapter nfcAdapter = null;

    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];

    ImageView avatar;
    TextView userInfo;
    LinearLayout infoLayout;
    String passportInfo = "";
    Alice alice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alice = new Alice(new AliceContextBuilder().build());
        PackageManager pm = this.getPackageManager();
        boolean isNFCSupported = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
        if (isNFCSupported) {
            setUpViews();
        } else {
            Toast.makeText(this, "Device doesn't supports NFC", Toast.LENGTH_LONG).show();
        }
    }

    private void getUserData(String IDNumber) {
        if (new Is_Online(this).Is_Connected()){
            Retrofit retrofit=new RestClient().getClientLog();
            APIService api=retrofit.create(APIService.class);


            List<String> list = new ArrayList<>();
            list.add("collaterals");

            Call<ResponseBody> executeNetworkRequest = api.getData(IDNumber,"a763593aa22f86188562db37b96fccf7");
            loading.setVisibility(View.VISIBLE);
            APIHelper.enqueueWithRetry(executeNetworkRequest,1,new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    loading.setVisibility(View.GONE);
                    String instructions = "";

                    int responseCode = response.code();
                    if (responseCode == 200) {

                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());

                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.optJSONObject(0);

                                Gson gson = new Gson();
                                Type type = new TypeToken<UserData>(){}.getType();

                                UserData userData = gson.fromJson(jsonObject.toString(), type);

                                tapCardLayout.setVisibility(View.VISIBLE);
                                passportInfo = "ID Number : " + userData.getId() + "\n"
                                        + "Given Name : " + userData.getGivenNames() + "\n"
                                        + "Surname : " + userData.getSurname() + "\n"
                                        + "Passport : " + userData.getPassport() + "\n"
                                        + "Visa Number : " + userData.getVisaNumber() + "\n"
                                        + "Valid From : " + userData.getValidFrom() + "\n"
                                        + "Valid To : " + userData.getValidTo() + "\n"
                                        + "Foreigner Number : " + userData.getForeignerNo() + "\n"
                                        + "Registration Date : " + userData.getRegistrationDate() + "\n"
                                        + "Sponsor Name : " + userData.getSponsorName() + "\n"
                                        + "Nationality : " + userData.getNationality() + "\n"
                                        + "Sex : " + userData.getSex() + "\n"
                                        + "Picture URL : " + userData.getPhoto();

                            }else {
                                Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
                            instructions = jsonObject.optString("message");
                            Snackbar.make(loading, instructions,
                                    Snackbar.LENGTH_LONG)
                                    .show();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    loading.setVisibility(View.GONE);
                    Snackbar.make(loading, "Network Timeout. Please retry.",
                            Snackbar.LENGTH_LONG)
                            .show();
                }
            });


        }else {
            Toast.makeText(this, "No connection", Toast.LENGTH_LONG).show();
        }
    }

    private void setUpViews() {
        loading = findViewById(R.id.anim);
        idNumber = findViewById(R.id.id_number);

        requestData = findViewById(R.id.submit);
        requestData.setOnClickListener(this);
        tapCardLayout = findViewById(R.id.tap_card_layout);

        attemptWriteIntent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        tapCardLayout.setVisibility(View.GONE);
                        idNumber.setText("");
                    }else {
                        Toast.makeText(this, "No picture selected", Toast.LENGTH_LONG).show();
                    }
                });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Please enable missing permissions to use this service.", Toast.LENGTH_LONG).show();
            checkPermissions();
        }else {
            requestData.setEnabled(true);
            nfcAdapter = NfcAdapter.getDefaultAdapter(this);
            pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
            writeTagFilters = new IntentFilter[] { tagDetected };
        }

        avatar = findViewById(R.id.avatar);
        userInfo = findViewById(R.id.info_textview);
        infoLayout = findViewById(R.id.read_data_container);

        AliceContext aliceContext = new AliceContextBuilder()
                .setAlgorithm(AliceContext.Algorithm.AES)
                .setMode(AliceContext.Mode.CBC) // or AliceContext.Mode.CTR
                .setIvLength(16)
                .build();

    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.NFC};
        Permissions.check(this/*context*/, permissions, "The permissions are required to allow you upload a suitable picture for your profile."/*rationale*/, null/*options*/, new PermissionHandler() {
            @Override
            public void onGranted() {
                requestData.setEnabled(true);
                nfcAdapter = NfcAdapter.getDefaultAdapter(MainActivity.this);
                pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, new Intent(MainActivity.this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
                tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
                writeTagFilters = new IntentFilter[] { tagDetected };
            }

            @Override
            public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                super.onDenied(context, deniedPermissions);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onClick(View view) {
        if (view == requestData) {
            closeKeyboard();
            idNumber.setError(null);

            String id_number = idNumber.getText().toString().trim();

            if (TextUtils.isEmpty(id_number)) {
                idNumber.setError("Required!");
            }else {
                getUserData(id_number);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Please enable missing permissions to use this service.", Toast.LENGTH_LONG).show();
            checkPermissions();
        }else {
            if (nfcAdapter != null) {
                nfcAdapter.enableForegroundDispatch(this,pendingIntent, writeTagFilters,null);
            }
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        /*Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        MifareClassic mifareClassic = MifareClassic.get(tag);*/

        /*if (tapCardLayout.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(passportInfo)) {
            try {
                if (!mifareClassic.isConnected()) {
                    mifareClassic.connect();
                }

                if (mifareClassic.authenticateSectorWithKeyA(1, MifareClassic.KEY_NFC_FORUM)) {
                    String data = "lover" + "C1C2C3" + "lover";
                    mifareClassic.writeBlock(7, data.getBytes(StandardCharsets.UTF_8));
                }else if (mifareClassic.authenticateSectorWithKeyA(1, MifareClassic.KEY_DEFAULT)){
                    String data = "lover" + "C1C2C3" + "lover";
                    //Log.e("Byte length", data.getBytes(StandardCharsets.UTF_8).length + "");
                    mifareClassic.writeBlock(7, data.getBytes(StandardCharsets.UTF_8));
                }else if (mifareClassic.authenticateSectorWithKeyA(1, MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY)){

                    String data = "lovers" + "C1C2C3" + "lovers";
                    mifareClassic.writeBlock(7, data.getBytes(StandardCharsets.UTF_8));

                }else if (mifareClassic.authenticateSectorWithKeyA(1, "lover".getBytes(StandardCharsets.UTF_8))){
                    String data = "lover" + "C1C2C3" + "lover";
                    mifareClassic.writeBlock(7, data.getBytes(StandardCharsets.UTF_8));

                }else {
                    Snackbar.make(loading, "Authentication failed.",
                            Snackbar.LENGTH_LONG)
                            .show();
                }

                mifareClassic.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Proceed to read data?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Read", (dialog, which) -> {
                try {
                    if (!mifareClassic.isConnected()) {
                        mifareClassic.connect();
                    }
                    boolean auth = mifareClassic.authenticateSectorWithKeyA(1, "lover".getBytes(StandardCharsets.UTF_8));
                    if (auth) {
                        Log.e("Sector Trail", new String(mifareClassic.readBlock(7)));
                    }else {
                        Snackbar.make(loading, "Authentication failed.",
                                Snackbar.LENGTH_LONG)
                                .show();
                    }

                    mifareClassic.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            builder1.setNegativeButton(
                    "Cancel",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }*/


        if (tapCardLayout.getVisibility() == View.VISIBLE && !TextUtils.isEmpty(passportInfo)) {

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            Ndef ndef = Ndef.get(tag);
            boolean writeSuccess = NFCUtil.INSTANCE.createNFCMessage(passportInfo,intent);

            if (writeSuccess) {
                tapCardLayout.setVisibility(View.GONE);
                idNumber.setText("");
                Snackbar.make(loading, "Passport info written successfully",
                        Snackbar.LENGTH_LONG)
                        .show();

                /*AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setMessage("Do you want to make this tag Read Only?");
                builder1.setCancelable(true);
                builder1.setPositiveButton("Proceed", (dialog, which) -> {
                    if (ndef.canMakeReadOnly()) {
                        try {
                            if (ndef.isConnected()) {
                                ndef.makeReadOnly();
                                Snackbar.make(loading, "Passport now read only.",
                                        Snackbar.LENGTH_LONG)
                                        .show();
                            }else {
                                Toast.makeText(this, "Could not make tag read only.", Toast.LENGTH_LONG).show();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Could not make tag read only.", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                builder1.setNegativeButton(
                        "Cancel",
                        (dialog, id) -> dialog.cancel());

                AlertDialog alert11 = builder1.create();
                alert11.show();*/

            }else {
                Snackbar.make(loading, "Could not write data, please try again",
                        Snackbar.LENGTH_LONG)
                        .show();
            }

            /*if (ndef.isWritable()) {
                boolean writeSuccess = NFCUtil.INSTANCE.createNFCMessage(passportInfo,intent);

                if (writeSuccess) {
                    tapCardLayout.setVisibility(View.GONE);
                    idNumber.setText("");
                    Snackbar.make(loading, "Passport info written successfully",
                            Snackbar.LENGTH_LONG)
                            .show();

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setMessage("Do you want to make this tag Read Only?");
                    builder1.setCancelable(true);
                    builder1.setPositiveButton("Proceed", (dialog, which) -> {
                        if (ndef.canMakeReadOnly()) {
                            try {
                                if (ndef.isConnected()) {
                                    ndef.makeReadOnly();
                                    Snackbar.make(loading, "Passport now read only.",
                                            Snackbar.LENGTH_LONG)
                                            .show();
                                }else {
                                    Toast.makeText(this, "Could not make tag read only.", Toast.LENGTH_LONG).show();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(this, "Could not make tag read only.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                    builder1.setNegativeButton(
                            "Cancel",
                            (dialog, id) -> dialog.cancel());

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                }else {
                    Snackbar.make(loading, "Could not write data, please try again",
                            Snackbar.LENGTH_LONG)
                            .show();
                }



            }else {
                Snackbar.make(loading, "This NFC tag is read only and cannot be written into!",
                        Snackbar.LENGTH_LONG)
                        .show();
            }
*/

        }else {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
            builder1.setMessage("Proceed to read data?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Read", (dialog, which) -> {
                readFromIntent(intent);
            });

            builder1.setNegativeButton(
                    "Cancel",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    private void showReadData(String readData) {
        if (!TextUtils.isEmpty(readData)) {
            infoLayout.setVisibility(View.VISIBLE);
            tapCardLayout.setVisibility(View.GONE);


            String[] separatedString = readData.split("\n");
            String avatarURL = separatedString[separatedString.length - 1];
            String [] endpoints = avatarURL.split("/");
            String endpoint = endpoints[endpoints.length - 1];

            String display_data = "";
            for (int i = 0; i < separatedString.length; i++) {

                if (i != separatedString.length - 1) {
                    if (i == 0) {
                        display_data = display_data.concat(separatedString[i]).trim();
                    } else {
                        display_data = display_data.concat(separatedString[i]);
                    }
                    display_data = display_data.concat("\n\n");
                }
            }

            userInfo.setText(display_data);

            Log.e("Avatar url", endpoint);

            Picasso.get().load("http://e-qaran.so:5555/" + endpoint).placeholder(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), R.drawable.bussiness_man, null))).centerInside().
                    resize(Dp_to_Px.dpToPx(MainActivity.this,250),Dp_to_Px.dpToPx(MainActivity.this,250)).into(avatar, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;
        String text = "";

        byte[] payload = msgs[0].getRecords()[0].getPayload();
        try {
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
            int languageCodeLength = payload[0] & 0063;
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
            Log.e("Dump", text);
            showReadData(text);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void closeKeyboard() {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {
            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
}