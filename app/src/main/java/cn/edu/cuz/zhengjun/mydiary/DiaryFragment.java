package cn.edu.cuz.zhengjun.mydiary;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.GenericArrayType;
import java.net.URISyntaxException;
import java.nio.file.FileStore;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DiaryFragment extends Fragment {
    private static final String ARG_DIARY_ID = "diary_id";
    public static final String DIALOG_DATE = "DialogDate";
    public static final String DIALOG_TIME = "DialogTime";


    public static final int REQUEST_VIDEO_RECORD = 0;
    public static final int REQUEST_DATE = 0;
    public static final int REQUEST_TIME= 1;
    public static final int REQUEST_PHOTO = 2;
    public static final int REQUEST_CONTACT = 1;

    private ImageButton mVideoButton,videoPlayerButton;
    private Diary mDiary;
    private EditText mTitleField;
    private EditText mContentField;
    private ImageView mCollectedImageView;
    private Button mDateButton;
    private Button mTimeButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private  Uri videoUri;

    private File mPhotoFile;
    private File mVideoFile;

    SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public DiaryFragment() {
    }

    public static DiaryFragment newInstance(UUID diaryId){
        Bundle args = new Bundle();
        args.putSerializable(ARG_DIARY_ID, diaryId);

        DiaryFragment fragment = new DiaryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID diaryId = (UUID) getArguments().getSerializable(ARG_DIARY_ID);
        mDiary = DiaryLab.get(getActivity()).getDiary(diaryId);
        mPhotoFile = DiaryLab.get(getActivity()).getPhotoFile(mDiary);
        //mVideoFile = DiaryLab.get(getActivity()).getVideoFile(mDiary);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_diary, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_diary:
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.delete_diary)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DiaryLab.get(getActivity()).deleteDiary(mDiary);
                                getActivity().finish();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
                return true;
            case R.id.share_diary:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getDiaryReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.diary_share_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_diary, container, false);


        mCollectedImageView = v.findViewById(R.id.diary_collected);
        if(mDiary.isCollected()) {
            mCollectedImageView.setImageResource(R.drawable.ic_action_collected);
        } else {
            mCollectedImageView.setImageResource(R.drawable.ic_action_incollected);
        }
        mCollectedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDiary.setCollected(!mDiary.isCollected());
                if(mDiary.isCollected()) {
                    mCollectedImageView.setImageResource(R.drawable.ic_action_collected);
                } else {
                    mCollectedImageView.setImageResource(R.drawable.ic_action_incollected);
                }
            }
        });


        mTitleField = v.findViewById(R.id.diary_title);
        mTitleField.setText(mDiary.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                mDiary.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mContentField = v.findViewById(R.id.diary_content);
        mContentField.setText(mDiary.getContent());
        mContentField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                mDiary.setContent(s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = v.findViewById(R.id.diary_date);
        mTimeButton = v.findViewById(R.id.diary_time);


        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mDiary.getDate());
                dialog.setTargetFragment(DiaryFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mDiary.getDate());
                dialog.setTargetFragment(DiaryFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mPhotoButton = (ImageButton) v.findViewById(R.id.diary_camera);

        final  Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(getActivity().getPackageManager()) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "cn.edu.cuz.zhengjun.mydiary.fileprovider",mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(captureImage,REQUEST_PHOTO);

                List<ResolveInfo>cameraActivities = getActivity().getPackageManager().queryIntentActivities(captureImage,
                        PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo activity : cameraActivities){
                    getActivity().grantUriPermission(activity.activityInfo.packageName,uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.diary_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mPhotoFile != null && mPhotoFile.exists()){
                    Uri uri = FileProvider.getUriForFile(getActivity(),
                            "cn.edu.cuz.zhengjun.mydiary.fileprovider",mPhotoFile);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("image/*");
                    i.putExtra(Intent.EXTRA_STREAM, uri);
                    i = Intent.createChooser(i, getString(R.string.send_report));
                    startActivity(i);
                }
            }
        });

        super.onCreate(savedInstanceState);
        mVideoButton = (ImageButton) v.findViewById(R.id.video_button);
        mVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String directoryPath = getActivity().getFilesDir() + "/";
                String fileName = System.currentTimeMillis() + ".mp4";
                File videoFile = new File(directoryPath, fileName);
                videoUri = FileProvider.getUriForFile(getActivity(),
                        "cn.edu.cuz.zhengjun.mydiary.fileprovider", videoFile);

                try {
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                    startActivityForResult(intent, REQUEST_VIDEO_RECORD);
                } catch (ActivityNotFoundException e){
                    Toast.makeText(getActivity(), "设备上没有录像程序", Toast.LENGTH_SHORT).show();
                }
            }
        });


        videoPlayerButton = (ImageButton) v.findViewById(R.id.video_player_button);
        videoPlayerButton.setEnabled(true);
        videoPlayerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(videoUri, "video/mp4");
                    startActivity(intent);
                } catch(ActivityNotFoundException e){
                    Toast.makeText(getActivity(), "设备上没有视频播放程序", Toast.LENGTH_SHORT).show();
                }
            }
        });





        updatePhotoView();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        DiaryLab.get(getActivity()).updateDiary(mDiary);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mDiary.getDate().setYear(date.getYear());
            mDiary.getDate().setMonth(date.getMonth());
            mDiary.getDate().setDate(date.getDate());
            updateDate();
        } else if (requestCode == REQUEST_TIME) {
            Date date = (Date)data
                    .getSerializableExtra(TimePickerFragment.EXTRA_DATE);
            mDiary.getDate().setHours(date.getHours());
            mDiary.getDate().setMinutes(date.getMinutes());
            updateDate();
        }else if (requestCode == REQUEST_PHOTO){
            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "cn.edu.cuz.zhengjun.mydiary.fileprovider",mPhotoFile);
            getActivity().revokeUriPermission(uri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    private void updateDate() {
        mDateButton.setText(dateFormat.format(mDiary.getDate()));
        mTimeButton.setText(timeFormat.format(mDiary.getDate()));
    }

    private String getDiaryReport() {
        String dateString = datetimeFormat.format(mDiary.getDate());
        String report = getString(R.string.diary_share,
            mDiary.getTitle(), mDiary.getContent(), dateString);
        return report;
    }

    private  void updatePhotoView(){
        if (mPhotoFile == null || !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);
    }

}
