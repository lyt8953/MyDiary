package cn.edu.cuz.zhengjun.mydiary;

import androidx.fragment.app.Fragment;

public class DiaryListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new DiaryListFragment();
    }

}
