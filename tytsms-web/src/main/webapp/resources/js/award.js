jQuery(document).ready(function(){		
  //中奖名单定时显示
  //jQuery(function(){ 
  // 	setInterval(startRequest,60000);  
  //});
    function startRequest() {  
        jQuery("#userlist").html("");
        jQuery.ajax({  
            url: webPath+"/ajax_drawInfo.htm",  
            type: 'POST',  
		    dataType : 'json',
            success: function (json) {  
                var temp = "";  
                jQuery.each(json, function (i, c) {  
                   temp += "<li><div class=\"name\">"+c.userName+"</div><div class=\"ym\">抽中了<span>"+c.awardName+"</span></div></li>";
                });  
                jQuery("#userlist").append(temp);  
            }  
        });  
	 } 
  	 startRequest();
  	 //关闭中奖信息
  	jQuery(".adraw_record_close").click(function(){
		jQuery('#adraw_mask').css({display: "none", height: jQuery(document).height()});
  	    jQuery('.adraw_record').css("display","none");
  	    jQuery(".recordlist").html("");
  	    return false;
  	});
	 //关闭提示
	 jQuery(".adraw_dialog_close").click(function(){
		jQuery('#adraw_mask').css({display: "none", height: jQuery(document).height()});
        jQuery('.adraw_dialog').css("display","none");
        jQuery(".adraw_dialog_content").html("");
        return false;
     });
		
	 //点击分享按钮，显示框
	 jQuery(".share_box_btn").click(function(){
	       var isl = jQuery("#islgin").val();	       
	       if(isl){ 
	           jQuery('.share_box').toggle();
	           return false;
	 	   }else{  
	 	       var url = webPath+"/user_dialog_login.htm";
	     	   dialogLogin(url);
	 	   }
	  });
	  //点击邀请按钮，跳转到邀请页面
	  jQuery(".sp_right_register").click(function(){
	      var isl = jQuery("#islgin").val();	       
	       if(isl){ 
	           this.href= webPath+"/buyer/invite_register.htm";
               this.target = "_blank"; 
	 	   }else{  
	 	       var url = webPath+"/user_dialog_login.htm";
	     	   dialogLogin(url);
	 	   }
	  });
	 //
	 jQuery("#adraw_info_btn").click(function(){
		 var isl = jQuery("#islgin").val();	       
         if(isl){ 
	        jQuery('.adraw_record').css("display","block");
		    jQuery('#adraw_mask').css({display: "block", height: jQuery(document).height()});
	        jQuery.ajax({  
	            url: webPath+"/ajax_WinningRecordInfo.htm",  
	            type: 'POST',  
			    dataType : 'json',
	            success: function (json) {  
	                var temp = "";  
	                jQuery.each(json, function (i, c) {  
	                   temp += "<li><div class=\"recordlist_name\">"+c.userName+"</div><div class=\"recordlist_gift\">"+c.awardName+"</div><div class=\"recordlist_time\">"+c.addTime+"</div></li>";
	                });  
	                jQuery(".recordlist").append(temp);  
	            }  
            });  
 	     }else{  
 	        var url = webPath+"/user_dialog_login.htm";
     	    dialogLogin(url);
 	    }
     });
     
	 jQuery('#adraw_inner').click(function (){
     	var isl = jQuery("#islgin").val();
 	    if(isl){ 
 	       lottery();
 	    }else{  
 	       var url = webPath+"/user_dialog_login.htm";
     	   dialogLogin(url);
 	    }
	});

	var lock = true;
	function lottery() {
		var token = jQuery("#token").val();
		if(lock){
			lock = false;
			jQuery.ajax({
			type : 'POST',
				url : webPath+'/index_draw.htm?token='+token,
				dataType : 'json',
				cache : false,
				error : function() {
					jQuery('#adraw_mask').css({display: "block", height: jQuery(document).height()});
					jQuery(".adraw_dialog").css("display","block");
					var strHtml ="<div class='tip_box'> <div class='tip_box_h'><span class='tip_box_img'><img src='"+webPath+"/resources/style/system/front/default/images/usercenter/lo_icon.png'></span>";
					strHtml +="<span class='tip_box_txt'>网络超时，请检查您的网络设置！</span></div> </div>";
					jQuery(".adraw_dialog_content").append(strHtml);
					lock = true;
			 		return false;
				},
				success : function(json) {
				    var msg = json.msg; //提示信息
					var num = json.num; //识别逻辑判断
					if(num == 0){
				      jQuery('#adraw_mask').css({display: "block", height: jQuery(document).height()});
				      jQuery(".adraw_dialog").css("display","block");
				      jQuery(".adraw_dialog_content").append(msg);
				      location.reload();
				      return false;
				    }
					var tokens = json.token;
					jQuery("#token").val(tokens);
				    if(num == 1){
				      jQuery('#adraw_mask').css({display: "block", height: jQuery(document).height()});
				      jQuery(".adraw_dialog").css("display","block");
				      jQuery(".adraw_dialog_content").append(msg);
				      lock = true;
				      return false;
				    }
				    if(num == 2){
				    	 jQuery('#adraw_mask').css({display: "block", height: jQuery(document).height()});
				      jQuery(".adraw_dialog").css("display","block");
				      jQuery(".adraw_dialog_content").append(msg);
				      lock = true;
				      return false;
				    }
				    if(num == 3){
				      jQuery('#adraw_mask').css({display: "block", height: jQuery(document).height()});
				      jQuery(".adraw_dialog").css("display","block");
				      jQuery(".adraw_dialog_content").append(msg);
				      lock = true;
				      return false;
				    }
				    var angle = parseInt(json.angle); //角度 
					jQuery("#imgs").rotate({ //inner内部指针转动，outer外部转盘转动
				       duration : 8000, //转动时间 
				       angle : 0, //开始角度 
				       animateTo : 2160 + angle, //转动角度 
				       easing : jQuery.easing.easeOutSine, //动画扩展 
				       callback : function() {
				    	  jQuery('#adraw_mask').css({display: "block", height: jQuery(document).height()});
				          jQuery(".adraw_dialog").css("display","block");
				          jQuery(".adraw_dialog_content").append(msg)
					      lock = true;
				          return false;
				      }
					});
			    }
			});
		}
	}
	function geturl(cmd) {
	 	jQuery.ajax({  
	        url: webPath+"/shareInfo.htm",
	        data:"shareCode="+cmd,
	        type: 'POST',  
		    dataType : 'json',
	        success: function (json) {  
	        }  
	    });
	}
	window._bd_share_config={
		"common":{
	       "bdText":"泰易淘试运营抽奖活动",
	       "bdUrl":webPath+"/taiyitao/buyer/adraw.htm",
	       "bdMini":"2",
	       "bdMiniList":false,
	       "bdPic":"",
	       "bdStyle":"0",
	       "bdSize":"16",
	       "onAfterClick":geturl
	    },
	    "share":{},
   };
	with(document)0[(getElementsByTagName('head')[0]||body).
	appendChild(createElement('script')).src='http://bdimg.share.baidu.com/static/api/js/share.js?v=89860593.js?cdnversion='+~(-new Date()/36e5)];
});