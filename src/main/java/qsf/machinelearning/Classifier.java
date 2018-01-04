package qsf.machinelearning;

import qsf.entity.FeatureVector;

import java.io.IOException;
import java.util.List;

/**
 * @author Qian Shaofeng
 * created on 2018/1/3.
 */
public interface Classifier {

    public boolean train(FeatureVector[] data) throws IOException;

    public int[] predict(FeatureVector[] featureVector);

    public boolean hasTrain(String uid);

}
