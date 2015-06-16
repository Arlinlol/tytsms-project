package com.iskyshop.manage.buyer.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.iskyshop.core.annotation.SecurityMapping;
import com.iskyshop.core.mv.JModelAndView;
import com.iskyshop.core.security.support.SecurityUserHolder;
import com.iskyshop.core.tools.CommUtil;
import com.iskyshop.core.tools.DateUtils;
import com.iskyshop.core.tools.StringUtils;
import com.iskyshop.foundation.domain.Award;
import com.iskyshop.foundation.domain.IntegralLog;
import com.iskyshop.foundation.domain.Share;
import com.iskyshop.foundation.domain.SysConfig;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.foundation.service.IAwardService;
import com.iskyshop.foundation.service.IIntegralLogService;
import com.iskyshop.foundation.service.IShareService;
import com.iskyshop.foundation.service.ISysConfigService;
import com.iskyshop.foundation.service.IUserConfigService;
import com.iskyshop.foundation.service.IUserService;
import com.taiyitao.core.util.Token;

/**
 * 
 * 功能描述：
 * <p>
 * 版权所有：广州泰易淘网络科技有限公司
 * <p>
 * 未经本公司许可，不得以任何方式复制或使用本程序任何部分
 * 
 * @author cty 新增日期：2015年2月12日
 * @author cty 修改日期：2015年2月12日
 *
 */
@Controller
public class AwardViewAction {
	
	private static Log log = LogFactory.getLog(AwardViewAction.class);

	@Autowired
	private ISysConfigService configService;
	@Autowired
	private IUserConfigService userConfigService;
	@Autowired
	private IAwardService awardService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IIntegralLogService integralLogService;
	@Autowired
	private IShareService shareService;
	
	private static final String AWARD_COUNT = "award_count";
	private static final String SHARE_INTEGRAL = "share_integral";
	private static final String SHARE_LIST = "share_list";
	
	//private static final Object lock = new Object();
	
	

