package de.customtv.start;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class LauncherPrefsActivity extends Activity {
    private SharedPreferences prefs;
    private CheckBox clock24;
    private CheckBox showDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.fullscreen(this);
        prefs = getSharedPreferences("mein_start", MODE_PRIVATE);
        setContentView(buildUi());
    }

    private View buildUi() {
        FrameLayout root = new FrameLayout(this);
        root.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Ui.BG_TOP, Ui.BG_BOTTOM}));

        LinearLayout column = new LinearLayout(this);
        column.setOrientation(LinearLayout.VERTICAL);
        column.setPadding(Ui.dp(this, 16), Ui.dp(this, 14),
                Ui.dp(this, 16), Ui.dp(this, 10));
        root.addView(column, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(Ui.dp(this, 8), 0, Ui.dp(this, 8), 0);
        column.addView(topBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 44)));

        TextView back = Ui.text(this, "\u2039  Back", 14, Ui.TEXT, false);
        back.setGravity(Gravity.CENTER);
        back.setPadding(Ui.dp(this, 12), Ui.dp(this, 6),
                Ui.dp(this, 12), Ui.dp(this, 6));
        back.setBackground(Ui.pillSelector(this));
        back.setFocusable(true);
        back.setClickable(true);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Ui.focusScale(back);
        topBar.addView(back, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = Ui.text(this, "LightOS", 20, Ui.TEXT, true);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleLp.leftMargin = Ui.dp(this, 16);
        topBar.addView(title, titleLp);

        TextView subtitle = Ui.text(this, "Customize Home", 12, Ui.TEXT_MUTED, false);
        subtitle.setPadding(Ui.dp(this, 8), 0, Ui.dp(this, 8), 0);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = Ui.dp(this, 6);
        column.addView(subtitle, subLp);

        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setFocusable(false);
        scroll.setClipToPadding(false);
        scroll.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setFocusable(false);
        scroll.addView(list, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        column.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        TextView section = Ui.text(this, "CLOCK & DATE", 11, Ui.ACCENT, true);
        section.setLetterSpacing(0.12f);
        section.setPadding(Ui.dp(this, 8), 0, Ui.dp(this, 8), 0);
        LinearLayout.LayoutParams sectionLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sectionLp.topMargin = Ui.dp(this, 10);
        sectionLp.bottomMargin = Ui.dp(this, 8);
        list.addView(section, sectionLp);

        clock24 = addCheckBox(list, "24-hour clock",
                prefs.getBoolean("clock_24h", true));
        showDate = addCheckBox(list, "Show date",
                prefs.getBoolean("show_date", true));

        TextView note = Ui.text(this,
                "Changes apply immediately when you return to the home screen.",
                11, Ui.TEXT_MUTED, false);
        note.setPadding(Ui.dp(this, 8), 0, Ui.dp(this, 8), 0);
        LinearLayout.LayoutParams noteLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        noteLp.topMargin = Ui.dp(this, 12);
        list.addView(note, noteLp);

        TextView footer = Ui.text(this,
                "OK = Toggle     \u2022     BACK = Settings",
                10, Ui.TEXT_MUTED, false);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footerLp.topMargin = Ui.dp(this, 8);
        column.addView(footer, footerLp);

        clock24.post(new Runnable() {
            @Override
            public void run() {
                clock24.requestFocus();
            }
        });

        return root;
    }

    private CheckBox addCheckBox(LinearLayout list, String label, boolean checked) {
        CheckBox box = new CheckBox(this);
        box.setText(label);
        box.setTextColor(Ui.TEXT);
        box.setTextSize(16);
        box.setChecked(checked);
        box.setFocusable(true);
        box.setPadding(Ui.dp(this, 14), Ui.dp(this, 12),
                Ui.dp(this, 14), Ui.dp(this, 12));
        box.setBackground(Ui.cardSelector(this));
        ColorStateList tint = new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_checked},
                        new int[]{}
                },
                new int[]{Ui.ACCENT, Ui.TEXT_MUTED});
        box.setButtonTintList(tint);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = Ui.dp(this, 8);
        box.setLayoutParams(lp);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == clock24) {
                    prefs.edit().putBoolean("clock_24h", isChecked).apply();
                } else if (buttonView == showDate) {
                    prefs.edit().putBoolean("show_date", isChecked).apply();
                }
            }
        });
        list.addView(box);
        return box;
    }
}
