package cn.edu.cuz.zhengjun.mydiary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;
import java.util.UUID;

public class DiaryPagerActivity extends AppCompatActivity {
    public static final String EXTRA_TYPE = "type";
    public static final String EXTRA_ASC = "asc";
    public static final String EXTRA_DIARY_ID = "diary_id";

    private ViewPager mViewPager;
    private Button mJumpToFirstButton;
    private Button mJumpToLastButton;
    private List<Diary> mDiaries;

    public static Intent newIntent(Context packageContent, int type, boolean asc, UUID diaryId){
        Intent intent = new Intent(packageContent, DiaryPagerActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_ASC, asc);
        intent.putExtra(EXTRA_DIARY_ID, diaryId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_pager);

        mViewPager = findViewById(R.id.diary_view_pager);
        mJumpToFirstButton = findViewById(R.id.jump_to_first_button);
        mJumpToLastButton = findViewById(R.id.jump_to_last_button);

        mJumpToFirstButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });

        mJumpToLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(mDiaries.size() - 1);
            }
        });

        int type = getIntent().getIntExtra(EXTRA_TYPE, 0);
        boolean asc = getIntent().getBooleanExtra(EXTRA_ASC, false);
        mDiaries = DiaryLab.get(this).getDiaries(type, asc);
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                Diary diary = mDiaries.get(position);
                return DiaryFragment.newInstance(diary.getId());
            }

            @Override
            public int getCount() {
                return mDiaries.size();
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setJumpButtons(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        UUID diaryId = (UUID)getIntent().getSerializableExtra(EXTRA_DIARY_ID);
        for(int i = 0; i < mDiaries.size(); i++) {
            if(mDiaries.get(i).getId().equals(diaryId)){
                mViewPager.setCurrentItem(i);
                setJumpButtons(i);
                break;
            }
        }
    }

    void setJumpButtons(int index){
        mJumpToFirstButton.setEnabled(index != 0);
        mJumpToLastButton.setEnabled(index != mDiaries.size() - 1);
    }
}
