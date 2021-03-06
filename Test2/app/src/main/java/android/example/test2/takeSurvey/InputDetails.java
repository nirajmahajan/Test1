package android.example.test2.takeSurvey;

import android.content.Intent;
import android.example.test2.R;
import android.example.test2.Welcome;
import android.example.test2.database.AppDatabase;
import android.example.test2.database.Tables.Child;
import android.example.test2.database.Tables.Contact;
import android.example.test2.database.Tables.Parent;
import android.example.test2.database.Tables.Person;
import android.example.test2.database.Tables.SocialWorker;
import android.example.test2.database.Tables.Survey;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class InputDetails extends AppCompatActivity {

    private String childFirstName = "";
    private String childLastName = "";
    private String parentFirstName = "";
    private String parentLastName = "";
    private int childGender = -1;
    private int parentGender = -1;
    private int childAge = -1;
    private int parentAge = -1;
    private String parentOccupation = "";
//    private Image childImage = null;
//    private Image parentSignature = null;
    private long phoneNumber = -1;
    private long alternatePhoneNumber = -1;
    private String mailId = "";
    private String addressLine = "";
    private String district = "";
    private String state = "";
    private String city = "";
    private byte[] BLOB = null;
    private int pincode = -1;

    private SocialWorker socialWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_details);

        Intent intent = getIntent();
        String username = intent.getStringExtra("SOCIAL_WORKER_USERNAME");
        socialWorker = AppDatabase.getAppDatabase(getApplicationContext()).socialWorkerDao().findSocialWorkerByUsername(username).get(0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_cancel) {
            finish();
        }
        else if (id == R.id.menu_done) {

            childFirstName = ((EditText)findViewById(R.id.et_input_child_first_name)).getText().toString();
            childLastName = ((EditText)findViewById(R.id.et_input_child_last_name)).getText().toString();
            parentFirstName = ((EditText)findViewById(R.id.et_input_parent_first_name)).getText().toString();
            parentLastName = ((EditText)findViewById(R.id.et_input_parent_last_name)).getText().toString();
            int pGender = ((RadioGroup)findViewById(R.id.rg_input_parent_gender)).getCheckedRadioButtonId();
            if(pGender == R.id.radio_parent_female) {
                parentGender = 0;
            } else if (pGender == R.id.radio_parent_male){
                parentGender = 1;
            } else if (pGender == R.id.radio_parent_other) {
                parentGender = 2;
            }
            int cGender = ((RadioGroup)findViewById(R.id.rg_input_child_gender)).getCheckedRadioButtonId();
            if(cGender == R.id.radio_child_female) {
                childGender = 0;
            } else if (cGender == R.id.radio_child_male){
                childGender = 1;
            } else if (cGender == R.id.radio_child_other) {
                childGender = 2;
            }

            String strchildAge = ((EditText) findViewById(R.id.et_input_child_age)).getText().toString();
            if (!strchildAge.equals("")) {
                childAge = Integer.parseInt(strchildAge);
            }
            String strparentAge = ((EditText) findViewById(R.id.et_input_parent_age)).getText().toString();
            if (!strparentAge.equals("")) {
                parentAge = Integer.parseInt(strparentAge);
            }

            String strphone = ((EditText) findViewById(R.id.et_input_phone)).getText().toString();
            if (!strphone.equals("")) {
                phoneNumber = Long.parseLong(strphone);
            }

            String straltphone = ((EditText) findViewById(R.id.et_input_alternate_phone)).getText().toString();
            if (!straltphone.equals("")) {
                alternatePhoneNumber = Long.parseLong(straltphone);
            }

            String strpincode = ((EditText) findViewById(R.id.et_input_address_pincode)).getText().toString();
            if (!strpincode.equals("")) {
                pincode = Integer.parseInt(strpincode);
            }

            mailId = ((EditText)findViewById(R.id.et_input_mail)).getText().toString();
            addressLine = ((EditText)findViewById(R.id.et_input_address_first)).getText().toString() + ((EditText)findViewById(R.id.et_input_address_second)).getText().toString();;
            district = ((EditText)findViewById(R.id.et_input_address_district)).getText().toString();
            city = ((EditText)findViewById(R.id.et_input_address_city)).getText().toString();
            state = ((EditText)findViewById(R.id.et_input_address_state)).getText().toString();

            if(!allFilled()) {
                Toast.makeText(getApplicationContext(), "Cannot Proceed Until All Information has been filled", Toast.LENGTH_LONG).show();
            }
            else {
                Person pChild = new Person();
                Person pParent = new Person();
                Child child = new Child();
                Parent parent = new Parent();
                Survey survey = new Survey();
                Contact contact = new Contact();

                AppDatabase db = AppDatabase.getAppDatabase(getApplicationContext());

                db.beginTransaction();

                long[] personIds = db.personDao().Insert(pParent, pChild);
                long[] surveyIds = db.surveyDao().Insert(survey);
                long[] contactIds = db.contactDao().Insert(contact);
                long[] childIds = db.childDao().Insert(child);
                long[] parentIds = db.parentDao().Insert(parent);
//                db.socialWorkerDao().Update(socialWorker);


                pChild.setId((int) personIds[1]);
                pChild.setFirstName(childFirstName);
                pChild.setLastName(childLastName);
                pChild.setAge(childAge);
                pChild.setGender(childGender);

                pParent.setId((int) personIds[0]);
                pParent.setFirstName(parentFirstName);
                pParent.setLastName(parentLastName);
                pParent.setAge(parentAge);
                pParent.setGender(parentGender);

                child.setId((int) childIds[0]);
                child.setPersonId((int) personIds[1]);
                child.setParentId((int) parentIds[0]);
                child.setSurveyId((int) surveyIds[0]);
                child.setImage(BLOB);

                parent.setId((int) parentIds[0]);
                parent.setChildId((int) childIds[0]);
                parent.setPersonId((int) personIds[0]);
                parent.setEmployment(parentOccupation);
                parent.setContactId((int) contactIds[0]);

                survey.setId((int) surveyIds[0]);
                survey.setChildId((int) childIds[0]);
                survey.setSocialWorkerUsername(socialWorker.getUsername());
//                survey.setSurveyData(null);

                contact.setId((int) contactIds[0]);
                contact.setAddress_line(addressLine);
                contact.setPhoneNumber(phoneNumber);
                contact.setAlternatePhoneNumber(alternatePhoneNumber);
                contact.setCity(city);
                contact.setDistrict(district);
                contact.setState(state);
                contact.setMailId(mailId);
                contact.setPincode(pincode);

                db.personDao().Update(pParent);
                db.personDao().Update(pChild);
                db.surveyDao().Update(survey);
                db.contactDao().Update(contact);
                db.childDao().Update(child);
                db.parentDao().Update(parent);
//                db.socialWorkerDao().Update(socialWorker);


//                Integer[] initialSurveys = socialWorker.getSurveyIds();
//                ArrayList<Integer> temp = new ArrayList<>(Arrays.asList(initialSurveys));
//
//                temp.add(survey.getId());
//                socialWorker.setSurveyIds(temp.toArray(new Integer[temp.size()]));
//
//

                db.setTransactionSuccessful();
                db.endTransaction();
                Toast.makeText(getApplicationContext(), "Data Added to database", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Welcome.class);
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean allFilled() {
        return !((childFirstName.equals("")) ||
                (childLastName.equals("")) ||
                (parentFirstName.equals("")) ||
                (parentLastName.equals("")) ||
                (childGender == -1) ||
                (parentGender == -1) ||
                (childAge == -1) ||
                (parentAge == -1) ||
                (phoneNumber == -1) ||
                addressLine.equals("") ||
                district.equals("") ||
                state.equals("") ||
                city.equals("") ||
                (BLOB == null) ||
                (pincode == -1));
    }

    public void takeImage(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        final int REQUEST_TAKE_PHOTO = 1;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            BLOB = stream.toByteArray();

            Toast.makeText(getApplicationContext(), String.valueOf(BLOB.toString()), 10).show();

            final ImageButton imageButton = findViewById(R.id.im_button_take_new);
            setPic(imageButton, BLOB);
        }
    }

    private void setPic(ImageButton imageView, byte[] BLOB) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(BLOB, 0, BLOB.length, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeByteArray(BLOB, 0, BLOB.length, bmOptions);
        imageView.setImageBitmap(bitmap);
    }
}
