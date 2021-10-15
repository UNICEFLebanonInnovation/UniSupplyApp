package com.unicefwinterizationplatform.winterization_android;

/**
 * Created by Tarek on 7/27/15.
 */


import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
        import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
        import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.couchbase.lite.SavedRevision;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditItemView extends HeaderActivity {

    NumberPicker np;
    TextView titleText, deliveredText;
    EditText comment;
    ItemObject itemObject;
    Button doneButton;
    Button completeAllBtn;
    Button resetBtn;
    Button addComment;
    ScrollView scroll;
    String docId;

    CommentAdapter commentAdapter;
    ListView commentList;

    final Context context = this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edititem);

        final ActionBar actionBar = getActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        itemObject = (ItemObject)getIntent().getSerializableExtra("ITEM");
        docId = getIntent().getStringExtra("DIST_ID");

        scroll = (ScrollView)findViewById(R.id.scrollview);

        np = (NumberPicker) findViewById(R.id.numberPicker1);
        //np1 = (NumberPicker) findViewById(R.id.numberPicker2);
        titleText = (TextView) findViewById(R.id.textView_titleText);
        deliveredText = (TextView) findViewById(R.id.delivered);
        //destroyedText = (TextView) findViewById(R.id.destroyed);
        completeAllBtn = (Button) findViewById(R.id.completeall);
        doneButton = (Button) findViewById(R.id.Done);
        resetBtn = (Button) findViewById(R.id.reset);
        comment = (EditText) findViewById(R.id.comment_text);

        //comment.setText(itemObject.getComment());
        titleText.setText(itemObject.getItemType());

        np.setMinValue(0);
        np.setMaxValue(itemObject.getQuantity() - itemObject.getDelivered());
        np.setValue(0);
        np.setWrapSelectorWheel(false);

        addComment = (Button) findViewById(R.id.comment_button);

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!comment.getText().toString().isEmpty()) {


                    Map<String, Object> newComment = new HashMap<String, Object>();
                    Map<String, String> userProf = UserPrefs.getUserProfile(context);

                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm");
                    Calendar calendar = GregorianCalendar.getInstance();
                    String currentTimeString = dateFormatter.format(calendar.getTime());


                    newComment.put("comment", comment.getText().toString());
                    newComment.put("user", userProf.get("user"));
                    newComment.put("date", currentTimeString);

                    commentAdapter.addComment(newComment);

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    Toast.makeText(context, "Comment \"" + comment.getText() + "\" has been added.", Toast.LENGTH_SHORT).show();
                    comment.setText("");
                    commentAdapter.notifyDataSetChanged();
                    scroll.fullScroll(View.FOCUS_DOWN);

                    CouchBaseManager.getInstance(getApplicationContext()).addCommentInLog(docId, itemObject.getItemType(), CouchBaseManager.DistStat.COMMENT_ADDED);

                }

            }
        });


        deliveredText.setText("0/"+(itemObject.getQuantity()-itemObject.getDelivered()));

        //destroyedText.setText(itemObject.getDestroyed()+"/"+( itemObject.getQuantity() - itemObject.getDelivered()));


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_generic, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final LinearLayout linearLayout = (LinearLayout) promptsView
                        .findViewById(R.id.layout_gen);

                TextView alertText = new TextView(getApplicationContext());
                linearLayout.addView(alertText);
                alertText.setTextSize(20);
                alertText.setTextColor(getResources().getColor(R.color.black));
                alertText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                alertText.setGravity(Gravity.CENTER);

                alertText.setText("Are you sure you want to\nreset?");


                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        itemObject.setDelivered(0);
                                        np.setMaxValue(itemObject.getQuantity());
                                        deliveredText.setText(np.getValue()+"/"+np.getMaxValue());

                                        dialog.cancel();

                                    }
                                })
                        .setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();


            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             //   itemObject.setComment(comment.getText().toString());

                itemObject.setCommentArray(commentAdapter.getList());

                itemObject.setDelivered(itemObject.getDelivered() + np.getValue());

                int position = getIntent().getIntExtra("position",0);
                Intent intent = new Intent();
                intent.putExtra("ITEM", itemObject);
                intent.putExtra("position", position);
                LogObject logObject = new LogObject();
                logObject.setItemName(itemObject.getItemType());
                logObject.setAmount(itemObject.getDelivered().toString());
                SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");
                Calendar calendar = GregorianCalendar.getInstance();
                String currentTimeString = dateFormatter.format(calendar.getTime());
                logObject.setTime(currentTimeString);
                intent.putExtra("LOG", logObject);

                //logObject.setPlace();

                setResult(1, intent);
                finish();

            }
        });

        completeAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                np.setValue(np.getMaxValue());
              //  itemObject.setDelivered(itemObject.getDelivered() + np.getMaxValue());
                deliveredText.setText(np.getValue()+"/"+np.getMaxValue());
               // np1.setMaxValue(0);
              //  destroyedText.setText("0/0");
              //  itemObject.setDestroyed(np1.getValue());

            }
        });

        np.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub

                String delivered =  newVal +"/"+ np.getMaxValue();

                deliveredText.setText(delivered);


               //itemObject.setDelivered(itemObject.getDelivered() + newVal);


               // np1.setMaxValue(itemObject.getQuantity() - itemObject.getDelivered());
              //  np1.setWrapSelectorWheel(true);
              //  np1.setWrapSelectorWheel(false);
             //   destroyedText.setText(np1.getValue()+"/"+np1.getMaxValue());
             //   itemObject.setDestroyed(np1.getValue());
            }
        });


     /*   np1.setOnValueChangedListener(new OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {

                String destroyed =  newVal +"/"+ numberPicker.getMaxValue();

                itemObject.setDestroyed(newVal);
                destroyedText.setText(destroyed);

                np1.setWrapSelectorWheel(true);
                np1.setWrapSelectorWheel(false);

            }
        });*/


        ArrayList<Map<String,Object>> arr; //= new ArrayList<>();


        arr = itemObject.getCommentArray();

        commentAdapter = new CommentAdapter(this);
        commentAdapter.addList(arr);// new ListAdapter(this, android.R.layout.simple_list_item_1, arr);
        commentList = (ListView) this.findViewById(R.id.comments_list);
        commentList.setAdapter(commentAdapter);

        commentList.setOnItemClickListener(onItemClickListener());


   /*     LinearLayout ll = (LinearLayout) findViewById(R.id.comments_layout);

        LinearLayout innerLayout = new LinearLayout(getApplicationContext());

        innerLayout.setOrientation(LinearLayout.HORIZONTAL);



        TextView Text = new TextView(getApplicationContext());
        Text.setTextSize(25);
        Text.setTextColor(Color.BLACK);
        Text.setText("Comment 2");

        EditText edit = new EditText(getApplicationContext());
        edit.setTextSize(25);
        edit.setTextColor(Color.BLACK);
        edit.setHint("comment");

       //editText.setId();
        innerLayout.addView(Text);
        innerLayout.addView(edit);

        ll.addView(innerLayout);*/


    }


    private AdapterView.OnItemClickListener onItemClickListener() {

        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {

                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.alert_editcomment, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                final Map<String,Object> comment = commentAdapter.getItem(i);

                userInput.setText(comment.get("comment")+"");

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {


                                        comment.put("comment",userInput.getText().toString());

                                        commentAdapter.editCommentToList(comment, i);
                                        CouchBaseManager.getInstance(getApplicationContext()).addCommentInLog(docId, itemObject.getItemType(), CouchBaseManager.DistStat.COMMENT_EDITED);


                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        };

    }



    @Override
    public void onBackPressed() {

       checkOnBack();
}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                checkOnBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void checkOnBack()
 {
     if (np.getValue() > 0) {
         LayoutInflater li = LayoutInflater.from(context);
         View promptsView = li.inflate(R.layout.alert_generic, null);

         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

         // set prompts.xml to alertdialog builder
         alertDialogBuilder.setView(promptsView);

         final LinearLayout linearLayout = (LinearLayout) promptsView
                 .findViewById(R.id.layout_gen);

         TextView alertText = new TextView(getApplicationContext());
         linearLayout.addView(alertText);
         alertText.setTextSize(20);
         alertText.setTextColor(getResources().getColor(R.color.black));
         alertText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
         alertText.setGravity(Gravity.CENTER);

         alertText.setText("Are you sure you want to go back\nwithout saving?");


         // set dialog message
         alertDialogBuilder
                 .setCancelable(false)
                 .setPositiveButton("Yes",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {

                                 dialog.cancel();
                                 finish();
                             }
                         })
                 .setNegativeButton("No",
                         new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                                 dialog.cancel();
                             }
                         });

         // create alert dialog
         AlertDialog alertDialog = alertDialogBuilder.create();

         // show it
         alertDialog.show();

     } else {

         finish();
     }
 }

}
//MENU ITEMS MIGHT USE IN FUTURE
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.item_menu, menu);

        menu.findItem(R.id.action_done).setShowAsAction( MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menu.findItem(R.id.action_comp).setShowAsAction( MenuItem.SHOW_AS_ACTION_WITH_TEXT);


        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Navigate "up" the demo structure to the launchpad activity.
                // See http://developer.android.com/design/patterns/navigation.html for more.
                this.finish();
                return true;

            case R.id.action_done:
                // Go to the previous step in the wizard. If there is no previous step,
                // setCurrentItem will do nothing.
                this.finish();
                return true;

            case R.id.action_comp:
                // Advance to the next step in the wizard. If there is no next step, setCurrentItem
                // will do nothing.
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }*/





