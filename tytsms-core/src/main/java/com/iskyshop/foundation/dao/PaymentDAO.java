package com.iskyshop.foundation.dao;
import org.springframework.stereotype.Repository;
import com.iskyshop.core.base.GenericDAO;
import com.iskyshop.foundation.domain.Payment;
@Repository("paymentDAO")
public class PaymentDAO extends GenericDAO<Payment> {

}