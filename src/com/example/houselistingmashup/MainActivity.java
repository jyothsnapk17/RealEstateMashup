package com.example.houselistingmashup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.AppEventsLogger;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.houselistingmashup.MESSAGE";
	
	//FB track 
	@Override
	protected void onResume() {
	  super.onResume();

	  // Logs 'install' and 'app activate' App Events.
	  AppEventsLogger.activateApp(this);
	}
	
	//FB track usage 
	@Override
	protected void onPause() {
	  super.onPause();

	  // Logs 'app deactivate' App Event.
	  AppEventsLogger.deactivateApp(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
//		Spinner stateSpin = (Spinner)findViewById(R.id.state) ;
		
		Button send_form = (Button)findViewById(R.id.searchButton) ;
		send_form.setOnClickListener(submitForm);
		
		
		ImageView zillow = (ImageView)findViewById(R.id.zillow_tag) ; 
				
		zillow.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
//						Intent intent = new Intent(v.getContext(), URLActivity.class);
//						intent.putExtra(EXTRA_MESSAGE, "http://www.zillow.com");
//						startActivity(intent);
						Intent intent1 = new Intent() ; 
						intent1.setAction(Intent.ACTION_VIEW) ; 
						intent1.addCategory(Intent.CATEGORY_BROWSABLE) ; 
						intent1.setData(Uri.parse("http://www.zillow.com"));
					}
				
				});

	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private  class JSONParser {

		  InputStream is = null;
		  JSONObject jObj = null;
		  String json = "";
		  String url=null;
		  List<NameValuePair> nvp=null;
		  
		  // constructor
		  public JSONParser() {

		  }

		  // function get json from url
		  // by making HTTP POST or GET method
		   public JSONObject makeHttpRequest(String url,
		        List<NameValuePair> params) {
		      BackGroundTask Task= new BackGroundTask(url,  params);
		    try {
		        return Task.execute().get();
		    } catch (InterruptedException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		        return null;
		    } catch (ExecutionException e) {
		        // TODO Auto-generated catch block
		        e.printStackTrace();
		        return null;
		    }
		}
	
		   private class BackGroundTask extends AsyncTask<String, String, JSONObject>{
			   List<NameValuePair> postparams = new ArrayList<NameValuePair>();
			   String URL=null;
			   String method = null;
	
			   public BackGroundTask(String url, List<NameValuePair> params) {
				   URL=url;
				   postparams=params;
			   }
	    
			   @Override
			   protected JSONObject doInBackground(String... params) {
				   // TODO Auto-generated method stub
				   // Making HTTP request
				   try {
					   // Making HTTP request 
					   // check for request method

					   // request method is POST
					   // defaultHttpClient
					   DefaultHttpClient httpClient = new DefaultHttpClient();
					   HttpPost httpPost = new HttpPost(URL);
					   httpPost.setEntity(new UrlEncodedFormEntity(postparams));
            
					   HttpResponse httpResponse = httpClient.execute(httpPost);
					   HttpEntity httpEntity = httpResponse.getEntity();
					   is = httpEntity.getContent();         

				   } catch (UnsupportedEncodingException e) {
					   e.printStackTrace();
				   } catch (ClientProtocolException e) {
					   e.printStackTrace();
				   } catch (IOException e) {
					   e.printStackTrace();
				   }
				   
				   try {
					   
					   BufferedReader reader = new BufferedReader(new InputStreamReader(
							   is, "iso-8859-1"), 8);
					   StringBuilder sb = new StringBuilder();
					   String line = null;
					   while ((line = reader.readLine()) != null) {
						   sb.append(line + "\n");
					   }
					   is.close();
					   json = sb.toString();
				   } catch (Exception e) {
					   Log.e("Buffer Error", "Error converting result " + e.toString());
				   }

				   // try parse the string to a JSON object
				   try {
					   jObj = new JSONObject(json);
				   } catch (JSONException e) {
					   Log.e("JSON Parser", "Error parsing data " + e.toString());
				   }

				   // return JSON String
				   return jObj;

			   }
		  }
	}
	
	// Implement the OnClickListener callback
	private OnClickListener submitForm = new OnClickListener() {
		
		public void onClick(View v) {
			// do something when the button is clicked
			
			//get values in input fields
			String streetAddress = ((EditText)findViewById(R.id.streetaddress)).getText().toString() ; 
			String city = ((EditText)findViewById(R.id.city)).getText().toString() ;
			Spinner stateSpin = (Spinner)findViewById(R.id.state) ;
			String state = stateSpin.getSelectedItem().toString() ;
			
			Log.d("SPINNER", stateSpin.getPrompt().toString()) ;
			Log.d("BUTTONCLICK", "street address: " + streetAddress + " , city : " + city + " , state : " + state );
			TextView address_error = (TextView)findViewById(R.id.address_error) ;
			TextView city_error = (TextView)findViewById(R.id.city_error) ; 
			TextView state_error = (TextView)findViewById(R.id.state_error) ; 
			Boolean valid = true; 
			
			if(streetAddress.isEmpty()) {
				valid = false; 
				address_error.setText(R.string.error_text) ;
			}
			else {
				address_error.setText("");
			}
			if(city.isEmpty()) {
				valid = false ; 
				city_error.setText(R.string.error_text) ;
			}
			else {
				city_error.setText("");
			}
			if(state.isEmpty()) {
				valid = false ;
				state_error.setText(R.string.error_text) ;
			}
			else {
				state_error.setText("");
			}
			
			if(valid) {
				// make an asynchronous call
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
		        nameValuePairs.add(new BasicNameValuePair("streetAddress", streetAddress));
		        nameValuePairs.add(new BasicNameValuePair("city", city));
		        nameValuePairs.add(new BasicNameValuePair("state", state));
				JSONParser makeRequest = new JSONParser() ; 
		        JSONObject res = makeRequest.makeHttpRequest("http://hw8realestate-env-kullatir.elasticbeanstalk.com", nameValuePairs);
//				String jsonString = "{\"MetaInfo\":{\"Currency\":\"$\",\"linkOut\":\"http://www.zillow.com/homedetails/2636-Menlo-Ave-Los-Angeles-CA-90007/20593083_zpid/\",\"address\":\"2636 Menlo Ave, Los Angeles, CA, 90007\"},\"Property Type\":\"Duplex\",\"Last Sold Price\":\"$115,000.00\",\"Year Built\":\"1924\",\"Last Sold Date\":\"29-Jul-1996\",\"Lot Size\":\"5242 sq. ft. \",\"Zestimate®  Property Estimate as of 13-Nov-2014\":\"$476,987.00\",\"Finished Area\":\"1728 sq. ft. \",\"30 Days Overall Change\":\"28,222.00\",\"Bathrooms\":\"2.0\",\"All Time Property Range\":\"$386,359.00 - $581,924.00\",\"Bedrooms\":\"2\",\"Rent Zestimate® Valuation as of 03-Nov-2014\":\"$1,880.00\",\"Tax Assessment Year\":\"2013\",\"30 Days Rent Change\":\"-20.00\",\"Tax Assessment\":\"$152,082.00\",\"All Time Rent Range\":\"$1,466.00 - $2,707.00\",\"MapInfo\":{\"latitude\":\"34.03163\",\"longitude\":\"-118.289972\"},\"Charts\":{\"1 year\":\"http://www.zillow.com/app?chartDuration=1year&chartType=partner&height=300&page=webservice%2FGetChart&service=chart&showPercent=true&width=600&zpid=20593083\",\"5 years\":\"http://www.zillow.com/app?chartDuration=5years&chartType=partner&height=300&page=webservice%2FGetChart&service=chart&showPercent=true&width=600&zpid=20593083\",\"10 years\":\"http://www.zillow.com/app?chartDuration=10years&chartType=partner&height=300&page=webservice%2FGetChart&service=chart&showPercent=true&width=600&zpid=20593083\"}}";	
//				String jsonString = "http://www.zillow.com/homedetails/2636-Menlo-Ave-Los-Angeles-CA-90007/20593083_zpid/" ;
//		        JSONObject res = null;
		        
		        if(res.has("errorMsg")) {
		        	TextView noresult = ((TextView)findViewById(R.id.no_result)) ;
		        	noresult.setVisibility(View.VISIBLE);
		        }
		        else {
		        	//create Intent to second page
					Intent intent = new Intent(v.getContext(), ZillowTab.class);
					intent.putExtra(EXTRA_MESSAGE, res.toString());
				    startActivity(intent);

		        }
			}
		}
	};
	
	//temporary utility - remove
	public static String convertStreamToString(InputStream is) throws Exception {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) {
	      sb.append(line).append("\n");
	    }
	    reader.close();
	    return sb.toString();
	}

	public static String getStringFromFile (String filePath) throws Exception {
	    File fl = new File(filePath);
	    FileInputStream fin = new FileInputStream(fl);
	    String ret = convertStreamToString(fin);
	    //Make sure you close all streams.
	    fin.close();        
	    return ret;
	}
	
//	@Override
//	protected void onResume() {
//	  super.onResume();
//
//	  // Logs 'install' and 'app activate' App Events.
//	  AppEventsLogger.activateApp(this);
//	}
//	
//	@Override
//	protected void onPause() {
//	  super.onPause();
//
//	  // Logs 'app deactivate' App Event.
//	  AppEventsLogger.deactivateApp(this);
//	}
}
