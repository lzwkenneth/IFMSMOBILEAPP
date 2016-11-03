package kenneth.jf.siaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import java.util.concurrent.ExecutionException;

import static kenneth.jf.siaapp.R.layout.ticket_list;


/**
 * View ticket list from Ticket.xml
 */

public class viewTicketList extends Fragment {
    View myView;
    private RestTemplate restTemplate = ConnectionInformation.getInstance().getRestTemplate();
    private String url = ConnectionInformation.getInstance().getUrl();
    ArrayList<Ticket> TicketList = new ArrayList<Ticket>();
    ArrayList<Ticket> ticketSelected = new ArrayList<>();
    FragmentManager fragmentManager = getFragmentManager();
    String checkTixResponse;
    String spinnerSelected;
    ArrayList<String> spinList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(ticket_list, container, false);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Retrieving the Tickets...");
        progressDialog.show();

        new viewTicketList.viewAllTickets().execute();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        if (ConnectionInformation.getInstance().getAuthenticated()) {
                            Log.d("TAG", "After authenticated");
                            displayListView();
                            //checkButtonClick();
                        } else {
                            Log.d("TAG", "After NOT authenticated");

                        }

                        progressDialog.dismiss();
                    }
                }, 5000);


        myView = inflater.inflate(ticket_list, container, false);
        Button proceedToCheckOut = (Button) myView.findViewById(R.id.proceedToCheckOut);
        proceedToCheckOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                double sum = 0.0;
                Spinner spin = (Spinner) myView.findViewById(R.id.spinnerTicketList);
                spinList = new ArrayList<>();
                for(int i=0;i<TicketList.size();i++){
                    spinnerSelected = spin.getSelectedItem().toString();
                    spinList.add(spinnerSelected);
                    TicketList.get(i).setNumTix(Integer.valueOf(spinnerSelected));
                    ticketSelected.add(TicketList.get(i));
                    sum += Double.valueOf(TicketList.get(i).getPrice()) * Integer.valueOf(spinnerSelected);
                }
                //NEED TO CHECK IF TICKETSSSSSSS are available
                try {
                    System.out.println("REACHED CHECKING OF TICKETS");
                    new checkTix().execute().get();
                    System.out.println("CHECK TICKET RESPONSE: " + checkTixResponse);
                    if(checkTixResponse.equals("\"\"")) {
                        System.out.println("CHECK AGAIN: " +checkTixResponse);
                    }
                    else{
                        //showDialog(getActivity(), "Insufficient Tickets Left", checkTixResponse, "Yes", "No").show();
                        showDialog(getActivity(), "Insufficient Tickets Left", "Click continue to edit the tickets selection", "Continue", "Back To Events List").show();
                        return;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }



                Intent intent = new Intent(getActivity(), dashboard.class);
                intent.putExtra("key2", "ticketSum");
                intent.putExtra("price", String.valueOf(sum));
                System.err.println("ARRAYLIST: " + TicketList.size());


                Intent intent2 = new Intent(getActivity(), ConfirmationActivity.class);
                intent2.putParcelableArrayListExtra("ticketList", TicketList);
                intent2.putStringArrayListExtra("spinList",spinList);
                intent2.putExtra("num", TicketList.size());

                startActivity(intent2);



                startActivity(intent);
                System.out.println("TOTAL PRICE IS      " + sum);
                Toast.makeText(getActivity(),"Checking out..."+ sum, Toast.LENGTH_SHORT).show();


                // intent.putParcelableArrayListExtra();

            }
        });



     /*   Button removeBtn = (Button) myView.findViewById(R.id.ButtonRemoveFromCart);

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=new String();
                for(int i=0;i<TicketList2.size();i++) {
                    str=str.concat(TicketList2.get(i).getName());

                }
                Toast.makeText(getActivity(), "Ticket LISTING 2: " + str, Toast.LENGTH_SHORT).show();
                dataAdapter = new viewTicketList.MyCustomAdapter(getActivity(), R.layout.ticket_info, TicketList2);
                ListView listView = (ListView) myView.findViewById(R.id.listView1);
                // Assign adapter to ListView
                listView.setAdapter(dataAdapter);

            }
        });*/

    /*    Button proceedToCheckOut = (Button) myView.findViewById(R.id.proceedToCheckOut);
        proceedToCheckOut.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), dashboard.class);
                // intent.putParcelableArrayListExtra();


            }
        });*/

        return myView;
    }
    private AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                //Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                //Intent intent = new Intent(Intent.ACTION_VIEW, uri);

            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                goBackEventlist();
            }
        });
        return downloadDialog.show();
    }

    private void goBackEventlist() {
        Intent intent = new Intent(getActivity(),dashboard.class);
        intent.putExtra("key2", "goToEventList");
        startActivity(intent);
    }





    viewTicketList.MyCustomAdapter dataAdapter = null;





    private class viewAllTickets extends AsyncTask<Void, Void, String> {


        protected String doInBackground(Void... params) {
            Log.d("TAG", "DO IN BACKGROUND");
            try {
                JSONObject request = new JSONObject();
                //ticket ID
                String value = getActivity().getIntent().getStringExtra("eventId");
                request.put("eventId", value);
                System.out.println("event ID OF VIEW AN Ticket in ticketShowInfo: " + value);


                HttpEntity<String> request2 = new HttpEntity<String>(request.toString(), ConnectionInformation.getInstance().getHeaders());
                Log.d("TAGGGGGGGGREQUEST", ConnectionInformation.getInstance().getHeaders().getAccept().toString());
                String url2 = "https://" + url + "/tixViewEventCat";

                Log.d("TAG", "BEFORE VERIFYING" + restTemplate.getMessageConverters().toString());
                Log.d("TAG", request2.toString());
                // Log.d("TAG",request2.getBody());
                ResponseEntity<TicketListObject[]> responseEntity = restTemplate.exchange(url2, HttpMethod.POST, request2, TicketListObject[].class);
                for (TicketListObject t : responseEntity.getBody()) {

                }
                for (TicketListObject m : responseEntity.getBody()) {
                    Ticket e = new Ticket();
                    e.setName(m.getTicketName());
                    e.setCode(m.getId());
                    e.setPrice(m.getPrice());
                    e.setSelected(false);
                    TicketList.add(e);
                    //return list
                    Log.d("loopforticketlistobject", m.toString());
                }
            } catch (Exception e) {
                Log.e("TAG", e.getMessage(), e);
            }

            return null;
        }


        protected void onPostExecute(String greeting) {

            Log.d("TAG", "DO POST EXECUTE");
        }

    }

    //Ticket LISTING
    private void displayListView() {
        //Array list of Tickets

        //create an ArrayAdaptar from the String Array
        // TicketList = list;
        System.out.println("Size: " + TicketList.size());
        dataAdapter = new viewTicketList.MyCustomAdapter(this.getActivity(), R.layout.ticket_info, TicketList);
        ListView listView = (ListView) myView.findViewById(R.id.listViewTicket);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Ticket Ticket = (Ticket) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(),
                        "Clicked on Row: " + Ticket.getName(),
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Ticket> {

        private ArrayList<Ticket> TicketList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Ticket> TicketList) {
            super(context, textViewResourceId, TicketList);
            this.TicketList = new ArrayList<>();
            this.TicketList.addAll(TicketList);
        }

        private class ViewHolder {
            TextView ticketName;
            TextView code2;
            // CheckBox name;
            Spinner spinner;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            viewTicketList.MyCustomAdapter.ViewHolder holder = null;
            Log.v("ConvertView", String.valueOf(position));

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.ticket_info, null);

                holder = new viewTicketList.MyCustomAdapter.ViewHolder();
                holder.ticketName = (TextView)convertView.findViewById(R.id.ticketName);
                holder.code2 = (TextView) convertView.findViewById(R.id.code2);
                // holder.name = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.spinner = (Spinner) convertView.findViewById(R.id.spinnerTicketList);


                //change to spinner
                convertView.setTag(holder);

                /*holder.name.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        Ticket Ticket = (Ticket) cb.getTag();
                        Toast.makeText(getActivity(),
                                "Clicked on Checkbox: " + cb.getText() +
                                        " is " + cb.isChecked(),
                                Toast.LENGTH_LONG).show();
                        Ticket.setSelected(cb.isChecked());

                        //retrieve Ticket Details From Backend

                    }
                });
*/

            } else {
                holder = (viewTicketList.MyCustomAdapter.ViewHolder) convertView.getTag();
            }

            Ticket Ticket = TicketList.get(position);

            holder.ticketName.setText(Ticket.getName());
            holder.code2.setText(" ( $" + String.valueOf(Ticket.getPrice()) + " )");
            //holder.name.setChecked(Ticket.isSelected());
            holder.code2.setTag(Ticket);

            return convertView;

        }
    }

  /*  private void checkButtonClick() {

        Button myButton = (Button) myView.findViewById(R.id.findSelected1);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                StringBuffer responseText = new StringBuffer();
                responseText.append("The following were selected...\n");
                //this list shows the Tickets that are selected
                ArrayList<Ticket> TicketList = dataAdapter.TicketList;
                for(int i=0;i<TicketList.size();i++){
                    Ticket Ticket = TicketList.get(i);

                    if(Ticket.isSelected()){
                        responseText.append("\n" + Ticket.getName());
                    }
                }
                Toast.makeText(getActivity(),
                        responseText, Toast.LENGTH_LONG).show();
            }
        });
    }*/


    private class checkTix extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {
            Log.d("TAG", "DO IN BACKGROUND CHECKING TICKET AMOUNT");
            try {
                JSONArray jar = new JSONArray();

                for(int i=0;i<ticketSelected.size();i++){
                    JSONObject obj = new JSONObject();
                    obj.put("categoryId", ticketSelected.get(i).getCode());
                    obj.put("numTickets", ticketSelected.get(i).getNumTix());
                    System.out.println("CODE: " + ticketSelected.get(i).getCode() + "NUM TIX: " + ticketSelected.get(i).getNumTix());
                    jar.put(obj);
                }

                /*obj.put("categoryId", 1);
                obj.put("numTickets", 100000);
                jar.put(obj);
                JSONObject obj2 = new JSONObject();
                obj2.put("categoryId", 2);
                obj2.put("numTickets", 100000);
                jar.put(obj2);*/

                HttpEntity<String> request2 = new HttpEntity<String>(jar.toString(), ConnectionInformation.getInstance().getHeaders());
                Log.d("TAGGGGGGGGREQUEST", ConnectionInformation.getInstance().getHeaders().getAccept().toString());
                String url2 = "https://" + url + "/tixCheckTix";

                Log.d("TAG", "BEFORE VERIFYING" + restTemplate.getMessageConverters().toString());
                Log.d("TAG", request2.toString());
                // Log.d("TAG",request2.getBody());
                ResponseEntity<String> responseEntity = restTemplate.exchange(url2, HttpMethod.POST, request2, String.class);

                checkTixResponse = responseEntity.getBody().toString();




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
