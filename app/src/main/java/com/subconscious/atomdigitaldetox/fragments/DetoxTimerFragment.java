package com.subconscious.atomdigitaldetox.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.jackandphantom.circularprogressbar.CircleProgressbar;
import com.subconscious.atomdigitaldetox.R;
import com.subconscious.atomdigitaldetox.events.ActionEvent;
import com.subconscious.atomdigitaldetox.helper.ServiceUtils;
import com.subconscious.atomdigitaldetox.helper.Utils;
import com.subconscious.atomdigitaldetox.models.content.Flow;
import com.subconscious.atomdigitaldetox.services.DigitalDetoxService;
import com.subconscious.atomdigitaldetox.store.SharedPrefManager;

import org.greenrobot.eventbus.EventBus;
import org.parceler.Parcels;

public class DetoxTimerFragment extends ContentBaseFragment {

    /* Keys */
    private static final String KEY_DATA = "data";
    private static final String TREE_DURATION_LEFT_KEY = "TREEKEY";
    private static final String TOTAL_TREE_DURATION_KEY = "DURATION";
    private static final String RUNNING_KEY = "RUNNING";

    /* Data Models */
    private Flow.TimerFlowData data;

    /* View Holders */
    private TextView titleTV;
    private TextView headerTV;
    private CircleProgressbar circularProgressContainer;
    private ImageView imageIV;
    private TextView imageDescription;
    private TextView giveUpTV;
    private AlertDialog alertDialog;
    private CountDownTimer countDownTimer;

    /* Variables */
    private Boolean isFinished;
    private Boolean isWithered;
    private long totalTimerLength;
    private long detoxDuration;

