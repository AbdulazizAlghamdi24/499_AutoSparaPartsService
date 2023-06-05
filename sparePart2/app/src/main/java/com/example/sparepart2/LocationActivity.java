package com.example.sparepart2;

/*
public class LocationActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private Button Locationbtn;
    private TextView locationview;
    //private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

       // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
/*
        Locationbtn = findViewById(R.id.locationbtn);
        locationview = findViewById(R.id.locationview);
        Locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    /*private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    String address = addresses.get(0).getAddressLine(0);
                                    locationview.setText(address);

                                    // Save the location in your database using the same PHP file and method from SignUpPage
                                    saveLocationToDB(address);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                locationview.setText("Unable to get location. Make sure location is enabled on the device");
                            }
                        }
                    });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    private void saveLocationToDB(String location) {
        SignupTask signupTask = new SignupTask();
        signupTask.execute(location);
    }

    private class SignupTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String location = params[0];

            try {
                URL url = new URL("http://192.168.0.248/499_spareparts/signup.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                JSONObject requestData = new JSONObject();
                requestData.put("location", location);

                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(requestData.toString().getBytes());
                outputStream.flush();
                outputStream.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null) {
                // Trim the response string to remove leading/trailing whitespace
                response = response.trim();

                // Check if the response starts with unwanted characters
                while (response.charAt(0) != '{' && response.length() > 1) {
                    // Remove the first character from the response string
                    response = response.substring(1);
                }

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    String message = jsonResponse.getString("message");

                    // Display the response message to the user
                    Toast.makeText(LocationActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Handle the response based on the message
                    if (message.equals("Location saved successfully.")) {
                        // Handle success scenario
                        // ...
                    } else {
                        // Handle other scenarios, such as error messages
                        // ...
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Handle JSON parsing error
                    Toast.makeText(LocationActivity.this, "Error parsing JSON response: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                // Handle null response error
                Toast.makeText(LocationActivity.this, "Null response received", Toast.LENGTH_SHORT).show();
            }
        }
    }

     */