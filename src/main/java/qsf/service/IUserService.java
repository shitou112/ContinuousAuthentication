package qsf.service;

import qsf.entity.User;

/**
 * @author Qian Shaofeng
 * created on 2018/1/3.
 */
public interface IUserService {

    int deleteByPrimaryKey(String userid);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String userid);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}
