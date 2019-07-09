package com.ff.eventbus.manager;

import com.ff.eventbus.Person;
import com.ff.hermes.annotion.ClassId;

/**
 * description: 服务端单例
 * author: FF
 * time: 2019-07-06 11:26
 */
@ClassId("com.ff.eventbus.manager.UserManager")
public class UserManager implements IUserManager {

    private Person mPerson;

    private static class UserManagerHolder {
        private static final UserManager INSTANCE = new UserManager();
    }

    private UserManager() {

    }

    public static UserManager getInstance() {
        return UserManagerHolder.INSTANCE;
    }

    @Override
    public Person getPerson() {
        return mPerson;
    }

    @Override
    public void setPerson(Person person) {
        mPerson = person;
    }
}
