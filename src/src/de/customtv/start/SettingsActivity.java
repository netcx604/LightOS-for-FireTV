package de.customtv.start;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    private View firstTile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.fullscreen(this);
        setContentView(buildUi());
        if (firstTile != null) {
            firstTile.post(new Runnable() {
                @Override
                public void run() {
                    firstTile.requestFocus();
                }
            });
        }
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
        back.setId(View.generateViewId());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Ui.focusScale(back);
        topBar.addView(back, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = Ui.text(this, "Settings", 20, Ui.TEXT, true);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleLp.leftMargin = Ui.dp(this, 16);
        topBar.addView(title, titleLp);

        TextView subtitle = Ui.text(this,
                "Home and Fire TV system", 12, Ui.TEXT_MUTED, false);
        subtitle.setPadding(Ui.dp(this, 8), 0, Ui.dp(this, 8), 0);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = Ui.dp(this, 6);
        column.addView(subtitle, subLp);

        ScrollView scroll = new ScrollView(this);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setFocusable(false);
        scroll.setFillViewport(false);
        scroll.setClipToPadding(false);
        scroll.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setFocusable(false);
        scroll.addView(list, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        column.addView(scroll, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        list.addView(sectionLabel("LIGHTOS"));

        firstTile = addSetting(list, "Customize Home", android.R.drawable.ic_menu_edit,
                new Intent(this, LauncherPrefsActivity.class), null);
        addSetting(list, "Apps & Appstore", android.R.drawable.ic_menu_manage,
                new Intent(this, AppsActivity.class), null);

        list.addView(sectionLabel("FIRE TV"));

        addSetting(list, "Network & Internet", android.R.drawable.ic_menu_compass,
                AppRepository.settingsComponentIntent("com.amazon.tv.settings.v2",
                        "com.amazon.tv.settings.v2.tv.network.NetworkActivity"),
                AppRepository.mainSettingsIntent());
        addSetting(list, "Display & Sound", android.R.drawable.ic_menu_view,
                AppRepository.settingsComponentIntent("com.amazon.tv.settings.v2",
                        "com.amazon.tv.settings.v2.tv.display_sounds.DisplayAndSoundsActivity"),
                AppRepository.mainSettingsIntent());
        addSetting(list, "Remote & Bluetooth", android.R.drawable.ic_menu_manage,
                AppRepository.mainSettingsIntent(), null);
        addSetting(list, "Accessibility", android.R.drawable.ic_menu_help,
                AppRepository.settingsComponentIntent("com.amazon.tv.settings.v2",
                        "com.amazon.tv.settings.v2.tv.accessibility.AccessibilityActivity"),
                AppRepository.mainSettingsIntent());
        addSetting(list, "Account", android.R.drawable.ic_menu_myplaces,
                AppRepository.settingsComponentIntent("com.amazon.tv.settings.v2",
                        "com.amazon.tv.settings.v2.tv.my_account.MyAccountActivity"),
                AppRepository.mainSettingsIntent());
        addSetting(list, "Info & Update", android.R.drawable.ic_menu_info_details,
                AppRepository.settingsComponentIntent("com.amazon.tv.settings.v2",
                        "com.amazon.tv.settings.v2.tv.device.DeviceActivity"),
                AppRepository.mainSettingsIntent());
        addSetting(list, "All Fire TV Settings", android.R.drawable.ic_menu_preferences,
                AppRepository.mainSettingsIntent(), null);

        TextView footer = Ui.text(this,
                "OK = Open     \u2022     BACK = Home",
                10, Ui.TEXT_MUTED, false);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        footerLp.topMargin = Ui.dp(this, 8);
        column.addView(footer, footerLp);

        if (firstTile != null) {
            back.setNextFocusDownId(firstTile.getId());
            firstTile.setNextFocusUpId(back.getId());
        }

        return root;
    }

    private TextView sectionLabel(String text) {
        TextView label = Ui.text(this, text, 11, Ui.ACCENT, true);
        label.setLetterSpacing(0.12f);
        label.setPadding(Ui.dp(this, 8), 0, Ui.dp(this, 8), 0);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = Ui.dp(this, 10);
        lp.bottomMargin = Ui.dp(this, 6);
        label.setLayoutParams(lp);
        return label;
    }

    private View addSetting(LinearLayout list, String label, int iconRes,
                            final Intent intent, final Intent fallback) {
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.HORIZONTAL);
        tile.setGravity(Gravity.CENTER_VERTICAL);
        tile.setPadding(Ui.dp(this, 14), Ui.dp(this, 12),
                Ui.dp(this, 14), Ui.dp(this, 12));
        tile.setBackground(Ui.cardSelector(this));
        tile.setFocusable(true);
        tile.setClickable(true);
        tile.setId(View.generateViewId());
        tile.setContentDescription(label);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 60));
        lp.bottomMargin = Ui.dp(this, 8);
        tile.setLayoutParams(lp);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconRes);
        icon.setColorFilter(Ui.ACCENT);
        icon.setFocusable(false);
        tile.addView(icon, new LinearLayout.LayoutParams(
                Ui.dp(this, 24), Ui.dp(this, 24)));

        TextView text = Ui.text(this, label, 15, Ui.TEXT, false);
        text.setMaxLines(1);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setFocusable(false);
        LinearLayout.LayoutParams textLp = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textLp.leftMargin = Ui.dp(this, 14);
        tile.addView(text, textLp);

        TextView chevron = Ui.text(this, "\u203A", 20, Ui.TEXT_MUTED, false);
        chevron.setFocusable(false);
        tile.addView(chevron);

        tile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(intent, fallback);
            }
        });
        list.addView(tile);
        return tile;
    }

    private void open(Intent intent, Intent fallback) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            openFallback(fallback);
        } catch (SecurityException e) {
            openFallback(fallback);
        }
    }

    private void openFallback(Intent fallback) {
        if (fallback != null) {
            try {
                startActivity(fallback);
                Toast.makeText(this, "Opened via Fire TV settings",
                        Toast.LENGTH_SHORT).show();
                return;
            } catch (ActivityNotFoundException ignored) {
                // fall through
            } catch (SecurityException ignored) {
                // fall through
            }
        }
        Toast.makeText(this, "Setting unavailable", Toast.LENGTH_SHORT).show();
    }
}
