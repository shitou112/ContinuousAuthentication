package qsf.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import qsf.entity.FeatureVector;
import qsf.entity.UserTouch;
import qsf.machinelearning.Classifier;
import qsf.machinelearning.SVMClassifier;
import qsf.service.IUserClassifierService;
import qsf.service.IUserTouchService;
import qsf.utils.MathUtils;

import java.io.IOException;
import java.util.List;

/**
 * @author Qian Shaofeng
 * created on 2018/1/3.
 */
@Service
public class UserClassifierService implements IUserClassifierService {

    private static Logger logger = Logger.getLogger(UserClassifierService.class);


    @Autowired
    private IUserTouchService userTouchService;

    @Override
    public Classifier getClassifier(String uid) {
        return classifierHashMap.getOrDefault(uid, null);
    }

    @Override
    public void put(String uid, Classifier classifier) {
        classifierHashMap.put(uid, classifier);
    }


    @Override
    public int computeUserTouchsScore(List<UserTouch> userTouchs) {
        String uid = userTouchs.get(0).getUserid();
        Classifier classifier = getClassifier(userTouchs.get(0).getUserid());
        int score = -1;

        //分类器训练
        if (classifier==null) {
            train(classifier, uid);
            logger.info("===============training finish=========================");
        }

        //分类器预测
        if (classifierStateMap.getOrDefault(uid, 0) == 2) {

            classifier = classifierHashMap.getOrDefault(uid, null);
            if (classifier!=null) {
                score = predict(classifier, userTouchs) >= 0.5 ? 1 : 0;
            }
        }

        return score;
    }

    private void train(Classifier classifier, String uid){
        if (classifierStateMap.getOrDefault(uid, 0)==0 && userTouchService.countUserTouchByUid(uid) >= MIN_TRAIN_NUM) {
            try {
                classifierStateMap.put(uid, 1);

                // 获取正样本
                List<UserTouch> posUserTouchs = userTouchService.selectUid(uid, MAX_POS_NUM);
                int pos_num = posUserTouchs.size();

                // 获取负样本（负样本是正样本的1.2倍，负样本取最新的和用户uid不同的样例）
                int neg_num = (int) (pos_num * 1.2);
                List<UserTouch> negUserTouchs = userTouchService.getOtherUserTouchs(uid, neg_num);
                posUserTouchs.addAll(negUserTouchs);

                FeatureVector[] train_data = new FeatureVector[posUserTouchs.size()];
                for (int i = 0; i < posUserTouchs.size(); ++i) {
                    train_data[i] = FeatureVector.generateFV(posUserTouchs.get(i));
                    if (i < pos_num)
                        train_data[i].setLabel(1);
                }

                classifier = new SVMClassifier(25);
                classifier.train(train_data);

                classifierHashMap.put(uid, classifier);
                classifierStateMap.put(uid, 2);
            } catch (IOException e) {
                logger.error("train io exception");
            }
        }
    }

    private double predict(Classifier classifier, List<UserTouch> userTouchs){
        int nums = userTouchs.size();
        FeatureVector[] featureVectors = new FeatureVector[nums];
        for (int i = 0; i < nums; ++i)
            featureVectors[i] = FeatureVector.generateFV(userTouchs.get(i));

        int[] labels = classifier.predict(featureVectors);
        double mean_value = MathUtils.sum(labels) * 1.0 / nums;
        return mean_value;
    }

}
