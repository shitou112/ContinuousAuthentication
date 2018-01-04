package qsf.service;

import qsf.entity.FeatureVector;
import qsf.entity.UserTouch;
import qsf.machinelearning.Classifier;

import java.util.HashMap;
import java.util.List;

/**
 * @author Qian Shaofeng
 * created on 2018/1/4.
 */
public interface IUserClassifierService {

    HashMap<String, Classifier> classifierHashMap = new HashMap<String, Classifier>();

    //一共有三种状态：0表示未训练，1表示正在训练，2表示训练完成
    HashMap<String, Integer> classifierStateMap = new HashMap<>();

    int MIN_TRAIN_NUM = 2;

    int MAX_POS_NUM = 2000;

    public Classifier getClassifier(String uid);

    public void put(String uid, Classifier classifier);

    public int computeUserTouchsScore(List<UserTouch> userTouches);
}
