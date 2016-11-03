package kenneth.jf.siaapp;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;


/**
 * Created by User on 16/10/2016.
 */

public class location extends Fragment {
    View myView;
    boolean test = true;

    private RestTemplate restTemplate = ConnectionInformation.getInstance().getRestTemplate();
    private String url = ConnectionInformation.getInstance().getUrl();
    ArrayList<LocationListObject> locationList = new ArrayList<LocationListObject>();
    ArrayList<LocationListObject> locationList2 = new ArrayList<LocationListObject>();

    FragmentManager fragmentManager = getFragmentManager();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving the locations...");
        progressDialog.show();
        new viewLocations().execute();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if ( ConnectionInformation.getInstance().getAuthenticated() ){
                            Log.d("TAG","After authenticated");
                            displayListView();
                            //checkButtonClick();
                        }
                        else{
                            Log.d("TAG","After NOT authenticated");

                        }
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 5000);
        myView = inflater.inflate(R.layout.location,container,false);

        return myView;
    }

    MyCustomAdapter dataAdapter = null;

    private class viewLocations extends AsyncTask<Void, Void, String> {


        protected String doInBackground(Void... params) {
            Log.d("TAG", "DO IN BACKGROUND");
            try {

                HttpEntity<String> request2 = new HttpEntity<String>(ConnectionInformation.getInstance().getHeaders());
                Log.d("TAGGGGGGGGREQUEST", ConnectionInformation.getInstance().getHeaders().getAccept().toString());
                String url2 = "https://" + url + "/building/viewBuildings";
                Log.d("TAG", "BEFORE VERIFYING" + restTemplate.getMessageConverters().toString());
                Log.d("TAG",request2.toString());
                // Log.d("TAG",request2.getBody());
                ResponseEntity<LocationListObject[]> responseEntity = restTemplate.exchange(url2, HttpMethod.GET, request2, LocationListObject[].class);

                for ( LocationListObject m : responseEntity.getBody()){
                    LocationListObject obj = new LocationListObject();
                    obj.setName(m.getName());
                    obj.setAddress(m.getAddress());
                    obj.setCity(m.getCity());
                    obj.setNumFloor(m.getNumFloor());
                    obj.setPostalCode(m.getPostalCode());
                    locationList.add(obj);
                    //return list
                }


            } catch (Exception e) {
                Log.e("TAG", e.getMessage(), e);
            }

            return null;
        }

        protected void onPostExecute(String greeting) {

            Log.d("TAG", "DO POST EXECUTE");
            Log.d("location: " , String.valueOf(locationList.size()));

        }
    }

    //location LISTING
    private void displayListView() {
        //Array list of locations

        //create an ArrayAdaptar from the String Array
        // locationList = list;
        System.out.println("Size: " + locationList.size());
        dataAdapter = new MyCustomAdapter(getActivity(), R.layout.location_info, locationList);
        ListView listView = (ListView) myView.findViewById(R.id.locationLV);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                LocationListObject location = (LocationListObject) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(),
                        "Clicked on Row: " + location.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<LocationListObject> {

        private ArrayList<LocationListObject> locationList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<LocationListObject> locationList) {
            super(context, textViewResourceId, locationList);
            this.locationList = new ArrayList<>();
            this.locationList.addAll(locationList);
        }

        private class ViewHolder {
            TextView code;
            //CheckBox name;
            Button locationInfo;
            Button locationList;
        }


        //suppose to populate the arraylist with locationlistobject
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.location_info, null);

                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.codeLocation);
                //holder.name = (CheckBox) convertView.findViewById(R.id.checkBoxlocation);
                holder.locationInfo = (Button) convertView.findViewById(R.id.locationInfo);

                convertView.setTag(holder);

               /* holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        LocationListObject location = (LocationListObject) cb.getTag();
                        Toast.makeText(getActivity(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        //location.setSelected(cb.isChecked());

                        //retrieve location Details From Backend

                    }
                });*/


            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }

            LocationListObject location = locationList.get(position);
            holder.code.setText(location.getName());
            //holder.name.setChecked(location.isSelected());
            holder.code.setTag(location);
            holder.locationInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button cb = (Button) view ;
                    LocationListObject location = (LocationListObject) cb.getTag();
                    int pos = position +1;
                    //send details using bundle to the next fragment
                    Intent intent = new Intent(getActivity(),  dashboard.class);
                    intent.putExtra("key2", "locationInfo");
                    intent.putExtra("locationId", pos);
                    startActivity(intent);


                }
            });
            /*holder.locationList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button cb = (Button) view ;
                    location location = (location)cb.getTag();
                    int pos = position +1;
                    //send details using bundle to the next fragment
                    Intent intent = new Intent(getActivity(),  dashboard.class);
                    intent.putExtra("key2", "location");
                    intent.putExtra("locationId", String.valueOf(pos));
                    System.out.println("FROM POSITION in location Listing: " + pos);
                    startActivity(intent);


                }
            });*/
            return convertView;

        }
    }

    /*private void checkButtonClick() {

        Button myButton = (Button) myView.findViewById(R.id.findSelected);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v("Index removed: ", "fkkkk");
                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");
                //this list shows the locations that are selected
                ArrayList<LocationListObject> locationList = dataAdapter.locationList;
                for(int i=0;i<locationList.size();i++){
                    LocationListObject location = locationList.get(i);

                    if(location.isSelected()){
                        responseText.append("\n" + location.getName());
                    }
                }
                Toast.makeText(getActivity(),
                        responseText, Toast.LENGTH_LONG).show();

            }
        });

    }*/
}