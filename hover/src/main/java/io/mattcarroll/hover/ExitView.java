/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mattcarroll.hover;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.RelativeLayout;

/**
 * Fullscreen View that provides an exit "drop zone" for users to exit the Hover Menu.
 */
class ExitView extends RelativeLayout {

    private static final String TAG = "ExitView";

    private static final int FADE_DURATION = 250;
    private static final int ENTER_EXIT_DURATION = 250;
    private static final float EXIT_ICON_DEFAULT_SCALE_X = 1.0f;
    private static final float EXIT_ICON_DEFAULT_SCALE_Y = 1.0f;
    private static final float EXIT_ICON_TARGET_SCALE_X = 1.2f;
    private static final float EXIT_ICON_TARGET_SCALE_Y = 1.2f;
    private static final float EXIT_ICON_DEFAULT_ROTATION = 0f;
    private static final float EXIT_ICON_TARGET_ROTATION = 90f;
    private static final float EXIT_ICON_DEFAULT_ALPHA = 0.6f;
    private static final float EXIT_ICON_TARGET_ALPHA = 0.75f;
    private static final float EXIT_VIEW_TARGET_ALPHA = 1.0f;
    private static final float EXIT_VIEW_TARGET_Y = 0f;
    private static final float EXIT_VIEW_BASE_ALPHA = 0f;
    private static final float EXIT_VIEW_BASE_Y = 800f;

    private int mExitRadiusInPx;
    private View mExitIcon;
    private View mExitGradient;
    private ViewGroup mVgExit;
    public ObjectAnimator enterAnimation = null;
    public ObjectAnimator exitAnimation = null;
    public boolean isExitAnimated = false;

    public ExitView(@NonNull Context context) {
        this(context, null);
    }

