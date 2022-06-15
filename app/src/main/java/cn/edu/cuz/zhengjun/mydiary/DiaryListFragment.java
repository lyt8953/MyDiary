package cn.edu.cuz.zhengjun.mydiary;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;

public class DiaryListFragment extends Fragment {
    private static final String SAVED_TYPE = "type";
    private static final String SAVED_ASC = "asc";

    private int type;   //0 全部 1 收藏
    private boolean asc;
    private RecyclerView mDiaryRecyclerView;
    private DiaryAdaper mAdapter;

    SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_diary_list, container, false);
        mDiaryRecyclerView = (RecyclerView) v.findViewById(R.id.diary_recycler_view);
        mDiaryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            type = savedInstanceState.getInt(SAVED_TYPE);
            asc = savedInstanceState.getBoolean(SAVED_ASC);
        }

        updateUI();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_diary_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.sort);
        if (asc) {
            subtitleItem.setTitle(R.string.date_desc);
        } else {
            subtitleItem.setTitle(R.string.date_asc);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_diary:
                Diary diary = new Diary();
                DiaryLab.get(getActivity()).addDiary(diary);
                Intent intent = DiaryPagerActivity.newIntent(getActivity(), 0, asc, diary.getId());
                startActivity(intent);
                return true;
            case R.id.show_all:
                type = 0;
                updateUI();
                return true;
            case R.id.show_collected:
                type = 1;
                updateUI();
                return true;
            case R.id.sort:
                asc = !asc;
                getActivity().invalidateOptionsMenu();
                updateUI();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        DiaryLab diaryLab = DiaryLab.get(getActivity());
        int diaryCount = diaryLab.getDiaries(type, asc).size();
        String subtitle = getString(R.string.subtitle_format, diaryCount);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_TYPE, type);
        outState.putBoolean(SAVED_ASC, asc);
    }


    private void updateUI() {
        DiaryLab diaryLab = DiaryLab.get(getActivity());
        List<Diary> diaries = diaryLab.getDiaries(type, asc);

        if(mAdapter == null) {
            mAdapter = new DiaryAdaper(diaries);
            mDiaryRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setDiaries(diaries);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class DiaryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mCollectedImageView;
        private Diary mDiary;

        public DiaryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_diary, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = itemView.findViewById(R.id.diary_title);
            mDateTextView = itemView.findViewById(R.id.diary_date);
            mCollectedImageView = itemView.findViewById(R.id.diary_collected);
        }

        public void bind(Diary diary) {
            mDiary = diary;
            mTitleTextView.setText(mDiary.getTitle());
            mDateTextView.setText(dateTimeFormat.format(mDiary.getDate()));
            if(mDiary.isCollected())
                mCollectedImageView.setImageResource(R.drawable.ic_action_collected);
            else
                mCollectedImageView.setImageResource(R.drawable.ic_action_incollected);
        }

        @Override
        public void onClick(View view) {
            Intent intent = DiaryPagerActivity.newIntent(getActivity(), type, asc, mDiary.getId());
            startActivity(intent);
        }
    }

    private class DiaryAdaper extends RecyclerView.Adapter<DiaryHolder> {
        private List<Diary> mDiaries;

        public DiaryAdaper(List<Diary> diaries) {
            mDiaries = diaries;
        }

        public void setDiaries(List<Diary> diaries){
            mDiaries = diaries;
        }

        @Override
        public DiaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new DiaryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(DiaryHolder diaryHolder, int position) {
            Diary diary = mDiaries.get(position);
            diaryHolder.bind(diary);
        }

        @Override
        public int getItemCount() {
            return mDiaries.size();
        }
    }
}
