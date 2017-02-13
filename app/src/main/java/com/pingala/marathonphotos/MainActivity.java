package com.pingala.marathonphotos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ListView listView;
    ArrayList<String> stringArrayList;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new MemberIdTask().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });

        return true;
    }

    class MemberIdTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Loading...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            stringArrayList = new ArrayList<>();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReferenceFromUrl("https://marathonphotos-b4348.firebaseio.com/MemberList");
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        String memberId = data.child("memberId").getValue(String.class);
                        stringArrayList.add(memberId);
                    }
                    adapter = new ListViewAdapter(MainActivity.this, R.layout.item_listview, stringArrayList);
                    listView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pd.dismiss();
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, MemberImages.class);
                    intent.putExtra("ImagesLink","https://marathonphotos-b4348.firebaseio.com/Photos/" + (String) parent.getItemAtPosition(position));
                    startActivity(intent);

                }
            });
        }
    }
}
