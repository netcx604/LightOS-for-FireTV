package de.customtv.start;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;

public final class IconFactory {
    private static final Map<String, Drawable> CACHE = new HashMap<String, Drawable>();

    private IconFactory() {
    }

    public static Drawable get(Context context, AppRepository.Entry entry) {
        if (entry == null) {
            return null;
        }
        String key = entry.key;
        Drawable cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        Drawable icon;
        if (AppRepository.KEY_SETTINGS.equals(entry.key)) {
            icon = Ui.gearIcon(context, 0xFFFFFFFF);
        } else if (AppRepository.YOUTUBE_FIRE.equals(entry.pkg)
                || AppRepository.YOUTUBE_GOOGLE.equals(entry.pkg)) {
            icon = Ui.youtubeIcon(context);
        } else if (AppRepository.KEY_APPSTORE.equals(entry.key)
                || AppRepository.APPSTORE.equals(entry.pkg)) {
            icon = Ui.appstoreIcon(context);
        } else {
            icon = Ui.circular(context, entry.icon, 96);
        }
        CACHE.put(key, icon);
        return icon;
    }
}
