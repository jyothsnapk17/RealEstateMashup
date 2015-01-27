package com.example.houselistingmashup;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;


public class ZillowTab extends android.support.v4.app.FragmentActivity implements ActionBar.TabListener {

	public final static String EXTRA_MESSAGE1 = "com.example.houselistingmashup.URL";
	private static Integer numPages = 2 ;
	private static UiLifecycleHelper uiHelper;
	private BasicInfoFragment mainFragment;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v13.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
				
//		if (savedInstanceState == null) {
//        	// Add the fragment on initial activity setup
//        	mainFragment = new BasicInfoFragment();
//            getSupportFragmentManager()
//            .beginTransaction()
//            .add(android.R.id.content, mainFragment.getParentFragment())
//            .commit();
//        } else {
//        	// Or set the fragment from restored state info
//        	mainFragment = (BasicInfoFragment) getSupportFragmentManager()
//        	.findFragmentById(android.R.id.content);
//        }
		
		setContentView(R.layout.activity_zillow_tab);
		
		uiHelper = new UiLifecycleHelper(this, null);
	    uiHelper.onCreate(savedInstanceState);
		
		// Get the message from the intent
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
	    JSONObject results = null ; 
	    try {
			 results = new JSONObject(message) ;
			 Log.d("JSON returned", results.toString()); 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(this.getSupportFragmentManager(), results);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.zillow_tab, menu);
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

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
	    if (state.isOpened()) {
	        Log.i("FBLOGIN" , "Logged in...");
	    } else if (state.isClosed()) {
	        Log.i("FBLOGIN" , "Logged out...");
	    }
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
	    @Override
	    public void call(Session session, SessionState state, Exception exception) {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	@Override
	protected void onActivityResult(int requestCode, final int resultCode, final Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    Log.d("DATA", data.toString()) ;
	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	        	boolean didComplete = FacebookDialog.getNativeDialogDidComplete(data);
	        	String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
	        	String postId = FacebookDialog.getNativeDialogPostId(data);
	        	
	        	Log.e("FBActivity", String.format("Error: %s", error.toString()));
	            Log.e("FBError" , resultCode + "") ; 
	            Log.e("BUNDLE", data.toString()) ;
	            
	            Log.d("DIDCOMPLETE", didComplete + "" ) ;
	            Log.d("COMPLETION GESTURE", completionGesture + "") ; 
	            Log.d("POST ID", postId + "") ;
	            Toast.makeText(ZillowTab.this, "Error - Story not posted!", Toast.LENGTH_LONG).show();
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	        	boolean didCancel = FacebookDialog.getNativeDialogDidComplete(data);
	        	String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
	        	String postId = FacebookDialog.getNativeDialogPostId(data);
	        	
	        	Log.i("FBActivity", "Success!");
	            Log.i("FBResultCode", resultCode + "");
	            Log.i("FBBUNDLE", data.toString()) ;
	            
	            Log.d("DIDCANCEL", didCancel + "" ) ;
	            Log.d("COMPLETION GESTURE", completionGesture + "") ; 
	            Log.d("POST ID", postId + "") ;
//	            Context context = getBaseContext() ;
//	            CharSequence text = "Story Posted!";
//	            int duration = Toast.LENGTH_SHORT;

	            if (didCancel)
                {
                    if (completionGesture == null || FacebookDialog.COMPLETION_GESTURE_CANCEL.equals(completionGesture)){
                        Toast.makeText(getBaseContext(),"Story Posted!",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // track post
                        Toast.makeText(getBaseContext(),"Story Posted!",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getBaseContext(),"Error - Story Not Posted!",Toast.LENGTH_SHORT).show();
                }
//	            Toast.makeText(getBaseContext(), "Story Posted!", Toast.LENGTH_LONG).show();
	          
	        }
	    });
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

		private JSONObject results = null ; 
		public SectionsPagerAdapter(android.support.v4.app.FragmentManager fragmentManager, JSONObject res) {
			super(fragmentManager);
			results = res; 
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			switch (position) {
			case 0:
				return BasicInfoFragment.newInstance(position + 1, results);
			case 1:
				return HistoricalEstimatesFragment.newInstance(position + 1, results);
			}
			return null ; 
		}

		@Override
		public int getCount() {
			// Show 2 total pages.
			return numPages;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			}
			return null;
		}
	}

	
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class BasicInfoFragment extends android.support.v4.app.Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private static final String ARG_JSON_RES = "json_result";
		
		private static final String[] keys = {"Property Type", "Year Built", "Lot Size", "Finished Area", "Bathrooms", "Bedrooms", "Tax Assessment Year", "Tax Assessment",  "Last Sold Price", "Last Sold Date", "Zestimate®  Property Estimate as of ", 
			"30 Days Overall Change", "All Time Property Range", "Rent Zestimate® Valuation as of ", "30 Days Rent Change", "All Time Rent Range" } ; 
		private String[] values ;
		private static final String TAG = "BASICINFOFRAGMENT";
