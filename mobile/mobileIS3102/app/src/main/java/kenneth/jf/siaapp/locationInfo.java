package kenneth.jf.siaapp;

/**
 * Created by Kenneth on 6/11/2016.
 */

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static java.lang.System.load;
import static kenneth.jf.siaapp.R.id.ttttt;

/**
 * Created by User on 24/10/2016.
 */

public class locationInfo extends Fragment {
    View myView;
    int i;
    private RestTemplate restTemplate = ConnectionInformation.getInstance().getRestTemplate();
    private String url = ConnectionInformation.getInstance().getUrl();
    TextView title;
    LocationListObject building;

    TextView desc;
    TextView address;
    ImageView img;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        try {


            final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Getting Location Info");
            progressDialog.show();
            new viewAnEvent().execute().get();
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            if (ConnectionInformation.getInstance().getAuthenticated()) {
                                Log.d("TAG", "After authenticated");

                                title = (TextView) myView.findViewById(R.id.eventTitle123b);
                                //  type = (TextView) myView.findViewById(R.id.eventType);

                                desc = (TextView) myView.findViewById(R.id.eventDescriptionb);
                                address = (TextView) myView.findViewById(R.id.eventAddressb);
                                img = (ImageView) myView.findViewById(R.id.tttttb);
                                final TextView vvv = (TextView) myView.findViewById(R.id.textLoadingb);


                                //System.out.println("REACHED HERE AT EVENT SHOW INFO: " + eventDetail.getTitle());

                                title.setText(building.getName());
                                // type.setText(eventDetail.getType());
                                StringBuilder sb = new StringBuilder();
                                sb.append(building.getAddress());
                                sb.append("\n");
                                sb.append(building.getCity() + ", " + building.getPostalCode());
                                address.setText(sb.toString());
                                desc.setText("Number of floors: " + building.getNumFloor());
                                System.out.println("https://" + ConnectionInformation.getInstance().getUrl() + "/" + building.getPicPath());

                                if ( (building.getPicPath() == null) ){
                                    vvv.setText("No building image uploaded");
                                }
                                else if (  !building.getPicPath().equals("null") && !building.getPicPath().equals("")) {                                    PicassoTrustAll.getInstance(getContext())
                                            .load("https://" + ConnectionInformation.getInstance().getUrl() + "/" + building.getPicPath())
                                            //      .into(img);
                                            .into(img, new Callback() {
                                                @Override
                                                public void onSuccess() {
                                                    vvv.setVisibility(View.GONE);
                                                }

                                                @Override
                                                public void onError() {

                                                }


                                                //address.setText(eventDetail.getAddress().toString());
                                            });
                                }
                                else{
                                    vvv.setText("No building image uploaded");
                                }
                            } else {
                                Log.d("TAG", "After NOT authenticated");

                            }
                            // onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }

                    , 3500);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        myView = inflater.inflate(R.layout.show_location_info, container, false);


        return myView;
    }


    private class viewAnEvent extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            Log.d("TAG", "DO IN BACKGROUND");
            try {
                JSONObject request = new JSONObject();
                //Event ID
                //long value = getActivity().getIntent().getIntExtra("eventId",1);
               String value = getActivity().getIntent().getStringExtra("eventId");
                Long lvalue = Long.valueOf(value);
                request.put("eventId", lvalue);
               // System.out.println("LOCATION ID OF VIEWANEVENT in locationInfo: " + ConnectionInformation.getInstance().getBuildingId());


                HttpEntity<String> request2 = new HttpEntity<String>(request.toString(), ConnectionInformation.getInstance().getHeaders());
                Log.d("TAGGGGGGGGREQUEST", ConnectionInformation.getInstance().getHeaders().getAccept().toString());
                String url2 = "https://" + url + "/tixViewBuilding";

                Log.d("TAG", "BEFORE VERIFYING" + restTemplate.getMessageConverters().toString());
                Log.d("TAG", request2.toString());
                // Log.d("TAG",request2.getBody());
                ResponseEntity<LocationListObject> responseEntity = restTemplate.exchange(url2, HttpMethod.POST, request2, LocationListObject.class);

                building = responseEntity.getBody();
                Log.d("loopforeventlistobject", responseEntity.getBody().getAddress());


            } catch (Exception e) {
                Log.e("TAG", e.getMessage(), e);
            }

            return null;
        }


        protected void onPostExecute(String greeting) {

            Log.d("TAG", "DO POST EXECUTE");
        }


    }


}
