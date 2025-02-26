package com.chess.clock.views;

import static com.chess.clock.views.ViewUtils.showView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.core.content.ContextCompat;

import com.chess.clock.R;
import com.chess.clock.entities.AppTheme;
import com.chess.clock.entities.ClockTime;

@Keep
public class ClockButton extends FrameLayout {

    private final int idleTextColor;
    private final int runningTextColor;
    int idleBgColor = ContextCompat.getColor(getContext(), R.color.gray_light);

    private final TextView timeTv;
    private final TextView movesTv;
    private final TextView controlNameTv;
    private final View timeOptions;
    private final View stageOne;
    private final View stageTwo;
    private final View stageThree;

    public ClockButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_clock_button, this, true);
        timeTv = view.findViewById(R.id.clockTimeTv);
        movesTv = view.findViewById(R.id.movesTv);
        stageOne = view.findViewById(R.id.stageOne);
        stageTwo = view.findViewById(R.id.stageTwo);
        stageThree = view.findViewById(R.id.stageThree);
        controlNameTv = view.findViewById(R.id.stageNameTv);
        timeOptions = view.findViewById(R.id.adjustTimeImg);

        setForeground(ViewUtils.getSelectableItemBgDrawable(getContext()));

        idleTextColor = getResources().getColor(R.color.black_70);
        runningTextColor = getResources().getColor(R.color.white);
    }

    public void setTime(long timeMillis) {
        ClockTime clockTime = ClockTime.calibrated(timeMillis);
        timeTv.setText(clockTime.toReadableFormat());
        if (clockTime.atLeaseOneHourLeft()) {
            timeTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.clock_timer_textSize_small));
        } else {
            timeTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.clock_timer_textSize_normal));
        }
    }

    @SuppressLint("DefaultLocale")
    public void setMoves(int moves) {
        movesTv.setText(getContext().getString(R.string.moves_x, moves));
    }

    public void setClockButtonClickListener(ClockClickListener listener) {
        setOnClickListener(v -> listener.onClickClock());
        timeOptions.setOnClickListener(v -> listener.onClickOptions());
    }

    public void updateUi(
            AppTheme theme,
            State state
    ) {
        switch (state) {
            case IDLE:
            case LOCKED:
                setBackgroundColor(idleBgColor);
                timeTv.setTextColor(idleTextColor);
                break;
            case RUNNING:
                setBackgroundColor(ContextCompat.getColor(getContext(), theme.primaryColorRes));
                timeTv.setTextColor(runningTextColor);
                break;
            case FINISHED:
                setBackgroundColor(ContextCompat.getColor(getContext(), R.color.red));
                timeTv.setTextColor(idleTextColor);
                break;
        }
        setClickable(state != State.LOCKED);
        boolean hideStageControls = state != State.IDLE;
        ViewUtils.isInvisible(timeOptions, hideStageControls);
        ViewUtils.isInvisible(controlNameTv, hideStageControls);
    }

    private void setStageBg(View stage, Boolean active) {
        if (active) {
            stage.setBackgroundResource(R.drawable.shape_stage_fill);
        } else {
            stage.setBackgroundResource(R.drawable.shape_stage_empty);
        }
    }

    public void updateStage(int stageId, String timeControlName) {
        //stage one is always filled if visible
        setStageBg(stageTwo, stageId > 0);
        setStageBg(stageThree, stageId > 1);
        controlNameTv.setText(timeControlName);
    }

    public void setStages(int stagesNumber) {
        // no stages indicators visible for 1 stage game
        showView(stageOne, stagesNumber > 1);
        showView(stageTwo, stagesNumber > 1);
        showView(stageThree, stagesNumber > 2);
    }

    public enum State {
        IDLE, LOCKED, RUNNING, FINISHED
    }

    @Keep
    public interface ClockClickListener {
        void onClickClock();

        void onClickOptions();
    }
}