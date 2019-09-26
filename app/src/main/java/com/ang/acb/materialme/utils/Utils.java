package com.ang.acb.materialme.utils;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.palette.graphics.Palette;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat();
    private static GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2, 1, 1);

    public static Date formatPublishedDate(String publishedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
        try {
            return dateFormat.parse(publishedDate);
        } catch (ParseException ex) {
            return new Date();
        }
    }

    public static Spanned formatArticleByline(Date publishedDate, String author) {
        // See: https://medium.com/androiddevelopers/spantastic-text-styling-with-spans-17b0c16b4568
        Spanned byline;
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {
            byline = Html.fromHtml(DateUtils.getRelativeTimeSpanString(
                    publishedDate.getTime(),
                    System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_ALL).toString()
                    + " by <font color='#ffffff'>" + author + "</font>");
        } else {
            // If date is before 1902, just show the string.
            byline = Html.fromHtml(dateFormat.format(publishedDate) +
                    " by <font color='#ffffff'>" + author + "</font>");
        }
        return byline;
    }

    public static float dipToPixels(Context context, float dipValue) {
        // See: https://stackoverflow.com/questions/8399184/convert-dip-to-px-in-android
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static Palette.Swatch getDominantColor(Palette palette) {
        // To extract prominent colors from an image, we can use the Platte
        // class. When a palette is generated, a number of colors with different
        // profiles are extracted from the image: vibrant, dark vibrant,
        // light vibrant, muted, dark muted, light muted, and dominant.
        // These can be retrieved from the appropriate getter method.
        // See: https://developer.android.com/training/material/palette-colors
        Palette.Swatch result = palette.getDominantSwatch();
        if (palette.getVibrantSwatch() != null) {
            result = palette.getVibrantSwatch();
        } else if (palette.getMutedSwatch() != null) {
            result = palette.getMutedSwatch();
        }
        return result;
    }
}
