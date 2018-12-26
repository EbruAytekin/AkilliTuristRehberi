package com.example.akillsehirr;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Tab1 extends Fragment implements LocationListener{
    ListView liste;
    RequestQueue requestQueue;
    ArrayList<String> listee;
    ArrayList<Double> uzaklik;
    ArrayList<Double> enlem;
    ArrayList<Double> boylam;
    Button otel;
    Button restoran;
    Button hastane;
    Button gezilecek;

    String url = "http://192.168.43.50/Anasayfa.php";
    ArrayAdapter<String> adapter;
    public LocationManager locationManager;
    private LocationListener locationListener;
    private Location mMevcutKonum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.tab1, container, false);

        liste = (ListView) rootView.findViewById(R.id.liste);
        listee = new ArrayList<>();
        uzaklik = new ArrayList<>();
        enlem = new ArrayList<>();
        boylam = new ArrayList<>();
        otel = (Button) rootView.findViewById(R.id.button2);
        restoran = (Button) rootView.findViewById(R.id.button5);
        hastane = (Button) rootView.findViewById(R.id.button4);
        gezilecek = (Button) rootView.findViewById(R.id.button3);
        otel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://192.168.43.50/Filtreleme.php?kategoriID=1";
                listele(url);
            }
        });
        restoran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://192.168.43.50/Filtreleme.php?kategoriID=3";
                listele(url);
            }
        });
        hastane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://192.168.43.50/Filtreleme.php?kategoriID=2";
                listele(url);
            }
        });
        gezilecek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = "http://192.168.43.50/Filtreleme.php?kategoriID=4";
                listele(url);
            }
        });
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        getLocation();
        listele(url);

        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  itemValue    = (String) liste.getItemAtPosition(position);
                Intent result = new Intent(getActivity().getApplicationContext(),MapsActivity.class);
                result.putExtra("ENLEM", enlem.get(position));
                result.putExtra("BOYLAM",boylam.get(position));
                result.putExtra("AD",listee.get(position));
                startActivity(result);
            }
        });



        return rootView;
    }
    public void listele(String url){
        try {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            listee.clear();
            uzaklik.clear();
            enlem.clear();
            boylam.clear();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONArray mekanlar = response.getJSONArray("mekanlar");
                        for (int i = 0; i < mekanlar.length(); i++) {
                            JSONObject mekan = mekanlar.getJSONObject(i);
                            String ad = mekan.getString("mekanAdi");
                            double enlem = mekan.getDouble("enlem");
                            double boylam = mekan.getDouble("boylam");
                            ekle(ad,enlem,boylam);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sirala(5);
                    adapter = new  ArrayAdapter<String>(getActivity().getApplicationContext(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, listee);
                    liste.setAdapter(adapter);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            requestQueue.add(jsonObjectRequest);

        }catch (Exception e){

        }
    }
    public void getLocation(){
        try
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, (LocationListener) Tab1.this);
            mMevcutKonum = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            Toast.makeText(getActivity().getApplicationContext(),String.valueOf(mMevcutKonum.getLatitude()) +" " + String.valueOf(mMevcutKonum.getLongitude()),Toast.LENGTH_LONG).show();
        }
        catch (SecurityException ex)
        {
            Toast.makeText(getActivity().getApplicationContext(),ex.getMessage().toString(),Toast.LENGTH_LONG).show();
        }
    }


    public void ekle(String ad, Double en, Double boy){
        try{
            double a = 6371, b = 6356752.314245, f = 1 / 298.257223563;
            double L = Math.toRadians(boy - mMevcutKonum.getLongitude());
            double U1 = Math.atan((1 - f) * Math.tan(Math.toRadians(mMevcutKonum.getLatitude())));
            double U2 = Math.atan((1 - f) * Math.tan(Math.toRadians(en)));
            double sinU1 = Math.sin(U1), cosU1 = Math.cos(U1);
            double sinU2 = Math.sin(U2), cosU2 = Math.cos(U2);
            double cosSqAlpha;
            double sinSigma;
            double cos2SigmaM;
            double cosSigma;
            double sigma;

            double lambda = L, lambdaP, iterLimit = 100;
            do
            {
                double sinLambda = Math.sin(lambda), cosLambda = Math.cos(lambda);
                sinSigma = Math.sqrt(	(cosU2 * sinLambda)
                        * (cosU2 * sinLambda)
                        + (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                        * (cosU1 * sinU2 - sinU1 * cosU2 * cosLambda)
                );

                cosSigma = sinU1 * sinU2 + cosU1 * cosU2 * cosLambda;
                sigma = Math.atan2(sinSigma, cosSigma);
                double sinAlpha = cosU1 * cosU2 * sinLambda / sinSigma;
                cosSqAlpha = 1 - sinAlpha * sinAlpha;
                cos2SigmaM = cosSigma - 2 * sinU1 * sinU2 / cosSqAlpha;

                double C = f / 16 * cosSqAlpha * (4 + f * (4 - 3 * cosSqAlpha));
                lambdaP = lambda;
                lambda = 	L + (1 - C) * f * sinAlpha
                        * 	(sigma + C * sinSigma
                        * 	(cos2SigmaM + C * cosSigma
                        * 	(-1 + 2 * cos2SigmaM * cos2SigmaM)
                )
                );

            } while (Math.abs(lambda - lambdaP) > 1e-12 && --iterLimit > 0);


            double uSq = cosSqAlpha * (a * a - b * b) / (b * b);
            double A = 1 + uSq / 16384
                    * (4096 + uSq * (-768 + uSq * (320 - 175 * uSq)));
            double B = uSq / 1024 * (256 + uSq * (-128 + uSq * (74 - 47 * uSq)));
            double deltaSigma =
                    B * sinSigma
                            * (cos2SigmaM + B / 4
                            * (cosSigma
                            * (-1 + 2 * cos2SigmaM * cos2SigmaM) - B / 6 * cos2SigmaM
                            * (-3 + 4 * sinSigma * sinSigma)
                            * (-3 + 4 * cos2SigmaM * cos2SigmaM)));

            double s = a * A * (sigma - deltaSigma);
            listee.add(ad);
            uzaklik.add(s);
            enlem.add(en);
            boylam.add(boy);

        }catch (Exception e){
            //Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
        }


    }

    public void sirala(int id){
        try {
            for (int i = 0; i < listee.size()-1; i++) { //Dizimizin değerlerini sırası ile alıyoruz

                Double sayi = uzaklik.get(i); //sıradaki değeri sayi değişkenine atıyoruz
                int temp = i; //sayi 'nin indeksini temp değerine atıyoruz
                String mekan = listee.get(i);
                Double en = enlem.get(i);
                Double boy = boylam.get(i);

                for (int j = i+1; j < listee.size() ; j++) { //dizimizde i' den sonraki elemanlara bakıyoruz
                    if(uzaklik.get(j)<sayi){ //sayi değişkeninden küçük sayı var mı
                        sayi = uzaklik.get(j); //varsa sayi değişkenimizide değiştiriyoruz
                        mekan = listee.get(j);
                        temp = j; //indeks değerinide değiştiriyoruz
                    }
                }

                if(temp != i){ //temp değeri başlangıç değeri ile aynı değil ise , yani list[i]'nin değerinden küçük sayı varsa onları yer değiştiriyoruz
                    uzaklik.set(temp,uzaklik.get(i));
                    uzaklik.set(i,sayi);
                    listee.set(temp,listee.get(i));
                    listee.set(i,mekan);
                    enlem.set(temp,enlem.get(i));
                    enlem.set(i,en);
                    boylam.set(temp,boylam.get(i));
                    boylam.set(i,boy);
                }

            }

            for(int l=0;l<listee.size();l++){
                listee.set(l,listee.get(l)+"-"+String.valueOf(uzaklik.get(l)));
            }
        }catch (Exception e){
            //Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
