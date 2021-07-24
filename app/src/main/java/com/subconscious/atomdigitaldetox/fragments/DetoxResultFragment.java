package com.subconscious.atomdigitaldetox.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.subconscious.atomdigitaldetox.R;
import com.subconscious.atomdigitaldetox.events.ActionEvent;
import com.subconscious.atomdigitaldetox.helper.Utils;
import com.subconscious.atomdigitaldetox.models.content.Flow;
import com.subconscious.atomdigitaldetox.store.SharedPrefManager;
import com.subconscious.atomdigitaldetox.views.RoundedIconButton;

import org.greenrobot.eventbus.EventBus;
import org.parceler.Parcels;

public class DetoxResultFragment extends ContentBaseFragment {

    /* Keys */
    private static final String KEY_DATA = "data";
    private static final String TREE_DURATION_LEFT_KEY = "TREEKEY";
    private static final String TOTAL_TREE_DURATION_KEY = "DURATION";
    private static final String RUNNING_KEY = "RUNNING";

    /* Data Model */
    private Flow.TimerFlowData data;

    /* View Holders */
    private TextView titleTV;
    private TextView headerTV;
    private ImageView imageIV;
    private TextView imageDescription;
    private RoundedIconButton actionButton;
    private RoundedIconButton homeButton;

    /* Instantiate Fragment Handler -- START -- */
    public static DetoxResultFragment getInstance(@NonNull Flow.TimerFlowData data) {
        DetoxResultFragment fragment = new DetoxResultFragment();
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
        return inflater.inflate(R.layout.fragment_detox_result, container, false);
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
        initViews(view);
        initListeners();
        setupViews();
    }

    /* View Handlers -- START -- */
    private void initViews(View view) {
        titleTV = view.findViewById(R.id.tv_title);
        headerTV = view.findViewById(R.id.tv_sub_header);
        imageIV = view.findViewById(R.id.iv_image);
        imageDescription = view.findViewById(R.id.tv_description);
        actionButton = view.findViewById(R.id.button_next);
        homeButton = view.findViewById(R.id.button_home);
    }

    private void initListeners() {
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefManager.removeKey(getContext(), TOTAL_TREE_DURATION_KEY);
                SharedPrefManager.removeKey(getContext(), TREE_DURATION_LEFT_KEY);
                SharedPrefManager.removeKey(getContext(), RUNNING_KEY);
                handleNextAction();
            }
        });
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPrefManager.setLong(getContext(), TREE_DURATION_LEFT_KEY, SharedPrefManager.getLong(getContext(), TOTAL_TREE_DURATION_KEY));
                SharedPrefManager.setBoolean(getContext(), RUNNING_KEY, true);
                goBackHome();
            }
        });
    }

    private void setupViews() {
        titleTV.setText(data.getTitle());
        titleTV.setAllCaps(true);
        headerTV.setText(data.getHeader());
        if (data.getHeaderTextSize() > 0) headerTV.setTextSize(data.getHeaderTextSize());
        imageDescription.setText(data.getImageDescription());
        imageDescription.setTextSize(18);
        imageIV.setImageDrawable(Utils.getDrawable(getContext(), data.getImageUri()));
        actionButton.setButtonLabel(data.getAction().getLabel());
        actionButton.setVisibility(View.VISIBLE);
        if (data.getEnd() != null) {
            homeButton.setVisibility(View.VISIBLE);
            homeButton.setButtonLabel(data.getEnd().getLabel());

        }
    }
    /* View Handlers -- END -- */

    /* Event Publisher -- START --*/
    private void handleNextAction() {
        EventBus.getDefault().post(new ActionEvent(data.getAction().getNext(),
                data.getAction().isMarkCompletion(),
                data.getAction().getFeedbackFormId(),
                data.getAction().isAddToStack(), null));
    }

    private void goBackHome() {
        EventBus.getDefault().post(new ActionEvent(data.getEnd().getNext(),
                data.getAction().isMarkCompletion(),
                data.getAction().getFeedbackFormId(),
                data.getAction().isAddToStack(), null));
    }
    /* Event Publisher -- END --*/
}