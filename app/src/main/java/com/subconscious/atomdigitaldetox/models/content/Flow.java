package com.subconscious.atomdigitaldetox.models.content;

import org.parceler.Parcel;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class Flow {

    public interface FlowData {}

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class AudioFlowData implements FlowData {
        private static final String TYPE = "audio";

        //Header
        private String title;
        private String header;
        private int headerTextSize;
        private String imageUri;
        private String imageDescription;

        //Body
        private String backgroundImageUri;
        private boolean play;
        private String uri;
        private String duration;

        //Footer
        private Action onComplete;
        private Action action;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class TimerInputFlowData implements FlowData {
        public static final String TYPE = "timerInput";

        //Header
        private String title;
        private String header;
        private int headerTextSize;
        private String imageUri;

        //Body
        private String backgroundImageUri;
        private int durationMinutes;

        //Footer
        private Action action;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class TimerFlowData implements FlowData {
        public static final String TYPE = "timer";

        //Header
        private String title;
        private String header;
        private int headerTextSize;
        private String imageUri;
        private String imageDescription;

        //Body
        private String backgroundImageUri;
        private boolean play;
        private int durationMinutes;

        //Footer
        private Action onComplete;
        private Action action;
        private Action end;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class TextFlowData implements FlowData {
        private static final String TYPE = "text";
        //Header
        private String gravity;

        //Body
        private String content;
        private String contentTextAlignment;
        private String contentTitle;

        //Footer
        private List<Action> actions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Parcel
    public static class ImageTextFlowData implements FlowData {
        private static final String TYPE = "imageText";
        //Header
        private String imageId;

        //Body
        private String content;
        private String contentTitle;

        //Footer
        private List<Action> actions;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Parcel
    public static class Question implements FlowData{
        private String id;
        private String question;
        private String questionType;
        private String answer;
        private boolean required;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Parcel
    public static class QuestionChoice extends Question implements FlowData{
        private List<Choice> choices;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Parcel
    public static class QuestionInput extends Question implements FlowData{
        private String inputHint;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Parcel
    public static class QuestionFillBlank extends Question implements FlowData{
        private String inputHint;
        private String prefix;
        private String suffix;
    }

    @Deprecated
    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class QuizFlowData implements FlowData {
        private static final String TYPE = "quiz";
        private String imageId;
        private String subHeader;
        private String question;
        private List<Choice> choices;
        private Action action;
    }

    @Deprecated
    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class QuizInputFlowData implements FlowData {
        private static final String TYPE = "quizInput";
        private String question;
        private String inputHint;
        private String imageId;
        private String content;
        private String answer;
        private Action action;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class ImageTextQuizFlowData implements FlowData {
        private static final String TYPE = "quizImage";
        private String imageId;
        private String content;
        List<Question> questions;
        private List<Action> actions;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class TextQuizFlowData implements FlowData {
        private static final String TYPE = "textQuiz";
        private String header;
        List<Question> questions;
        private Action action;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class RitualReminderSettingsFlowData implements FlowData {
        private static final String TYPE = "ritualReminderSettings";

        //Header
        private String gravity;

        //Body
        private String content;
        private String contentTextAlignment;
        private String contentTitle;
        private List<String> locations;
        private List<String> anchors;

        //Footer
        private Action action;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class RitualSetupCompleteFlowData implements FlowData {
        private static final String TYPE = "ritualSetupComplete";
        //Header
        private String gravity;

        //Body
        private String content;
        private String contentTextAlignment;
        private String contentTitle;

        //Footer
        private Action action;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class Action {
        private String label;
        private String next;
        private boolean addToStack = true;
        private boolean markCompletion;
        private String feedbackFormId;
        private boolean showAlertDialog;
    }

    @Data
    @AllArgsConstructor
    @Parcel
    @NoArgsConstructor
    public static class Choice {
        private String label;
        private String value;
        private boolean correct;
        private String next;
    }
}