    public ExitView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_hover_menu_exit, this, true);

        mExitIcon = findViewById(R.id.view_exit);
        mVgExit = findViewById(R.id.vg_exit);
        mExitGradient = findViewById(R.id.view_exit_gradient);
        mExitRadiusInPx = getResources().getDimensionPixelSize(R.dimen.hover_exit_radius);
        mExitIcon.setAlpha(EXIT_ICON_DEFAULT_ALPHA);

        setAnimations();
    }

    private Interpolator getExitViewInterpolator() {
        return PathInterpolatorCompat.create(0.75f, 0f, 0.25f, 1f);
    }

    private void setAnimations() {
        PropertyValuesHolder enterAnimationScaleX = PropertyValuesHolder.ofFloat("scaleX", EXIT_ICON_DEFAULT_SCALE_X, EXIT_ICON_TARGET_SCALE_X);
        PropertyValuesHolder enterAnimationScaleY = PropertyValuesHolder.ofFloat("scaleY", EXIT_ICON_DEFAULT_SCALE_Y, EXIT_ICON_TARGET_SCALE_Y);
        PropertyValuesHolder enterAnimationRotate = PropertyValuesHolder.ofFloat("rotation", EXIT_ICON_DEFAULT_ROTATION, EXIT_ICON_TARGET_ROTATION);
        PropertyValuesHolder enterAnimationAlpha = PropertyValuesHolder.ofFloat("alpha", EXIT_ICON_DEFAULT_ALPHA, EXIT_ICON_TARGET_ALPHA);
        enterAnimation = ObjectAnimator.ofPropertyValuesHolder(mExitIcon, enterAnimationScaleX, enterAnimationScaleY, enterAnimationRotate, enterAnimationAlpha);
        enterAnimation.setDuration(ENTER_EXIT_DURATION);
        enterAnimation.setInterpolator(getExitViewInterpolator());
        enterAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                initExitIconViewStatus();
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {
                initExitIconViewStatus();
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        PropertyValuesHolder exitAnimationScaleX = PropertyValuesHolder.ofFloat("scaleX", EXIT_ICON_TARGET_SCALE_X, EXIT_ICON_DEFAULT_SCALE_X);
        PropertyValuesHolder exitAnimationScaleY = PropertyValuesHolder.ofFloat("scaleY", EXIT_ICON_TARGET_SCALE_Y, EXIT_ICON_DEFAULT_SCALE_Y);
        PropertyValuesHolder exitAnimationRotate = PropertyValuesHolder.ofFloat("rotation", EXIT_ICON_TARGET_ROTATION, EXIT_ICON_DEFAULT_ROTATION);
        PropertyValuesHolder exitAnimationAlpha = PropertyValuesHolder.ofFloat("alpha", EXIT_ICON_TARGET_ALPHA, EXIT_ICON_DEFAULT_ALPHA);
        exitAnimation = ObjectAnimator.ofPropertyValuesHolder(mExitIcon, exitAnimationScaleX, exitAnimationScaleY, exitAnimationRotate, exitAnimationAlpha);
        exitAnimation.setDuration(ENTER_EXIT_DURATION);
        exitAnimation.setInterpolator(getExitViewInterpolator());
    }

    private void initExitIconViewStatus() {
        mExitIcon.setScaleY(EXIT_ICON_DEFAULT_SCALE_Y);
        mExitIcon.setScaleX(EXIT_ICON_DEFAULT_SCALE_X);
        mExitIcon.setRotation(EXIT_ICON_DEFAULT_ROTATION);
    }

    public boolean isInExitZone(@NonNull Point position, @NonNull Point screenSize) {
        int exitXExcludeThresholdLeft = screenSize.x / 10;
        int exitXExcludeThresholdRight = screenSize.x * 9 / 10;

        Rect exitArea = new Rect(
                0 - mExitIcon.getWidth(),
                screenSize.y * 4 / 6,
                screenSize.x + mExitIcon.getWidth(),
                screenSize.y + mExitIcon.getHeight()
        );

        Rect excludedXExitAreaLeft = new Rect(
                0 - mExitIcon.getWidth(),
                screenSize.y * 4 / 6,
                exitXExcludeThresholdLeft,
                screenSize.y * 5 / 6
        );

        Rect excludedXExitAreaRight = new Rect(
                exitXExcludeThresholdRight,
                screenSize.y * 4 / 6,
                screenSize.x + mExitIcon.getWidth(),
                screenSize.y * 5 / 6
        );

        return exitArea.contains(position.x, position.y)
                && !excludedXExitAreaLeft.contains(position.x, position.y)
                && !excludedXExitAreaRight.contains(position.x, position.y);
    }

    private Point getExitZoneCenter() {
        return new Point(
                (int) (mExitIcon.getX() + (mExitIcon.getWidth() / 2)),
                (int) (mExitIcon.getY() + (mExitIcon.getHeight() / 2))
        );
    }

    private double calculateDistance(@NonNull Point p1, @NonNull Point p2) {
        return Math.sqrt(
                Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2)
        );
    }

    public void startEnterExitAnim() {
        if (enterAnimation != null && !enterAnimation.isRunning() && !isExitAnimated) {
            enterAnimation.start();
            isExitAnimated = true;
        }
    }

    public void startExitExitAnim() {
        if (exitAnimation != null && !exitAnimation.isRunning() && isExitAnimated) {
            exitAnimation.start();
            isExitAnimated = false;
        }
    }

    public void show() {
        resetExitButtonAnimation();

        ObjectAnimator exitGradientAnimator = ObjectAnimator.ofFloat(mExitGradient, "alpha", EXIT_VIEW_TARGET_ALPHA);
        exitGradientAnimator.setDuration(FADE_DURATION);
        exitGradientAnimator.setInterpolator(getExitViewInterpolator());
        exitGradientAnimator.start();

        ObjectAnimator vgExitAnimator = ObjectAnimator.ofFloat(mVgExit, "y", EXIT_VIEW_BASE_Y, EXIT_VIEW_TARGET_Y);
        vgExitAnimator.setDuration(FADE_DURATION);
        vgExitAnimator.setInterpolator(getExitViewInterpolator());
        vgExitAnimator.start();

        setVisibility(VISIBLE);
    }

    public void resetExitButtonAnimation() {
        isExitAnimated = false;
        initExitIconViewStatus();
    }

    public void hide() {
        ObjectAnimator vgExitAnimator = ObjectAnimator.ofFloat(mVgExit, "y", EXIT_VIEW_TARGET_Y, EXIT_VIEW_BASE_Y);
        vgExitAnimator.setDuration(FADE_DURATION);
        vgExitAnimator.setInterpolator(getExitViewInterpolator());
        vgExitAnimator.start();

        ObjectAnimator exitGradientAnimator = ObjectAnimator.ofFloat(mExitGradient, "alpha", EXIT_VIEW_BASE_ALPHA);
        exitGradientAnimator.setDuration(FADE_DURATION);
        exitGradientAnimator.setInterpolator(getExitViewInterpolator());
        exitGradientAnimator.start();

        exitGradientAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
}
