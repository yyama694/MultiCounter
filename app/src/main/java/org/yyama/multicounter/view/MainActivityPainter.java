package org.yyama.multicounter.view;


import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.yyama.multicounter.R;
import org.yyama.multicounter.model.Counter;
import org.yyama.multicounter.model.CounterGroup;

// MainActivity にはなるべく描画以外のロジックを書く
// このクラスにはMainActivityを使用した描画に関するロジックを書く
public class MainActivityPainter {
    private MainActivityPainter() {
    }

    public static void paintAll(MainActivity activity) {
        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.counter_group_linear_layout);
        layout.removeAllViews();
        CounterGroup counterGroup = activity.getCounterGroups().getCurrentCounterGroup();
        int cnt = counterGroup.getCounterGroup().size();
        for (int i = 0; i < cnt; i++) {
            // カウンターを配置
            LinearLayout v = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.counter, null);
            layout.addView(v);
            Counter counter = counterGroup.getCounterGroup().get(i);
            v.setTag(counter.getId());
            TextView textView = (TextView) v.findViewById(R.id.counter_num);
            textView.setText(String.valueOf(counter.getNum()));
            TextView title = (TextView) v.findViewById(R.id.counter_title);
            title.setText(String.valueOf(counter.getTitle()));
            // 線を引く
            LinearLayout v2 = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.border, null);
            layout.addView(v2);
        }
    }

    public static void deleteCounter(MainActivity activity, String tag) {
        LinearLayout ll = activity.findViewById(R.id.counter_group_linear_layout);

        for (int i = 0; i < ll.getChildCount(); i++) {
            String tag2 = (String) ll.getChildAt(i).getTag();

            Log.d("counter", "child:" + ll.getChildAt(i).getTag() + ":" + i+":"+ tag2);
            if (tag.equals(tag2)) {
                ll.removeViewAt(i); // カウンター
                ll.removeViewAt(i); //　下線
                break;
            }
        }
    }

    public static void setNumber(MainActivity activity,String id,int num) {
        LinearLayout ll = activity.findViewById(R.id.counter_group_linear_layout);

        for (int i = 0; i < ll.getChildCount(); i++) {
            String tag = (String) ll.getChildAt(i).getTag();

            if (id.equals(tag)) {
                TextView tv= ll.getChildAt(i).findViewById(R.id.counter_num);
                tv.setText(String.valueOf(num));
                break;
            }
        }
    }

    public static void reloadName(MainActivity activity,String id,String name) {
        LinearLayout ll = activity.findViewById(R.id.counter_group_linear_layout);

        for (int i = 0; i < ll.getChildCount(); i++) {
            String tag = (String) ll.getChildAt(i).getTag();

            if (id.equals(tag)) {
                TextView tv= ll.getChildAt(i).findViewById(R.id.counter_title);
                tv.setText(name);
                break;
            }
        }
    }

}