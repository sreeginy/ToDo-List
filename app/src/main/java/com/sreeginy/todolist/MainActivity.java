package com.sreeginy.todolist;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sreeginy.todolist.Adapter.ToDoAdapter;
import com.sreeginy.todolist.Model.ToDo;
import com.sreeginy.todolist.Utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDialogCloserListner  {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseHelper myDB;
    private List<ToDo> mList;
    private ToDoAdapter adapter;

    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.add);
        myDB = new DatabaseHelper(MainActivity.this);
        mList = new ArrayList<>();
        adapter = new ToDoAdapter(myDB, MainActivity.this);

        swipeRefreshLayout = findViewById(R.id.swiperefresh);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        storeDataInArrays();

        mList = myDB.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                storeDataInArrays();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }



    void storeDataInArrays() {
        mList.clear();
        Cursor cursor = myDB.readAllData();

        try {
            if (cursor.moveToFirst()) {
                do {
                    ToDo toDoList = new ToDo();
                    toDoList.setTask(cursor.getString(0));
                    mList.add(toDoList);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }




    public void showDataInArrays(String task) {
        mList.clear();
        Cursor cursor = myDB.getDataByName(task);
        try {
            if (cursor.moveToFirst()) {

                do {
                    ToDo toDoList = new ToDo();
                    toDoList.setTask(cursor.getString(0));
                    mList.add(toDoList);
                    Log.e("search", toDoList.toString());
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

        swipeRefreshLayout.setRefreshing(false);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showDataInArrays(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // You can implement real-time search functionality here
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList = myDB.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);
        adapter.notifyDataSetChanged();
    }

}