//		SessionStatusCallback statusCallback = new SessionStatusCallback();
//		private static UiLifecycleHelper uiHelper;
		
		/**
		 *  Facebook helper class
		 */
		 private Session.StatusCallback callback = new Session.StatusCallback() {
		        @Override
		        public void call(Session session, SessionState state,
		                Exception exception) {
		            onSessionStateChange(session, state, exception);
		        }
		 };
		 
		 private void onSessionStateChange(Session session, SessionState state,
		            Exception exception) {
		        if (state.isOpened()) {
		        	Log.d("BASICINFO_FRAGMENT", "Logged in") ;
		            // System.out.println("Logged in...");
		        } else if (state.isClosed()) {
		            // System.out.println("Logged out...");
		        	Log.d("BASICINFO_FRAGMENT", "Logged out..."); 
		        }
		    }
		
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static BasicInfoFragment newInstance(int sectionNumber, JSONObject res) {
			BasicInfoFragment fragment = new BasicInfoFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			args.putString(ARG_JSON_RES, res.toString());
//			Log.d("PASSED JSON", res.toString()) ;
			fragment.setArguments(args);
			return fragment;
		}

		public BasicInfoFragment() {
		}

		
		public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		        uiHelper = new UiLifecycleHelper(getActivity(), callback);
		        uiHelper.onCreate(savedInstanceState);
		}
		 
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_zillow_tab,
					container, false);
			Bundle args = getArguments();
			
//			uiHelper = new UiLifecycleHelper((Activity) rootView.getContext(), null);
//		    uiHelper.onCreate(savedInstanceState);
		    
