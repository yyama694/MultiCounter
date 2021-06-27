package org.yyama.multicounter.view;


import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.yyama.multicounter.R;

// MainActivity にはなるべく描画以外のロジックを書く
// このクラスにはMainActivityを使用した描画に関するロジックを書く
public class MainActivityPainter {
    private MainActivityPainter() {
    }

    public static void paintAll(MainActivity activity) {
    }

    public static void deleteCounter(MainActivity activity, String tag) {
        RecyclerView ll = activity.findViewById(R.id.counter_group_linear_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            if (tag.equals(ll.getChildAt(i).getTag())) {
                ll.removeViewAt(i); // カウンター
                ll.removeViewAt(i); //　下線
                break;
            }
        }
    }

    public static void setNumber(MainActivity activity, String tag, int num) {
        RecyclerView ll = activity.findViewById(R.id.counter_group_linear_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            if (tag.equals(ll.getChildAt(i).getTag())) {
                TextView tv = ll.getChildAt(i).findViewById(R.id.counter_num);
                tv.setText(String.valueOf(num));
                break;
            }
        }
    }

    public static void reloadName(MainActivity activity, String tag, String name) {
        RecyclerView ll = activity.findViewById(R.id.counter_group_linear_layout);
        for (int i = 0; i < ll.getChildCount(); i++) {
            if (tag.equals(ll.getChildAt(i).getTag())) {
                TextView tv = ll.getChildAt(i).findViewById(R.id.counter_title);
                tv.setText(name);
                break;
            }
        }
    }
}