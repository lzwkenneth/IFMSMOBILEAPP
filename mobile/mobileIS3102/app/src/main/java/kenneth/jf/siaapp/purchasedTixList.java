package kenneth.jf.siaapp;

import android.app.Fragment;
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
import android.widget.AdapterView.OnItemClickListener;
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
import java.util.concurrent.ExecutionException;


/**
 * Created by User on 28/10/2016.
 */



// After payment is confirmed and the tickets are supposed to show the qr codes from the backend
public class purchasedTixList extends Fragment{
    View myView;
    ListView lv;
    ArrayList<TicketObject> list = new ArrayList<>();
    private RestTemplate restTemplate = ConnectionInformation.getInstance().getRestTemplate();
    private String url = ConnectionInformation.getInstance().getUrl();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.purchased_ticketlist, container, false);
        try {
            new viewPurchasedTix().execute().get();
         /*   for(int i=0;i<list.size();i++){
                System.err.println("LISTVIEW: "+ list.size());
            }*/

            lv = (ListView)myView.findViewById(R.id.purchasedTixList);
           // TicketObject[] arr = (TicketObject[]) list.toArray();
            lv.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, list));
            lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    TicketObject o = (TicketObject) lv.getItemAtPosition(position);
                    String qr = o.getTicketUUID();
                    Intent intent = new Intent(getActivity(), dashboard.class);
                    intent.putExtra("key2", "showQRcode");
                    intent.putExtra("qrcode", qr);
                    startActivity(intent);
                }
            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return myView;
    }

    private class viewPurchasedTix extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            Log.d("TAG", "DO IN BACKGROUND");
            try {


                HttpEntity<String> request2 = new HttpEntity<String>(ConnectionInformation.getInstance().getHeaders());
                Log.d("TAGGGGGGGGREQUEST", ConnectionInformation.getInstance().getHeaders().getAccept().toString());
                String url2 = "https://" + url + "/tixGetTix";


                Log.d("TAG", "BEFORE VERIFYING" + restTemplate.getMessageConverters().toString());
                Log.d("TAG",request2.toString());
                // Log.d("TAG",request2.getBody());
                ResponseEntity<TicketObject[]> responseEntity = restTemplate.exchange(url2, HttpMethod.GET, request2, TicketObject[].class);
                Log.d("TAG",responseEntity.getStatusCode().toString());



                for ( TicketObject o : responseEntity.getBody()){
                    TicketObject ticketObject = o;
                    list.add(ticketObject);
                   // System.err.println("Tickets: " + o.getTicketDetails());
                }

                // ResponseEntity<CategoryObject[]> responseEntity2 = restTemplate.exchange("https://" + url + "/tixViewTicketCat", HttpMethod.POST, request2, CategoryObject[].class);

                // Log.d("loopforTicketlistobject", responseEntity.getBody().toString());
                //for( CategoryObject o : responseEntity2.getBody()){
                //    Log.d("loopforTicketlistobject2", o.toString());
                // }


            } catch (Exception e) {
                Log.e("TAG", e.getMessage(), e);
            }

            return null;
        }
        protected void onPostExecute(String greeting) {
            Log.d("TAG", "DO POST EXECUTE");
        }
    }

    /*private class MyCustomAdapter extends ArrayAdapter<TicketObject> {

        private ArrayList<TicketObject> list;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<TicketObject> list) {
            super(context, textViewResourceId, list);
            this.list = new ArrayList<>();
            this.list.addAll(list);
        }

        private class ViewHolder {
            TextView nameTix;
            Button showQRcode;
        }


        //suppose to populate the arraylist with Ticketlistobject
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            purchasedTixList.MyCustomAdapter.ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.purchased_tix_info, null);

                holder = new purchasedTixList.MyCustomAdapter.ViewHolder();
                holder.nameTix = (TextView) convertView.findViewById(R.id.nameTix);

                holder.showQRcode = (Button)convertView.findViewById(R.id.showQRcode);
                convertView.setTag(holder);

                holder.nameTix.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        Button cb = (Button) v ;
                        TicketObject tixObject = (TicketObject) cb.getTag();
                        Toast.makeText(getActivity(),
                                "Clicked on button",
                                Toast.LENGTH_LONG).show();

                    }
                });
            }
            else {
                holder = (purchasedTixList.MyCustomAdapter.ViewHolder) convertView.getTag();
            }

            TicketObject tixObject = list.get(position);
            holder.nameTix.setText(tixObject.getTicketDetails());
            holder.nameTix.setTag(tixObject);
            holder.showQRcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Button cb = (Button) view ;
                    TicketObject tixObject = (TicketObject) cb.getTag();
                    int pos = position +1;
                    //send details using bundle to the next fragment
                    Intent intent = new Intent(getActivity(),  dashboard.class);
                    intent.putExtra("key2", "tixInfo");
                    intent.putExtra("tixId", pos);
                    startActivity(intent);
                }
            });

            return convertView;

        }
    }
*/

}
