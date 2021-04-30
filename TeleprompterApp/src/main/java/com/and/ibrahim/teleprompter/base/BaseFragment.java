package com.and.ibrahim.teleprompter.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;

public abstract class BaseFragment extends Fragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setUp(view);
    }

    protected abstract void setUp(View view);
}
