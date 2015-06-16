/**
 * 处理全选或全部反选
 */
function selectAll(obj) {
	var status = jQuery(obj).attr("checked");
	var id = jQuery(obj).attr("id");
	if (status == "checked") {
		jQuery("#ListForm").find(":checkbox[id!=" + id + "]").attr("checked",
				"checked");
	} else {
		jQuery("#ListForm").find(":checkbox[id!=" + id + "]").attr("checked",
				false);
	}
}
/*
 * 系统通用方法，根据参数来决定处理的url和参数
 */
function cmd() {
	var url = arguments[0];
	var mulitId = "";
	jQuery("#ListForm").find(":checkbox:checked").each(function() {
		if (jQuery(this).val() != "") {
			mulitId += jQuery(this).val() + ",";
		}
	});
	if (mulitId != "") {
		jQuery("#ListForm #mulitId").val(mulitId);
		if (confirm("确定要执行该操作？")) {
			jQuery("#ListForm").attr("action", url);
			jQuery("#ListForm").submit();
		}
	} else {
		alert("至少选择一条数据记录");
	}
}
/**
 * / /* 火狐下取本地全路径
 */
function getFullPath(obj) {
	if (obj) {
		// ie
		if (window.navigator.userAgent.indexOf("MSIE") >= 1) {
			obj.select();
			if (window.navigator.userAgent.indexOf("MSIE") == 25) {
				obj.blur();
			}
			return document.selection.createRange().text;
		} else if (window.navigator.userAgent.indexOf("Firefox") >= 1) { // firefox
			if (obj.files) {
				return window.URL.createObjectURL(obj.files.item(0));
			}
			return obj.value;
		}
		return obj.value;
	}
}
// 自动生成查询条件
function query() {
	jQuery("#queryCondition").empty();
	jQuery.each(jQuery("#queryForm :input"), function() {
		if (this.type != "button" && this.value != "") {
			jQuery("#queryCondition").append(
					"<input name='q_" + this.name + "'type='hidden' id='q_"
							+ this.name + "' value='" + this.value + "' />");
		}
	});
	jQuery("#ListForm").submit();
}
// 表单方式分页
function gotoPage(n) {
	jQuery("#currentPage").val(n);
	jQuery("#ListForm").submit();
}
/** 增加系统提示 */
function tipStyle() {
	if (jQuery.isFunction(jQuery().poshytip)) {
		jQuery("input[title!='']").poshytip({
			className : 'tip-skyblue',
			timeOnScreen : 2000,
			alignTo : 'cursor',
			alignX : 'right',
			alignY : 'bottom',
			offsetX : 3,
			offsetY : 3
		});
		jQuery("img[title!='']").poshytip({
			className : 'tip-skyblue',
			timeOnScreen : 2000,
			alignTo : 'cursor',
			alignX : 'right',
			alignY : 'bottom',
			offsetX : 3,
			offsetY : 3
		});
		jQuery("a[title!='']").poshytip({
			className : 'tip-skyblue',
			timeOnScreen : 2000,
			alignTo : 'cursor',
			alignX : 'right',
			alignY : 'bottom',
			offsetX : 3,
			offsetY : 3
		});
		jQuery("textarea[title!='']").poshytip({
			className : 'tip-skyblue',
			timeOnScreen : 2000,
			alignTo : 'cursor',
			alignX : 'right',
			alignY : 'bottom',
			offsetX : 3,
			offsetY : 3
		});
	}
}
// 模拟alert
var alert_timer_id;
function showDialog() {
	var id = arguments[0];// 窗口id
	var title = arguments[1]; // 窗口标题
	var content = arguments[2];// 提示内容
	var type = arguments[3];// 0为提示框，1为确认框,2为发布框,3 aler提示框
	var icon = arguments[4];// 显示图标，包括warning,succeed,question,smile,sad,error
	var second = arguments[5];// 倒计时时间数
	var confirm_action = arguments[6];// callback方法

	if (id == undefined || id == "") {
		id = 1;
	}
	if (title == undefined || title == "") {
		title = "系统提示";
	}
	if (type == undefined || type == "") {
		type == 0;
	}
	if (icon == undefined || icon == "") {
		icon = "succeed";
	}
	if (second == undefined || second == "") {
		second = 60;
	}
	var s = "<div id='"
			+ id
			+ "'><div class='message_white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'></a><div><div class='message_white_iframe'><div class='message_white_title'><span>"
			+ title
			+ "</span></div><div class='message_white_box'><span class='message_white_img_"
			+ icon
			+ "'></span><span class='message_white_font'>"
			+ content
			+ "</span></div><h3 class='message_white_title_bottom'><span id='time_down'>"
			+ second
			+ "</span>秒后窗口关闭</h3></div></div></div><div class='black_overlay'></div>";

	var c = "<div id='"
			+ id
			+ "'><div class='message_white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'></a><div ><div class='message_white_iframe_del'><div class='message_white_title'><span>"
			+ title
			+ "</span></div><div class='message_white_box_del'><span class='message_white_img_"
			+ icon
			+ "'></span><span class='message_white_font'>"
			+ content
			+ "</span></div>   <div class='message_white_box1'><input id='sure' type='button' value='确定'/><input id='cancel' type='button' value='取消'/></div>    </div></div></div><div class='black_overlay'></div>";

	var m = "<div id='"
			+ id
			+ "'><div class='release_page'> <div class='release_page_title'><span class='release_page_title_left'>我要发布</span><span class='release_page_title_right'><a href='javascript:void(0);' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'>×</a></span></div><div class='release_page_ul'><ul>																						<li class='baby'><a href='javascript:void(0);' id='share_select_3' share_mark='3'><div class='margin'><span>宝贝</span><br /> 晒出喜欢的宝贝</div></a></li>																														<li class='pictures'><a href='javascript:void(0);' id='share_select_4' share_mark='4'><div class='margin'><span>店铺</span><br /> 分享精品店铺</div></a></li>																															<li class='article'><a href='javascript:void(0);' id='share_select_5' share_mark='5'><div class='margin'><span>新鲜事</span><br /> 写心得写攻略</div></a></li></ul></div></div><div class='black_overlay'></div>";

	var a = "<div id='"
			+ id
			+ "'><div class='article_page'><div class='article_page_title'><span class='article_page_title_left'>"
			+ title
			+ "</span><span class='article_page_title_right'><a href='javascript:void(0);' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'>×</a></span></div><div class='article_page_main'><div class='h2'><h2>正文</h2></div><textarea id='select_share_dynamic' name='select_share_dynamic' placeholder='生活每天都有新鲜事.每天都来聊一聊'></textarea><div class='article_page_bottom'><div class='article_page_input_left' id='input_show_error'></div><input type='button' value='分 享' id='select_button_dynamic' share_mark='dynamic' /></div></div></div><div class='black_overlay'></div></div>"

	var alert = "<div id='"
			+ id
			+ "'><div class='message_white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"
			+ id
			+ "\").remove();'></a><div ><div class='message_white_iframe_del'><div class='message_white_title'><span>"
			+ title
			+ "</span></div><div class='message_white_box_del'><span class='message_white_img_"
			+ icon
			+ "'></span><span class='message_white_font'>"
			+ content
			+ "</span></div></div></div></div><div class='black_overlay'></div>";

	if (type == 0) {// 消息框
		jQuery("body").append(s);
	}
	if (type == 1) {// 确认框
		jQuery("body").append(c);
	}
	if (type == 2) {// 发布框
		jQuery("body").append(m);
	}
	if (type == 3) {// alert提示框
		jQuery("body").append(alert);
	}
	if (type == 5) {// 发布框_个人主页-发布-新鲜事窗口
		jQuery("body").append(a);
	}
	var top = jQuery(window).scrollTop()
			+ (jQuery(window).height() - jQuery(document).outerHeight()) / 2;
	jQuery(".message_white_content").css("margin-top",
			jQuery(window).scrollTop() + "px");
	var h = jQuery(document).height();
	jQuery('.black_overlay').css("height", h);

	// 设置关闭时间
	alert_timer_id = window.setInterval("closewin('" + id + "')", 1000);
	// 点击确定
	jQuery("#sure").click(function() {
		jQuery("#" + id).remove();
		runcallback(confirm_action);
	});

	function runcallback(callback) {
		callback();
	}
	// 点击取消
	jQuery("#cancel").click(function() {
		jQuery("#" + id).remove();
	});
	// 点击选择发布类型，将参数添加到页面隐藏域中
	jQuery("a[id^=share_select_]").click(function() {
		jQuery("#share_select_mark").val(jQuery(this).attr("share_mark"));
		jQuery("#" + id).remove();
		runcallback(confirm_action);
	});
	// 点击所有发布确认按钮
	jQuery("input[id^=select_button_]").click(function() {
		var share_mark = jQuery(this).attr("share_mark");
		var content = jQuery("#select_share_" + share_mark).val();
		if (content.length > 0) {
			if (content.length > 140) {
				jQuery("#input_show_error").html("输入字数不能多于140个字！");
			} else {
				jQuery("#share_select_content").val(content);
				jQuery("#" + id).remove();
				runcallback(confirm_action);
			}
		} else {
			jQuery("#input_show_error").html("请输入内容！");
		}
	});

}

