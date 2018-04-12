package com.huiyun.amnews.been;

import java.io.Serializable;

public class AppInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8256502425809752966L;
	private String screenshots_name;
	private String update_description;
	private String score_img;
	private String download_count;
	private String thumbnail_name;
	private String version;
	private String id;
	private String last_modified_by;
	private String level;
	private int trample_count;
	private String download_url;
	private String name;
	private String attr1;
	private String attr2;
	private String attr3;
	private String attr4;
	private String attr5;
	private String attr6;
	private String attr7;
	private String created_by;
	private String click_count;
	private String package_name;
	private String category1;
	private String category2;
	private int collection_count;
	private int size;
	private int last_modified_time;
	private int created_time;
	private String introduction;
	private int praise_count;
	private String is_del;
	private int downloadSize;
	private int isPublic;
	private int downloadProgress;
	private String name1;
	private String name2;
	private String name3;
	private String isPraised;
	private String isTrample;
	private String comment_count;
	private String comment_score;
	private String auth; //授权
	private String system_require;
	private boolean isChoiced;



	private boolean isAvailable; //是否安装
	private boolean isUpdate; //是否升级




	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}

	public String getCategory1() {
		return category1;
	}

	public void setCategory1(String category1) {
		this.category1 = category1;
	}

	public String getClick_count() {
		return click_count;
	}

	public void setClick_count(String click_count) {
		this.click_count = click_count;
	}

	public String getDownload_count() {
		return download_count;
	}

	public void setDownload_count(String download_count) {
		this.download_count = download_count;
	}

	public String getCategory2() {
		return category2;
	}

	public void setCategory2(String category2) {
		this.category2 = category2;
	}

	private boolean tag = true;
	
	public boolean isTag() {
		return tag;
	}

	public void setTag(boolean tag) {
		this.tag = tag;
	}

	public AppInfo() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getDownloadSize() {
		return downloadSize;
	}

	public void setDownloadSize(int downloadSize) {
		this.downloadSize = downloadSize;
	}

	public int getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}

	public String getUpdateDescription() {
		return update_description;
	}

	public void setUpdateDescription(String updateDescription) {
		this.update_description = updateDescription;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public int getDownloadProgress() {
		return downloadProgress;
	}

	public void setDownloadProgress(int downloadProgress) {
		this.downloadProgress = downloadProgress;
	}

	public String getThumbnailName() {
		return thumbnail_name;
	}

	public void setThumbnailName(String thumbnailName) {
		this.thumbnail_name = thumbnailName;
	}

	public String getDownloadUrl() {
		return download_url;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.download_url = downloadUrl;
	}

	public String getScreenshotsName() {
		return screenshots_name;
	}

	public void setScreenshotsName(String screenshotsName) {
		this.screenshots_name = screenshotsName;
	}

	public int getPraiseCount() {
		return praise_count;
	}

	public void setPraiseCount(int praiseCount) {
		this.praise_count = praiseCount;
	}

	public int getTrampleCount() {
		return trample_count;
	}

	public void setTrampleCount(int trampleCount) {
		this.trample_count = trampleCount;
	}

	public int getCollectionCount() {
		return collection_count;
	}

	public void setCollectionCount(int collectionCount) {
		this.collection_count = collectionCount;
	}

	public String getCreatedBy() {
		return created_by;
	}

	public void setCreatedBy(String createdBy) {
		this.created_by = createdBy;
	}

	public int getCreatedTime() {
		return created_time;
	}

	public void setCreatedTime(int createdTime) {
		this.created_time = createdTime;
	}

	public String getLastModifiedBy() {
		return last_modified_by;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.last_modified_by = lastModifiedBy;
	}

	public int getLastModifiedTime() {
		return last_modified_time;
	}

	public void setLastModifiedTime(int lastModifiedTime) {
		this.last_modified_time = lastModifiedTime;
	}

	public String getIsDel() {
		return is_del;
	}

	public void setIsDel(String isDel) {
		this.is_del = isDel;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getName3() {
		return name3;
	}

	public void setName3(String name3) {
		this.name3 = name3;
	}

	public String getIsPraised() {
		return isPraised;
	}

	public void setIsPraised(String isPraised) {
		this.isPraised = isPraised;
	}

	public String getIsTrample() {
		return isTrample;
	}

	public void setIsTrample(String isTrample) {
		this.isTrample = isTrample;
	}

	public String getScore_img() {
		return score_img;
	}

	public void setScore_img(String score_img) {
		this.score_img = score_img;
	}

	public String getAttr1() {
		return attr1;
	}

	public void setAttr1(String attr1) {
		this.attr1 = attr1;
	}

	public String getAttr2() {
		return attr2;
	}

	public void setAttr2(String attr2) {
		this.attr2 = attr2;
	}

	public String getAttr3() {
		return attr3;
	}

	public void setAttr3(String attr3) {
		this.attr3 = attr3;
	}

	public String getAttr4() {
		return attr4;
	}

	public void setAttr4(String attr4) {
		this.attr4 = attr4;
	}

	public String getAttr5() {
		return attr5;
	}

	public void setAttr5(String attr5) {
		this.attr5 = attr5;
	}

	public String getAttr6() {
		return attr6;
	}

	public void setAttr6(String attr6) {
		this.attr6 = attr6;
	}

	public String getAttr7() {
		return attr7;
	}

	public void setAttr7(String attr7) {
		this.attr7 = attr7;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getComment_count() {
		return comment_count;
	}

	public void setComment_count(String comment_count) {
		this.comment_count = comment_count;
	}

	public String getComment_score() {
		return comment_score;
	}

	public void setComment_score(String comment_score) {
		this.comment_score = comment_score;
	}

	public boolean isChoiced() {
		return isChoiced;
	}

	public void setChoiced(boolean choiced) {
		isChoiced = choiced;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getSystem_require() {
		return system_require;
	}

	public void setSystem_require(String system_require) {
		this.system_require = system_require;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean available) {
		isAvailable = available;
	}

	public boolean isUpdate() {
		return isUpdate;
	}

	public void setUpdate(boolean update) {
		isUpdate = update;
	}
}