//			Log.d("BUNDLE CONTENTS", args.getString(ARG_JSON_RES)) ;
			values = new String[16] ;
			
			try {
				final JSONObject res = new JSONObject(args.getString(ARG_JSON_RES)) ;
				final JSONObject metaInfo = res.getJSONObject("MetaInfo") ; ;

				Iterator<String> iter = res.keys();
			    while (iter.hasNext()) {
			        String key = iter.next();
			        int position = 0; 
			        for (int i=0;i<16;i++) {
			        	if(key.contains(keys[i])) {
//			        		Log.d("ORIGINAL - MATCHED" , key + "--" + keys[i]) ;
			        		position = i ; 
			        		try {
					            values[position] = res.get(key).toString() ;
					            if (!key.equalsIgnoreCase(keys[i])) {
					            	keys[i] = key ; 
					            }
					        } catch (JSONException e) {
					            // Something went wrong!
					        }
			        		break ;
			        	}
			        }
			        
			        
			    }
			    for(int i=0;i<16;i++) {
//			    	createTableRow( rootView, keys[i],  values[i]) ;
			    	Log.d(keys[i], values[i]) ;
			    }
			    
			    //add table 
				createTable(rootView, keys, values, metaInfo) ;
				//print Keyhash
//				printKeyHash() ;
				
				Button fbShare = (Button)rootView.findViewById(R.id.shareButton);
//				fbShare.setFragment(this);
				TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f) ;
				param.setMargins(2, 2, 2, 2);
				fbShare.setLayoutParams(param);
				fbShare.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.d("FBBUTTON", "onclickListener") ;
						// TODO Auto-generated method stub
						if (FacebookDialog.canPresentShareDialog(v.getContext(), FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
							// Publish the post using the Share Dialog
							Log.d("FACEBOOK", "SHARE DIALOG USED.") ;
							Log.d("FaceBook share dialog", res.toString()) ;
							FacebookDialog shareDialog;
							try {
								String priceInfo = "Last Sold Price: " + res.getString("Last Sold Price") + ", 30 Days Overall Change: ";
								String overallChange = res.getString("30 Days Overall Change") ;
								priceInfo += overallChange ;
								JSONObject charts = res.getJSONObject("Charts") ;
 								shareDialog = new FacebookDialog.ShareDialogBuilder((Activity) v.getContext())
									.setLink(metaInfo.getString("linkOut"))
									.setName(metaInfo.getString("address"))
									.setCaption(metaInfo.getString("address")) 
									.setPicture(charts.getString("1 year"))
									.setDescription(priceInfo)
									.build();
								uiHelper.trackPendingDialogCall(shareDialog.present());
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							
//							publishFeedDialog();	
							
							Log.d("FACEBOOK", "NATIVE DIALOG USED.") ;
							String priceInfo;
							try {
								priceInfo = "Last Sold Price: " + res.getString("Last Sold Price") + ", 30 Days Overall Change: ";
							
								String overallChange = res.getString("30 Days Overall Change") ;
								priceInfo += overallChange ;
								JSONObject charts = res.getJSONObject("Charts") ;
									// Fallback. For example, publish the post using the Feed Dialog
								Log.d("Facebook", "feed dialog") ;
								Bundle params = new Bundle();
							    params.putString("name", metaInfo.getString("address"));
							    params.putString("caption", metaInfo.getString("address"));
							    params.putString("description", priceInfo);
							    params.putString("link", metaInfo.getString("linkOut"));
							    params.putString("picture", charts.getString("1 year"));
	
							    Session session = Session.getActiveSession();
							    if (session != null &&
							           (session.isOpened() || session.isClosed()) ) {
							        onSessionStateChange(session, session.getState(), null);
							    }
							    else {
							    	Log.d("FBSession", "No active session") ;
							    	
							    }
							    
							    WebDialog feedDialog = (
							        new WebDialog.FeedDialogBuilder((Activity)v.getContext(), session.getActiveSession(), params))
							        .setOnCompleteListener(new OnCompleteListener() {
	
							            @Override
							            public void onComplete(Bundle values,
							                FacebookException error) {
							                if (error == null) {
							                    // When the story is posted, echo the success
							                    // and the post Id.
							                    final String postId = values.getString("post_id");
							                    if (postId != null) {
							                        Toast.makeText(getActivity(),
							                            "Posted story, id: "+postId,
							                            Toast.LENGTH_SHORT).show();
							                    } else {
							                        // User clicked the Cancel button
							                        Toast.makeText(getActivity().getApplicationContext(), 
							                            "Publish cancelled", 
							                            Toast.LENGTH_SHORT).show();
							                    }
							                } else if (error instanceof FacebookOperationCanceledException) {
							                    // User clicked the "x" button
							                    Toast.makeText(getActivity().getApplicationContext(), 
							                        "Publish cancelled", 
							                        Toast.LENGTH_SHORT).show();
							                } else {
							                    // Generic, ex: network error
							                    Toast.makeText(getActivity().getApplicationContext(), 
							                        "Error posting story", 
							                        Toast.LENGTH_SHORT).show();
							                }
							            }
	
							        })
							        .build();
							    feedDialog.show();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("ROOTVIEW", rootView.toString()) ;
			
			TextView terms_of_use = (TextView)rootView.findViewById(R.id.terms_of_use) ;
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			terms_of_use.setGravity(Gravity.CENTER);
			terms_of_use.setLayoutParams(lp);
			terms_of_use.setText(Html.fromHtml("Use is subject to <a href=\"" + R.string.zillow_terms_of_use + "\">Terms of Use</a>"));
			terms_of_use.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(), URLActivity.class);
					intent.putExtra(EXTRA_MESSAGE1, "http://www.zillow.com/corp/Terms.htm");
					startActivity(intent);
				}
			
			});
			
			TextView zestimate = (TextView)rootView.findViewById(R.id.zestimate) ; 
			lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			zestimate.setGravity(Gravity.CENTER);
			zestimate.setLayoutParams(lp);
			zestimate.setText(Html.fromHtml("<a href=\"" + R.string.zillow_what_is + "\">What\'s a Zestimate?</a>")); 
			zestimate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(), URLActivity.class);
					intent.putExtra(EXTRA_MESSAGE1, "http://www.zillow.com/zestimate");
					startActivity(intent);
				}
			
			});
			
			return rootView;
		}
		
		private void printKeyHash() {
		    // Add code to print out the key hash
		    try {
		        PackageInfo info = getActivity().getPackageManager().getPackageInfo("com.example.houselistingmashup", PackageManager.GET_SIGNATURES);
		        for (Signature signature : info.signatures) {
		            MessageDigest md = MessageDigest.getInstance("SHA");
		            md.update(signature.toByteArray());
		            Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
		        }
		    } catch (NameNotFoundException e) {
		        Log.e("KeyHash:", e.toString());
		    } catch (NoSuchAlgorithmException e) {
		        Log.e("KeyHash:", e.toString());
		    }
		}
		
		@Override
		public void onActivityResult(int requestCode, final int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);

		    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
		        @Override
		        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
		            Log.e("FBActivity", String.format("Error: %s", error.toString()));
		            Log.e("FBError" , resultCode + "") ; 
		            Log.e("BUNDLE", data.toString()) ;
		        }

		        @Override
		        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
		            Log.i("FBActivity", "Success!");
		        }
		    });
		}
		
		@Override
		public void onResume() {
		    super.onResume();
		    
		    Session session = Session.getActiveSession();
			if (session != null &&
					(session.isOpened() || session.isClosed()) ) {
				onSessionStateChange(session, session.getState(), null);
			}
			
		    uiHelper.onResume();
		}

		@Override
		public void onSaveInstanceState(Bundle outState) {
		    super.onSaveInstanceState(outState);
		    uiHelper.onSaveInstanceState(outState);
		}

		@Override
		public void onPause() {
		    super.onPause();
		    uiHelper.onPause();
		}

		@Override
		public void onDestroy() {
		    super.onDestroy();
		    uiHelper.onDestroy();
		}
		
		private void publishFeedDialog() {
	        Bundle params = new Bundle();
	        params.putString("name", "Facebook SDK for Android");
	        params.putString("caption", "Build great social apps and get more installs.");
	        params.putString("description", "The Facebook SDK for Android makes it easier and faster to develop Facebook integrated Android apps.");
	        params.putString("link", "https://developers.facebook.com/android");
	        params.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");
	        
	        // Invoke the dialog
	    	WebDialog feedDialog = (
	    			new WebDialog.FeedDialogBuilder(getActivity(),
	    					Session.getActiveSession(),
	    					params))
	    					.setOnCompleteListener(new OnCompleteListener() {

	    						@Override
	    						public void onComplete(Bundle values,
	    								FacebookException error) {
	    							if (error == null) {
	    								// When the story is posted, echo the success
	    				                // and the post Id.
	    								final String postId = values.getString("post_id");
	        							if (postId != null) {
	        								Toast.makeText(getActivity(),
	        										"Posted story, id: "+postId,
	        										Toast.LENGTH_SHORT).show();
	        							} else {
	        								// User clicked the Cancel button
	        								Toast.makeText(getActivity().getApplicationContext(), 
	        		                                "Publish cancelled", 
	        		                                Toast.LENGTH_SHORT).show();
	        							}
	    							} else if (error instanceof FacebookOperationCanceledException) {
	    								// User clicked the "x" button
	    								Toast.makeText(getActivity().getApplicationContext(), 
	    		                                "Publish cancelled", 
	    		                                Toast.LENGTH_SHORT).show();
	    							} else {
	    								// Generic, ex: network error
	    								Toast.makeText(getActivity().getApplicationContext(), 
	    		                                "Error posting story", 
	    		                                Toast.LENGTH_SHORT).show();
	    							}
	    						}
	    						
	    						})
	    					.build();
	    	feedDialog.show();
	    }
		
		public void createTable(View v, String[] keys, String[] values, final JSONObject metaInfo) {
			  TableLayout tl = (TableLayout)v.findViewById(R.id.basic_info_table);

			  TableRow tr = new TableRow(v.getContext());
			  LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			  tr.setWeightSum(1f);
			  tr.setLayoutParams(lp);
			  tr.setBackgroundResource(R.drawable.cell_border);

			  ImageView arrow ; 
			  TextView tvLeft = new TextView(v.getContext());
			  
			  lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
			  tvLeft.setLayoutParams(lp);
			  tvLeft.setGravity(Gravity.START);
			  
			  try {
				tvLeft.setText(Html.fromHtml("<a href=\"" + metaInfo.getString("linkOut") + "\">" + metaInfo.getString("address") + "</a>"));
			  } catch (JSONException e) {
				  // TODO Auto-generated catch block
				  e.printStackTrace();
			  }
			  
			  tvLeft.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(), URLActivity.class);
					try {
						Log.d("CLICK", metaInfo.getString("linkOut")) ;
						intent.putExtra(EXTRA_MESSAGE1, metaInfo.getString("linkOut"));
						startActivity(intent);
					} catch (JSONException e) {
						  // TODO Auto-generated catch block
						  e.printStackTrace();
					}
					
				} 
				  
			  });
			  
			  tr.addView(tvLeft);
			  tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			  
			  for(int i=0;i<keys.length; i++) {
				 
				  tr = new TableRow(v.getContext());
//				  tr.setBackgroundResource(R.drawable.row_border);
				  lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//				  tr.setGravity(Gravity.CENTER);
				  tr.setWeightSum(1f);
				  tr.setLayoutParams(lp);
				  
//				  tr.setBackground(gd);
	
				  tvLeft = new TextView(v.getContext());
				  lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.6f);
				  tvLeft.setLayoutParams(lp);
