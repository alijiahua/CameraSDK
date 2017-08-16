package com.lmiot.cameralibrary.Camera_new;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lmiot.cameralibrary.Camera_new.adapter.LocalPictureAndVideoAdapter;
import com.lmiot.cameralibrary.Camera_new.utils.ContentCommon;
import com.lmiot.cameralibrary.Camera_new.utils.DatabaseUtil;
import com.lmiot.cameralibrary.R;
import com.lmiot.cameralibrary.Util.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 *
 **/

public class LocalPictureAndVideoActivity extends BaseActivity implements OnItemClickListener, View.OnClickListener {


    private String TAG = "LocalPictureAndVideoActivity";
    private int wh;
    private String strDID;
    private String cameraName;
    private DatabaseUtil mDbUtil;
    private ArrayList<Map<String, ArrayList<String>>> ListItem;
    private List<String> videotimes;
    private boolean isFirstStart = false;
    private LocalPictureAndVideoAdapter mAdapter;
    private ArrayList<MyItem> items;
    private ImageView mIvBack;
    private TextView mTvBack;
    private TextView mIdTitle;
    private TextView mTvModify;
    private ImageView mIvAdd;
    private ListView mIdPictureListview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDataFromOther();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();
        wh = width > height ? height : width;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_video);
        mDbUtil = new DatabaseUtil(this);
        ListItem = new ArrayList<Map<String, ArrayList<String>>>();
        videotimes = new ArrayList<String>();
        initView();

    }

    @Override
    protected void onResume() {
        Log.i("info", "LocalPictureAndVideoActivity onResume");
        items = initData();
        sort(items);

        Log.d("LocalPictureAndVideoAct", "items.size():" + items.size());

        mAdapter = new LocalPictureAndVideoAdapter(this, items, wh / 5);
        mIdPictureListview.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        if (items.isEmpty()) {
            ToastUtil.ToastMessage(LocalPictureAndVideoActivity.this, "本地录像为空！");
            finish();
        }
        super.onResume();
    }


    public void sort(ArrayList<MyItem> items) {
        MyItem temps;
        MyItem pre;
        MyItem after;
        for (int i = 0; i < items.size(); i++) {
            for (int j = 0; j < items.size() - 1; j++) {
                pre = items.get(j);
                after = items.get(j + 1);

                if (pre.data.compareTo(after.data) < 0) {
                    temps = items.get(j);
                    items.set(j, items.get(j + 1));
                    items.set(j + 1, temps);
                }
            }

        }
    }

    public ArrayList<MyItem> initData() {
        ArrayList<MyItem> items = new ArrayList<MyItem>();
        ListItem.clear();
        ListItem.add(initPicData());//从数据库加载图片跟视频文件到ListItem里
        ListItem.add(initVideoData());//

        for (int i = 0; i < ListItem.size(); i++) {
            Map<String, ArrayList<String>> map = ListItem.get(i);
            Iterator<String> it = map.keySet().iterator();
            while (it.hasNext()) {
                MyItem item = new MyItem();
                String data = it.next();
                item.data = data;
                item.paths = map.get(data);
                item.type = item.paths.get(0).endsWith("jpg") ? 1 : 2;
                items.add(item);
            }

        }
        return items;
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.iv_back || i == R.id.tv_back) {
            finish();
            overridePendingTransition(R.anim.out_to_right, R.anim.in_from_left);

        } else if (i == R.id.tv_modify) {
        }

    }

    public class MyItem {
        public String data;
        public ArrayList<String> paths;
        public int type = -1;
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initView() {


        mIvBack = findViewById(R.id.iv_back);
        mTvBack = findViewById(R.id.tv_back);
        mIdTitle = findViewById(R.id.id_title);
        mTvModify = findViewById(R.id.tv_modify);
        mIvAdd = findViewById(R.id.iv_add);
        mIdPictureListview = findViewById(R.id.id_picture_listview);

        mIvBack.setOnClickListener(this);
        mTvBack.setOnClickListener(this);
        mTvModify.setOnClickListener(this);

        mIvAdd.setVisibility(View.GONE);
        mTvModify.setVisibility(View.GONE);
        mIdTitle.setText("本地视频");
        mIdPictureListview.setOnItemClickListener(this);

    }

    private Map<String, ArrayList<String>> initVideoData() {

        Map<String, ArrayList<String>> childMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> groupList = new ArrayList<String>();
        mDbUtil.open();
        Cursor cursor = mDbUtil.queryAllVideo(strDID);//查询
        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor
                    .getColumnIndex(DatabaseUtil.KEY_FILEPATH));//根据列的下标
            // sdcard
            File file = null;
            try {
                file = new File(filePath);
                if (file == null || !file.exists()) {
                    boolean delResult = mDbUtil.deleteVideoOrPicture(strDID,
                            filePath, DatabaseUtil.TYPE_VIDEO);
                    continue;//
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String s1 = filePath.substring(filePath.lastIndexOf("/") + 1);

            if (!videotimes.contains(s1)) {
                videotimes.add(s1);
            }

            String date = s1.substring(0, 10);
            Log.d("tag", "date:" + date);
            if (!groupList.contains(date)) {
                groupList.add(date);
                ArrayList<String> list = new ArrayList<String>();
                list.add(filePath);
                childMap.put(date, list);
            } else {
                childMap.get(date).add(filePath);
            }
        }

        mDbUtil.close();
        Collections.sort(groupList, new Comparator<String>() {

            @Override
            public int compare(String object1, String object2) {
                return object2.compareTo(object1);
            }
        });
        return childMap;
    }

    private Map<String, ArrayList<String>> initPicData() {
        Map<String, ArrayList<String>> childMap = new HashMap<String, ArrayList<String>>();
        ArrayList<String> groupList = new ArrayList<String>();
        mDbUtil.open();
        Cursor cursor = mDbUtil.queryAllPicture(strDID);
        while (cursor.moveToNext()) {
            String filePath = cursor.getString(cursor
                    .getColumnIndex(DatabaseUtil.KEY_FILEPATH));

            File file = null;
            try {
                file = new File(filePath);
                if (file == null || !file.exists()) {
                    boolean delResult = mDbUtil.deleteVideoOrPicture(strDID,
                            filePath, DatabaseUtil.TYPE_PICTURE);
                    continue;
                }
            } catch (Exception e) {

            }
            String s1 = filePath.substring(filePath.lastIndexOf("/") + 1);
            String date = s1.substring(0, 10);

            if (!groupList.contains(date)) {
                groupList.add(date);
                ArrayList<String> list = new ArrayList<String>();
                list.add(filePath);

                childMap.put(date, list);
                Log.i("info", "groupList:" + groupList);
                Log.i("info", "childMap:" + childMap);
            } else {
                childMap.get(date).add(filePath);
            }
        }

        mDbUtil.close();
        Collections.sort(groupList, new Comparator<String>() {

            @Override
            public int compare(String object1, String object2) {
                return object2.compareTo(object1);
            }
        });
        return childMap;

    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getDataFromOther() {
        Intent intent = getIntent();
        strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
        cameraName = intent.getStringExtra(ContentCommon.STR_CAMERA_NAME);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {

        String date = items.get(position).data;
        Intent intent = null;
        int type = items.get(position).type;

        if (type == 1) {
            //intent = new Intent(this, ShowLocalPicGridActivity.class);
            //此处直接跳转到本地图像观看，可以根据自己业务逻辑进行编写！
        } else if (type == 2) {
            intent = new Intent(this, LocalVideoGridActivity.class);
            final ArrayList<String> arrayListtime = new ArrayList<String>();
            Log.d("LocalPictureAndVideoAct", "arrayListtime.size():" + videotimes.size());
            if (videotimes.size() > 0) {
                for (int i = 0; i < videotimes.size(); i++) {
                    String mess = videotimes.get(i).substring(0, 10);
                    if (mess.equals(date)) {
                        arrayListtime.add(videotimes.get(i));
                    }
                }
            }
            intent.putExtra("videotime", arrayListtime);
        }

        intent.putExtra("did", strDID);
        intent.putExtra("list", items.get(position).paths);
        intent.putExtra("date", date);
        intent.putExtra(ContentCommon.STR_CAMERA_NAME, cameraName);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // groupList.clear();
        // groupList = null;
        // childMap.clear();
        // childMap = null;
    }
}