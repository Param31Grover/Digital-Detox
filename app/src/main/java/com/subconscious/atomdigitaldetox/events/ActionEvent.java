package com.subconscious.atomdigitaldetox.events;

import com.subconscious.atomdigitaldetox.models.detox.DetoxSetting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ActionEvent {
    @lombok.NonNull
    private String next;
    private boolean markCompletion;
    private String feedbackFormId;
    private boolean addToStack = true;
    private DetoxSetting detoxSetting;
}
