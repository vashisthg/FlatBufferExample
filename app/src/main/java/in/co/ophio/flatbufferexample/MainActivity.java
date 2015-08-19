package in.co.ophio.flatbufferexample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import in.co.ophio.flatbufferexample.model.JsonPerson;
import in.co.ophio.flatbufferexample.model.JsonPersonList;
import in.co.ophio.flatbufferexample.model.Person;
import in.co.ophio.flatbufferexample.model.PersonList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readFlatBufferObject();
        readJsonObject();
    }

    // reading in UI thread just for example
    private void readJsonObject() {

        long time = System.nanoTime();
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.people_json);
            String json = DataReader.loadJsonString(inputStream);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray peopleJsonArray = jsonObject.getJSONArray("people");
            int peopleSize = peopleJsonArray.length();

            JsonPersonList jsonPersonList = new JsonPersonList();
            jsonPersonList.people = new ArrayList<>(peopleSize);

            for (int i = 0; i < peopleSize; i++) {
                JsonPerson jsonPerson = getJsonPerson(peopleJsonArray.getJSONObject(i));
                jsonPersonList.people.add(jsonPerson);
            }

            long timeToDeserialize = System.nanoTime() - time;
            Log.d(TAG, "time to deserialize Person from json: " + getMillisFromnano(timeToDeserialize));
            inputStream.close();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JsonPerson getJsonPerson(JSONObject jsonObject) throws JSONException {
        JsonPerson jsonPerson = new JsonPerson();
        jsonPerson.id = jsonObject.getString("id");
        jsonPerson.fName = jsonObject.getString("fName");
        jsonPerson.lName = jsonObject.getString("lName");
        jsonPerson.email = jsonObject.getString("email");

        JSONArray friendsArray = jsonObject.getJSONArray("friends");

        if (friendsArray != null && friendsArray.length() > 0) {
            int length = friendsArray.length();
            jsonPerson.friends = new ArrayList<>(length);

            for (int i = 0; i < length; i++) {
                JSONObject friendObject = friendsArray.getJSONObject(i);
                JsonPerson friend = getJsonPerson(friendObject);
                jsonPerson.friends.add(friend);
            }
        }

        return jsonPerson;
    }


    // reading in UI thread just for example
    private void readFlatBufferObject() {

        try {
            long readBufferStartTime = System.nanoTime();

            InputStream inputStream = getResources().openRawResource(R.raw.people_fb);
            byte[] data = DataReader.loadBytes(inputStream);

            ByteBuffer bb = ByteBuffer.wrap(data);
            PersonList personList = PersonList.getRootAsPersonList(bb);

            for (int i = 0; i < personList.peopleLength(); i++) {
                Person person = personList.people(i);
                extractFriends(person);
            }

            long timeToDeserialize = System.nanoTime() - readBufferStartTime;
            Log.d(TAG, "time to deserialize Person from flatbuffer: " + getMillisFromnano(timeToDeserialize));
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private String getMillisFromnano(long timeToDeserialize) {
        return timeToDeserialize / 1000000 + " ms, " +
                timeToDeserialize % 1000000 + " ns";
    }

    private void extractFriends(Person person) {
        for (int j = 0; j < person.friendsLength(); j++) {
            Person friend = person.friends(j);
            extractFriends(friend);
        }
    }

}
