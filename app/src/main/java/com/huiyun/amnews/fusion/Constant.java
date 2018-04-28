package com.huiyun.amnews.fusion;

public class Constant {
	
	 public static final String UNICODE = "UTF-8";
	 

//	 public static final String SERVER_URL = "http://www.dingjianapp.com/index.php?s=/";
//	 public static final String SERVER_URL = "http://139.196.24.150/index.php?s=/";
//	 public static final String SERVER_URL = "http://112.74.79.96:88/php/index.php?s=/";
//	public static final String SERVER_URL = "http://112.74.79.96:88/php/vr/index.php?s=/";
//	public static final String SERVER_URL = "http://apk.weiyucheye.com/index.php/";
//	public static final String SERVER_URL_NEW = "http://apk.weiyucheye.com/index.php/";
	public static final String SERVER_URL = "http://www.gziwl.com/index.php/";
	public static final String SERVER_URL_NEW = "http://www.gziwl.com/index.php/";
//	截图
//	 public static final String SCREENSHOT_URL_PREFIX = "http://112.74.79.96:88/php/vr/Public/djh/screenshot/";
//	缩略图
//	 public static final String THUMBNAIL_URL_PREFIX = "http://www.dingjianapp.com/Public/djh/thumbnail/";
//	 public static final String THUMBNAIL_URL_PREFIX = "http://139.196.24.150/Public/djh/thumbnail/";
//	 public static final String THUMBNAIL_URL_PREFIX = "http://112.74.79.96:88/php/vr/Public/djh/thumbnail/";

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

	//更多精品应用    （Api/more）
	public static final String MORE_APP_LIST_URL = SERVER_URL_NEW + "Api/more";

	//三、 获取首页推荐应用    （Api/home_recommend）
	public static final String FIRST_INSTALL_APP_LIST_URL = SERVER_URL_NEW + "Api/home_recommend";

	//八、榜单页面    （Api/billboard_page）
	public static final String RANKING_APP_LIST_URL = SERVER_URL_NEW + "Api/billboard_page";

	//、搜索页面    （Api/search_page）
	public static final String SEARCH_PAGE_URL = SERVER_URL_NEW + "Api/search_page";

	//六、分类页面    （Api/category_page）
	public static final String CATEGORY_URL = SERVER_URL_NEW + "Api/category_page";

	//七、游戏页面    （Api/game_page）
	public static final String GAME_FRAGMENT_DATA = SERVER_URL_NEW + "Api/game_page";

	//广告轮播图
	public static final String APP_AD = SERVER_URL_NEW + "App/ad/type/1";

	//九、意见反馈    （Api/feedback）
	public static final String FEED_BACK_URL =SERVER_URL_NEW  + "Api/feedback";

	//十、搜索    （Api/search）
	public static final String SEARCH_APP_URL =SERVER_URL_NEW  + "Api/search";

	//更新
	public static final String UPDATE_APP_URL =SERVER_URL_NEW  + "Api/update_app";

	//十二、增加下载次数    （User/download_count）
	public static final String DOWN_LOAD_COUNT_URL =SERVER_URL_NEW  + "User/add_count";

	//启动页 十四、启动页图片    （Api/boot）
	public static final String SPLASH_IMG_ =SERVER_URL_NEW  + "Api/boot";

	//上传文件
	public static final String UPLOAD_FILE_URL =SERVER_URL_NEW  + "User/upload";

	//首页访问人数统计    （Api/visit）
	public static final String VISIT_URL =SERVER_URL_NEW  + "Api/visit";
}
