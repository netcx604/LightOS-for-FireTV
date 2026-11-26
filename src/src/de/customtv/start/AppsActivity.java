package de.customtv.start;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class AppsActivity extends Activity {
    private GridView grid;
    private AppAdapter adapter;
    private TextView appstoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.fullscreen(this);
        setContentView(buildUi());
        loadApps();
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

        TextView title = Ui.text(this, "Apps & Appstore", 20, Ui.TEXT, true);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleLp.leftMargin = Ui.dp(this, 16);
        topBar.addView(title, titleLp);

        appstoreButton = Ui.text(this, "Amazon Appstore", 13, 0xFF000000, true);
        appstoreButton.setGravity(Gravity.CENTER);
        appstoreButton.setPadding(Ui.dp(this, 14), Ui.dp(this, 8),
                Ui.dp(this, 14), Ui.dp(this, 8));
        appstoreButton.setBackground(Ui.rounded(this, 0xFFFFFFFF, 16));
        appstoreButton.setFocusable(true);
        appstoreButton.setClickable(true);
        appstoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startActivity(AppRepository.amazonAppstoreIntent(AppsActivity.this));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(AppsActivity.this, "Appstore unavailable",
                            Toast.LENGTH_SHORT).show();
                } catch (SecurityException e) {
                    Toast.makeText(AppsActivity.this, "Appstore blocked",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        Ui.focusScale(appstoreButton);
        topBar.addView(appstoreButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView subtitle = Ui.text(this,
                "Manage installed apps \u2013 new apps in the Amazon Appstore",
                12, Ui.TEXT_MUTED, false);
        subtitle.setPadding(Ui.dp(this, 8), 0, Ui.dp(this, 8), 0);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = Ui.dp(this, 6);
        column.addView(subtitle, subLp);

        grid = new GridView(this);
        grid.setId(View.generateViewId());
        grid.setNumColumns(5);
        grid.setHorizontalSpacing(Ui.dp(this, 12));
        grid.setVerticalSpacing(Ui.dp(this, 12));
        grid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        grid.setClipToPadding(false);
        grid.setPadding(Ui.dp(this, 8), Ui.dp(this, 16), Ui.dp(this, 8), 0);
        grid.setSelector(gridSelector());
        grid.setDrawSelectorOnTop(true);
        grid.setFocusable(true);
        grid.setVerticalScrollBarEnabled(false);
        adapter = new AppAdapter();
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                open(adapter.getItem(position));
            }
        });
        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                AppRepository.Entry entry = adapter.getItem(position);
                if (entry.userApp) {
                    try {
                        startActivity(AppRepository.uninstallIntent(entry.pkg));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(AppsActivity.this, "Uninstall not possible",
                                Toast.LENGTH_SHORT).show();
                    } catch (SecurityException e) {
                        Toast.makeText(AppsActivity.this, "Uninstall blocked",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    try {
                        startActivity(AppRepository.appInfoIntent(entry.pkg));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(AppsActivity.this, "App info unavailable",
                                Toast.LENGTH_SHORT).show();
                    } catch (SecurityException e) {
                        Toast.makeText(AppsActivity.this, "App info blocked",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        });
        column.addView(grid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        TextView footer = Ui.text(this,
                "OK = Open     \u2022     Long OK = App info / Uninstall",
                10, Ui.TEXT_MUTED, false);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footerLp.topMargin = Ui.dp(this, 8);
        column.addView(footer, footerLp);

        grid.setNextFocusUpId(appstoreButton.getId());
        appstoreButton.setNextFocusDownId(grid.getId());
        back.setNextFocusDownId(grid.getId());

        return root;
    }

    private void loadApps() {
        List<AppRepository.Entry> entries = AppRepository.appsEntries(this);
        adapter.setEntries(entries);
        grid.post(new Runnable() {
            @Override
            public void run() {
                grid.requestFocus();
                grid.setSelection(0);
            }
        });
    }

    private void open(AppRepository.Entry entry) {
        if (entry == null) {
            return;
        }
        try {
            startActivity(entry.intent);
        } catch (ActivityNotFoundException e) {
            if (entry.fallback != null) {
                try {
                    startActivity(entry.fallback);
                    return;
                } catch (ActivityNotFoundException ignored) {
                    // fall through
                } catch (SecurityException ignored) {
                    // fall through
                }
            }
            Toast.makeText(this, "App unavailable", Toast.LENGTH_SHORT).show();
        } catch (SecurityException e) {
            Toast.makeText(this, "App blocked", Toast.LENGTH_SHORT).show();
        }
    }

    private StateListDrawable gridSelector() {
        StateListDrawable states = new StateListDrawable();

        GradientDrawable pressed = new GradientDrawable();
        pressed.setColor(0x338AB4F8);
        pressed.setCornerRadius(Ui.dp(this, 12));
        pressed.setStroke(Ui.dp(this, 3), Ui.ACCENT);

        GradientDrawable focused = new GradientDrawable();
        focused.setColor(Color.TRANSPARENT);
        focused.setCornerRadius(Ui.dp(this, 12));
        focused.setStroke(Ui.dp(this, 3), Ui.ACCENT);

        states.addState(new int[]{android.R.attr.state_pressed}, pressed);
        states.addState(new int[]{android.R.attr.state_focused}, focused);
        states.addState(new int[]{android.R.attr.state_selected}, focused);
        states.addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));
        return states;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            View focused = getCurrentFocus();
            if (focused == grid) {
                appstoreButton.requestFocus();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private class AppAdapter extends BaseAdapter {
        private final List<AppRepository.Entry> entries =
                new ArrayList<AppRepository.Entry>();

        void setEntries(List<AppRepository.Entry> newEntries) {
            entries.clear();
            entries.addAll(newEntries);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public AppRepository.Entry getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout card;
            ImageView icon;
            TextView label;
            if (convertView == null) {
                card = new LinearLayout(AppsActivity.this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setGravity(Gravity.CENTER);
                card.setPadding(Ui.dp(AppsActivity.this, 8),
                        Ui.dp(AppsActivity.this, 8),
                        Ui.dp(AppsActivity.this, 8),
                        Ui.dp(AppsActivity.this, 8));
                card.setBackground(Ui.rounded(AppsActivity.this, Ui.SURFACE, 12,
                        1, Ui.BORDER));
                card.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Ui.dp(AppsActivity.this, 120)));

                icon = new ImageView(AppsActivity.this);
                icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                icon.setFocusable(false);
                card.addView(icon, new LinearLayout.LayoutParams(
                        Ui.dp(AppsActivity.this, 48),
                        Ui.dp(AppsActivity.this, 48)));

                label = Ui.text(AppsActivity.this, "", 13, Ui.TEXT, false);
                label.setGravity(Gravity.CENTER);
                label.setMaxLines(2);
                label.setEllipsize(TextUtils.TruncateAt.END);
                label.setFocusable(false);
                LinearLayout.LayoutParams labelLp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                labelLp.topMargin = Ui.dp(AppsActivity.this, 6);
                card.addView(label, labelLp);

                card.setTag(new Holder(icon, label));
            } else {
                card = (LinearLayout) convertView;
                Holder holder = (Holder) card.getTag();
                icon = holder.icon;
                label = holder.label;
            }

            AppRepository.Entry entry = entries.get(position);
            icon.setImageDrawable(IconFactory.get(AppsActivity.this, entry));
            label.setText(entry.label);
            card.setContentDescription(entry.label);
            return card;
        }
    }

    private static class Holder {
        final ImageView icon;
        final TextView label;

        Holder(ImageView icon, TextView label) {
            this.icon = icon;
            this.label = label;
        }
    }
}