function closewin(id) {
	var count = parseInt(jQuery("#" + id + " span[id=time_down]").text());
	count--;
	if (count == 0) {
		jQuery("#" + id).remove();
		window.clearInterval(alert_timer_id);
	} else {
		jQuery("#" + id + " span[id=time_down]").text(count);
	}
}
function dialogLogin(dialog_uri) {
	var dialog_title = "会员登录";
	var dialog_id = "user_login";
	var dialog_height = "100";
	var dialog_width = "400";
	var dialog_top = "";
	var dialog_left = 300;
	if (dialog_uri != undefined && dialog_uri != "") {
		jQuery("body")
				.append(
						"<div id='"
								+ dialog_id
								+ "'><div class='white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"
								+ dialog_id
								+ "\").remove();'></a><div class='white_box'><h1>"
								+ dialog_title
								+ "</h1><div class='content_load'></div></div></div><div class='black_overlay'></div></div>");
		dialog_top = "435";
		var h = "2336";
		jQuery('.black_overlay').css("height", h);
		var dialog_left = "459.5";
		jQuery(".white_content").css("position", "absolute").css("top",
				parseInt(dialog_top) + "px").css("left",
				parseInt(dialog_left) + "px");
		jQuery.ajax({
			type : 'POST',
			url : dialog_uri,
			async : false,
			data : '',
			success : function(html) {
				jQuery(".content_load").remove();
				jQuery("#" + dialog_id + " .white_content").css("width",
						dialog_width);
				jQuery("#" + dialog_id + " .white_box h1").after(html);
				jQuery("#" + dialog_id).show();
			}
		});
		jQuery("#" + dialog_id + " .white_box h1").css("cursor", "move")
	}
}
function open_im() {
	var goods_id = arguments[0];
	var url = arguments[1];
	var type = arguments[2]; // 打开类型，user为用户打开，store为商家打开，plat为平台打开
	var to_type = arguments[3];
	var store_id = arguments[4];
	if (type == "store") {
		window
				.open(
						url + "/store_chatting.htm",
						'store',
						'height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
	}
	if (type == "user") {
		window
				.open(
						url + "/user_chatting.htm?gid=" + goods_id + "&type="
								+ to_type + "&store_id=" + store_id,
						'',
						'height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
	}
	if (type == "plat") {
		window
				.open(
						url + "/admin/plat_chatting.htm",
						'plat',
						'height=660,width=1000,top=200,left=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
	}
}
/**
 * 系统加载
 */
jQuery(document)
		.ready(
				function() {
					// 改变系统提示的样式
					jQuery("span .w").mousemove(function() {
						var id = jQuery(this.parentNode).attr("id");
						if (id = "nothis") {
							jQuery(this.parentNode).attr("id", "this")
						}
					}).mouseout(function() {
						var id = jQuery(this.parentNode).attr("id");
						if (id = "this") {
							jQuery(this.parentNode).attr("id", "nothis")
						}
					});
					//
					tipStyle();
					//
					jQuery(
							"a[dialog_uri!=undefined],input[dialog_uri!=undefined],dt[dialog_uri!=undefined]")
							.click(
									function(e) {
										var dialog_uri = jQuery(this).attr(
												"dialog_uri");
										var dialog_title = jQuery(this).attr(
												"dialog_title");
										var dialog_id = jQuery(this).attr(
												"dialog_id");
										var dialog_height = jQuery(this).attr(
												"dialog_height");
										var dialog_width = jQuery(this).attr(
												"dialog_width");
										var dialog_top = jQuery(this).attr(
												"dialog_top");
										var dialog_left = 300;
										if (dialog_uri != undefined
												&& dialog_uri != "") {
											jQuery("body")
													.append(
															"<div id='"
																	+ dialog_id
																	+ "'><div class='white_content'> <a href='javascript:void(0);' class='white_close' onclick='javascript:jQuery(\"#"
																	+ dialog_id
																	+ "\").remove();'></a><div class='white_box'><h1>"
																	+ dialog_title
																	+ "</h1><div class='content_load'></div></div></div><div class='black_overlay'></div></div>");
											e.preventDefault();
											if (dialog_top == undefined
													|| dialog_top == "") {
												dialog_top = jQuery(window)
														.scrollTop()
														+ (jQuery(window)
																.height() - jQuery(
																document)
																.outerHeight())
														/ 2 - dialog_height / 2;
											} else {
												dialog_top = parseInt(dialog_top)
														+ jQuery(window)
																.scrollTop();
											}
											var h = jQuery(document).height();
											jQuery('.black_overlay').css(
													"height", h);
											var dialog_left = (jQuery(document)
													.width() - dialog_width) / 2;
											jQuery(".white_content")
													.css("position", "absolute")
													.css(
															"top",
															parseInt(dialog_top)
																	+ "px")
													.css(
															"left",
															parseInt(dialog_left)
																	+ "px");
											jQuery
													.ajax({
														type : 'POST',
														url : dialog_uri,
														async : false,
														data : '',
														success : function(html) {
															jQuery(
																	".content_load")
																	.remove();
															jQuery(
																	"#"
																			+ dialog_id
																			+ " .white_content")
																	.css(
																			"width",
																			dialog_width);
															jQuery(
																	"#"
																			+ dialog_id
																			+ " .white_box h1")
																	.after(html);
															jQuery(
																	"#"
																			+ dialog_id)
																	.show();
														}
													});
											jQuery(
													"#" + dialog_id
															+ " .white_box h1")
													.css("cursor", "move")
											jQuery(
													"#" + dialog_id
															+ " .white_content")
													.draggable(
															{
																handle : " .white_box h1"
															});
										}
									});

				});