    /* Instantiate Fragment Handler -- START -- */
    public static DetoxTimerFragment getInstance(@NonNull Flow.TimerFlowData data) {
        DetoxTimerFragment fragment = new DetoxTimerFragment();
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
        View view = inflater.inflate(R.layout.fragment_detox_timer, container, false);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener((view1, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                showAlertDialog();
                return true;
            }
            return false;
        });
        return view;
    }

    /* Parcel Handler --START-- */
    private void processInputData() {
        data = Parcels.unwrap(getArguments().getParcelable(KEY_DATA));
    }

    private boolean isValidToProceed() {
        return null != data;
    }
    /* Parcel Handler --END-- */

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isWithered = false;
        isFinished = false;
        if (!SharedPrefManager.getBoolean(getContext(), RUNNING_KEY)) handleNextAction();
        initViews(view);
        setupViews();
        if (!ServiceUtils.getInstance(getContext()).isMyServiceRunning(DigitalDetoxService.class)) {
            ServiceUtils.getInstance(getContext()).startTrackingService(DigitalDetoxService.class);
        }
    }

    /* View Handlers -- START -- */
    private void initViews(View view) {
        titleTV = view.findViewById(R.id.tv_title);
        headerTV = view.findViewById(R.id.tv_sub_header);
        circularProgressContainer = view.findViewById(R.id.circular_progress);
        imageIV = view.findViewById(R.id.iv_image);
        imageDescription = view.findViewById(R.id.tv_description);
        giveUpTV = view.findViewById(R.id.tv_giveup);
    }

    private void setupViews() {
        titleTV.setText(data.getTitle());
        titleTV.setAllCaps(true);
        headerTV.setText(data.getHeader());
        giveUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
        if (data.getHeaderTextSize() > 0) headerTV.setTextSize(data.getHeaderTextSize());
        if (data.getDurationMinutes() > 0) {
            imageDescription.setText(Utils.getDisplayString(data.getDurationMinutes() * 60 * 1000));
            imageDescription.setTextSize(48);
        }
        circularProgressContainer.setBackground(Utils.getDrawable(getContext(), data.getBackgroundImageUri()));
        imageIV.setImageDrawable(Utils.getDrawable(getContext(), data.getImageUri()));
    }
    /* View Handlers -- END -- */

    @Override
    public boolean onBackPressed() {
        showAlertDialog();
        return false;
    }

    /* Timer Handlers -- START -- */
    private void initiateTimer() {
        totalTimerLength = data.getDurationMinutes() * 60 * 1000;
        if (SharedPrefManager.containsKey(getContext(), TREE_DURATION_LEFT_KEY)) {
            totalTimerLength = SharedPrefManager.getLong(getContext(), TREE_DURATION_LEFT_KEY);
        }
        detoxDuration = SharedPrefManager.getLong(getContext(), TOTAL_TREE_DURATION_KEY);
        String display = Utils.getDisplayString(totalTimerLength);
        imageDescription.setText(display);
        imageDescription.setTextSize(48);
        startCountdown(totalTimerLength);
        SharedPrefManager.setLong(getContext(), TREE_DURATION_LEFT_KEY, totalTimerLength);
    }

    private void startCountdown(long duration) {
        if (null != countDownTimer)
            countDownTimer.cancel();
        countDownTimer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinished = SharedPrefManager.getLong(getContext(), TREE_DURATION_LEFT_KEY);
                float progress = 100 - ((float) millisUntilFinished * 100 / (float) detoxDuration);
                circularProgressContainer.setProgressWithAnimation(progress, 1000);
                imageDescription.setText(Utils.getDisplayString(millisUntilFinished));
                if (!SharedPrefManager.getBoolean(getContext(), RUNNING_KEY)) {
                    isWithered = true;
                    handleNextAction();
                    countDownTimer.cancel();
                }
            }

            @Override
            public void onFinish() {
                isFinished = true;
                imageDescription.setText("COMPLETED !");
                detoxCompleted();
            }
        };
    }

    private void startTimer() {
        try {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            countDownTimer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* Timer Handlers -- END -- */

    /* Event Publisher -- START -- */
    private void handleNextAction() {
        ServiceUtils.getInstance(getContext()).stopTrackingService(DigitalDetoxService.class);
        SharedPrefManager.setBoolean(getContext(), RUNNING_KEY, false);
        EventBus.getDefault().post(new ActionEvent(data.getAction().getNext(),
                data.getAction().isMarkCompletion(),
                data.getAction().getFeedbackFormId(),
                data.getAction().isAddToStack(), null));
    }

    private void detoxCompleted() {
        ServiceUtils.getInstance(getContext()).stopTrackingService(DigitalDetoxService.class);
        EventBus.getDefault().post(new ActionEvent(data.getOnComplete().getNext(),
                data.getOnComplete().isMarkCompletion(),
                data.getOnComplete().getFeedbackFormId(),
                data.getOnComplete().isAddToStack(), null));
    }
    /* Event Publisher -- END -- */

    /* Alert Dialog Handler -- START -- */
    private void showAlertDialog() {
        if (null != alertDialog) {
            alertDialog.show();
            return;
        }
        LayoutInflater layoutInflater = this.getLayoutInflater();
        LinearLayout alertDialogueContainer = (LinearLayout) layoutInflater.inflate(R.layout.alert_dialog_tree_withered, null);
        LinearLayout cancelButton = alertDialogueContainer.findViewById(R.id.button_cancel);
        LinearLayout yesButton = alertDialogueContainer.findViewById(R.id.button_yes);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this.getContext(), R.style.AppCompatAlertDialogStyle);
        builder.setView(alertDialogueContainer);
        alertDialog = builder.create();

        cancelButton.setOnClickListener(view -> alertDialog.dismiss());
        yesButton.setOnClickListener(view -> {
            alertDialog.dismiss();
            handleNextAction();
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

    /*Lifecycle Handlers -- START -- */
    @Override
    public void onPause() {
        super.onPause();
        hideAlertDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
        ServiceUtils.getInstance(getContext()).stopTrackingService(DigitalDetoxService.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        initiateTimer();
        startTimer();
        if (isFinished) detoxCompleted();
        if (isWithered) handleNextAction();
    }
    /*Lifecycle Handlers -- END -- */
}