package com.sreeginy.todolist;

import static com.sreeginy.todolist.Utils.DatabaseHelper.TABLE_NAME;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.TooltipCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sreeginy.todolist.Adapter.ToDoAdapter;
import com.sreeginy.todolist.Model.ToDo;
import com.sreeginy.todolist.Utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnDialogCloseListener {

    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private DatabaseHelper myDB;
    private List<ToDo> mList;
    private ToDoAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        fab = findViewById(R.id.add);
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        myDB = new DatabaseHelper(MainActivity.this);
        mList = new ArrayList<>();
        adapter = new ToDoAdapter(myDB, MainActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerViewTouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                storeDataInArrays();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        storeDataInArrays();
    }

    void storeDataInArrays() {
        mList.clear();
        mList = myDB.getAllTasks();
        Collections.reverse(mList);
        adapter.setTasks(mList);
        adapter.notifyDataSetChanged();
    }

//void storeDataInArrays() {
//    mList.clear();
//    Cursor cursor = null;
//
//    try {
//        cursor = myDB.readAllData();
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                ToDo toDoList = new ToDo();
//                toDoList.setTask(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK)));
//                mList.add(toDoList);
//            } while (cursor.moveToNext());
//        }
//    } finally {
//        if (cursor != null) {
//            cursor.close();
//        }
//    }
//
//    adapter.setTasks(mList); // Set the updated list in the adapter
//    adapter.notifyDataSetChanged();
//}


    public void showDataInArrays(String task) {
        Cursor cursor = myDB.getDataByName(task);
        List<ToDo> taskList = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            int taskColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_TASK);
            int statusColumnIndex = cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS);

            do {
                if (taskColumnIndex != -1 && statusColumnIndex != -1) {
                    ToDo toDoList = new ToDo();
                    toDoList.setTask(cursor.getString(taskColumnIndex));
                    toDoList.setStatus(cursor.getInt(statusColumnIndex));
                    taskList.add(toDoList);
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        Collections.reverse(taskList);
        adapter.setTasks(taskList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);

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
//
//        MenuItem deleteAllItem = menu.findItem(R.id.delete_all);
//        deleteAllItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
////                deleteAllTasks();
//                return true;
//            }
//        });

        MenuItem refreshItem = menu.findItem(R.id.menu_refresh);
        refreshItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                storeDataInArrays();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }


//    private void deleteAllTasks() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Delete All Tasks");
//        builder.setMessage("Are you sure you want to delete all tasks?");
//        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                myDB.deleteAllTasks();
//                storeDataInArrays();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }
//
    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        storeDataInArrays();
    }
}



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.top_menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) menuItem.getActionView();
//        searchView.setMaxWidth(Integer.MAX_VALUE);
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                showDataInArrays(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                // You can implement real-time search functionality here
//                return false;
//            }
//        });
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public void onDialogClose(DialogInterface dialogInterface) {
//        storeDataInArrays();
//    }

