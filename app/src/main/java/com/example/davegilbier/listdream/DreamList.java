package com.example.davegilbier.listdream;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
/**
 * Created by Dave Gilbier on 09/10/2017.
 */
public class DreamList extends AppCompatActivity {


    ListView listView;
    ArrayList<Dream> list;
    DreamListAdapter adapter = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dream_list_activity);

        listView = (ListView) findViewById(R.id.listView);
        list = new ArrayList<>();
        adapter = new DreamListAdapter(this, R.layout.dream_items, list);
        listView.setAdapter(adapter);

        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM DREAM");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dream = cursor.getString(1);
            String description = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Dream(dream, description, image, id));
        }
        adapter.notifyDataSetChanged();

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                CharSequence[] items = {"Update", "Delete"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(DreamList.this);

                dialog.setTitle("Choose an action");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0) {
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM DREAM");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogUpdate(DreamList.this, arrID.get(position));


                        } else {
                            Cursor c = MainActivity.sqLiteHelper.getData("SELECT id FROM DREAM");
                            ArrayList<Integer> arrID = new ArrayList<Integer>();
                            while (c.moveToNext()){
                                arrID.add(c.getInt(0));
                            }
                            showDialogDelete(arrID.get(position));
                        }
                    }
                });
                dialog.show();
                return true;
            }
        });
    }

    ImageView imageViewDream;
    private void showDialogUpdate(Activity activity, final int position){

        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.update_dream_activity);
        dialog.setTitle("Update");

        imageViewDream = (ImageView) dialog.findViewById(R.id.imageViewDream);
        final EditText edtDream = (EditText) dialog.findViewById(R.id.edtDream);
        final EditText edtDescription = (EditText) dialog.findViewById(R.id.edtDescription);
        Button btnUpdate = (Button) dialog.findViewById(R.id.btnUpdate);

        // iset ang width and height sa dialog
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.95);
        int height = (int) (activity.getResources().getDisplayMetrics().heightPixels * 0.7);
        dialog.getWindow().setLayout(width, height);
        dialog.show();

        imageViewDream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request photo library
                ActivityCompat.requestPermissions(
                        DreamList.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        888
                );
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MainActivity.sqLiteHelper.updateData(
                            edtDream.getText().toString().trim(),
                            edtDescription.getText().toString().trim(),
                            MainActivity.imageViewToByte(imageViewDream),
                            position
                    );
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "UPDATE SUCCESS!",Toast.LENGTH_SHORT).show();
                }
                catch (Exception error) {
                    Log.e("UPDATE FAIL!!!", error.getMessage());
                }
                updateDreamList();
            }
        });
    }

    private void showDialogDelete(final int idDream){
        final AlertDialog.Builder dialogDelete = new AlertDialog.Builder(DreamList.this);

        dialogDelete.setTitle("Wait...");
        dialogDelete.setMessage("Delete this dream? Really? ");
        dialogDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    MainActivity.sqLiteHelper.deleteData(idDream);
                    Toast.makeText(getApplicationContext(), "DELETE SUCCESS!",Toast.LENGTH_SHORT).show();
                } catch (Exception e){
                    Log.e("DELETE FAIL!!!", e.getMessage());
                }
                updateDreamList();
            }
        });

        dialogDelete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogDelete.show();
    }

    private void updateDreamList(){
        // get all data from sqlite
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM DREAM");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String dream = cursor.getString(1);
            String description = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            list.add(new Dream(dream, description, image, id));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == 888){
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 888);
            }
            else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 888 && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageViewDream.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