//				  tvLeft.setBackgroundResource(R.drawable.cell_border);
				  tvLeft.setGravity(Gravity.START);
//				  tvLeft.setBackgroundColor(Color.WHITE);
				  tvLeft.setText(keys[i]);
				  
				  arrow = new ImageView(v.getContext()) ; 
				  lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.1f);
				  arrow.setLayoutParams(lp);
				  arrow.setPadding(0, 0, 0, 0);
//				  arrow.setImageDrawable(v.getResources().getDrawable(R.drawable.down_r));
				  arrow.setImageResource(R.drawable.down_r);
				  
				  
				  TextView tvCenter = new TextView(v.getContext());
				  lp = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 0.4f);
				  tvCenter.setLayoutParams(lp);
				  tvCenter.setGravity(Gravity.RIGHT);
				  if(keys[i] == "30 Days Overall Change" || keys[i] == "30 Days Rent Change") {
					  if(values[i].contains("-")) {
						  values[i] = values[i].replace("-", "$") ;
						  tvCenter.setCompoundDrawablesWithIntrinsicBounds(
				                    R.drawable.down_r, 0, 0, 0);
//						  arrow.setImageResource(R.drawable.down_r);
					  }
					  else {
						  values[i] = "$" + values[i] ; 
						  tvCenter.setCompoundDrawablesWithIntrinsicBounds(
				                    R.drawable.up_g, 0, 0, 0);
//						  arrow.setImageResource(R.drawable.up_g);
					  }
				  }
				 
