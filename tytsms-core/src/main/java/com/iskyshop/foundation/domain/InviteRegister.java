package com.iskyshop.foundation.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

/**
 * 注册邀请实体
 * @author DevinYang
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "inviteregister")
public class InviteRegister extends IdEntity{
	
	private static final long serialVersionUID = 1L;
	@Column(name="inviter_id",nullable=true)
	private Long inviterId; //邀请人id
	@Column(name="inviter_name")
	private String inviterName;//邀请人姓名
	@Column(name="invitee_id",nullable=true)
	private Long inviteeId;//被邀请人id
	@Column(name="invitee_name")
	private String inviteeName;//被邀请人姓名
	@Column(name="invite_date")
	private String inviteDate; //邀请时间
	@Column(name="invite_rule")
	private String inviteRule;//邀请规则
	
	
	public Long getInviterId() {
		return inviterId;
	}
	public void setInviterId(Long inviterId) {
		this.inviterId = inviterId;
	}
	public String getInviterName() {
		return inviterName;
	}
	public void setInviterName(String inviterName) {
		this.inviterName = inviterName;
	}
	public Long getInviteeId() {
		return inviteeId;
	}
	public void setInviteeId(Long inviteeId) {
		this.inviteeId = inviteeId;
	}
	public String getInviteeName() {
		return inviteeName;
	}
	public void setInviteeName(String inviteeName) {
		this.inviteeName = inviteeName;
	}
	public String getInviteDate() {
		return inviteDate;
	}
	public void setInviteDate(String inviteDate) {
		this.inviteDate = inviteDate;
	}
	public String getInviteRule() {
		return inviteRule;
	}
	public void setInviteRule(String inviteRule) {
		this.inviteRule = inviteRule;
	}
	
	
	
	
	
	
	
	
	
	
	

}