	/**
	 * 跳转到抽奖页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SecurityMapping(title = "买家中心", value = "/buyer/adraw.htm*", rtype = "buyer", rname = "买家中心", rcode = "user_center", rgroup = "用户中心")
	@RequestMapping("/buyer/adraw.htm")
	public ModelAndView user_login_success(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new JModelAndView(
				"user/default/usercenter/buyer_adraw.html",
				configService.getSysConfig(),
				this.userConfigService.getUserConfig(), 0, request, response);
		mv.addObject("Token",Token.getInstance().getTokenString(request.getSession()));
		return mv;
	}
	
	public List<Award> awardList(){
		Map map = new HashMap();
		map.put("addTime", CommUtil.formatDate(DateUtils.getString(new Date())+ " 00:00:00"));
		List<Long> prizeList = new ArrayList();
		prizeList.add(2L);
		prizeList.add(9L);
		prizeList.add(11L);
		map.put("award_number", prizeList);
		List<Award> awards = this.awardService.query(
				"select obj from Award obj where obj.addTime>=:addTime and obj.award_number not in(:award_number) order by obj.addTime desc", map, -1, -1);
		return awards;
	}
	
	/**
	 * ajax获取中奖名单
	 * @param request
	 * @param response
	 */
	@RequestMapping("/ajax_drawInfo.htm")
	private void ajaxAwardInfo(HttpServletRequest request,
			HttpServletResponse response){
		List<Award> awards = awardList();
		List<Map<String,Object>> list = new ArrayList();
		for (Award a : awards) {
			Map<String,Object> map = new HashMap();
			map.put("userName",  CommUtil.substring(a.getUser().getUserName(), 5));
			map.put("awardName", a.getAward_name());
			list.add(map);
		}
		JSONArray json = new JSONArray().fromObject(list);
		response.setContentType("text/html;charset=UTF-8");
		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
		}
	}
	
	/**
	 * ajax获取用户中奖记录信息
	 * @param request
	 * @param response
	 */
	@RequestMapping("/ajax_WinningRecordInfo.htm")
	public void ajaxWinningRecordInfo(HttpServletRequest request,
			HttpServletResponse response) {
		Map params = new HashMap();
		params.put("user_id", SecurityUserHolder.getCurrentUser().getId());List<Long> prizeList = new ArrayList();
		prizeList.add(2L);
		prizeList.add(9L);
		prizeList.add(11L);
		params.put("award_number", prizeList);
		List<Award> awards = this.awardService.query(
				"select obj from Award obj where obj.user.id =:user_id and obj.award_number not in(:award_number) order by obj.addTime desc", params, 0, 10);
		List<Map<String,Object>> list = new ArrayList();
		for (Award a : awards) {
			Map<String,Object> map = new HashMap();
			map.put("userName", CommUtil.substring(a.getUser().getUserName(), 5));
			map.put("awardName", a.getAward_name());
			map.put("addTime", CommUtil.formatShortDate(a.getAddTime()));
			list.add(map);
		}
		JSONArray json = new JSONArray().fromObject(list);
		response.setContentType("text/html;charset=UTF-8");
		try {
			response.getWriter().write(json.toString());
		} catch (IOException e) {
		}
	}
	
	/**
	 * 用户分享抽奖活动获取积分
	 * @param request
	 * @param response
	 * @param share
	 */
	@RequestMapping("/shareInfo.htm")
	public void addShareIntegral(HttpServletRequest request,
			HttpServletResponse response,String shareCode){
		User user = SecurityUserHolder.getCurrentUser();
		SysConfig config = this.configService.getSysConfig();
		boolean ischecked = false;
        if(user != null){
			HttpSession session = request.getSession();
			AtomicInteger share_integral = (AtomicInteger) session.getAttribute(SHARE_INTEGRAL);
			List share_list = (List) session.getAttribute(SHARE_LIST);
			if(share_integral == null){
				synchronized (AwardViewAction.class) {
					share_integral = (AtomicInteger) session.getAttribute(SHARE_INTEGRAL);
					if(share_integral == null){
						Map maps = new HashMap();
						maps.put("user_id", SecurityUserHolder.getCurrentUser().getId());
						maps.put("share_type", 1L);
						List<Share> shareList = this.shareService.query("select obj from Share obj where obj.user.id=:user_id and obj.share_type:=share_type", maps, -1, -1);
						share_integral = new AtomicInteger(shareList.size());
						session.setAttribute(SHARE_INTEGRAL,share_integral);
						//分享前5个平台有积分送
						if(share_integral.intValue() <5){
							List<String> list = new ArrayList();
							 for (Share s : shareList) {
								 list.add(s.getShare_code());
							 }
							 session.setAttribute(SHARE_LIST,list);
							 if(!list.contains(shareCode)){
								 share_list.add(shareCode);
								 share_integral.incrementAndGet();
								 ischecked = true;
							 }
						}
					}else{
						//分享前5个平台有积分送
						if(share_integral.intValue() <5){
							 if(!share_list.contains(shareCode)){
								 share_list.add(shareCode);
								 share_integral.incrementAndGet();
								 ischecked = true;
							 }
						}
					}
				}
			}else{
				synchronized(AwardViewAction.class){
					//分享前5个平台有积分送
					if(share_integral.intValue() <5){
						 if(!share_list.contains(shareCode)){
							 share_list.add(shareCode);
							 share_integral.incrementAndGet();
							 ischecked = true;
						 }
					}
				}
			}
			if(ischecked){
		    	//根据用户ID查询用户积分
				User users = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
				//用户分享信息记录
				Share shares = new Share();
				shares.setAddTime(new Date());
				shares.setDeleteStatus(0);
				shares.setShare_code(shareCode);
				shares.setShare_title("抽奖活动");
				shares.setShare_type(1L);
				shares.setUser(SecurityUserHolder.getCurrentUser());
				this.shareService.save(shares);
				//增加用户积分
				users.setIntegral(users.getIntegral()+config.getShareRegister());
				this.userService.update(users);
				//积分日志记录
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("用户分享增加"+config.getShareRegister()+"积分");
				log.setIntegral(config.getShareRegister());
				log.setIntegral_user(user);
				log.setType("share");
				this.integralLogService.save(log);
		    }
        }
	}
	
	/**
	 * 抽奖
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping("/index_draw.htm")
	@Transactional
	public void drawview(HttpServletRequest request,
			HttpServletResponse response) {
		User user = SecurityUserHolder.getCurrentUser();
		SysConfig config = this.configService.getSysConfig();
		List<String> prizeList = new ArrayList();
		prizeList.add("2");
		prizeList.add("9");
		prizeList.add("11");
		String webPath = CommUtil.getURL(request);
		int num = 0;
		Map<String,Object> params = new HashMap<String, Object>();
		boolean ischecked = false;
		//防止重复请求
		if (!Token.isTokenStringValid(
				request.getParameter(Token.TOKEN_STRING_NAME),
				request.getSession())) {
			num = 0;
		}else{
			HttpSession session = request.getSession();
			//判断用户是否已登录
			if(user == null){ 
				num = 1;
			}else{
				AtomicInteger awardCount = (AtomicInteger) session.getAttribute(AWARD_COUNT);
				if(awardCount == null) { //处理高并发的清楚
					synchronized(AwardViewAction.class){ //如500个并发
						awardCount = (AtomicInteger) session.getAttribute(AWARD_COUNT);
						if(awardCount == null) {
							//访问数据库
							Map map = new HashMap();
							map.put("user_id", SecurityUserHolder.getCurrentUser().getId());
							map.put("addTime", CommUtil.formatDate(DateUtils.getString(new Date())+ " 00:00:00"));
							List<Award> awards = this.awardService.query(
									"select obj from Award obj where obj.user.id=:user_id and obj.addTime >=:addTime ", map, -1, -1);
							awardCount = new AtomicInteger(awards.size());
							request.getSession().setAttribute(AWARD_COUNT,awardCount);
							//判断用户抽奖次数
							if(awardCount.intValue() >= 30){
							    	num = 3;//退出抽奖
							}else{
								//用户抽奖次数增加
								awardCount.incrementAndGet();
								ischecked = true;
							}
						} else{ 
							if(awardCount.intValue() >= 30){
								num = 3;//退出抽奖
							} else {
								//用户抽奖次数增加
								awardCount.incrementAndGet();
								ischecked = true;
							}
						}
					}
				} else {//正常流程
					synchronized(AwardViewAction.class){
						if(awardCount.intValue() >= 30){
							num = 3;//退出抽奖
						} else {
							//用户抽奖次数增加
							awardCount.incrementAndGet();
							ischecked = true;
						}
					}
				}
			}
		}
		if(ischecked){
			params = getUserAwardInfo(config);
		}
		String token = Token.getInstance().getTokenString(request.getSession());
		Object[] result = null;
		if(num == 0){
			result = (Object[]) params.get("result");
			num = (int) params.get("num");
		}
		response.setContentType("text/html;charset=UTF-8");
		try {
			String strHtml = "";
			if(num == 0){//重复提交
				strHtml +="<div class='tip_box'> <div class='tip_box_h'><span class='tip_box_img'><img src='"+webPath+"/resources/style/system/front/default/images/usercenter/lo_icon.png'></span>";
				strHtml +="<span class='tip_box_txt'>请不要重复请求!</span></div> </div>";
				response.getWriter().write("{\"num\":\""+num+"\",\"msg\":\""+strHtml+"\"}");
			}else if(num == 1){ //用户没有登录
				strHtml +="<div class='tip_box'> <div class='tip_box_h'><span class='tip_box_img'><img src='"+webPath+"/resources/style/system/front/default/images/usercenter/award_tip1.jpg'></span>";
				strHtml +="<span class='tip_box_txt'>亲,您还没登录,赶紧登录拿大奖!</span></div> </div>";
				response.getWriter().write("{\"num\":\""+num+"\",\"msg\":\""+strHtml+"\",\"token\":\""+token+"\"}");
			}else if(num == 2){ //用户不满足积分
				strHtml +="<div class='tip_box'> <div class='tip_box_h'><span class='tip_box_img'><img src='"+webPath+"/resources/style/system/front/default/images/usercenter/award_tip2.jpg'></span>";
				strHtml +="<span class='tip_box_txt'>亲,积分已用完,赶快点分享和邀请赚积分吧!</span></div> </div> ";
				response.getWriter().write("{\"num\":\""+num+"\",\"msg\":\""+strHtml+"\",\"token\":\""+token+"\"}");
			}else if(num == 3){ //用户当天抽奖次数已满
				strHtml +="<div class='tip_box'> <div class='tip_box_h'><span class='tip_box_img'><img src='"+webPath+"/resources/style/system/front/default/images/usercenter/award_tip3.jpg'></span>";
				strHtml +="<span class='tip_box_txt'>亲,您今天的抽奖次数已用完，明天再来吧!</span></div> </div>";
				response.getWriter().write("{\"num\":\""+num+"\",\"msg\":\""+strHtml+"\",\"token\":\""+token+"\"}");
			}else if(num == 4){ //已抽奖
				if(!prizeList.contains(result[1].toString())){
					strHtml +="<div class='tip_box'> <div class='tip_box_h'><span class='tip_box_img'><img src='"+webPath+"/resources/style/system/front/default/images/usercenter/award_tip4.jpg'></span>";
					strHtml +="<span class='tip_box_txt'>恭喜你中奖了,获得"+result[2]+",加油呀！离大奖不远了!</span></div> </div> ";
				}else{
					strHtml +="<div class='tip_box'> <div class='tip_box_h'><span class='tip_box_img'><img src='"+webPath+"/resources/style/system/front/default/images/usercenter/award_tip5.jpg'></span>";
					strHtml +="<span class='tip_box_txt'>亲，再接再励哦，多转几次机会更容易中奖!</span></div> </div> ";
				}
				response.getWriter().write("{\"angle\":\""+result[0]+"\",\"msg\":\""+strHtml+"\",\"token\":\""+token+"\"}");
				System.out.println("转动角度:"+result[0]+"\t奖项ID:"+result[1]+"\t提示信息:"+result[2]);
			}
		} catch (IOException e) {
			log.info(e);
		}
	}
	
	/**
	 * 抽奖逻辑
	 * @param config
	 * @return
	 */
    private Map<String,Object> getUserAwardInfo(SysConfig config){
    	Map<String,Object> params = new HashMap();
    	int num = 0;
    	Object[] result = null;
    	Object[][] prizeArr = new  Object[][]{
			//id,min,max，prize【奖项】,v【中奖率】
			//外面的转盘转动
			{1,330,359,StringUtils.isEmpty(config.getAwardOne())?"10000积分":config.getAwardOne()+"积分",1},  //1  10000
			{2,270,299,StringUtils.isEmpty(config.getAwardEight())?"50积分":config.getAwardEight()+"积分",4},//3 50 
			{3,240,269,StringUtils.isEmpty(config.getAwardTen())?"再接再厉":config.getAwardTen(),26}, //4 再接再厉 
			{4,210,239,StringUtils.isEmpty(config.getAwardFour())?"1000积分":config.getAwardFour()+"积分",1}, //5 1000 
			{5,30,59,StringUtils.isEmpty(config.getAwardFive())?"500积分":config.getAwardFive()+"积分",1},//11 500
			{6,300,329,StringUtils.isEmpty(config.getAwardSix())?"300积分":config.getAwardSix()+"积分",2},  //2 300 
			{7,150,179,StringUtils.isEmpty(config.getAwardTwo())?"5000积分":config.getAwardTwo()+"积分",1}, //7 5000
			{8,180,209,StringUtils.isEmpty(config.getAwardNine())?"10积分":config.getAwardNine()+"积分",8},//6 10  
			{9,90,119,StringUtils.isEmpty(config.getAwardSeven())?"100积分":config.getAwardSeven()+"积分",3},//9 100 
			{10,120,149,StringUtils.isEmpty(config.getAwardTen())?"再接再厉":config.getAwardTen(),26},//8   再接再厉
			{11,60,89,StringUtils.isEmpty(config.getAwardThree())?"2000积分":config.getAwardThree()+"积分",1},//10 2000
			{12,0,29,StringUtils.isEmpty(config.getAwardTen())?"再接再厉":config.getAwardTen(),26}//12 再接再厉
		};
    	//根据用户ID查询用户积分 
		for (int i = 0; i < 5; i++) {
			User users = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			if(users.getIntegral() < config.getLuckDraw()){ //用户积分是否满足
				num = 2;
			}else{
				result = award(prizeArr);//抽奖后返回角度和奖品等级
				if("0".equals(result[1].toString()) || "3".equals(result[1].toString()) || 
						"6".equals(result[1].toString()) || "10".equals(result[1].toString())){
					continue;
				}
				//用户积分减少
				users.setIntegral(users.getIntegral()-config.getLuckDraw());
				boolean fals = this.userService.update(users);
			    if(!fals){
			    	continue;
			    }
				//积分日志记录
				IntegralLog log = new IntegralLog();
				log.setAddTime(new Date());
				log.setContent("用户抽奖减少"+config.getLuckDraw()+"积分");
				log.setIntegral(-config.getLuckDraw());
				log.setIntegral_user(users);
				log.setType("award");
				this.integralLogService.save(log);
				
				//记录抽奖信息
				Long aintegral = new Long(result[1].toString());
				Award aw = new Award(); 
				aw.setAward_integral(config.getLuckDraw());
				aw.setAward_name(result[2].toString());
				aw.setAward_type(0L);
				aw.setAward_number(aintegral);
				aw.setAddTime(new Date());
				aw.setDeleteStatus(0);
				aw.setUser(SecurityUserHolder.getCurrentUser());
				this.awardService.save(aw);
				//查看用户是否中奖
				addUserIntegral(aintegral);
				num = 4;
				break;
			}
		}
		params.put("num", num);
		params.put("result", result);
		return params;
    }
	
	//抽奖并返回角度和奖项
	public Object[] award(Object[][] prizeArr){
		//概率数组
		Integer obj[] = new Integer[prizeArr.length];
		for(int i=0;i<prizeArr.length;i++){
			obj[i] = (Integer) prizeArr[i][4];
		}
		Integer prizeId = getRand(obj); //根据概率获取奖项id
		//旋转角度
		int angle = new Random().nextInt((Integer)prizeArr[prizeId][2]-(Integer)prizeArr[prizeId][1])+(Integer)prizeArr[prizeId][1];
		String msg = (String) prizeArr[prizeId][3];//提示信息
		return new Object[]{angle,prizeId,msg};
	}
	
	//根据概率获取奖项
	public Integer getRand(Integer obj[]){
		Integer result = null;
		try {
			int  sum = 0;//概率数组的总概率精度 
			for(int i=0;i<obj.length;i++){
				sum+=obj[i];
			}
			for(int i=0;i<obj.length;i++){//概率数组循环 
				int randomNum = new Random().nextInt(sum);//随机生成1到sum的整数
				if(randomNum<obj[i]){//中奖
					result = i;
					break;
				}else{
					sum -=obj[i];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 查看用户是否中奖
	 * @param aintegral
	 */
	private void addUserIntegral(Long aintegral){
		List<Long> prizeList = new ArrayList();
		prizeList.add(2L);
		prizeList.add(9L);
		prizeList.add(11L);
		//判断用户是否抽中奖
		if(!prizeList.contains(aintegral)){
			//根据用户ID查询用户积分
			User u = this.userService.getObjById(SecurityUserHolder.getCurrentUser().getId());
			//用户抽奖增加积分
			int awardIntegral = 0;
			switch (aintegral.intValue()) {
				case 0:
					awardIntegral = 10000;
					break;
				case 1:
					awardIntegral = 50;
					break;
				case 3:
					awardIntegral = 1000;
					break;
				case 4:
					awardIntegral = 500;
					break;
				case 5:
					awardIntegral = 300;
					break;
				case 6:
					awardIntegral = 5000;
					break;
				case 7:
					awardIntegral = 10;
					break;
				case 8:
					awardIntegral = 100;
					break;
				case 10:
					awardIntegral = 2000;
					break;
				default:
					awardIntegral = 0;
					break;
			}
			u.setIntegral(u.getIntegral()+awardIntegral);
			this.userService.update(u);

			//积分日志记录
			IntegralLog logs = new IntegralLog();
			logs.setAddTime(new Date());
			logs.setContent("用户抽奖增加"+awardIntegral+"积分");
			logs.setIntegral(awardIntegral);
			logs.setIntegral_user(SecurityUserHolder.getCurrentUser());
			logs.setType("award");
			this.integralLogService.save(logs);
		}
	}
}
