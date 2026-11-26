package de.customtv.start;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {
    private final List<ViewGroup> rows = new ArrayList<ViewGroup>();
    private final Map<View, Integer> rowIndex = new HashMap<View, Integer>();

    private SharedPreferences prefs;
    private TextClock clock;
    private TextView dateView;
    private TextView hint;
    private LinearLayout dockRow;
    private HorizontalScrollView dockScroller;
    private FrameLayout root;
    private ImageView heroIcon;
    private ImageView heroWatermark;
    private TextView heroTitle;
    private TextView heroSubtitle;
    private TextView heroLabel;
    private boolean editMode;
    private boolean centerLongEntry;
    private View lastDockCard;
    private boolean firstFocus = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Ui.fullscreen(this);
        prefs = getSharedPreferences("mein_start", MODE_PRIVATE);
        setContentView(buildUi());
    }

    private View buildUi() {
        root = new FrameLayout(this);
        root.setBackground(heroGradient(null));

        heroWatermark = new ImageView(this);
        heroWatermark.setScaleType(ImageView.ScaleType.FIT_CENTER);
        heroWatermark.setAlpha(0.12f);
        heroWatermark.setFocusable(false);
        FrameLayout.LayoutParams watermarkLp = new FrameLayout.LayoutParams(
                Ui.dp(this, 360), Ui.dp(this, 360));
        watermarkLp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        watermarkLp.rightMargin = Ui.dp(this, 48);
        root.addView(heroWatermark, watermarkLp);

        LinearLayout heroContent = new LinearLayout(this);
        heroContent.setOrientation(LinearLayout.VERTICAL);
        heroContent.setGravity(Gravity.START);
        FrameLayout.LayoutParams heroLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        heroLp.gravity = Gravity.LEFT | Gravity.TOP;
        heroLp.leftMargin = Ui.dp(this, 64);
        heroLp.rightMargin = Ui.dp(this, 64);
        heroLp.topMargin = Ui.dp(this, 90);
        root.addView(heroContent, heroLp);

        heroLabel = Ui.text(this, "YOUR APPS", 12, Ui.ACCENT, true);
        heroLabel.setLetterSpacing(0.16f);
        heroContent.addView(heroLabel);

        LinearLayout heroRow = new LinearLayout(this);
        heroRow.setOrientation(LinearLayout.HORIZONTAL);
        heroRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams heroRowLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        heroRowLp.topMargin = Ui.dp(this, 14);
        heroContent.addView(heroRow, heroRowLp);

        heroIcon = new ImageView(this);
        heroIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        heroIcon.setFocusable(false);
        LinearLayout.LayoutParams heroIconLp = new LinearLayout.LayoutParams(
                Ui.dp(this, 96), Ui.dp(this, 96));
        heroIconLp.rightMargin = Ui.dp(this, 28);
        heroRow.addView(heroIcon, heroIconLp);

        LinearLayout heroText = new LinearLayout(this);
        heroText.setOrientation(LinearLayout.VERTICAL);
        heroText.setGravity(Gravity.CENTER_VERTICAL);
        heroRow.addView(heroText, new LinearLayout.LayoutParams(
                Ui.dp(this, 480), ViewGroup.LayoutParams.WRAP_CONTENT));

        heroTitle = Ui.text(this, "My Apps", 48, Ui.TEXT, true);
        heroTitle.setMaxLines(1);
        heroTitle.setEllipsize(TextUtils.TruncateAt.END);
        heroText.addView(heroTitle);

        heroSubtitle = Ui.text(this, "Choose an app", 16, Ui.TEXT_MUTED, false);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = Ui.dp(this, 8);
        heroText.addView(heroSubtitle, subLp);

        View scrim = new View(this);
        scrim.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xB3000000, 0x00000000}));
        FrameLayout.LayoutParams scrimLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 110));
        scrimLp.gravity = Gravity.TOP;
        root.addView(scrim, scrimLp);

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(Ui.dp(this, 28), Ui.dp(this, 14),
                Ui.dp(this, 28), 0);
        FrameLayout.LayoutParams topLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Ui.dp(this, 58));
        topLp.gravity = Gravity.TOP;
        root.addView(topBar, topLp);

        final LinearLayout topFocusRow = new LinearLayout(this);
        topFocusRow.setOrientation(LinearLayout.HORIZONTAL);
        topFocusRow.setGravity(Gravity.CENTER_VERTICAL);
        topBar.addView(topFocusRow, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        addTab(topFocusRow, "Home", new Runnable() {
            @Override
            public void run() {
                focusFirstApp();
            }
        });
        addTab(topFocusRow, "Account", new Runnable() {
            @Override
            public void run() {
                openAccount();
            }
        });
        addTab(topFocusRow, "Apps", new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, AppsActivity.class));
            }
        });
        addTab(topFocusRow, "Movies", new Runnable() {
            @Override
            public void run() {
                openEntry(AppRepository.prime(MainActivity.this));
            }
        });
        addTab(topFocusRow, "Shows", new Runnable() {
            @Override
            public void run() {
                openEntry(AppRepository.prime(MainActivity.this));
            }
        });
        addTab(topFocusRow, "Settings", new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });

        LinearLayout clockBox = new LinearLayout(this);
        clockBox.setOrientation(LinearLayout.VERTICAL);
        clockBox.setGravity(Gravity.END);
        clock = new TextClock(this);
        clock.setFormat12Hour("HH:mm");
        clock.setFormat24Hour("HH:mm");
        clock.setTextColor(Ui.TEXT);
        clock.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        clock.setTypeface(Typeface.DEFAULT_BOLD);
        clock.setIncludeFontPadding(false);
        clock.setGravity(Gravity.END);
        clockBox.addView(clock);

        dateView = Ui.text(this, "", 10, Ui.TEXT_MUTED, false);
        dateView.setGravity(Gravity.END);
        LinearLayout.LayoutParams dateLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dateLp.topMargin = Ui.dp(this, 1);
        clockBox.addView(dateView, dateLp);
        topBar.addView(clockBox, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addRow(topFocusRow);

        LinearLayout bottom = new LinearLayout(this);
        bottom.setOrientation(LinearLayout.VERTICAL);
        bottom.setGravity(Gravity.CENTER_HORIZONTAL);

        hint = Ui.text(this,
                "OK = Open     \u2022     MENU / Long OK = Move",
                10, Ui.TEXT_MUTED, false);
        hint.setGravity(Gravity.CENTER);
        bottom.addView(hint, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        dockScroller = new HorizontalScrollView(this);
        dockScroller.setHorizontalScrollBarEnabled(false);
        dockScroller.setFocusable(false);
        dockScroller.setClipToPadding(false);
        dockScroller.setClipChildren(false);
        dockScroller.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        dockScroller.setBackground(dockBackground());
        LinearLayout.LayoutParams dockLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, Ui.dp(this, 120));
        dockLp.topMargin = Ui.dp(this, 6);
        bottom.addView(dockScroller, dockLp);

        dockRow = new LinearLayout(this);
        dockRow.setOrientation(LinearLayout.HORIZONTAL);
        dockRow.setGravity(Gravity.CENTER_VERTICAL);
        dockRow.setFocusable(false);
        dockRow.setPadding(Ui.dp(this, 16), Ui.dp(this, 16),
                Ui.dp(this, 16), Ui.dp(this, 16));
        dockScroller.addView(dockRow, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addRow(dockRow);

        FrameLayout.LayoutParams bottomLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        bottomLp.bottomMargin = Ui.dp(this, 28);
        bottomLp.leftMargin = Ui.dp(this, 24);
        bottomLp.rightMargin = Ui.dp(this, 24);
        root.addView(bottom, bottomLp);

        updateHero(null);
        return root;
    }

    private void addTab(LinearLayout row, String text, final Runnable action) {
        TextView tab = Ui.text(this, text, 13, Ui.TEXT, false);
        tab.setTextColor(Ui.tabTextColors());
        tab.setGravity(Gravity.CENTER);
        tab.setPadding(Ui.dp(this, 12), Ui.dp(this, 7),
                Ui.dp(this, 12), Ui.dp(this, 7));
        tab.setBackground(Ui.pillSelector(this));
        tab.setFocusable(true);
        tab.setClickable(true);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = Ui.dp(this, 2);
        row.addView(tab, lp);
        tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.run();
            }
        });
    }

    private Drawable dockBackground() {
        GradientDrawable dock = new GradientDrawable();
        dock.setColor(0x33FFFFFF);
        dock.setCornerRadius(Ui.dp(this, 28));
        dock.setStroke(Ui.dp(this, 1), 0x55FFFFFF);
        return dock;
    }

    private StateListDrawable dockIconSelector() {
        StateListDrawable states = new StateListDrawable();

        GradientDrawable pressed = new GradientDrawable();
        pressed.setShape(GradientDrawable.OVAL);
        pressed.setColor(0x33FFFFFF);
        pressed.setStroke(Ui.dp(this, 3), 0xFFFFFFFF);

        GradientDrawable focused = new GradientDrawable();
        focused.setShape(GradientDrawable.OVAL);
        focused.setColor(0x11FFFFFF);
        focused.setStroke(Ui.dp(this, 3), 0xFFFFFFFF);

        states.addState(new int[]{android.R.attr.state_pressed}, pressed);
        states.addState(new int[]{android.R.attr.state_focused}, focused);
        states.addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));
        return states;
    }

    private View createDockIcon(final AppRepository.Entry entry) {
        FrameLayout card = new FrameLayout(this);
        card.setBackground(new InsetDrawable(dockIconSelector(), Ui.dp(this, 4)));
        card.setFocusable(true);
        card.setClickable(true);
        card.setLongClickable(true);
        card.setTag(entry.key);
        card.setContentDescription(entry.label);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                Ui.dp(this, 88), Ui.dp(this, 88));
        lp.rightMargin = Ui.dp(this, 8);
        card.setLayoutParams(lp);

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(IconFactory.get(this, entry));
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setFocusable(false);
        FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(
                Ui.dp(this, 72), Ui.dp(this, 72));
        iconLp.gravity = Gravity.CENTER;
        card.addView(icon, iconLp);

        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEntry(entry);
            }
        });
        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                centerLongEntry = true;
                enterEditMode(v);
                return true;
            }
        });
        card.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.animate().scaleX(hasFocus ? 1.10f : 1f)
                        .scaleY(hasFocus ? 1.10f : 1f)
                        .setDuration(120).start();
                if (hasFocus) {
                    lastDockCard = v;
                    updateHero(entry);
                    int target = v.getLeft() - Ui.dp(MainActivity.this, 24);
                    dockScroller.smoothScrollTo(Math.max(0, target), 0);
                }
            }
        });
        return card;
    }

    private void updateHero(AppRepository.Entry entry) {
        if (entry == null) {
            heroLabel.setText("YOUR APPS");
            heroTitle.setText("My Apps");
            heroSubtitle.setText("Choose an app");
            Drawable appIcon = Ui.circular(this,
                    AppRepository.icon(this, getPackageName()), 96);
            heroIcon.setImageDrawable(appIcon);
            heroWatermark.setImageDrawable(appIcon);
            root.setBackground(heroGradient(null));
            return;
        }
        heroLabel.setText("YOUR APPS");
        heroTitle.setText(entry.label);
        heroSubtitle.setText(descriptionFor(entry));
        Drawable displayIcon = IconFactory.get(this, entry);
        heroIcon.setImageDrawable(displayIcon);
        heroWatermark.setImageDrawable(displayIcon);
        root.setBackground(heroGradient(entry));
    }

    private String descriptionFor(AppRepository.Entry entry) {
        if (AppRepository.PRIME.equals(entry.pkg)) {
            return "Movies, shows and more";
        }
        if (AppRepository.YOUTUBE_FIRE.equals(entry.pkg)
                || AppRepository.YOUTUBE_GOOGLE.equals(entry.pkg)) {
            return "Videos and music";
        }
        if (AppRepository.APPSTORE.equals(entry.pkg)) {
            return "Manage and install apps";
        }
        if (AppRepository.NETFLIX.equals(entry.pkg)) {
            return "Shows and movies";
        }
        if (AppRepository.SETTINGS_V2.equals(entry.pkg)
                || AppRepository.KEY_SETTINGS.equals(entry.key)) {
            return "System settings";
        }
        return "Open with OK";
    }

    private Drawable heroGradient(AppRepository.Entry entry) {
        int top = 0xFF16233A;
        int middle = 0xFF0E1420;
        if (entry != null) {
            if (AppRepository.PRIME.equals(entry.pkg)) {
                top = 0xFF0B2A4A;
                middle = 0xFF071018;
            } else if (AppRepository.YOUTUBE_FIRE.equals(entry.pkg)
                    || AppRepository.YOUTUBE_GOOGLE.equals(entry.pkg)) {
                top = 0xFF3A0B0B;
                middle = 0xFF160A0A;
            } else if (AppRepository.NETFLIX.equals(entry.pkg)) {
                top = 0xFF3B0A12;
                middle = 0xFF1A060A;
            } else if (AppRepository.KEY_APPSTORE.equals(entry.key)
                    || AppRepository.APPSTORE.equals(entry.pkg)) {
                top = 0xFF3A2500;
                middle = 0xFF1A1206;
            } else if (AppRepository.KEY_SETTINGS.equals(entry.key)) {
                top = 0xFF1E2A38;
                middle = 0xFF10161E;
            }
        }
        return new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{top, middle, Ui.BG_BOTTOM});
    }

    private void enterEditMode(View card) {
        editMode = true;
        card.requestFocus();
        hint.setText("LEFT / RIGHT = Move     \u2022     OK = Done");
        for (int i = 0; i < dockRow.getChildCount(); i++) {
            View child = dockRow.getChildAt(i);
            child.setAlpha(child == card ? 1f : 0.5f);
        }
        Toast.makeText(this, "Use left/right to move. Press OK when done.",
                Toast.LENGTH_SHORT).show();
    }

    private void exitEditMode() {
        editMode = false;
        centerLongEntry = false;
        hint.setText("OK = Open     \u2022     MENU / Long OK = Move");
        for (int i = 0; i < dockRow.getChildCount(); i++) {
            dockRow.getChildAt(i).setAlpha(1f);
        }
    }

    private void moveCard(int direction) {
        View focused = getCurrentFocus();
        if (focused == null || focused.getParent() != dockRow) {
            return;
        }
        int index = dockRow.indexOfChild(focused);
        int next = index + direction;
        if (index < 0 || next < 0 || next >= dockRow.getChildCount()) {
            return;
        }
        dockRow.removeViewAt(index);
        dockRow.addView(focused, next);
        focused.requestFocus();
        saveOrder();
    }

    private void saveOrder() {
        StringBuilder order = new StringBuilder();
        for (int i = 0; i < dockRow.getChildCount(); i++) {
            Object tag = dockRow.getChildAt(i).getTag();
            if (tag instanceof String) {
                if (order.length() > 0) {
                    order.append('|');
                }
                order.append((String) tag);
            }
        }
        prefs.edit().putString("app_order", order.toString()).apply();
    }

    private List<AppRepository.Entry> orderedEntries() {
        List<AppRepository.Entry> all = AppRepository.homeEntries(this);
        String order = prefs.getString("app_order", "");
        if (order == null || order.isEmpty()) {
            return all;
        }
        List<AppRepository.Entry> result = new ArrayList<AppRepository.Entry>();
        Set<String> used = new HashSet<String>();
        String[] keys = order.split("\\|");
        for (String key : keys) {
            for (AppRepository.Entry entry : all) {
                if (entry.key.equals(key) && !used.contains(key)) {
                    result.add(entry);
                    used.add(key);
                    break;
                }
            }
        }
        for (AppRepository.Entry entry : all) {
            if (!used.contains(entry.key)) {
                result.add(entry);
            }
        }
        return result;
    }

    private void refreshAppRow() {
        if (dockRow == null) {
            return;
        }
        View focused = getCurrentFocus();
        String focusKey = null;
        if (focused != null && isDescendantOf(focused, dockRow)) {
            View card = directChildOf(dockRow, focused);
            if (card != null && card.getTag() instanceof String) {
                focusKey = (String) card.getTag();
            }
        }
        if (editMode) {
            exitEditMode();
        }
        dockRow.removeAllViews();
        List<AppRepository.Entry> entries = orderedEntries();
        for (AppRepository.Entry entry : entries) {
            dockRow.addView(createDockIcon(entry));
        }
        if (focusKey != null) {
            View target = findCard(dockRow, focusKey);
            if (target != null) {
                target.requestFocus();
            } else if (dockRow.getChildCount() > 0) {
                dockRow.getChildAt(0).requestFocus();
            }
        } else if (firstFocus) {
            firstFocus = false;
            focusFirstApp();
        }
    }

    private void focusFirstApp() {
        if (dockRow == null || dockRow.getChildCount() == 0) {
            return;
        }
        final View target = dockRow.getChildAt(0);
        target.post(new Runnable() {
            @Override
            public void run() {
                target.requestFocus();
            }
        });
    }

    private void openAccount() {
        Intent account = AppRepository.settingsComponentIntent(
                AppRepository.SETTINGS_V2,
                "com.amazon.tv.settings.v2.tv.my_account.MyAccountActivity");
        if (tryStart(account)) {
            return;
        }
        if (tryStart(AppRepository.mainSettingsIntent())) {
            Toast.makeText(this, "Account via Fire TV settings", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Account unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void openEntry(AppRepository.Entry entry) {
        if (entry == null) {
            return;
        }
        if (!tryStart(entry.intent)) {
            if (entry.fallback != null && tryStart(entry.fallback)) {
                return;
            }
            Toast.makeText(this, "App unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean tryStart(Intent intent) {
        try {
            startActivity(intent);
            return true;
        } catch (android.content.ActivityNotFoundException e) {
            return false;
        } catch (SecurityException e) {
            return false;
        }
    }

    private void addRow(ViewGroup container) {
        int index = rows.size();
        rows.add(container);
        rowIndex.put(container, index);
    }

    private int findRow(View view) {
        View current = view;
        while (current != null) {
            Integer index = rowIndex.get(current);
            if (index != null) {
                return index;
            }
            if (!(current.getParent() instanceof View)) {
                return -1;
            }
            current = (View) current.getParent();
        }
        return -1;
    }

    private View nearestInRow(ViewGroup container, View from) {
        if (container == null) {
            return null;
        }
        int[] fromLocation = new int[2];
        from.getLocationOnScreen(fromLocation);
        int fromX = fromLocation[0] + from.getWidth() / 2;
        View best = null;
        int bestDistance = Integer.MAX_VALUE;
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (!child.isFocusable() || child.getVisibility() != View.VISIBLE) {
                continue;
            }
            int[] location = new int[2];
            child.getLocationOnScreen(location);
            int x = location[0] + child.getWidth() / 2;
            int distance = Math.abs(x - fromX);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = child;
            }
        }
        return best;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        boolean isCenter = keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                || keyCode == KeyEvent.KEYCODE_ENTER;

        if (event.getAction() == KeyEvent.ACTION_UP
                && isCenter && centerLongEntry) {
            centerLongEntry = false;
            return true;
        }

        if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_MENU) {
            if (editMode) {
                exitEditMode();
                return true;
            }
            View focused = getCurrentFocus();
            View card = null;
            if (focused != null && focused.getParent() == dockRow) {
                card = focused;
            } else if (lastDockCard != null
                    && lastDockCard.getParent() == dockRow) {
                card = lastDockCard;
            } else if (dockRow != null && dockRow.getChildCount() > 0) {
                card = dockRow.getChildAt(0);
            }
            if (card != null) {
                card.requestFocus();
                enterEditMode(card);
                return true;
            }
        }

        if (!editMode && event.getAction() == KeyEvent.ACTION_DOWN
                && isCenter && event.getRepeatCount() > 0) {
            View focused = getCurrentFocus();
            if (focused != null && focused.getParent() == dockRow) {
                centerLongEntry = true;
                enterEditMode(focused);
                return true;
            }
        }

        if (editMode && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                moveCard(-1);
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                moveCard(1);
                return true;
            }
            if (isCenter) {
                if (centerLongEntry) {
                    return true;
                }
                exitEditMode();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                exitEditMode();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                    || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        View focused = getCurrentFocus();
        if (focused != null) {
            int row = findRow(focused);
            if (row >= 0) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT
                        || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    ViewGroup container = rows.get(row);
                    int index = container.indexOfChild(focused);
                    if (index >= 0) {
                        int next = index + (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? 1 : -1);
                        if (next >= 0 && next < container.getChildCount()) {
                            View target = container.getChildAt(next);
                            if (target.isFocusable()) {
                                target.requestFocus();
                                return true;
                            }
                        }
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP
                        || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    int direction = keyCode == KeyEvent.KEYCODE_DPAD_DOWN ? 1 : -1;
                    int targetRow = row + direction;
                    while (targetRow >= 0 && targetRow < rows.size()) {
                        View target = nearestInRow(rows.get(targetRow), focused);
                        if (target != null) {
                            target.requestFocus();
                            return true;
                        }
                        targetRow += direction;
                    }
                    return true;
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyPrefs();
        refreshAppRow();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        refreshAppRow();
    }

    @Override
    public void onBackPressed() {
        if (editMode) {
            exitEditMode();
            return;
        }
        // Stay on the custom start screen.
    }

    private void applyPrefs() {
        boolean clock24 = prefs.getBoolean("clock_24h", true);
        boolean showDate = prefs.getBoolean("show_date", true);
        clock.setFormat12Hour(clock24 ? "HH:mm" : "h:mm");
        clock.setFormat24Hour(clock24 ? "HH:mm" : "h:mm");
        dateView.setVisibility(showDate ? View.VISIBLE : View.GONE);
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d", Locale.US);
        dateView.setText(format.format(new Date()));
    }

    private boolean isDescendantOf(View view, ViewGroup ancestor) {
        View current = view;
        while (current != null) {
            if (current == ancestor) {
                return true;
            }
            if (!(current.getParent() instanceof View)) {
                return false;
            }
            current = (View) current.getParent();
        }
        return false;
    }

    private View directChildOf(ViewGroup parent, View view) {
        View current = view;
        while (current != null) {
            if (current.getParent() == parent) {
                return current;
            }
            if (!(current.getParent() instanceof View)) {
                return null;
            }
            current = (View) current.getParent();
        }
        return null;
    }

    private View findCard(LinearLayout row, String key) {
        for (int i = 0; i < row.getChildCount(); i++) {
            View child = row.getChildAt(i);
            if (key.equals(child.getTag())) {
                return child;
            }
        }
        return null;
    }
}
