package com.subconscious.atomdigitaldetox.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.subconscious.atomdigitaldetox.helper.PermissionUtil;
import com.subconscious.atomdigitaldetox.models.content.Flow;
import com.subconscious.atomdigitaldetox.R;
import com.subconscious.atomdigitaldetox.events.ActionEvent;
import com.subconscious.atomdigitaldetox.helper.Utils;

import com.subconscious.atomdigitaldetox.models.detox.DetoxSetting;
import com.subconscious.atomdigitaldetox.store.SharedPrefManager;
import com.subconscious.atomdigitaldetox.views.RoundedIconButton;

import org.greenrobot.eventbus.EventBus;
import org.parceler.Parcels;

public class DetoxSettingsFragment extends ContentBaseFragment {

    /* Key */
    private static final String KEY_DATA = "data";
    private static final String TREE_DURATION_LEFT_KEY = "TREEKEY";
    private static final String TOTAL_TREE_DURATION_KEY = "DURATION";
    private static final String RUNNING_KEY = "RUNNING";

    /* Data Model */
    private Flow.TimerInputFlowData data;

    /* View Holders */
    private TextView titleTV;
    private TextView headerTV;
    private ImageView imageIV;
    private TextView durationTV;
    private ImageView decreaseButton;
    private ImageView increaseButton;
    private RoundedIconButton actionButton;
    private AlertDialog alertDialog;
    private Switch dndSwitch;

    private NotificationManager mNotificationManager;

    /* Variables */
    private int timerLengthInMinutes;

    /* Instantiate Fragment Handler -- START -- */
    public static DetoxSettingsFragment getInstance(@NonNull Flow.TimerInputFlowData data) {
        DetoxSettingsFragment fragment = new DetoxSettingsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_DATA, Parcels.wrap(data));
        fragment.setArguments(bundle);
        return fragment;
    }
    /* Instantiate Fragment Handler -- END -- */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        processInputData();
        if (!isValidToProceed()) {
            getActivity().finish();
        }
        return inflater.inflate(R.layout.fragment_detox_settings, container, false);
    }

    /* Parcel Handler --START-- */
    private void processInputData() {
        data = Parcels.unwrap(getArguments().getParcelable(KEY_DATA));
        mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (mNotificationManager != null) {
            mNotificationManager.cancelAll();
        }
    }

    private boolean isValidToProceed() {
        return null != data;
    }
    /* Parcel Handler --END-- */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initListeners();
        setupViews();
    }

    /* View Handlers -- START -- */
    private void initViews(View view) {
        titleTV = view.findViewById(R.id.tv_title);
        headerTV = view.findViewById(R.id.tv_sub_header);
        imageIV = view.findViewById(R.id.iv_image);
        durationTV = view.findViewById(R.id.tv_duration);
        decreaseButton = view.findViewById(R.id.button_decrease);
        increaseButton = view.findViewById(R.id.button_increase);
        actionButton = view.findViewById(R.id.button_next);
        dndSwitch = view.findViewById(R.id.dnd_switch);
    }

    private void initListeners() {
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerLengthInMinutes < 90) {
                    timerLengthInMinutes++;
                    durationTV.setText(Utils.getDisplayString(timerLengthInMinutes * 60 * 1000));
                }
            }
        });
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerLengthInMinutes > 1) {
                    timerLengthInMinutes--;
                    durationTV.setText(Utils.getDisplayString(timerLengthInMinutes * 60 * 1000));
                }
            }
        });
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PermissionUtil.getPermission(getContext());
                if (PermissionUtil.checkPermission(getContext())) {
                    setData();
                    handleNextAction();
                } else {
                    showAlertDialog();
                }
            }
        });
        dndSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALARMS);
                } else {
                    changeInterruptionFiler(NotificationManager.INTERRUPTION_FILTER_ALL);
                }
            }
        });
    }

    private void setupViews() {
        actionButton.setButtonLabel(data.getAction().getLabel());
        titleTV.setText(data.getTitle());
        titleTV.setAllCaps(true);
        headerTV.setText(data.getHeader());
        if (data.getHeaderTextSize() > 0) headerTV.setTextSize(data.getHeaderTextSize());
        durationTV.setText(Utils.getDisplayString(data.getDurationMinutes() * 60 * 1000));
        timerLengthInMinutes = data.getDurationMinutes();
        imageIV.setImageDrawable(Utils.getDrawable(getContext(), data.getImageUri()));
    }
    /* View Handlers -- END -- */

    /* Alert Dialog Handler -- START -- */
    private void showAlertDialog() {
        if (null != alertDialog) {
            alertDialog.show();
            return;
        }
        LayoutInflater layoutInflater = this.getLayoutInflater();
        LinearLayout alertDialogueContainer = (LinearLayout) layoutInflater.inflate(R.layout.alert_dialog_permissions, null);
        LinearLayout cancelButton = alertDialogueContainer.findViewById(R.id.button_cancel);
        LinearLayout yesButton = alertDialogueContainer.findViewById(R.id.button_yes);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this.getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setView(alertDialogueContainer);
        alertDialog = builder.create();

        cancelButton.setOnClickListener(view -> alertDialog.dismiss());
        yesButton.setOnClickListener(view -> {
            PermissionUtil.givePermission(getContext());
        });
        alertDialog.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                alertDialog.dismiss();
            }
            return true;
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void hideAlertDialog() {
        if (null != alertDialog) {
            alertDialog.dismiss();
        }
    }
    /* Alert Dialog Handler -- END -- */

    /* Set SharedPreferences  --START-- */
    private void setData() {
        SharedPrefManager.setLong(getContext(), TOTAL_TREE_DURATION_KEY, timerLengthInMinutes * 60 * 1000);
        SharedPrefManager.setLong(getContext(), TREE_DURATION_LEFT_KEY, timerLengthInMinutes * 60 * 1000);
        SharedPrefManager.setBoolean(getContext(), RUNNING_KEY, true);
    }
    /* Set SharedPreferences  --START-- */

    private void changeInterruptionFiler(int interruptionFilter){
        // If api level minimum 23
        if(mNotificationManager.isNotificationPolicyAccessGranted()){
            mNotificationManager.setInterruptionFilter(interruptionFilter);
        } else{
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
            startActivity(intent);
        }
    }

    /* Event Publisher -- START --*/
    private void handleNextAction() {
        EventBus.getDefault().post(new ActionEvent(data.getAction().getNext(),
                data.getAction().isMarkCompletion(),
                data.getAction().getFeedbackFormId(),
                data.getAction().isAddToStack(), new DetoxSetting(timerLengthInMinutes)));
    }
    /* Event Publisher -- END --*/

    /* Lifecycle Handlers -- START--*/
    @Override
    public void onPause() {
        super.onPause();
        hideAlertDialog();
    }
    /* Lifecycle Handler -- END -- */
}