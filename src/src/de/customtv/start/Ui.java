package de.customtv.start;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public final class Ui {
    public static final int BG_TOP = 0xFF0E0F13;
    public static final int BG_BOTTOM = 0xFF16181D;
    public static final int SURFACE = 0xFF1E2126;
    public static final int SURFACE_FOCUS = 0xFF2D3138;
    public static final int SURFACE_PRESSED = 0xFF3A3F47;
    public static final int BORDER = 0xFF2A2E35;
    public static final int TEXT = 0xFFFFFFFF;
    public static final int TEXT_MUTED = 0xFF9AA0A6;
    public static final int ACCENT = 0xFF8AB4F8;
    public static final int ACCENT_DARK = 0xFF1A73E8;
    public static final int HERO_TOP = 0xFF16233A;
    public static final int HERO_BOTTOM = 0xFF0E0F13;
    public static final int PROFILE = 0xFF3C4043;

    private Ui() {
    }

    public static int dp(Context context, int value) {
        return (int) (value * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    public static GradientDrawable rounded(Context context, int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(context, radiusDp));
        return drawable;
    }

    public static GradientDrawable rounded(Context context, int color, int radiusDp,
                                           int strokeDp, int strokeColor) {
        GradientDrawable drawable = rounded(context, color, radiusDp);
        if (strokeDp > 0) {
            drawable.setStroke(dp(context, strokeDp), strokeColor);
        }
        return drawable;
    }

    public static StateListDrawable cardSelector(Context context) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                rounded(context, SURFACE_PRESSED, 12, 3, ACCENT));
        states.addState(new int[]{android.R.attr.state_focused},
                rounded(context, SURFACE_FOCUS, 12, 3, ACCENT));
        states.addState(new int[]{},
                rounded(context, SURFACE, 12, 1, BORDER));
        return states;
    }

    public static StateListDrawable pillSelector(Context context) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                rounded(context, SURFACE_PRESSED, 18, 1, ACCENT));
        states.addState(new int[]{android.R.attr.state_focused},
                rounded(context, SURFACE_FOCUS, 18, 1, ACCENT));
        states.addState(new int[]{}, rounded(context, Color.TRANSPARENT, 18));
        return states;
    }

    public static StateListDrawable circleSelector(Context context) {
        StateListDrawable states = new StateListDrawable();
        GradientDrawable focused = new GradientDrawable();
        focused.setShape(GradientDrawable.OVAL);
        focused.setColor(0xFF5F6368);
        focused.setStroke(dp(context, 2), ACCENT);
        GradientDrawable normal = new GradientDrawable();
        normal.setShape(GradientDrawable.OVAL);
        normal.setColor(PROFILE);
        states.addState(new int[]{android.R.attr.state_pressed}, focused);
        states.addState(new int[]{android.R.attr.state_focused}, focused);
        states.addState(new int[]{}, normal);
        return states;
    }

    public static StateListDrawable heroSelector(Context context) {
        StateListDrawable states = new StateListDrawable();

        GradientDrawable focused = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF1A73E8, 0xFF0E0F13});
        focused.setCornerRadius(dp(context, 18));
        focused.setStroke(dp(context, 2), ACCENT);

        GradientDrawable pressed = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xFF2B84F0, 0xFF0E0F13});
        pressed.setCornerRadius(dp(context, 18));
        pressed.setStroke(dp(context, 2), ACCENT);

        GradientDrawable normal = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{HERO_TOP, HERO_BOTTOM});
        normal.setCornerRadius(dp(context, 18));
        normal.setStroke(dp(context, 1), 0xFF2A3A55);

        states.addState(new int[]{android.R.attr.state_pressed}, pressed);
        states.addState(new int[]{android.R.attr.state_focused}, focused);
        states.addState(new int[]{}, normal);
        return states;
    }

    public static TextView text(Context context, String value, float sp, int color,
                                boolean bold) {
        TextView view = new TextView(context);
        view.setText(value);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
        view.setTextColor(color);
        view.setIncludeFontPadding(false);
        if (bold) {
            view.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return view;
    }

    public static ColorStateList tabTextColors() {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_pressed},
                        new int[]{}
                },
                new int[]{TEXT, TEXT, TEXT_MUTED});
    }

    public static void focusScale(final View view) {
        view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.animate()
                        .scaleX(hasFocus ? 1.03f : 1f)
                        .scaleY(hasFocus ? 1.03f : 1f)
                        .setDuration(120)
                        .start();
            }
        });
    }

    public static void fullscreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    public static Drawable circular(Context context, Drawable source, int sizeDp) {
        int size = dp(context, sizeDp);
        Bitmap sourceBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas sourceCanvas = new Canvas(sourceBitmap);
        source.setBounds(0, 0, size, size);
        source.draw(sourceCanvas);

        int dominant = dominantColor(sourceBitmap);
        int width = sourceBitmap.getWidth();
        int height = sourceBitmap.getHeight();
        int[] pixels = new int[width * height];
        sourceBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        boolean[] seen = new boolean[pixels.length];
        int[] queue = new int[pixels.length];
        int head = 0;
        int tail = 0;

        for (int x = 0; x < width; x++) {
            tail = enqueue(queue, seen, tail, x);
            tail = enqueue(queue, seen, tail, (height - 1) * width + x);
        }
        for (int y = 0; y < height; y++) {
            tail = enqueue(queue, seen, tail, y * width);
            tail = enqueue(queue, seen, tail, y * width + width - 1);
        }

        while (head < tail) {
            int index = queue[head++];
            if (((pixels[index] >>> 24) & 0xFF) >= 128) {
                continue;
            }
            pixels[index] = dominant;
            int x = index % width;
            int y = index / width;
            if (x > 0) {
                tail = enqueue(queue, seen, tail, index - 1);
            }
            if (x < width - 1) {
                tail = enqueue(queue, seen, tail, index + 1);
            }
            if (y > 0) {
                tail = enqueue(queue, seen, tail, index - width);
            }
            if (y < height - 1) {
                tail = enqueue(queue, seen, tail, index + width);
            }
        }

        Bitmap filled = Bitmap.createBitmap(pixels, width, height,
                Bitmap.Config.ARGB_8888);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Path clip = new Path();
        clip.addCircle(width / 2f, height / 2f, width / 2f, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clip);
        canvas.drawBitmap(filled, 0, 0, null);
        canvas.restore();

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    private static int enqueue(int[] queue, boolean[] seen, int tail, int index) {
        if (index < 0 || index >= seen.length || seen[index]) {
            return tail;
        }
        seen[index] = true;
        queue[tail] = index;
        return tail + 1;
    }

    private static int dominantColor(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int[] bins = new int[4096];
        int best = 0;
        int bestCount = 0;
        for (int pixel : pixels) {
            if (((pixel >>> 24) & 0xFF) < 128) {
                continue;
            }
            int r = (pixel >>> 16) & 0xFF;
            int g = (pixel >>> 8) & 0xFF;
            int b = pixel & 0xFF;
            int key = ((r >> 4) << 8) | ((g >> 4) << 4) | (b >> 4);
            int count = ++bins[key];
            if (count > bestCount) {
                bestCount = count;
                best = key;
            }
        }
        if (bestCount == 0) {
            return 0xFF1E2126;
        }
        int r = Math.min(255, (((best >> 8) & 0xF) << 4) + 8);
        int g = Math.min(255, (((best >> 4) & 0xF) << 4) + 8);
        int b = Math.min(255, ((best & 0xF) << 4) + 8);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    public static Drawable youtubeIcon(Context context) {
        int size = dp(context, 96);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(0xFFFF0000);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);

        paint.setColor(0xFFFFFFFF);
        Path play = new Path();
        float cx = size / 2f;
        float cy = size / 2f;
        float width = size * 0.22f;
        float height = size * 0.25f;
        play.moveTo(cx - width * 0.55f, cy - height);
        play.lineTo(cx + width * 0.95f, cy);
        play.lineTo(cx - width * 0.55f, cy + height);
        play.close();
        canvas.drawPath(play, paint);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Drawable appstoreIcon(Context context) {
        int size = dp(context, 96);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint white = new Paint(Paint.ANTI_ALIAS_FLAG);
        white.setColor(0xFFFFFFFF);
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, white);

        Paint text = new Paint(Paint.ANTI_ALIAS_FLAG);
        text.setColor(0xFF111111);
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setTextAlign(Paint.Align.CENTER);
        text.setTextSize(size * 0.30f);
        canvas.drawText("apps", size / 2f, size * 0.55f, text);

        Paint smile = new Paint(Paint.ANTI_ALIAS_FLAG);
        smile.setColor(0xFFFF9900);
        smile.setStyle(Paint.Style.STROKE);
        smile.setStrokeWidth(size * 0.035f);
        smile.setStrokeCap(Paint.Cap.ROUND);
        RectF arc = new RectF(size * 0.27f, size * 0.52f,
                size * 0.73f, size * 0.72f);
        canvas.drawArc(arc, 20f, 140f, false, smile);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Drawable gearIcon(Context context, int color) {
        int size = dp(context, 96);
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);

        float cx = size / 2f;
        float cy = size / 2f;
        float outer = size * 0.46f;
        float body = outer * 0.72f;
        int teeth = 8;
        float toothWidth = outer * 0.34f;

        canvas.drawCircle(cx, cy, body, paint);
        for (int i = 0; i < teeth; i++) {
            canvas.save();
            canvas.rotate(i * 360f / teeth, cx, cy);
            canvas.drawRect(cx - toothWidth / 2f, cy - outer,
                    cx + toothWidth / 2f, cy - body + 1f, paint);
            canvas.restore();
        }

        Paint clear = new Paint(Paint.ANTI_ALIAS_FLAG);
        clear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(cx, cy, outer * 0.30f, clear);
        return new BitmapDrawable(context.getResources(), bitmap);
    }
}
