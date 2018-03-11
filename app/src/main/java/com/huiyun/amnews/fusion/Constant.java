package com.huiyun.amnews.fusion;

public class Constant {
	
	 public static final String UNICODE = "UTF-8";
	 

//	 public static final String SERVER_URL = "http://www.dingjianapp.com/index.php?s=/";
//	 public static final String SERVER_URL = "http://139.196.24.150/index.php?s=/";
//	 public static final String SERVER_URL = "http://112.74.79.96:88/php/index.php?s=/";
	public static final String SERVER_URL = "http://112.74.79.96:88/php/vr/index.php?s=/";
	public static final String SERVER_URL_NEW = "http://apk.weiyucheye.com/index.php/";
//	截图
	 public static final String SCREENSHOT_URL_PREFIX = "http://112.74.79.96:88/php/vr/Public/djh/screenshot/";
//	缩略图
//	 public static final String THUMBNAIL_URL_PREFIX = "http://www.dingjianapp.com/Public/djh/thumbnail/";
//	 public static final String THUMBNAIL_URL_PREFIX = "http://139.196.24.150/Public/djh/thumbnail/";
	 public static final String THUMBNAIL_URL_PREFIX = "http://112.74.79.96:88/php/vr/Public/djh/thumbnail/";

	 public static final String HEAD_URL = "http://freefood.qiniudn.com/";
	 public static final String QINIU_TOKEN = SERVER_URL + "User/gerQiniuToken";
	 public static final String LOGIN = SERVER_URL + "User/login";
	 public static final String REGISTER1_URL = SERVER_URL + "User/register";
	 public static final String REGISTER2_URL = SERVER_URL + "User/addUserInfo";
	 public static final String REGISTER3_URL = SERVER_URL + "User/saveUserAvatar";
	 public static final String CHANGE_AVATAR_URL = SERVER_URL+"User/alterAvatar";
	 public static final String CHANGE_NICKNAME_URL=SERVER_URL+"User/alterNickName";
	 public static final String GET_CATEGORY_BY_SENIOR_MENUID = SERVER_URL + "Category/getMCategoryBySeniorMenuId";
	 public static final String GET_CATEGORY_BY_APPID = SERVER_URL + "Category/getMCategoryByAppId";
	 public static final String GET_USERINFO_BY_ID = SERVER_URL + "User/getUserInfoById";
	 public static final String GET_APPINFO_BY_USERID = SERVER_URL + "App/getMAppInfoByUserId";
	public static final String GET_ALL_APPINFO_LIST = SERVER_URL + "App/getMallAppInfoList";
	public static final String ADD_COLLECTED_APP = SERVER_URL + "App/addMCollectedApp";
	public static final String ADD_PRAISE_APP = SERVER_URL + "App/addMPraiseApp";
	public static final String ADD_TRAMPLE_APP = SERVER_URL + "App/addMTrampleApp";
	public static final String GET_APP_PRAISE_COUNT_BY_ID = SERVER_URL + "App/getAppPraiseCountById";
	public static final String GET_APP_TRAMPLE_COUNT_BY_ID = SERVER_URL + "App/getAppTrampleCountById";
	public static final String IS_COLLECTED = SERVER_URL + "App/isCollected";
	public static final String IS_PRAISED = SERVER_URL + "App/isPraised";
	public static final String IS_TRAMPLE = SERVER_URL + "App/isTrample";
	public static final String ADDDOWNLOAD_COUNT = SERVER_URL + "App/addDownloadCount";


	//vr人气风向标,精品应用
	 public static final String HOT_FINAL_URL = SERVER_URL + "Category/getFirstCategory";

	//资源
	public static final String GET_APPINFO_BY_CATEGORYID = SERVER_URL + "App/getMAppInfoByCategoryId";
	//获取分类
	public static final String GET_CATEGORY_LIST = SERVER_URL + "Category/getCategory";
	//我的收藏
	public static final String MY_COLLECT_LIST = SERVER_URL + "User/mycollection";
	//我踩过的
	public static final String MY_TRAMPLE_LIST = SERVER_URL + "User/mytrample";
	//我赞过的
	public static final String MY_PRAISE_LIST = SERVER_URL + "User/mypraise";

	//获取点赞踩数量
	public static final String APP_COUNT = SERVER_URL + "App/getCount";
	//添加评论
	public static final String APP_ADD_COMMENT = SERVER_URL + "App/addComment";
	//获取评论列表
	public static final String APP_GET_COMMENT = SERVER_URL + "App/getComments";
	//搜索
	public static final String APP_SEARCH = SERVER_URL + "App/search";
	//获取验证码
	public static final String USER_SEND_SMS = SERVER_URL + "User/sendSMS";
	//忘记密码
	public static final String USER_FIND_PWD = SERVER_URL + "User/findPassword";
	//广告轮播图
	public static final String APP_AD = SERVER_URL + "App/ad/type/1/city/";

    //	下载送积分：User/receiveScore，参数：userId，token
    public static final String RECEIVE_SCORE = SERVER_URL + "User/receiveScore";

	//获取用户积分
	public static final String GET_SCORE = SERVER_URL + "User/getScore";

	//获取商城链接
	public static final String APP_SHOP = SERVER_URL + "App/shop";

	//获取更新
	public static final String APP_UPDATE = SERVER_URL + "App/update/version/";

	/**********************************************************************************************/
	//获取新闻分类    （Api/category）
	public static final String NEWS_CATEGORY_URL = SERVER_URL_NEW + "Api/category";

	//二、 获取时事新闻    （Api/all）
	public static final String NEWS_LIST_URL = SERVER_URL_NEW + "Api/all";

	//五、 获取首页推荐精品应用    （Api/home_selection）
	public static final String HOME_GAME_APP_LIST_URL = SERVER_URL_NEW + "Api/home_selection";
}
