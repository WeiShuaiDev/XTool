package com.linwei.tool.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.linwei.tool.adapter.JsonViewerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonRecyclerView extends RecyclerView {

    private JsonViewerAdapter mAdapter;

    public JsonRecyclerView(Context context) {
        this(context, null);
    }

    public JsonRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JsonRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView();
    }

    private void initView() {
        setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void bindJson(String jsonStr) {
        mAdapter = null;
        mAdapter = new JsonViewerAdapter(jsonStr);
        setAdapter(mAdapter);
    }

    public void bindJson(JSONArray array) {
        mAdapter = null;
        mAdapter = new JsonViewerAdapter(array);
        setAdapter(mAdapter);
    }

    public void bindJson(JSONObject object) {
        mAdapter = null;
        mAdapter = new JsonViewerAdapter(object);
        setAdapter(mAdapter);
    }

    public void setKeyColor(int color) {
        JsonViewerAdapter.KEY_COLOR = color;
    }

    public void setValueTextColor(int color) {
        JsonViewerAdapter.TEXT_COLOR = color;
    }

    public void setValueNumberColor(int color) {
        JsonViewerAdapter.NUMBER_COLOR = color;
    }

    public void setValueBooleanColor(int color) {
        JsonViewerAdapter.BOOLEAN_COLOR = color;
    }

    public void setValueUrlColor(int color) {
        JsonViewerAdapter.URL_COLOR = color;
    }

    public void setValueNullColor(int color) {
        JsonViewerAdapter.NUMBER_COLOR = color;
    }

    public void setBracesColor(int color) {
        JsonViewerAdapter.BRACES_COLOR = color;
    }

    public void setTextSize(float sizeDP) {
        if (sizeDP < 10) {
            sizeDP = 10;
        } else if (sizeDP > 30) {
            sizeDP = 30;
        }

        if (JsonViewerAdapter.TEXT_SIZE_DP != sizeDP) {
            JsonViewerAdapter.TEXT_SIZE_DP = sizeDP;
            if (mAdapter != null) {
                updateAll(sizeDP);
            }
        }
    }

    public void setScaleEnable(boolean enable) {
        if (enable) {
            addOnItemTouchListener(touchListener);
        } else {
            removeOnItemTouchListener(touchListener);
        }
    }

    public void updateAll(float textSize) {
        LayoutManager manager = getLayoutManager();

        int count = manager.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = manager.getChildAt(i);
            loop(view, textSize);
        }
    }

    private void loop(View view, float textSize) {
        if (view instanceof JsonItemView) {
            JsonItemView group = (JsonItemView) view;

            group.setTextSize(textSize);

            int childCount = group.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View view1 = group.getChildAt(i);
                loop(view1, textSize);
            }
        }
    }

    int mode;
    float oldDist;

    private void zoom(float f) {
        setTextSize(JsonViewerAdapter.TEXT_SIZE_DP * f);
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private final OnItemTouchListener touchListener = new OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, MotionEvent event) {
            switch (event.getAction() & event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mode = 1;
                    break;
                case MotionEvent.ACTION_UP:
                    mode = 0;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode -= 1;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDist = spacing(event);
                    mode += 1;
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (mode >= 2) {
                        float newDist = spacing(event);
                        if (Math.abs(newDist - oldDist) > 0.5f) {
                            zoom(newDist / oldDist);
                            oldDist = newDist;
                        }
                    }
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent event) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    };
}
