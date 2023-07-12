package com.example.publictransportationguidance.tracking.trackingModule.trackingHelpers;

import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

public class TextAnimator {
    private AnimationSet animationSet;
    private TranslateAnimation translateAnimation;

    public void startAnimation(TextView textView, String text) {
        translateAnimation = createTranslateAnimation(-0.9f, 0.1f, 0.0f, 0.0f);
        adjustTranslateAnimation(getAnimationDuration(text));
        createAnimationSet(translateAnimation);
        textView.startAnimation(animationSet);
    }

    public void stopAnimation(TextView textView) {
        textView.clearAnimation();
    }

    public void clearTextView(TextView textView) {
        textView.setText("");
    }

    public void destroyAnimatedText(TextView animatedTextView){
        stopAnimation(animatedTextView);
        clearTextView(animatedTextView);
    }

    public void refreshAnimation(String newText, TextView tvOfAnimatedText){
        stopAnimation(tvOfAnimatedText);
        tvOfAnimatedText.setText(newText);
        startAnimation(tvOfAnimatedText, newText);
    }

    private TranslateAnimation createTranslateAnimation(float startX, float endX, float startY, float endY) {
        return new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, startX,
                Animation.RELATIVE_TO_SELF, endX,
                Animation.RELATIVE_TO_SELF, startY,
                Animation.RELATIVE_TO_SELF, endY
        );
    }

    private int getAnimationDuration(String text) {
        int textLength = text.length();
        int pixelsToScroll = textLength * 10; // Adjust the scrolling speed as needed
        return pixelsToScroll * 5; // Adjust the duration as needed
    }

    private void adjustTranslateAnimation(int duration) {
        translateAnimation.setDuration(duration);
        translateAnimation.setRepeatCount(Animation.INFINITE);
        translateAnimation.setRepeatMode(Animation.RESTART);
    }

    private void createAnimationSet(TranslateAnimation translateAnimation) {
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(translateAnimation);
    }
}

