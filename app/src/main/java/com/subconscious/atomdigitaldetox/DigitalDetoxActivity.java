package com.subconscious.atomdigitaldetox;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.subconscious.atomdigitaldetox.events.ActionEvent;
import com.subconscious.atomdigitaldetox.fragments.ContentBaseFragment;
import com.subconscious.atomdigitaldetox.fragments.DetoxResultFragment;
import com.subconscious.atomdigitaldetox.fragments.DetoxTimerFragment;
import com.subconscious.atomdigitaldetox.fragments.DetoxSettingsFragment;
import com.subconscious.atomdigitaldetox.listeners.ActivityLifecycleCallback;
import com.subconscious.atomdigitaldetox.models.DetoxFragmentType;
import com.subconscious.atomdigitaldetox.models.content.Flow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class DigitalDetoxActivity extends AppCompatActivity {

    private static final String FRAGMENT_TYPE = "FRAGMENTTYPE";
    private DetoxFragmentType detoxFragmentType;

    HashMap<String, ContentBaseFragment> fragments = new HashMap<>();
    HashMap<String, Integer> progress = new HashMap<>();
    HashMap<String, Boolean> hideBackButton = new HashMap<>();
    HashMap<String, Boolean> hideCancelButton = new HashMap<>();
    Stack<String> flowStack = new Stack<>();

    private Flow.TimerFlowData timerData;

    private LinearLayout customBackButtonLL;
    private LinearLayout customCancelButtonLL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_flow);
        detoxFragmentType = (DetoxFragmentType) getIntent().getSerializableExtra(FRAGMENT_TYPE);
        if (detoxFragmentType == null) detoxFragmentType = DetoxFragmentType.SETTINGS;
        customBackButtonLL = findViewById(R.id.ll_button_back);
        customCancelButtonLL = findViewById(R.id.ll_button_cancel);
        String content = "{\"taskId\":\"qwertyuiop\",\"flow\":{\"$start\":{\"type\":\"timerInput\",\"data\":{\"title\":\"Mindful Breathing\",\"header\":\"“Your future is created by what you can do today, not tomorrow”\",\"imageUri\":\"ic_grown_tree\",\"durationMinutes\":5,\"backgroundImageUri\":\"tree_land_blue_border\",\"action\":{\"type\":\"button\",\"label\":\"Start the Detox!\",\"next\":\"timer_1\"}}},\"timer_1\":{\"type\":\"timer\",\"hideCancelButton\":true,\"hideBackButton\":true,\"data\":{\"title\":\"Mindful Breathing\",\"header\":\"“Your future is created by what you can do today, not tomorrow”\",\"imageUri\":\"ic_sapling\",\"backgroundImageUri\":\"tree_land_blue_border\",\"play\":true,\"durationMinutes\":5,\"onComplete\":{\"markCompletion\":true,\"next\":\"timer_3\"},\"action\":{\"showAlertDialog\":true,\"type\":\"Text\",\"label\":\"Give Up\",\"next\":\"timer_2\"}}},\"timer_2\":{\"type\":\"result\",\"hideBackButton\":true,\"hideCancelButton\":true,\"data\":{\"title\":\"\",\"header\":\"Uh oh, your tree has withered\",\"headerTextSize\":25,\"imageUri\":\"ic_meditation_tree_withered\",\"imageDescription\":\"Meditation is hard. Remember, embrace failure and don’t let it stop you from trying again\",\"backgroundImageUri\":\"tree_land_red_border\",\"end\":{\"type\":\"button\",\"label\":\"Try Again\",\"next\":\"timer_1\"},\"action\":{\"type\":\"button\",\"label\":\"Finish\",\"next\":\"$finish\"}}},\"timer_3\":{\"type\":\"result\",\"hideCancelButton\":true,\"hideBackButton\":true,\"data\":{\"title\":\"Mindful Breathing\",\"header\":\"Congratulations !\",\"headerTextSize\":25,\"imageUri\":\"ic_grown_tree\",\"imageDescription\":\"Congratulations, you have successfully completed your meditation and a tree has grown !\",\"backgroundImageUri\":\"tree_land_green_border\",\"action\":{\"type\":\"button\",\"label\":\"Finish\",\"next\":\"$finish\"}}}}}";
        init(content);
    }

    private void init(final String rawJSON) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    createFragments(rawJSON);
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
                // Flow must always start from "$start".
                String s = loadFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment, fragments.get(s))
                        .commit();

                flowStack.push(s);
                setupHeader(s);
            }
        });
        getApplication().registerActivityLifecycleCallbacks(new ActivityLifecycleCallback());
    }

    private String loadFragment() {
        for (Map.Entry<String, ContentBaseFragment> entry : fragments.entrySet()) {
            ContentBaseFragment contentBaseFragment = entry.getValue();
            if (detoxFragmentType.equals(DetoxFragmentType.getFragmentType(contentBaseFragment))) {
                return entry.getKey();
            }
        }
        return "$start";
    }

    private void setupHeader(String next) {
        setupCustomBackButton(next);
        setupCustomCancelButton(next);
    }

    private void setupCustomBackButton(String next) {
        int visibility = (null != hideBackButton.get(next)) ? View.INVISIBLE : View.VISIBLE;
        customBackButtonLL.setVisibility(visibility);
    }

    private void setupCustomCancelButton(String next) {
        int visibility = (null != hideCancelButton.get(next)) ? View.INVISIBLE : View.VISIBLE;
        customCancelButtonLL.setVisibility(visibility);
    }

    private void changeFragment(ActionEvent e) {
        String next = e.getNext();
        if ("$finish".equals(next)) {
            this.finish();
            return;
        }
        if (null != e.getDetoxSetting()) {
            int duration = e.getDetoxSetting().getDuration();
            timerData.setDurationMinutes(duration);
            fragments.put(next, DetoxTimerFragment.getInstance(timerData));
        }
        setupHeader(next);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        transaction.replace(R.id.fragment, fragments.get(next));
        transaction.addToBackStack(null);
        transaction.commit();
        flowStack.add(next);
    }


    private void createFragments(String rawJSON) throws Exception {
        JSONObject json = new JSONObject(rawJSON);
        JSONObject flow = json.getJSONObject("flow");
        fragments.clear();
        Iterator<String> keys = flow.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            JSONObject value = flow.getJSONObject(key);
            fragments.put(key, fragmentFactory(value.getString("type"), value.getJSONObject("data")));
            if (!value.isNull("progress"))
                progress.put(key, value.getInt("progress"));
            if (!value.isNull("hideCancelButton")
                    && value.getBoolean("hideCancelButton"))
                hideCancelButton.put(key, value.getBoolean("hideCancelButton"));
            if (!value.isNull("hideBackButton")
                    && value.getBoolean("hideBackButton"))
                hideBackButton.put(key, value.getBoolean("hideBackButton"));

        }
    }

    private ContentBaseFragment fragmentFactory(String type, JSONObject data) throws Exception {
        if ("timerInput".equals(type)) {
            return DetoxSettingsFragment.getInstance((Flow.TimerInputFlowData) flowDataFactory(type, data));
        } else if ("timer".equals(type)) {
            return processContentTimerFragment((Flow.TimerFlowData) flowDataFactory(type, data));
        } else if ("result".equals(type)) {
            return DetoxResultFragment.getInstance((Flow.TimerFlowData) flowDataFactory(type, data));
        }
        throw new UnsupportedOperationException();
    }

    private Flow.FlowData flowDataFactory(String type, JSONObject data) throws Exception {
        if ("timerInput".equals(type)) {
            return new Gson().fromJson(data.toString(), Flow.TimerInputFlowData.class);
        } else if ("timer".equals(type)) {
            return new Gson().fromJson(data.toString(), Flow.TimerFlowData.class);
        } else if ("result".equals(type)) {
            return new Gson().fromJson(data.toString(), Flow.TimerFlowData.class);
        }
        throw new UnsupportedOperationException();
    }

    private DetoxTimerFragment processContentTimerFragment(Flow.TimerFlowData data) {
        if (data.isPlay()) {
            timerData = data;
        }
        return DetoxTimerFragment.getInstance(data);
    }

    @Override
    public void onBackPressed() {

    }

    public void onBackButtonClick(View view) {
        onBackPressed();
    }

    public void onCancelButtonClick(View view) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onActionEvent(ActionEvent e) {
        changeFragment(e);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        detoxFragmentType = (DetoxFragmentType) getIntent().getSerializableExtra(FRAGMENT_TYPE);
        if (detoxFragmentType == null) detoxFragmentType = DetoxFragmentType.SETTINGS;
        String content = "{\"taskId\":\"qwertyuiop\",\"flow\":{\"$start\":{\"type\":\"timerInput\",\"data\":{\"title\":\"Mindful Breathing\",\"header\":\"“Your future is created by what you can do today, not tomorrow”\",\"imageUri\":\"ic_grown_tree\",\"durationMinutes\":5,\"backgroundImageUri\":\"tree_land_blue_border\",\"action\":{\"type\":\"button\",\"label\":\"Start the Detox!\",\"next\":\"timer_1\"}}},\"timer_1\":{\"type\":\"timer\",\"hideCancelButton\":true,\"hideBackButton\":true,\"data\":{\"title\":\"Mindful Breathing\",\"header\":\"“Your future is created by what you can do today, not tomorrow”\",\"imageUri\":\"ic_sapling\",\"backgroundImageUri\":\"tree_land_blue_border\",\"play\":true,\"durationMinutes\":5,\"onComplete\":{\"markCompletion\":true,\"next\":\"timer_3\"},\"action\":{\"showAlertDialog\":true,\"type\":\"Text\",\"label\":\"Give Up\",\"next\":\"timer_2\"}}},\"timer_2\":{\"type\":\"result\",\"hideBackButton\":true,\"hideCancelButton\":true,\"data\":{\"title\":\"\",\"header\":\"Uh oh, your tree has withered\",\"headerTextSize\":25,\"imageUri\":\"ic_meditation_tree_withered\",\"imageDescription\":\"Meditation is hard. Remember, embrace failure and don’t let it stop you from trying again\",\"backgroundImageUri\":\"tree_land_red_border\",\"end\":{\"type\":\"button\",\"label\":\"Try Again\",\"next\":\"timer_1\"},\"action\":{\"type\":\"button\",\"label\":\"Finish\",\"next\":\"$finish\"}}},\"timer_3\":{\"type\":\"result\",\"hideCancelButton\":true,\"hideBackButton\":true,\"data\":{\"title\":\"Mindful Breathing\",\"header\":\"Congratulations !\",\"headerTextSize\":25,\"imageUri\":\"ic_grown_tree\",\"imageDescription\":\"Congratulations, you have successfully completed your meditation and a tree has grown !\",\"backgroundImageUri\":\"tree_land_green_border\",\"action\":{\"type\":\"button\",\"label\":\"Finish\",\"next\":\"$finish\"}}}}}";
        init(content);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) manager.cancelAll();
    }
}
