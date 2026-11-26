package de.customtv.start;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class AppRepository {
    public static final String PRIME = "com.amazon.avod";
    public static final String APPSTORE = "com.amazon.venezia";
    public static final String LAUNCHER = "com.amazon.tv.launcher";
    public static final String YOUTUBE_FIRE = "com.amazon.firetv.youtube";
    public static final String YOUTUBE_GOOGLE = "com.google.android.youtube.tv";
    public static final String SETTINGS_V2 = "com.amazon.tv.settings.v2";
    public static final String SETTINGS_CORE = "com.amazon.tv.settings.core";
    public static final String NETFLIX = "com.netflix.ninja";
    public static final String FTVLAUNCHX = "de.codefaktor.ftvlaunchx";

    public static final String KEY_APPSTORE = "__appstore__";
    public static final String KEY_SETTINGS = "__settings__";

    public static final String PRIME_REF = "[reftype=browse/menu,refid=prime_root]";

    private static final String[] SYSTEM_ALLOWLIST = new String[]{
            YOUTUBE_GOOGLE,
            NETFLIX,
            "com.disney.disneyplus",
            "com.hulu.plus",
            "com.spotify.tv.android",
            "com.plexapp.android",
            "org.xbmc.kodi",
            "com.wow.wowtv",
            "de.joyn.androidtv",
            "com.zattoo.tv",
            "com.apple.atve.amazon.appletv"
    };

    private static final Map<String, Drawable> ICON_CACHE = new HashMap<String, Drawable>();

    private AppRepository() {
    }

    public static final class Entry {
        public final String key;
        public final String pkg;
        public final String label;
        public final Drawable icon;
        public final Intent intent;
        public final Intent fallback;
        public final boolean system;
        public final boolean userApp;

        Entry(String key, String pkg, String label, Drawable icon, Intent intent,
              Intent fallback, boolean system, boolean userApp) {
            this.key = key;
            this.pkg = pkg;
            this.label = label;
            this.icon = icon;
            this.intent = intent;
            this.fallback = fallback;
            this.system = system;
            this.userApp = userApp;
        }
    }

    public static List<Entry> homeEntries(Activity activity) {
        List<Entry> entries = new ArrayList<Entry>();
        add(entries, prime(activity));
        add(entries, youtube(activity));
        add(entries, appstore(activity));
        add(entries, settings(activity));

        Set<String> excluded = new HashSet<String>();
        excluded.add(activity.getPackageName());
        excluded.add(FTVLAUNCHX);
        excluded.add(LAUNCHER);
        excluded.add(PRIME);
        excluded.add(APPSTORE);
        excluded.add(YOUTUBE_FIRE);
        excluded.add(YOUTUBE_GOOGLE);
        entries.addAll(otherEntries(activity, excluded));
        return entries;
    }

    public static List<Entry> appsEntries(Activity activity) {
        List<Entry> entries = new ArrayList<Entry>();
        add(entries, prime(activity));
        add(entries, youtube(activity));
        add(entries, amazonAppstore(activity));
        add(entries, settings(activity));

        Set<String> excluded = new HashSet<String>();
        excluded.add(activity.getPackageName());
        excluded.add(FTVLAUNCHX);
        excluded.add(LAUNCHER);
        excluded.add(PRIME);
        excluded.add(APPSTORE);
        excluded.add(YOUTUBE_FIRE);
        excluded.add(YOUTUBE_GOOGLE);
        entries.addAll(otherEntries(activity, excluded));
        return entries;
    }

    private static void add(List<Entry> entries, Entry entry) {
        if (entry != null) {
            entries.add(entry);
        }
    }

    public static Entry prime(Activity activity) {
        if (!isInstalled(activity, PRIME)) {
            return null;
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(LAUNCHER,
                "com.amazon.tv.launcher.deeplink.activity.NavMenuScreenDeepLinkActivityDI"));
        intent.putExtra("ref", PRIME_REF);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent fallback = launch(activity, PRIME);
        return new Entry(PRIME, PRIME, "Prime Video", icon(activity, PRIME),
                intent, fallback, false, false);
    }

    public static Entry youtube(Activity activity) {
        if (isInstalled(activity, YOUTUBE_FIRE)) {
            return entryForPackage(activity, YOUTUBE_FIRE, "YouTube");
        }
        if (isInstalled(activity, YOUTUBE_GOOGLE)) {
            return entryForPackage(activity, YOUTUBE_GOOGLE, "YouTube");
        }
        return null;
    }

    public static Entry appstore(Activity activity) {
        Intent intent = new Intent(activity, AppsActivity.class);
        Intent fallback = amazonAppstoreIntent(activity);
        return new Entry(KEY_APPSTORE, APPSTORE, "Appstore",
                icon(activity, APPSTORE), intent, fallback, false, false);
    }

    public static Entry amazonAppstore(Activity activity) {
        if (!isInstalled(activity, APPSTORE)) {
            return null;
        }
        return new Entry(APPSTORE, APPSTORE, "Amazon Appstore",
                icon(activity, APPSTORE), amazonAppstoreIntent(activity), null,
                false, false);
    }

    public static Entry settings(Activity activity) {
        Intent intent = new Intent(activity, SettingsActivity.class);
        Intent fallback = mainSettingsIntent();
        return new Entry(KEY_SETTINGS, SETTINGS_V2, "Settings",
                icon(activity, SETTINGS_V2, SETTINGS_CORE, LAUNCHER),
                intent, fallback, false, false);
    }

    public static Entry simple(Activity activity, String key, String label,
                               Drawable iconDrawable, Intent intent) {
        return new Entry(key, key, label, iconDrawable, intent, null, false, false);
    }

    private static List<Entry> otherEntries(Activity activity, Set<String> excluded) {
        Map<String, Entry> apps = new LinkedHashMap<String, Entry>();
        addLauncherApps(activity, apps, Intent.CATEGORY_LEANBACK_LAUNCHER, excluded);
        addLauncherApps(activity, apps, Intent.CATEGORY_LAUNCHER, excluded);
        List<Entry> list = new ArrayList<Entry>(apps.values());
        Collections.sort(list, new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                return a.label.compareToIgnoreCase(b.label);
            }
        });
        return list;
    }

    private static void addLauncherApps(Activity activity, Map<String, Entry> out,
                                        String category, Set<String> excluded) {
        PackageManager pm = activity.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN).addCategory(category);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo ri : list) {
            if (ri.activityInfo == null || ri.activityInfo.applicationInfo == null) {
                continue;
            }
            ApplicationInfo ai = ri.activityInfo.applicationInfo;
            String pkg = ai.packageName;
            if (out.containsKey(pkg) || excluded.contains(pkg)) {
                continue;
            }
            boolean system = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
            if (system && !isSystemAllowed(pkg)) {
                continue;
            }
            Intent launch = new Intent(Intent.ACTION_MAIN)
                    .addCategory(category)
                    .setComponent(new ComponentName(ai.packageName, ri.activityInfo.name));
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String label = label(activity, pkg);
            out.put(pkg, new Entry(pkg, pkg, label, icon(activity, pkg),
                    launch, null, system, !system));
        }
    }

    private static boolean isSystemAllowed(String pkg) {
        for (String allowed : SYSTEM_ALLOWLIST) {
            if (allowed.equals(pkg)) {
                return true;
            }
        }
        return false;
    }

    private static Entry entryForPackage(Activity activity, String pkg,
                                         String labelOverride) {
        if (!isInstalled(activity, pkg)) {
            return null;
        }
        Intent launch = launch(activity, pkg);
        if (launch == null) {
            return null;
        }
        String label = labelOverride != null ? labelOverride : label(activity, pkg);
        boolean system = isSystem(activity, pkg);
        return new Entry(pkg, pkg, label, icon(activity, pkg), launch, null,
                system, !system);
    }

    public static Intent launch(Activity activity, String pkg) {
        PackageManager pm = activity.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(pkg);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return intent;
        }
        String[] categories = new String[]{Intent.CATEGORY_LEANBACK_LAUNCHER,
                Intent.CATEGORY_LAUNCHER};
        for (String category : categories) {
            Intent probe = new Intent(Intent.ACTION_MAIN).addCategory(category).setPackage(pkg);
            List<ResolveInfo> list = pm.queryIntentActivities(probe, 0);
            if (!list.isEmpty()) {
                ResolveInfo ri = list.get(0);
                Intent explicit = new Intent(Intent.ACTION_MAIN)
                        .addCategory(category)
                        .setComponent(new ComponentName(ri.activityInfo.packageName,
                                ri.activityInfo.name));
                explicit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                return explicit;
            }
        }
        return null;
    }

    public static Intent amazonAppstoreIntent(Activity activity) {
        Intent intent = launch(activity, APPSTORE);
        if (intent == null) {
            intent = new Intent();
            intent.setComponent(new ComponentName(APPSTORE,
                    "com.amazon.venezia.grid.AppsGridLauncherActivity"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    public static Intent mainSettingsIntent() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(LAUNCHER,
                "com.amazon.tv.launcher.ui.MainSettingsActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent settingsComponentIntent(String pkg, String cls) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(pkg, cls));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent appInfoIntent(String pkg) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + pkg));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Intent uninstallIntent(String pkg) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + pkg));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    public static Drawable icon(Activity activity, String... pkgs) {
        for (String pkg : pkgs) {
            Drawable cached = ICON_CACHE.get(pkg);
            if (cached != null) {
                return cached;
            }
        }
        PackageManager pm = activity.getPackageManager();
        for (String pkg : pkgs) {
            try {
                Drawable drawable = pm.getApplicationIcon(pkg);
                if (drawable != null) {
                    ICON_CACHE.put(pkg, drawable);
                    return drawable;
                }
            } catch (PackageManager.NameNotFoundException ignored) {
                // next package
            }
        }
        return activity.getResources().getDrawable(android.R.drawable.ic_menu_view);
    }

    public static String label(Activity activity, String pkg) {
        try {
            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(pkg, 0);
            return activity.getPackageManager().getApplicationLabel(ai).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return pkg;
        }
    }

    public static boolean isInstalled(Activity activity, String pkg) {
        try {
            activity.getPackageManager().getApplicationInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static boolean isSystem(Activity activity, String pkg) {
        try {
            ApplicationInfo ai = activity.getPackageManager().getApplicationInfo(pkg, 0);
            return (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
