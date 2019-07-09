package com.ff.eventbus.manager;

import com.ff.eventbus.Person;
import com.ff.hermes.annotion.ClassId;

/**
 * description:
 * author: FF
 * time: 2019-07-07 10:11
 */
@ClassId("com.ff.eventbus.manager.UserManager")
public interface IUserManager {

    Person getPerson();

    void setPerson(Person person);
}