//				  tvCenter.setBackgroundColor(Color.WHITE);
				  tvCenter.setText(values[i]);
//				  tvCenter.setBackgroundDrawable(gd);

	
				  tr.addView(tvLeft);
				  tr.addView(tvCenter);
				  if (i%2==0) {
					  tr.setBackgroundResource(R.drawable.row_blue) ;
				  }
				  else {
					  tr.setBackgroundResource(R.drawable.cell_border) ;
				  }

				  tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			  }
		}
	}
	

	
	public static class HistoricalEstimatesFragment extends android.support.v4.app.Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";
		private static final String ARG_JSON_RES = "json_result";
		
		private String[] uri = new String[3] ;
		private String[] title = {"1 Year", "5 Years", "10 Years"} ;
		private Bitmap[] images = { null, null, null }; 
		private int counter = 0 ;
		ImageView img  ;
		TextView description ;
		
		public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

		    private String url;
		    private ImageView imageView;
		    private int counter ;

		    public ImageLoadTask(String url, ImageView imageView, int counter ) {
		        this.url = url;
		        this.imageView = imageView;
		        this.counter = counter ; 
		    }

		    @Override
		    protected Bitmap doInBackground(Void... params) {
		        try {
		            URL urlConnection = new URL(url);
		            HttpURLConnection connection = (HttpURLConnection) urlConnection
		                    .openConnection();
		            connection.setDoInput(true);
		            connection.connect();
		            InputStream input = connection.getInputStream();
		            images[counter] = BitmapFactory.decodeStream(input);
		            return images[counter];
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        return null;
		    }

		    @Override
		    protected void onPostExecute(Bitmap result) {
		        super.onPostExecute(result);
		        imageView.setImageBitmap(result);
		        Log.d("ASYNCTASK","image SET") ;
		    }

		}
		
		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static HistoricalEstimatesFragment newInstance(int sectionNumber, JSONObject res) {
			HistoricalEstimatesFragment fragment = new HistoricalEstimatesFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			args.putString(ARG_JSON_RES, res.toString());
			fragment.setArguments(args);
			return fragment;
		}

		public HistoricalEstimatesFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_zillow_hist_estimate,
					container, false);
			
			Bundle args = getArguments();
			try {
				JSONObject res = new JSONObject(args.getString(ARG_JSON_RES)) ;
				JSONObject charts = res.getJSONObject("Charts") ; 
				JSONObject metaInfo = res.getJSONObject("MetaInfo") ;
				Log.d("CHARTS", charts.toString()) ;
				uri[0] = charts.get("1 year").toString() ; 
				uri[1] = charts.get("5 years").toString() ;
				uri[2] = charts.get("10 years").toString() ;
				
				img = (ImageView)rootView.findViewById(R.id.hist_image) ; 
				Log.d("IMAGE", img.toString()) ;
				ImageLoadTask Task = new ImageLoadTask(uri[0],  img, 0);
				Task.execute().get();
				
//				LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) img.getLayoutParams(); 
//				Log.d("WIDTH", params.width + "") ;
//				Log.d("HEIGHT", params.height + "" ) ;
//			    params.width = 600;
//			    params.height = 300;
//			    img.setLayoutParams(params);
			    
				description = (TextView)rootView.findViewById(R.id.description) ;
				description.setText("Historical Zestimate for the past " + title[0]);
				description.setGravity(Gravity.CENTER);
				
				TextView address = (TextView)rootView.findViewById(R.id.address) ;
				address.setText(metaInfo.getString("address").toString());
				address.setGravity(Gravity.CENTER);
				
				Button prevButton = (Button)rootView.findViewById(R.id.prev_button) ;
				prevButton.setOnClickListener(navigationImage);
				Button nextButton = (Button)rootView.findViewById(R.id.next_button) ;
				nextButton.setOnClickListener(navigationImage);
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// add graphs
			
			TextView terms_of_use = (TextView)rootView.findViewById(R.id.terms_of_use1) ;
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			terms_of_use.setGravity(Gravity.CENTER);
			terms_of_use.setLayoutParams(lp);
			terms_of_use.setText(Html.fromHtml("Use is subject to <a href=\"" + R.string.zillow_terms_of_use + "\">Terms of Use</a>"));
			terms_of_use.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(), URLActivity.class);
					intent.putExtra(EXTRA_MESSAGE1, "http://www.zillow.com/corp/Terms.htm");
					startActivity(intent);
				}
			
			});
			
			TextView zestimate = (TextView)rootView.findViewById(R.id.zestimate1) ; 
			lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			zestimate.setGravity(Gravity.CENTER);
			zestimate.setLayoutParams(lp);
			zestimate.setText(Html.fromHtml("<a href=\"" + R.string.zillow_what_is + "\">What\'s a Zestimate?</a>")); 
			zestimate.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(v.getContext(), URLActivity.class);
					intent.putExtra(EXTRA_MESSAGE1, "http://www.zillow.com/zestimate");
					startActivity(intent);
				}
			
			});
			
			return rootView;
		}
		
		private OnClickListener navigationImage = new OnClickListener() {
			
			public void onClick(View v) {
				// do something when the button is clicked
//				uri[0] = "" ;
				counter = (counter + 1)%3 ; 
				Log.d("YEAR", title[counter]) ;	
//				ImageView img = (ImageView)v.findViewById(R.id.hist_image) ; 
				if (img == null) {
					Log.d("IMAGEVIEW", "NULL") ;
				}
				if (images[counter] == null) {
					Log.d("ASYNCTASK - LOAD IMAGE", uri[counter]) ;
					ImageLoadTask Task= new ImageLoadTask(uri[counter],  img, counter);
	//				Log.d("Image Loaded", img.toString()) ;
					try {
						Task.execute().get();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					Log.d("BITMAP AVAILABLE LOCALLY" , uri[counter]) ;
					img.setImageBitmap(images[counter]);
				}
				description.setText("Historical Zestimate for the past " + title[counter]) ;
			}
		} ;
	}
}
