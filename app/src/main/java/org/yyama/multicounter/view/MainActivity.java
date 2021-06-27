package org.yyama.multicounter.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.appbar.AppBarLayout;

import org.yyama.multicounter.BuildConfig;
import org.yyama.multicounter.R;
import org.yyama.multicounter.dao.CounterDao;
import org.yyama.multicounter.dao.CounterGroupsDao;
import org.yyama.multicounter.dao.DBHelper;
import org.yyama.multicounter.model.Counter;
import org.yyama.multicounter.model.CounterGroup;
import org.yyama.multicounter.model.CounterGroups;
import org.yyama.multicounter.model.CounterSize;

import java.lang.reflect.Method;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements MultiCounterActivity {

    private CounterGroups counterGroups;
    private CounterDao counterDao;
    CounterGroupsDao counterGroupsDao;
    private AdView mAdView;
    private RecyclerView.Adapter mAdapter;

    public CounterGroups getCounterGroups() {
        return counterGroups;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SQLite初期設定
        DBHelper helper = new DBHelper(this);
        counterGroupsDao = new CounterGroupsDao(this, helper);
        counterDao = new CounterDao(helper);

        // データロード
        counterGroups = (CounterGroups) getIntent().getSerializableExtra("counterGroups");

        // ペイント
        MainActivityPainter.paintAll(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(counterGroups.getCurrentCounterGroup().getTitle());

        // ads 設定
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        if (new Random().nextInt(2) == 0) {
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else {
            ((ViewGroup) (mAdView.getParent())).removeView(mAdView);
        }

        // リサイクラービューの準備
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.counter_group_linear_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new CounterGroupAdapter(getCounterGroups().getCurrentCounterGroup(), this);
        recyclerView.setAdapter(mAdapter);
        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);

        // ドラックアンドドロップの操作を実装する
        itemTouchHelper = new ItemTouchHelper(
                // 上、下のドラッグを処理する Callback を作成する。スワイプは処理しない
                new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                        ItemTouchHelper.ACTION_STATE_IDLE) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        final int fromPos = viewHolder.getAdapterPosition();
                        final int toPos = target.getAdapterPosition();
                        mAdapter.notifyItemMoved(fromPos, toPos);
                        Log.d("counter", "onMove");
                        Log.d("counter", "pos1:" + ((CounterGroupAdapter.MyViewHolder) viewHolder).pos);
                        Log.d("counter", "pos1:" + ((CounterGroupAdapter.MyViewHolder) target).pos);

                        counterGroups.getCurrentCounterGroup().move(((CounterGroupAdapter.MyViewHolder) viewHolder).pos, ((CounterGroupAdapter.MyViewHolder) target).pos);
                        int posTmp = ((CounterGroupAdapter.MyViewHolder) viewHolder).pos;
                        ((CounterGroupAdapter.MyViewHolder) viewHolder).pos = ((CounterGroupAdapter.MyViewHolder) target).pos;
                        ((CounterGroupAdapter.MyViewHolder) target).pos = posTmp;

                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    }

                    // 行の長押しではドラッグできないようにする
                    @Override
                    public boolean isLongPressDragEnabled() {
                        return false;
                    }
                });

        // recyclerView と itemTouchHelper を紐づける
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private ItemTouchHelper itemTouchHelper;

    public ItemTouchHelper getItemTouchHelper() {
        return itemTouchHelper;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // ポップアップのメニューを出すための記述
        try {
            Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            method.setAccessible(true);
            method.invoke(menu, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_group, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 記録開始・停止メニューの操作可否判定
        boolean b = counterGroups.getCurrentCounterGroup().isRecording();
        Log.d("counter", "recoding group:" + b);
        if (b) {
            menu.findItem(R.id.counter_menu_start_recording).setVisible(false);
            menu.findItem(R.id.counter_menu_stop_recording).setVisible(true);
        } else {
            menu.findItem(R.id.counter_menu_start_recording).setVisible(true);
            menu.findItem(R.id.counter_menu_stop_recording).setVisible(false);
        }
        return true;
    }

    // カウンターグループの記録を開始する
    public void onClickStartRecordingGroup(final MenuItem menuItem) {
        if (hasPermission()) {
            counterGroups.getCurrentCounterGroup().startRecording();
        } else {
            waitingForPermissionCounterGroup = counterGroups.getCurrentCounterGroup();
            waitingForPermissionCounter = null;
            requestLocationPermission();
        }
        counterGroups.getCurrentCounterGroup().startRecording();
    }

    // カウンターグループの記録を停止する
    public void onClickStopRecordingGroup(final MenuItem menuItem) {
        counterGroups.getCurrentCounterGroup().stopRecording();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Uri uri = Uri.parse(BuildConfig.PRIVACY_POLICY_URL);
            Intent i = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d("counter", "MainActivity#onSupportNavigateUp");
        Intent i = new Intent(this, GroupListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("initial", false);
        finish();
        startActivity(i);
        return super.onSupportNavigateUp();
    }

    // カウンター追加
    @Override
    public void onClickDialogAddButton(String title) {
        counterGroups.getCurrentCounterGroup().addCounter(title, counterDao);
        MainActivityPainter.paintAll(this);
    }

    // プラスボタンクリック
    public void onClickBtnPlus(View view) {
        final CounterGroup cg = getCounterGroups().getCurrentCounterGroup();
        Counter counter = cg.findByid((String) ((View) view.getParent().getParent()).getTag());
        counter.increment();
        TextView textView = ((View) view.getParent()).findViewById(R.id.counter_num);
        textView.setText(String.valueOf(counter.getNum()));
        if (counter.isRecording()) {
            counterDao.outFile(counter, "increment", this);
        }
        if (cg.isRecording()) {
            counterDao.outGroupFile(cg, counter, "increment", this);
        }
    }

    // マイナスボタンクリック
    public void onClickBtnMinus(View view) {
        final CounterGroup cg = getCounterGroups().getCurrentCounterGroup();
        Counter counter = cg.findByid((String) ((View) view.getParent().getParent()).getTag());
        counter.decrement();
        TextView textView = ((View) view.getParent()).findViewById(R.id.counter_num);
        textView.setText(String.valueOf(counter.getNum()));
        if (counter.isRecording()) {
            counterDao.outFile(counter, "decrement", this);
        }
        if (cg.isRecording()) {
            counterDao.outGroupFile(cg, counter, "decrement", this);
        }
    }

    // カウンター追加ボタンクリック
    public void onClickAddBtn(View view) {
        DialogFragment df = new AddCounterDialog();
        Bundle args = new Bundle();
        args.putString("title", getResources().getString(R.string.counter_name));
        df.setArguments(args);
        df.show(getSupportFragmentManager(), "aa");
    }

    // カウンタメニュークリック
    public void onClickMenu(View view) {
        final PopupMenu counterMenuPopup = new PopupMenu(this, view);
        counterMenuPopup.inflate(R.menu.counter_menu);
        MenuItem menuItem = counterMenuPopup.getMenu().findItem(R.id.counter_menu_delete);
        menuItem.setActionView(view);
        MenuItem menuItem2 = counterMenuPopup.getMenu().findItem(R.id.counter_menu_reset);
        menuItem2.setActionView(view);
        MenuItem menuItem3 = counterMenuPopup.getMenu().findItem(R.id.counter_menu_set_number);
        menuItem3.setActionView(view);
        MenuItem menuItem4 = counterMenuPopup.getMenu().findItem(R.id.counter_menu_change_name);
        menuItem4.setActionView(view);
        MenuItem menuItem5 = counterMenuPopup.getMenu().findItem(R.id.counter_menu_start_recording);
        menuItem5.setActionView(view);
        MenuItem menuItem6 = counterMenuPopup.getMenu().findItem(R.id.counter_menu_stop_recording);
        menuItem6.setActionView(view);
        MenuItem menuItem7 = counterMenuPopup.getMenu().findItem(R.id.counter_menu_change_size);
        menuItem7.setActionView(view);
        // ポップアップのメニューを出すための記述
        try {
            Method method = counterMenuPopup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            method.setAccessible(true);
            method.invoke(counterMenuPopup.getMenu(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 記録開始・停止メニューの操作可否判定
        String counterId = (String) ((View) view.getParent().getParent()).getTag();
        boolean b = counterGroups.getCurrentCounterGroup().findByid(counterId).isRecording();
        if (b) {
            counterMenuPopup.getMenu().findItem(R.id.counter_menu_start_recording).setVisible(false);
            counterMenuPopup.getMenu().findItem(R.id.counter_menu_stop_recording).setVisible(true);
        } else {
            counterMenuPopup.getMenu().findItem(R.id.counter_menu_start_recording).setVisible(true);
            counterMenuPopup.getMenu().findItem(R.id.counter_menu_stop_recording).setVisible(false);
        }
        counterMenuPopup.show();
    }

    // カウンターメニューの削除クリック
    public void onClickDeleteMenu(final MenuItem menuItem) {
        final CounterGroup cg = getCounterGroups().getCurrentCounterGroup();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.want_to_delete_it)).setTitle(getString(R.string.delete_confirmation)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String tag = (String) ((View) (menuItem.getActionView().getParent().getParent())).getTag();
                cg.deleteCounter(tag);
//                MainActivityPainter.deleteCounter(MainActivity.this, tag);
                AppBarLayout appBarLayout = findViewById(R.id.appBar);
                appBarLayout.setExpanded(true, true);
                mAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    // カウンターメニューのリセットクリック
    public void onClickResetMenu(final MenuItem menuItem) {
        final CounterGroup cg = getCounterGroups().getCurrentCounterGroup();
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.want_to_reset_it)).setTitle(getString(R.string.reset_confirmation)).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String tag = (String) ((View) (menuItem.getActionView().getParent().getParent())).getTag();
                Counter c = cg.findByid(tag);
                c.reset();
                TextView textView = ((View) menuItem.getActionView().getParent().getParent()).findViewById(R.id.counter_num);
                textView.setText(String.valueOf(c.getNum()));
                if (c.isRecording()) {
                    counterDao.outFile(c, "reset", MainActivity.this);
                }
                if (cg.isRecording()) {
                    counterDao.outGroupFile(cg, c, "reset", MainActivity.this);
                }
            }
        }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    // カウンターメニューの値の設定クリック
    public void onClickSetNumber(final MenuItem menuItem) {
        SetNumberDialog df = new SetNumberDialog();
        Bundle args = new Bundle();
        args.putString("id", (String) ((View) menuItem.getActionView().getParent().getParent()).getTag());
        df.setArguments(args);
        df.show(getSupportFragmentManager(), "aa");
    }

    // 値を設定ダイアログのOKボタンクリック
    public void onClickSetNumberDialogOkButton(String id, int number) {
        CounterGroup cg = counterGroups.getCurrentCounterGroup();
        Counter c = cg.findByid(id);
        c.setNum(number);
        MainActivityPainter.setNumber(this, id, number);
        if (c.isRecording()) {
            counterDao.outFile(c, "set number", this);
        }
        if (cg.isRecording()) {
            counterDao.outGroupFile(cg, c, "set number", this);
        }
    }

    // 名前の変更メニュークリック
    public void onClickChangeName(final MenuItem menuItem) {
        ChangeCounterNameDialog df = new ChangeCounterNameDialog();
        Bundle args = new Bundle();
        String id = (String) ((View) menuItem.getActionView().getParent().getParent()).getTag();
        args.putString("id", id);
        args.putString("beforeName", counterGroups.getCurrentCounterGroup().findByid(id).getTitle());
        df.setArguments(args);
        df.show(getSupportFragmentManager(), "aa");
    }

    @Override
    public void changeName(String id, String newName) {
        // model の変更
        counterGroups.getCurrentCounterGroup().findByid(id).setTitle(newName);

        // view の変更
        MainActivityPainter.reloadName(this, id, newName);
    }

    // permissionの確認
    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    // 記録を開始するメニュークリック
    public void onClickStartRecording(final MenuItem menuItem) {
        String counterId = (String) ((View) menuItem.getActionView().getParent().getParent()).getTag();
        Counter c = counterGroups.getCurrentCounterGroup().findByid(counterId);
        if (hasPermission()) {
            c.startRecording();
        } else {
            waitingForPermissionCounter = c;
            waitingForPermissionCounterGroup = null;
            requestLocationPermission();
        }
    }

    private Counter waitingForPermissionCounter;
    private CounterGroup waitingForPermissionCounterGroup;

    private final int REQUEST_PERMISSION = 1000;

    // 許可を求める
    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
    }

    // 結果の受け取り
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION) {
            // 使用が許可された
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (waitingForPermissionCounter != null) {
                    waitingForPermissionCounter.startRecording();
                } else if (waitingForPermissionCounterGroup != null) {
                    waitingForPermissionCounterGroup.startRecording();
                }
            } else {
                // それでも拒否された時の対応
                Toast toast =
                        Toast.makeText(this, "Do not have permission.", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    // 記録を停止するメニュークリック
    public void onClickStopRecording(final MenuItem menuItem) {
        String counterId = (String) ((View) menuItem.getActionView().getParent().getParent()).getTag();
        Counter c = counterGroups.getCurrentCounterGroup().findByid(counterId);
        c.stopRecording();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("counter", "MainActivity#onStop");
    }

    // サイズ変更メニュークリック
    public void onClickChangeSize(final MenuItem menuItem) {
        final CounterSize[] counterSizes = CounterSize.values();
        final String[] sizes = new String[counterSizes.length];
        sizes[0] = getResources().getString(R.string.small_size);
        sizes[1] = getResources().getString(R.string.medium_size);
        sizes[2] = getResources().getString(R.string.large_size);
        String counterId = (String) ((View) menuItem.getActionView().getParent().getParent()).getTag();
        final Counter c = counterGroups.getCurrentCounterGroup().findByid(counterId);
        new AlertDialog.Builder(this)
                .setTitle(R.string.select_size)
                .setSingleChoiceItems(sizes, c.getSize().getId(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        setTmpSize(item);
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        c.setSize(selectedItemTmp);
                        mAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private int selectedItemTmp;

    private void setTmpSize(int item) {
        Log.d("counter", "item " + item + " selected");
        selectedItemTmp = item;
    }


    @Override
    protected void onPause() {
        counterGroupsDao.saveAll(counterGroups);
        super.onPause();
        Log.d("counter", "MainActivity#onPause");
    }

    // Back キーは　戻る（←）と同じ挙動とする。
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_BACK) {
            return super.onKeyDown(keyCode, event);
        } else {
            onSupportNavigateUp();
            return false;
        }
    }
}
