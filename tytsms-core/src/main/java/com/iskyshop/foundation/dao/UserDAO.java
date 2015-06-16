package com.iskyshop.foundation.dao;

import org.springframework.stereotype.Repository;

import com.iskyshop.core.base.GenericDAO;
import com.iskyshop.foundation.domain.User;

@Repository("userDAO")
public class UserDAO extends GenericDAO<User> {

}
