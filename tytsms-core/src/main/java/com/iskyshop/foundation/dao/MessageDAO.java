package com.iskyshop.foundation.dao;
import org.springframework.stereotype.Repository;
import com.iskyshop.core.base.GenericDAO;
import com.iskyshop.foundation.domain.Message;
@Repository("messageDAO")
public class MessageDAO extends GenericDAO<Message> {

}