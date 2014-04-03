package com.syt.health.kitchen.json;

import java.util.List;

/**
 * 保存帐号养生厨神项目的数据
 * 
 * @author tom
 *
 */
public class UserHKData {
	private String userid;			
	private String tokenid;		// 最近登录生成的tokenid
	private String desc;		// 最近登录的终端描述
	private List<Menu> menus;	// 跟该帐号相关的全日菜谱
	private List<OrderMeal> ordermeals;	// 跟该帐号相关的最近在外吃点菜菜谱
	
	
}
