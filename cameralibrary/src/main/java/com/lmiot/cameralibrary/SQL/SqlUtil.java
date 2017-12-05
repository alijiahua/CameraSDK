package com.lmiot.cameralibrary.SQL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.lmiot.cameralibrary.Util.DataUtil;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;


/**
 * 创建日期：2017-11-28 16:50
 * 作者:Mr Li
 * 描述:设备列表操作
 */
public class SqlUtil {
    private static final SqlUtil ourInstance = new SqlUtil();
    private CamerBeanDao mCamerBeanDao;


    public static SqlUtil getInstance() {

        return ourInstance;
    }

    private SqlUtil() {
    }

    /*初始化数据库相关*/
    public void initDbHelp(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "CameraRecluse-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        mCamerBeanDao = daoSession.getCamerBeanDao();
    }

    /**
     * 增加
     */
    public void add(CamerBean camerBean) {

        CamerBean search = search(camerBean.getCameraID());
        if(search!=null){
            mCamerBeanDao.update(search);
        }
        else{
            mCamerBeanDao.insert(camerBean);
        }

    }

    /**
     * 更新
     * @param camerBean
     */
    public void update(CamerBean camerBean) {
        mCamerBeanDao.update(camerBean);


    }


    /**
     * 根据ID删除
     */

    public void del(String cameraID) {
            QueryBuilder qb = mCamerBeanDao.queryBuilder();
            ArrayList<CamerBean> list = (ArrayList<CamerBean>) qb
                    .where(CamerBeanDao.Properties.UserName.eq(DataUtil.getUerName()))
                    .where(CamerBeanDao.Properties.CameraID.eq(cameraID))
                    .list();
            if (list.size() >0) {
                mCamerBeanDao.delete(list.get(0));
            }


    }

    /**
     * 根据ID查找
     */
    public CamerBean search(String cameraID) {
            QueryBuilder qb = mCamerBeanDao.queryBuilder();
            ArrayList<CamerBean> list = (ArrayList<CamerBean>) qb
                    .where(CamerBeanDao.Properties.UserName.eq(DataUtil.getUerName()))
                    .where(CamerBeanDao.Properties.CameraID.eq(cameraID)).list();
            if (list.size() > 0) {
               return list.get(0);
            } else {
                return null;
            }
    }

    /**
     * 查询所有并根据ID升序
     */
    public List<CamerBean> searchAll() {
        List<CamerBean> list = mCamerBeanDao.queryBuilder()
                .where(CamerBeanDao.Properties.UserName.eq(DataUtil.getUerName()))
                .build()
                .list();
       return  list;

    }


}
