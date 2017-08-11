package xy.hippocampus.cadenza.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import xy.hippocampus.cadenza.R;

/**
 * Created by Xavier Yin on 2017/7/28.
 */

public class ColorPalette {

    public static int[] getPrimaryColors(Context context) {
        return new int[]{
                ContextCompat.getColor(context, R.color.md_red_400),
                ContextCompat.getColor(context, R.color.md_red_800),
                ContextCompat.getColor(context, R.color.md_pink_300),
                ContextCompat.getColor(context, R.color.md_pink_800),
                ContextCompat.getColor(context, R.color.md_purple_800),
                ContextCompat.getColor(context, R.color.md_deep_purple_800),
                ContextCompat.getColor(context, R.color.md_indigo_800),
                ContextCompat.getColor(context, R.color.md_blue_800),
                ContextCompat.getColor(context, R.color.md_light_blue_800),
                ContextCompat.getColor(context, R.color.md_cyan_800),
                ContextCompat.getColor(context, R.color.md_teal_800),
                ContextCompat.getColor(context, R.color.md_green_800),
                ContextCompat.getColor(context, R.color.md_lime_800),
                ContextCompat.getColor(context, R.color.md_light_green_500),
                ContextCompat.getColor(context, R.color.md_yellow_800),
                ContextCompat.getColor(context, R.color.md_amber_A200),
                ContextCompat.getColor(context, R.color.md_orange_800),
                ContextCompat.getColor(context, R.color.md_deep_orange_800),
                ContextCompat.getColor(context, R.color.md_brown_800),
                ContextCompat.getColor(context, R.color.md_blue_grey_800)
        };
    }

    public static int[] getColorSuite(Context context, int c) {
        if (c == ContextCompat.getColor(context, R.color.md_red_400)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_red_500),
                    ContextCompat.getColor(context, R.color.md_red_500_alpha),
                    ContextCompat.getColor(context, R.color.md_red_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_red_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_red_900),
                    ContextCompat.getColor(context, R.color.md_red_900_alpha),
                    ContextCompat.getColor(context, R.color.md_red_200)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_pink_300)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_pink_400),
                    ContextCompat.getColor(context, R.color.md_pink_400_alpha),
                    ContextCompat.getColor(context, R.color.md_pink_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_pink_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_pink_900),
                    ContextCompat.getColor(context, R.color.md_pink_900_alpha),
                    ContextCompat.getColor(context, R.color.md_pink_200)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_purple_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_purple_900),
                    ContextCompat.getColor(context, R.color.md_purple_900_alpha),
                    ContextCompat.getColor(context, R.color.md_purple_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_deep_purple_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_deep_purple_900),
                    ContextCompat.getColor(context, R.color.md_deep_purple_900_alpha),
                    ContextCompat.getColor(context, R.color.md_deep_purple_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_indigo_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_indigo_900),
                    ContextCompat.getColor(context, R.color.md_indigo_900_alpha),
                    ContextCompat.getColor(context, R.color.md_indigo_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_blue_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_blue_900),
                    ContextCompat.getColor(context, R.color.md_blue_900_alpha),
                    ContextCompat.getColor(context, R.color.md_blue_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_light_blue_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_light_blue_900),
                    ContextCompat.getColor(context, R.color.md_light_blue_900_alpha),
                    ContextCompat.getColor(context, R.color.md_light_blue_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_cyan_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_cyan_900),
                    ContextCompat.getColor(context, R.color.md_cyan_900_alpha),
                    ContextCompat.getColor(context, R.color.md_cyan_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_teal_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_teal_900),
                    ContextCompat.getColor(context, R.color.md_teal_900_alpha),
                    ContextCompat.getColor(context, R.color.md_teal_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_green_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_green_900),
                    ContextCompat.getColor(context, R.color.md_green_900_alpha),
                    ContextCompat.getColor(context, R.color.md_green_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_light_green_500)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_light_green_600),
                    ContextCompat.getColor(context, R.color.md_light_green_600_alpha),
                    ContextCompat.getColor(context, R.color.md_light_green_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_lime_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_lime_900),
                    ContextCompat.getColor(context, R.color.md_lime_900_alpha),
                    ContextCompat.getColor(context, R.color.md_lime_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_yellow_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_yellow_900),
                    ContextCompat.getColor(context, R.color.md_yellow_900_alpha),
                    ContextCompat.getColor(context, R.color.md_yellow_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_amber_A200)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_amber_A700),
                    ContextCompat.getColor(context, R.color.md_amber_A700_alpha),
                    ContextCompat.getColor(context, R.color.md_amber_200)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_orange_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_orange_900),
                    ContextCompat.getColor(context, R.color.md_orange_900_alpha),
                    ContextCompat.getColor(context, R.color.md_orange_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_deep_orange_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_deep_orange_900),
                    ContextCompat.getColor(context, R.color.md_deep_orange_900_alpha),
                    ContextCompat.getColor(context, R.color.md_deep_orange_100)
            };
        } else if (c == ContextCompat.getColor(context, R.color.md_brown_800)) {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_brown_900),
                    ContextCompat.getColor(context, R.color.md_brown_900_alpha),
                    ContextCompat.getColor(context, R.color.md_brown_100)
            };
        } else {
            return new int[]{
                    ContextCompat.getColor(context, R.color.md_blue_grey_900),
                    ContextCompat.getColor(context, R.color.md_blue_grey_900_alpha),
                    ContextCompat.getColor(context, R.color.md_blue_grey_100)
            };
        }
    }
}
