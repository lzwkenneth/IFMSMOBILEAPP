package kenneth.jf.verificationapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by User on 7/11/2016.
 */

public class VerificationPage extends Activity{


    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    String discountCode;

    private RestTemplate restTemplate = ConnectionInformation.getInstance().getRestTemplate();
    private String url = ConnectionInformation.getInstance().getUrl();
    Discount discountObj;
    // SweetAlertDialog errorAlert;
    //SweetAlertDialog successAlert;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading QR Scanner");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // scanQR(myView);
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }

                , 500);

        // ImageView imageView = (ImageView)myView.findViewById(R.id.ttttt);
        Button scanner = (Button) findViewById(R.id.scanner);

        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQR(VerificationPage.this);
            }
        });
    }

    //product qr code mode
    public void scanQR(VerificationPage v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    /*    @Override
        public void onConfigurationChanged(Configuration newConfig){
            super.onConfigurationChanged(newConfig);
        }*/
    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        System.out.println("ACTVITIY DONE OK");
        if (requestCode == 0) {
            System.out.println("ACTVITIY DONE OK" + requestCode);
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                discountCode = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //Toast toast = Toast.makeText(this.this, "Content:" + discountCode + " Format:" + format, Toast.LENGTH_LONG);
                //toast.show();
                System.out.println("AFtER ok " + discountCode + "   " + format);
                TextView message = (TextView)findViewById(R.id.msg);
                message.setVisibility(View.INVISIBLE);
                try {
                    System.out.println("TRYYYACTVITIY DONE OK");
                    new viewDiscount().execute().get();

                    //message.setText("OK");
                    //message.setVisibility(View.VISIBLE);

                    //Toast.makeText(this, "DISCOUNT CODE: " + discountObj.getDiscountMessage(), Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        }
    }


    private class viewDiscount extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            Log.d("TAG", "DO IN BACKGROUND");
            try {
                JSONObject obj = new JSONObject();
                obj.put("discount", discountCode.toString());  //must be discount code  == "12345"
                //obj.put("discount", "12345");
                HttpEntity<String> request2 = new HttpEntity<String>(obj.toString(), ConnectionInformation.getInstance().getHeaders());
                Log.d("DISCOUNT REQUEST", ConnectionInformation.getInstance().getHeaders().getAccept().toString());
                String url2 = "https://" + url + "/redeemTicket";

                Log.d("TAG", "BEFORE VERIFYING" + restTemplate.getMessageConverters().toString());
                Log.d("TAG", request2.toString());
                // Log.d("TAG",request2.getBody());
                ResponseEntity<Integer> responseEntity = restTemplate.exchange(url2, HttpMethod.POST, request2, Integer.class);
                HttpStatus response = responseEntity.getStatusCode();
                if(response.equals(HttpStatus.OK)){
                    new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("Congratulations!")
                            .setContentText("OK VERIFIED")
                            .show();
                }
                else{
                    new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.SUCCESS_TYPE).setTitleText("Congratulations!")
                            .setContentText("NOTTTTTT VERIFIED")
                            .show();
                }


            } catch (Exception e) {
                Log.e("TAG", e.getMessage(), e);
                Toast.makeText(getApplicationContext(), "NO DISCOUNT STORED FOR THIS QR CODE", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String greeting) {
            Log.d("TAG", "DO POST EXECUTE");

        }
    }



}
