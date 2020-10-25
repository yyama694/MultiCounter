package org.yyama.multicounter.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.yyama.multicounter.BuildConfig;
import org.yyama.multicounter.R;
import org.yyama.multicounter.dao.CounterDao;
import org.yyama.multicounter.dao.CounterGroupsDao;
import org.yyama.multicounter.dao.DBHelper;
import org.yyama.multicounter.model.Counter;
import org.yyama.multicounter.model.CounterGroup;
import org.yyama.multicounter.model.CounterGroups;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class GroupListActivity extends AppCompatActivity implements MultiCounterActivity {

    private CounterGroups counterGroups;
    private CounterDao counterDao;
    CounterGroupsDao counterGroupsDao;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("counter", "GroupListActivity#onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Uri uri = Uri.parse(BuildConfig.PRIVACY_POLICY_URL);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void paintAll() {
        LinearLayout ll = findViewById(R.id.counter_group_layout);
        ll.removeAllViews();
        for (CounterGroup cg : counterGroups.getCounterGroupList()
        ) {
            LinearLayout v = (LinearLayout) getLayoutInflater().inflate(R.layout.content_group_list_line, null);
            TextView title = v.findViewById(R.id.group_list_line_title);
            title.setText(cg.getTitle());
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
        String groupId = counterDao.getNextGroupId();
        Counter ct = new Counter(counterDao.getNextId(), groupId, "New Counter", 0, "", false, Calendar.getInstance());
        List<Counter> list = new ArrayList<>();
        list.add(ct);
        CounterGroup cg = new CounterGroup(groupId, title, list);
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
        builder.setMessage(getString(R.string.want_to_delete_it)).setTitle(getString(R.string.delete_confirmation)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String tag = (String) ((View) (menuItem.getActionView().getParent().getParent())).getTag();
                counterGroups.deleteCounterGroup(tag);
                deleteGroup(tag);
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    // カウンターグループ選択
    public void onClickCounterGroup(View view) {
        counterGroups.setCurrentGroupId((String) view.getTag());
        counterGroupsDao.updateCurrentGroup(counterGroups.getCurrentGroupId());
        Intent i = new Intent(GroupListActivity.this, MainActivity.class);
        i.putExtra("counterGroups", counterGroups);
        i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    // カウンターグループ追加ボタンクリック
    public void onClickAddBtn(View view) {
        DialogFragment df = new AddCounterDialog();
        Bundle args = new Bundle();
        args.putString("title", getResources().getString(R.string.counter_group_name));
        df.setArguments(args);
        df.show(getSupportFragmentManager(), "aa");
    }

    // 名前の変更メニュークリック
    public void onClickChangeName(final MenuItem menuItem) {
        ChangeCounterNameDialog df = new ChangeCounterNameDialog();
        Bundle args = new Bundle();
        String id = (String) ((View) menuItem.getActionView().getParent().getParent()).getTag();
        args.putString("id", id);
        args.putString("beforeName", counterGroups.findCounterGroupsById(id).getTitle());
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
                ((TextView) ll.getChildAt(i).findViewById(R.id.group_list_line_title)).setText(newName);
                break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("counter", "GroupListActivity#onStart");
        // SQLite初期設定
        DBHelper helper = new DBHelper(this);
        counterGroupsDao = new CounterGroupsDao(this, helper);
        counterDao = new CounterDao(helper);
        counterGroups = counterGroupsDao.loadAll(counterDao);
        boolean initial = getIntent().getBooleanExtra("initial", true);
        if (initial) {
            counterGroups.setCurrentGroupId(counterGroupsDao.selectCurrentGroup());
            Intent i = new Intent(GroupListActivity.this, MainActivity.class);
            i.putExtra("counterGroups", counterGroups);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            return;
        }
        setContentView(R.layout.activity_goup_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        paintAll();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("counter", "GroupListActivity#onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("counter", "GroupListActivity#onRestart");
    }

    @Override
    protected void onStop() {
        counterGroupsDao.saveAll(counterGroups);
        super.onStop();
        Log.d("counter", "GroupListActivity#onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("counter", "GroupListActivity#onDestroy");
    }

}
