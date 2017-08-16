package xy.hippocampus.cadenza.view.theme;

import android.content.DialogInterface;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import uz.shift.colorpicker.LineColorPicker;
import uz.shift.colorpicker.OnColorChangedListener;
import xy.hippocampus.cadenza.R;
import xy.hippocampus.cadenza.controller.activity.common.mode.slanting.HomeActivity;
import xy.hippocampus.cadenza.controller.manager.PrefsManager;
import xy.hippocampus.cadenza.util.ColorPalette;

/**
 * Created by Xavier Yin on 2017/7/28.
 */

public class ColorSettings extends ThemeSettings {

    public ColorSettings(HomeActivity activity) {
        super(activity);
    }

    public void pickUpColor(@StringRes int title, final Callback callback) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.getActivity());

        View dialogLayout = LayoutInflater.from(this.getActivity()).inflate(R.layout.dialog_color_picker, null);
        final LineColorPicker colorPicker = (LineColorPicker) dialogLayout.findViewById(R.id.color_picker_primary);
        final TextView dialogTitle = (TextView) dialogLayout.findViewById(R.id.dialog_title);

        int[] primaryColors = ColorPalette.getPrimaryColors(getActivity());
        colorPicker.setColors(primaryColors);

        int primaryColor = PrefsManager.getInstance(this.getActivity()).acquirePrimaryColor();
        colorPicker.setSelectedColor(primaryColor);
        dialogTitle.setBackgroundColor(primaryColor);

        dialogTitle.setText(title);
        dialogBuilder.setView(dialogLayout);

        colorPicker.setOnColorChangedListener(new OnColorChangedListener() {

            @Override
            public void onColorChanged(int c) {
                dialogTitle.setBackgroundColor(c);
            }
        });

        dialogBuilder.setNegativeButton(this.getActivity().getString(R.string.action_cancel).toUpperCase(), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.setPositiveButton(this.getActivity().getString(R.string.action_confirm).toUpperCase(), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onColorSelected(colorPicker.getColor());
            }
        });

        dialogBuilder.show();
    }

    public interface Callback {
        void onColorSelected(int color);
    }
}
