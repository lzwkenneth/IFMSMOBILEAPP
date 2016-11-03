
package kenneth.jf.siaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;

import kenneth.jf.siaapp.R;

import static android.app.Activity.RESULT_OK;

import static kenneth.jf.siaapp.R.layout.qr_scanning;

public class QR_Scanner extends Fragment {
    View myView;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    String discountCode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(qr_scanning,container,false);
        Button scanner = (Button)myView.findViewById(R.id.scanner);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQR(myView);
            }
        });

       /* Button scanner1 = (Button)myView.findViewById(R.id.scanner2);
        scanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanBar(myView);
            }
        });*/



        return myView;
    }

    //product barcode mode
/*    public void scanBar(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(getActivity(), "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }*/

    //product qr code mode
    public void scanQR(View v) {
        try {
            //start the scanning activity from the com.google.zxing.client.android.SCAN intent
            Intent intent = new Intent(ACTION_SCAN);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(getActivity(), "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
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

    //on ActivityResult method
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //get the extras that are returned from the intent
                discountCode = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                //Toast toast = Toast.makeText(this.getActivity(), "Content:" + discountCode + " Format:" + format, Toast.LENGTH_LONG);
                //toast.show();

                try {
                    new viewDiscount().execute().get();
                    Toast.makeText(getActivity(), "DISCOUNT CODE: "+ discountObj.getDiscountMessage(),Toast.LENGTH_LONG).show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


            }
        }
    }
    private RestTemplate restTemplate = ConnectionInformation.getInstance().getRestTemplate();
    private String url = ConnectionInformation.getInstance().getUrl();
    Discount discountObj;
    private class viewDiscount extends AsyncTask<Void, Void, String> {


        protected String doInBackground(Void... params) {
            Log.d("TAG", "DO IN BACKGROUND");
            try {
                JSONObject obj = new JSONObject();
                obj.put("discount", discountCode);  //must be discount code  == "12345"
                //obj.put("discount", "12345");
                HttpEntity<String> request2 = new HttpEntity<String>(obj.toString(),ConnectionInformation.getInstance().getHeaders());
                Log.d("DISCOUNT REQUEST", ConnectionInformation.getInstance().getHeaders().getAccept().toString());
                String url2 = "https://" + url + "/viewDiscount";

                Log.d("TAG", "BEFORE VERIFYING" + restTemplate.getMessageConverters().toString());
                Log.d("TAG",request2.toString());
                // Log.d("TAG",request2.getBody());
                ResponseEntity<Discount> responseEntity = restTemplate.exchange(url2, HttpMethod.POST, request2, Discount.class);
                discountObj = responseEntity.getBody();
                System.out.println("RESPONSE ENTITY: " + responseEntity.getBody().getDiscountMessage());





            } catch (Exception e) {
                Log.e("TAG", e.getMessage(), e);
                Toast.makeText(getActivity(), "NO DISCOUNT STORED FOR THIS QR CODE", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        protected void onPostExecute(String greeting) {

            Log.d("TAG", "DO POST EXECUTE");

        }
    }




}