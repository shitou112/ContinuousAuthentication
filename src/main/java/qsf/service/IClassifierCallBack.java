package qsf.service;

import qsf.entity.UserTouch;

/**
 * @author Qian Shaofeng
 * created on 2018/1/3.
 */
public interface IClassifierCallBack {
    void callback(UserTouch[] userTouches);
}
