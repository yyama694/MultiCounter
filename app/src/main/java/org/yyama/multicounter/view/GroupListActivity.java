package org.yyama.multicounter.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import org.yyama.multicounter.R;
import org.yyama.multicounter.dao.CounterGroupsDao;
import org.yyama.multicounter.model.Counter;
import org.yyama.multicounter.model.CounterGroup;
import org.yyama.multicounter.model.CounterGroupId;
import org.yyama.multicounter.model.CounterGroups;
import org.yyama.multicounter.model.CounterId;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupListActivity extends AppCompatActivity implements MultiCounterActivity {

    private CounterGroups counterGroups;

    private boolean pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("counter","onCreate");
        super.onCreate(savedInstanceState);
        counterGroups = (CounterGroups) getIntent().getSerializableExtra("counterGroups");
        if (counterGroups == null) {
            Intent i = new Intent(GroupListActivity.this, MainActivity.class);
            i.putExtra("counterGroups", counterGroups);
            startActivity(i);
            pass=true;
            return;
        }
        setContentView(R.layout.activity_goup_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        paintAll();
    }

    private void paintAll() {
        LinearLayout ll = findViewById(R.id.counter_group_layout);
        ll.removeAllViews();
        for (CounterGroup cg : counterGroups.getCounterGroupList()
        ) {
            LinearLayout v = (LinearLayout) getLayoutInflater().inflate(R.layout.content_group_list_line, null);
            TextView title = v.findViewById(R.id.group_list_line_title);
            title.setText(cg.getTitle());
            TextView lastUpdate = v.findViewById(R.id.group_list_last_update);
            v.setTag(cg.getId());
            ll.addView(v);
            LinearLayout v2 = (LinearLayout) getLayoutInflater().inflate(R.layout.border, null);
            ll.addView(v2);


            // 更新日付の設定
            Calendar cal = cg.getCounterLastUpdateDateTime();
            DateFormat df = DateFormat.getDateTimeInstance();
            TextView lastUpdateDateTimeView = v.findViewById(R.id.group_list_last_update);
            lastUpdateDateTimeView.setText(df.format(cal.getTime()));
        }
    }

    @Override
    public void onClickDialogAddButton(String title) {
        Counter ct = new Counter();
        ct.setId(CounterId.nextId());
        ct.setNum(0);
        ct.setTitle("new Counter");
        ct.setLastUpdateDateTime(Calendar.getInstance());
        List<Counter> list = new ArrayList<>();
        list.add(ct);
        CounterGroup cg = new CounterGroup(list);
        cg.setId(CounterGroupId.nextId());
        cg.setTitle(title);
        counterGroups.addCounterGroup(cg);
        paintAll();
    }

    private void deleteGroup(String tag) {
        LinearLayout ll = findViewById(R.id.counter_group_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            String tag2 = (String) ll.getChildAt(i).getTag();
            if (tag.equals(tag2)) {
                ll.removeViewAt(i);
                ll.removeViewAt(i);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pass) {
            pass=false;
            return;
        }
        if(counterGroups == null) {
            Log.d("counter","onResume and contergroups is null.");

            counterGroups = CounterGroupsDao.loadAll();
            setContentView(R.layout.activity_goup_list) ;
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            paintAll();
        } else {
            Log.d("counter","onResume and contergroups is not null.");

        }
    }

    // カウンタメニュークリック
    public void onClickMenu(View view) {
        final PopupMenu counterMenuPopup = new PopupMenu(this, view);
        counterMenuPopup.inflate(R.menu.counter_group_menu);
        MenuItem menuItem1 = counterMenuPopup.getMenu().findItem(R.id.counter_group_menu_delete);
        menuItem1.setActionView(view);
        MenuItem menuItem2 = counterMenuPopup.getMenu().findItem(R.id.counter_group_menu_change_name);
        menuItem2.setActionView(view);
        counterMenuPopup.show();
    }

    // カウンターメニューの削除クリック
    public void OnClickDeleteMenu(final MenuItem menuItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("削除してよろしいですか？").setTitle("削除確認").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String tag = (String) ((View) (menuItem.getActionView().getParent().getParent())).getTag();
                counterGroups.deleteCounterGroup(tag);
                deleteGroup(tag);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    // カウンターグループ選択
    public void onClickCounterGroup(View view){
        counterGroups.setCurrentGroupId((String) view.getTag());
        Intent i = new Intent(GroupListActivity.this, MainActivity.class);
        i.putExtra("counterGroups", counterGroups);
        startActivity(i);
    }

    // カウンターグループ追加ボタンクリック
    public void onClickAddBtn(View view) {
        DialogFragment df = new AddCounterDialog();
        Bundle args = new Bundle();
        args.putString("title", "title (Counter)");
        df.setArguments(args);
        df.show(getSupportFragmentManager(), "aa");
    }

    // 名前の変更メニュークリック
    public void onClickChangeName(final MenuItem menuItem) {
        ChangeCounterNameDialog df = new ChangeCounterNameDialog();
        Bundle args = new Bundle();
        String id =(String) ((View)menuItem.getActionView().getParent().getParent()).getTag();
        args.putString("id", id);
        args.putString("beforeName",counterGroups.findCounterGroupsById(id).getTitle());
        df.setArguments(args);
        df.show(getSupportFragmentManager(), "aa");
    }

    // カウンターグループの名前変更
    @Override
    public void changeName(String id, String newName) {
        // model の変更
        counterGroups.findCounterGroupsById(id).setTitle(newName);

        // view の変更
        LinearLayout ll = findViewById(R.id.counter_group_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            String tag = (String) ll.getChildAt(i).getTag();
            if (id.equals(tag)) {
                ((TextView)ll.getChildAt(i).findViewById(R.id.group_list_line_title)).setText(newName);
                break;
            }
        }
    }


}
