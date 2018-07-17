package com.tc.controller;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.util.StringUtils;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20170112.TextScanRequest;
import com.aliyuncs.http.HttpResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.tc.aly.utils.JsonDecoder;
import com.tc.aly.utils.TextScan;
import com.tc.bean.BaseResponse;
import com.tc.bean.Code;
import com.tc.bean.DataResponse;
import com.tc.bean.Require;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.interceptor.TokenInterceptor;
import com.tc.model.Comment;
import com.tc.model.Message;
import com.tc.model.Notification;
import com.tc.model.TcContent;
import com.tc.model.TcTarget;
import com.tc.model.User;
import com.tc.model.UserCollect;
import com.tc.model.UserLike;
import com.tc.push.MessagePush;
import com.tc.sms.SmsVerifyKit;
import com.tc.token.TokenManage;
import com.tc.utils.CommonUtil;
import com.tc.utils.DateUtil;
import com.tc.utils.StringUtil;

/**
 * 
 * 
 * <p>
 * 用户注册：/api/user/register
 * <p>
 * 用户登录：/api/user/login
 * 
 * @author PB
 *
 */
@Before(TokenInterceptor.class)
public class UserController extends BaseController {

	/**
	 * 用户注册
	 * 
	 * @param username
	 * @param password
	 * @param nickname
	 * @param code
	 * @throws Exception
	 */
	@Clear
	public void register() throws Exception {

		String username = getPara("username");
		String password = getPara("password");
		String nickname = getPara("nickname");
		String code = getPara("code");
		String cid = getPara("cid");

		// nickname = new String(nickname.getBytes("ISO-8859-1"), "UTF-8");

		if (renderParamsNull(new Require().put(username, "username is null")
				.put(password, "password is null")
				.put(nickname, "nickname is null").put(code, "code is null"))) {

			String strSql = "select * from t_user where user_name=?";
			if (Db.findFirst(strSql, username) != null) {
				renderJson(new BaseResponse(Code.ACCOUNT_EXISTS,
						"the username has been already registered"));
				return;
			}

			if (renderCodeIsCorrect(username, code)) {
				new User().set(User.USER_NAME, username)
						.set(User.PASSWORD, password)
						.set(User.CREATE_DATE, DateUtil.getCurrentTime())
						.set(User.NICK_NAME, nickname).set("cid", cid).save();

				// User user = User.dao.findFirst(strSql, username);
				// new Message().set(Message.CONTENT,
				// Constant.text).set(Message.USER_ID, user.getUserId())
				// .set(Message.CREATE_TIME, DateUtil.getCurrentTime()).save();
				// MessagePush.sendMessage(user.getUserId(), 2, false);
				renderSuccess();
			}

		}
	}

	/**
	 * 用户登录
	 */
	@Clear
	public void login() {
		String username = getPara("username");
		String password = getPara("password");
		String cid = getPara("cid");
		if (renderParamsNull(new Require().put(username, "username is null")
				.put(password, "password is null"))) {

			String strSql = "select * from t_user where user_name=? and password=?";
			User user = User.dao.findFirst(strSql, username, password);
			DataResponse<Map<String, Object>> response = new DataResponse<Map<String, Object>>();
			if (user == null) {
				response.setCode(Code.FAIL).setMessage(
						"username or password is error");
				renderJson(response);
				return;
			}

			// 获取原来保存的CID，判断原来的CID与新的CID是否一样，如果不一样说明在新设备登录
			strSql = "select cid from t_user where user_name = ?";
			Record record = Db.findFirst(strSql, username);
			String oldCid = record.get("cid");
			if (!StringUtil.isEmpty(oldCid) && !oldCid.equals(cid)) {
				// 发送推送通知在其他设备登录
				MessagePush.sendMessage(user.getUserId(), oldCid, 3);
			}
			// 更新用户cid
			strSql = "update t_user set cid = ? where user_name = ?";
			Db.update(strSql, cid, username);

			Map<String, Object> map = user.getAttrs();
			map.put("token", TokenManage.getInstance().createToken(user));

			// 如果是第一次登录发送系统消息
			if (user.getInt("first_login") == 1) {
				CommonUtil.sendSystemMessage(Constant.welcomeText,
						user.getUserId());
				// 更新first_login
				strSql = "update t_user set first_login = ? where user_name = ?";
				Db.update(strSql, 0, username);
			}

			renderJson(new DataResponse<Map<String, Object>>(Code.SUCCESS, map));

		}
	}

	/**
	 * 验证验证码是否正确
	 * 
	 * @param username
	 * @param code
	 * @return
	 * @throws Exception
	 */
	private boolean renderCodeIsCorrect(String username, String code)
			throws Exception {
		String result = new SmsVerifyKit(Config.APPKEY, username, Config.ZONE,
				code).verify();

		JsonObject resultJson = new JsonParser().parse(result)
				.getAsJsonObject();
		int status = resultJson.get("status").getAsInt();

		if (status != 200) {
			// String error = resultJson.get("status").getAsString();
			renderJson(new BaseResponse(Code.CODE_ERROR, status + ""));
			return false;
		}
		return true;
	}

	/**
	 * 检查用户是否自动登录
	 */
	public void checkAutoLogin() {
		renderSuccess();
	}

	/**
	 * 发布吐槽信息
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void releaseTcContent() throws UnsupportedEncodingException {

		String userId = getPara("userId");
		String content = getPara("content");
		// content = new String(content.getBytes("ISO-8859-1"), "UTF-8");
		String targetId = getPara("targetId");
		String schoolId = getPara("schoolId");
		if (StringUtil.isEmpty(targetId)) {
			targetId = null;
		}
		if (!verifyUserState(userId)) {
			renderProhibit();
			return;
		}
		String typeId = getPara("typeId");
		int anonymous = getParaToInt("anonymous", 0);
		// String contentId = DateUtil.getCurrentTimeNoDel() + userId;
		// System.out.println("contentId-->"+contentId);
		if (!renderParamsNull(new Require().put(userId, "userId is null")
				.put(content, "content is null").put(typeId, "typeId is null")
				.put(schoolId, "schoolId is null")))
			return;

		TextScanRequest textScanRequest = TextScan.textScan(content);
		try {
			HttpResponse httpResponse = TextScan.getIAcsClient().doAction(
					textScanRequest);

			if (httpResponse.isSuccess()) {
				int result = JsonDecoder.jsonDecode(httpResponse);
				if (result == 1) {
					renderJson(new BaseResponse(Code.LLLEGAL_CONTENT, "涉嫌非法信息"));
					return;
				}

				List<UploadFile> files = getFiles();

				String rootPath = getRequest().getSession().getServletContext()
						.getRealPath("/");
				String fileNames = "";
				String fileName = "";
				for (int i = 0; i < files.size(); i++) {

					UploadFile file = files.get(i);
					fileName = DateUtil.getCurrentTimeNoDel() + i + ".jpg";
					File tempFile = new File(rootPath + "/upload/tc_content/"
							+ fileName);
					file.getFile().renameTo(tempFile);
					fileNames += fileName + ',';
				}
				if (!StringUtil.isEmpty(fileNames)) {
					fileNames = fileNames.substring(0, fileNames.length() - 1);
				}
				new TcContent().set(TcContent.USER_ID, userId)
						.set(TcContent.CONTENT, content)
						.set(TcContent.TARGET_ID, targetId)
						.set(TcContent.TYPE_ID, typeId)
						.set(TcContent.RELEASE_TIME, DateUtil.getCurrentTime())
						.set(TcContent.ANONYMOUS, anonymous)
						.set(TcContent.PIC_LIST, fileNames)
						.set(TcContent.SCHOOL_ID, schoolId).save();

				renderSuccess();
			}

		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 获取吐槽内容
	 */
	public void getTcContentList() {
		String userId = getPara("userId");
		int pageNum = getParaToInt("pageNum", 1);
		if (StringUtil.isEmpty(userId)) {
			renderParamsError("userId is null");
			return;
		}
		String typeId = getPara("typeId");
		if (StringUtil.isEmpty(typeId)) {
			renderParamsError("typeId is null");
			return;
		}
		String schoolId = getPara("schoolId");
		if (StringUtil.isEmpty(schoolId)) {
			renderParamsError("schoolId is null");
			return;
		}

		Page<TcContent> page = null;
		if (typeId.equals("001")) {
			// 获取热门内容
			page = TcContent.dao
					.paginate(
							pageNum,
							20,
							"select a.*,b.like_id,e.collect_id,c.avatar,c.nick_name,d.target_name,f.type_name",
							"from t_tc_content a left join (select like_id,content_id from t_tc_user_like where user_id=?) b on a.content_id = b.content_id left join (select collect_id,content_id from t_tc_user_collect where user_id=?) e on a.content_id = e.content_id LEFT JOIN t_user c on a.user_id= c.user_id LEFT JOIN t_tc_target d on a.target_id=d.target_id LEFT JOIN t_tc_type f on a.type_id = f.type_id where a.school_id=? order by count_like DESC",
							userId, userId, schoolId);
		} else if (typeId.equals("002")) {
			// 获取全部内容
			page = TcContent.dao
					.paginate(
							pageNum,
							20,
							"select a.*,b.like_id,e.collect_id,c.avatar,c.nick_name,d.target_name,f.type_name",
							"from t_tc_content a left join (select like_id,content_id from t_tc_user_like where user_id=?) b on a.content_id = b.content_id left join (select collect_id,content_id from t_tc_user_collect where user_id=?) e on a.content_id = e.content_id LEFT JOIN t_user c on a.user_id= c.user_id LEFT JOIN t_tc_target d on a.target_id=d.target_id LEFT JOIN t_tc_type f on a.type_id = f.type_id where a.school_id=? order by release_time DESC",
							userId, userId, schoolId);
		} else {
			// 获取指定类型的内容
			page = TcContent.dao
					.paginate(
							pageNum,
							20,
							"select a.*,b.like_id,e.collect_id,c.avatar,c.nick_name,d.target_name,f.type_name",
							"from t_tc_content a left join (select like_id,content_id from t_tc_user_like where user_id=?) b on a.content_id = b.content_id left join (select collect_id,content_id from t_tc_user_collect where user_id=?) e on a.content_id = e.content_id LEFT JOIN t_user c on a.user_id= c.user_id LEFT JOIN t_tc_target d on a.target_id=d.target_id LEFT JOIN t_tc_type f on a.type_id = f.type_id where a.type_id=? and a.school_id=? order by release_time DESC",
							userId, userId, typeId, schoolId);
		}

		List<TcContent> contentList = page.getList();
		renderJson(new DataResponse<List<TcContent>>(Code.SUCCESS,
				page.getTotalRow() + "", contentList));

	}

	/**
	 * 获取吐槽内容评论列表
	 */
	public void getTcContentCommentList() {
		String contentId = getPara("contentId");
		int pageNum = getParaToInt("pageNum", 1);
		if (StringUtil.isEmpty(contentId)) {
			renderParamsError("contentId is null");
			return;
		}
		Page<Comment> page = Comment.dao
				.paginate(
						pageNum,
						20,
						"select comment_id,comment_content,comment_time,from_user_id,nick_name,avatar,to_nick_name,to_comment_content,anonymous",
						"from t_user a,t_tc_content_comment b where content_id = ? and a.user_id=b.from_user_id order by comment_time DESC",
						contentId);
		List<Comment> commentList = page.getList();
		renderJson(new DataResponse<List<Comment>>(Code.SUCCESS,
				page.getTotalRow() + "", commentList));
	}

	/**
	 * 对吐槽内容发表评论
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void releaseTcContentComment() throws UnsupportedEncodingException {
		String userId = getPara("userId");
		String contentId = getPara("contentId");
		String commentContent = getPara("content");

		String toUserId = getPara("toUserId");
		String toCommentContent = getPara("toCommentContent");

		String toCommentId = getPara("toCommentId");
		String toNickName = getPara("toNickName");

		int anonymous = getParaToInt("anonymous", 0);

		Require require = new Require().put(userId, "userId is null")
				.put(contentId, "contentId is null")
				.put(commentContent, "content is null")
				.put(userId, "userId is null").put(userId, "userId is null")
				.put(userId, "userId is null");
		if (!StringUtil.isEmpty(toUserId)) {
			require.put(toUserId, "toUserId is null")
					.put(toCommentContent, "toCommentContent is null")
					.put(toCommentId, "toCommentId is null")
					.put(toNickName, "toNickName is null");
		}
		if (!renderParamsNull(require))
			return;

		if (!verifyUserState(userId)) {
			renderProhibit();
			return;
		}

		TextScanRequest textScanRequest = TextScan.textScan(commentContent);
		HttpResponse httpResponse;
		try {
			httpResponse = TextScan.getIAcsClient().doAction(textScanRequest);
			if (httpResponse.isSuccess()) {
				int result = JsonDecoder.jsonDecode(httpResponse);
				if (result == 1) {
					renderJson(new BaseResponse(Code.LLLEGAL_CONTENT, "涉嫌非法信息"));
					return;
				}

				Comment comment = new Comment()
						.set(Comment.ANONYMOUS, anonymous)
						.set(Comment.COMMENT_CONTENT, commentContent)
						.set(Comment.COMMENT_TIME, DateUtil.getCurrentTime())
						.set(Comment.CONTENT_ID, contentId)
						.set(Comment.USER_ID, userId);
				if (!StringUtil.isEmpty(toUserId)) {

					comment.set(Comment.TO_COMMENT_CONTENT, toCommentContent)
							.set(Comment.TO_COMMENT_ID, toCommentId)
							.set(Comment.TO_NICK_NAME, toNickName)
							.set(Comment.TO_USER_ID, toUserId);
				}

				comment.save();
				raiseCommentCount(contentId);

				if (StringUtil.isEmpty(toUserId)) {
					TcContent tcContent = TcContent.dao.findById(contentId);
					toUserId = tcContent.get("user_id") + "";
				}

				// System.out.println("touserId--->" + toUserId);
				if (!userId.equals(toUserId)) {
					MessagePush.sendMessage(toUserId, 0);
				}

				renderSuccess();

			}
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 校验用户状态
	 * 
	 * @param userId
	 * @return true:正常状态 false:禁言状态
	 */
	private boolean verifyUserState(String userId) {
		User user = User.dao.findById(userId);
		int state = user.get(User.USER_STATE);
		return state == 0 ? true : false;
	}

	/**
	 * 评论数加1
	 * 
	 * @param contentId
	 */
	private void raiseCommentCount(String contentId) {
		String strSql = "update t_tc_content set count_comment = count_comment+1 where content_id = ?";
		Db.update(strSql, contentId);
	}

	/**
	 * 评论数减1
	 * 
	 * @param contentId
	 */
	private void minusCommentCount(String contentId) {
		String strSql = "update t_tc_content set count_comment = count_comment-1 where content_id = ?";
		Db.update(strSql, contentId);
	}

	/**
	 * 点赞
	 */
	public void upContent() {
		String userId = getPara("userId");
		String contentId = getPara("contentId");
		if (!renderParamsNull(new Require().put(userId, "userId is null").put(
				contentId, "contentId is null")))
			return;

		String strSql = "select * from t_tc_user_like where user_id=? and content_id = ? ";
		Record record = Db.findFirst(strSql, userId, contentId);
		if (record == null) {

			new UserLike().set(UserLike.USER_ID, userId)
					.set(UserLike.CONTENT_ID, contentId)
					.set(UserLike.LIKE_TIME, DateUtil.getCurrentTime()).save();
			strSql = "update t_tc_content set count_like = count_like+1 where content_id = ?";

			TcContent tcContent = TcContent.dao.findById(contentId);
			String toUserId = tcContent.get("user_id") + "";
			if (!userId.equals(toUserId))
				MessagePush.sendMessage(toUserId, 1);
		} else {
			// 取消点赞
			int likeId = record.get("like_id");
			strSql = "DELETE from t_tc_user_like where like_id = ?";
			Db.update(strSql, likeId);
			strSql = "update t_tc_content set count_like = count_like-1 where content_id = ? and count_like>0";
		}

		Db.update(strSql, contentId);
		renderSuccess();
	}

	/**
	 * 收藏
	 */
	public void collectContent() {
		String userId = getPara("userId");
		String contentId = getPara("contentId");
		if (!renderParamsNull(new Require().put(userId, "userId is null").put(
				contentId, "contentId is null")))
			return;

		String strSql = "select * from t_tc_user_collect where user_id=? and content_id = ?";
		Record record = Db.findFirst(strSql, userId, contentId);
		if (record == null) {
			new UserCollect().set(UserCollect.USER_ID, userId)
					.set(UserCollect.CONTENT_ID, contentId).save();
		} else {
			// 取消点赞
			int collectId = record.get("collect_id");
			strSql = "DELETE from t_tc_user_collect where collect_id = ?";
			Db.update(strSql, collectId);
		}

		renderSuccess();
	}

	private void likeContentCancel() {
		String likeId = getPara("likeId");
		String contentId = getPara("contentId");
		if (!renderParamsNull(new Require().put(likeId, "likeId is null").put(
				contentId, "contentId is null")))
			return;
		String strSql = "DELETE from t_tc_user_like where like_id = ?";
		Db.update(strSql, likeId);
		strSql = "update t_tc_content set count_like = count_like-1 where content_id = ?";
		Db.update(strSql, contentId);
		renderSuccess();
	}

	/**
	 * 修改个人信息
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void modifyInfo() throws UnsupportedEncodingException {
		String userId = getPara("userId");
		String nickName = getPara("nickName");
		String school = getPara("school");
		String sex = getPara("sex");
		String birthday = getPara("birthday");
		String identityType = getPara("identityType");
		String entranceTime = getPara("entranceTime");
		if (!renderParamsNull(new Require().put(userId, "userId is null").put(
				school, "school is null")))
			return;

		// nickName = new String(nickName.getBytes("ISO-8859-1"), "UTF-8");
		User user = new User().dao().findById(userId)
				.set(User.NICK_NAME, nickName).set(User.SEX, sex)
				.set(User.BIRTHDAY, birthday)
				.set(User.IDENTITY_TYPE, identityType).set(User.SCHOOL, school)
				.set(User.ENTRANCE_TIME, entranceTime);
		user.update();
		// String strSql =
		// "update t_user set nick_name =?,sex=?,birthday=?,identity_type=?,school=?,entrance_time=? where user_id=?";
		// Db.update(strSql, nickName, sex, birthday, identityType, school,
		// entranceTime, userId);
		renderJson(new DataResponse<User>(Code.SUCCESS, user));
	}

	/**
	 * 获取用户个人信息
	 */
	public void getUserInfo() {
		String userId = getPara("userId");
		if (StringUtil.isEmpty(userId)) {
			renderParamsError("userId is null");
			return;
		}
		String strSql = "select * from t_user where user_id=?";
		User user = User.dao.findFirst(strSql, userId);
		renderJson(new DataResponse<User>(Code.SUCCESS, user));
	}

	/**
	 * 上传头像
	 */
	public void uploadAvatar() {
		String userId = getPara("userId");
		UploadFile avatar = getFile("image");

		if (!renderParamsNull(new Require().put(userId, "userId is null").put(
				avatar, "avatar is null"))) {
			return;
		}
		String imgName = avatar.getFileName();
		String rootPath = getRequest().getSession().getServletContext()
				.getRealPath("/");
		File file = new File(rootPath + "/upload/avatar/" + imgName);
		if (avatar.getFile().renameTo(file)) {

			String strSql = "update t_user set avatar = ? where user_id= ?";
			Db.update(strSql, imgName, userId);
			renderSuccess();
			return;
		}
		renderFail("上传头像失败");

	}

	/**
	 * 修改密码
	 */
	public void modifyPwd() {
		String userId = getPara("userId");
		String oldPassword = getPara("oldPassword");
		String newPassword = getPara("newPassword");
		if (renderParamsNull(new Require().put(userId, "userId is null")
				.put(oldPassword, "old password is null")
				.put(newPassword, "new password is null"))) {

			String strSql = "select * from t_user where user_id=? and password=?";
			User user = User.dao.findFirst(strSql, userId, oldPassword);
			if (user == null) {
				DataResponse<Map<String, Object>> response = new DataResponse<Map<String, Object>>();
				response.setCode(Code.FAIL).setMessage("old password is error");
				renderJson(response);
				return;
			} else {
				strSql = "update t_user set password=? where user_id=?";
				Db.update(strSql, newPassword, userId);
				renderSuccess();
			}
		}
	}

	/**
	 * 更换手机号
	 * 
	 * @throws Exception
	 */
	public void changePhone() throws Exception {
		String userId = getPara("userId");
		String username = getPara("username");
		String code = getPara("code");
		if (!renderParamsNull(new Require().put(username, "username is null")
				.put(userId, "userId is null").put(code, "code is null")))
			return;
		if (renderCodeIsCorrect(username, code)) {
			String strSql = "update t_user set user_name = ? where user_id = ?";
			Db.update(strSql, username, userId);
			renderSuccess();
		}

	}

	/**
	 * 用户反馈
	 */
	public void feedBack() {
		String userId = getPara("userId");
		String content = getPara("content");
		// try
		// {
		// //content = new String(content.getBytes("ISO-8859-1"), "UTF-8");
		// } catch (UnsupportedEncodingException e)
		// {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		if (!renderParamsNull(new Require().put(userId, "userId is null").put(
				content, "content is null")))
			return;
		String strSql = "INSERT INTO `t_feed_back` (`user_id`, `content`,`release_time`) VALUES (?,?,?)";
		Db.update(strSql, userId, content, DateUtil.getCurrentTime());
		renderSuccess();
	}

	/**
	 * 根据吐槽类型获取吐槽内容
	 * 
	 */
	@Deprecated
	public void getTcContentListByType() {
		String userId = getPara("userId");
		int pageNum = getParaToInt("pageNum", 1);
		String typeId = getPara("typeId");
		if (StringUtil.isEmpty(userId)) {
			renderParamsError("userId is null");
			return;
		}
		if (StringUtil.isEmpty(typeId)) {
			renderParamsError("typeId is null");
			return;
		}
		Page<TcContent> page = TcContent.dao
				.paginate(
						pageNum,
						20,
						"select a.content_id,a.user_id,a.release_time,a.content,a.pic_list,a.count_like,a.count_comment,a.type_id,a.target_id,a.anonymous,b.like_id,e.collect_id,c.avatar,c.user_id,c.nick_name,d.target_name",
						"from t_tc_content a left join (select like_id,content_id from t_tc_user_like where user_id=?) b on a.content_id = b.content_id left join (select collect_id,content_id from t_tc_user_collect where user_id=?) e on a.content_id = e.content_id LEFT JOIN t_user c on a.user_id= c.user_id LEFT JOIN t_tc_target d on a.target_id=d.target_id where a.type_id=? order by release_time DESC",
						userId, userId, typeId);

		// message 保存总数
		List<TcContent> contentList = page.getList();
		renderJson(new DataResponse<List<TcContent>>(Code.SUCCESS,
				page.getTotalRow() + "", contentList));

	}

	/**
	 * 根据吐槽对象获取吐槽内容
	 */
	public void getTcContentListByTarget() {
		String userId = getPara("userId");
		int pageNum = getParaToInt("pageNum", 1);
		String targetId = getPara("targetId");
		String schoolId = getPara("schoolId");

		if (!renderParamsNull(new Require().put(userId, "userId is null")
				.put(targetId, "targetId is null")
				.put(schoolId, "schoolId is null")))
			return;
		Page<TcContent> page = TcContent.dao
				.paginate(
						pageNum,
						20,
						"select a.content_id,a.user_id,a.release_time,a.content,a.pic_list,a.count_like,a.count_comment,a.type_id,a.target_id,a.anonymous,b.like_id,e.collect_id,c.avatar,c.user_id,c.nick_name,d.target_name",
						"from t_tc_content a left join (select like_id,content_id from t_tc_user_like where user_id=?) b on a.content_id = b.content_id left join (select collect_id,content_id from t_tc_user_collect where user_id=?) e on a.content_id = e.content_id LEFT JOIN t_user c on a.user_id= c.user_id LEFT JOIN t_tc_target d on a.target_id=d.target_id where a.target_id=? and a.school_id = ? order by release_time DESC",
						userId, userId, targetId, schoolId);

		List<TcContent> contentList = page.getList();
		renderJson(new DataResponse<List<TcContent>>(Code.SUCCESS,
				page.getTotalRow() + "", contentList));

	}

	/**
	 * 获取用户收藏的吐槽内容
	 * 
	 */
	public void getCollectedTcContentList() {
		String userId = getPara("userId");
		int pageNum = getParaToInt("pageNum", 1);
		if (StringUtil.isEmpty(userId)) {
			renderParamsError("userId is null");
			return;
		}

		Page<TcContent> page = TcContent.dao
				.paginate(
						pageNum,
						20,
						"select a.*,b.collect_id,c.avatar,c.nick_name,d.target_name",
						"from t_tc_user_collect b "
								+ "LEFT JOIN t_tc_content a on b.content_id = a.content_id "
								+ "LEFT JOIN t_user c on a.user_id = c.user_id "
								+ "LEFT JOIN t_tc_target d on d.target_id = a.target_id "
								+ "where b.user_id=? order by a.release_time DESC",
						userId);

		// message 保存总数
		List<TcContent> contentList = page.getList();
		renderJson(new DataResponse<List<TcContent>>(Code.SUCCESS,
				page.getTotalRow() + "", contentList));

	}

	/**
	 * 吐槽内容举报
	 */
	public void contentReport() {
		String userId = getPara("userId");
		String contentId = getPara("contentId");
		String commentId = getPara("commentId");
		int type = getParaToInt("type");

		if (!renderParamsNull(new Require().put(userId, "userId is null")
				.put(contentId, "contentId is null").put(type, "type is null")))
			return;
		String strSql = "INSERT INTO `tc`.`t_report` (`content_id`, `user_id`, `type`,`date`,comment_id) VALUES (?, ?, ?,?,?)";
		Db.update(strSql, contentId, userId, type, DateUtil.getCurrentTime(),
				commentId);
		renderSuccess();
	}

	/**
	 * 获取指定用户发布的吐槽内容
	 * 
	 */
	public void getTcContentListByUserId() {
		String userId = getPara("userId");
		int pageNum = getParaToInt("pageNum", 1);
		if (StringUtil.isEmpty(userId)) {
			renderParamsError("userId is null");
			return;
		}

		Page<TcContent> page = TcContent.dao
				.paginate(
						pageNum,
						20,
						"select a.*,b.like_id,e.collect_id,c.avatar,c.user_id,c.nick_name,d.target_name",
						"from t_tc_content a left join (select like_id,content_id from t_tc_user_like where user_id=?) b on a.content_id = b.content_id left join (select collect_id,content_id from t_tc_user_collect where user_id=?) e on a.content_id = e.content_id LEFT JOIN t_user c on a.user_id= c.user_id LEFT JOIN t_tc_target d on a.target_id=d.target_id where a.user_id = ? order by release_time DESC",
						userId, userId, userId);

		// message 保存总数
		List<TcContent> contentList = page.getList();
		renderJson(new DataResponse<List<TcContent>>(Code.SUCCESS,
				page.getTotalRow() + "", contentList));

	}

	/**
	 * 获取用户收到的点赞信息
	 * 
	 */
	public void getLikeInfo() {
		String userId = getPara("userId");
		int pageNum = getParaToInt("pageNum", 1);
		if (StringUtil.isEmpty(userId)) {
			renderParamsError("userId is null");
			return;
		}

		Page<TcContent> page = TcContent.dao
				.paginate(
						pageNum,
						20,
						"select a.user_id as from_user_id,b.*,c.nick_name,c.avatar,a.like_time,d.target_name",
						"from t_tc_user_like a,t_tc_content b LEFT JOIN t_tc_target d on b.target_id = d.target_id,t_user c where a.user_id = c.user_id and b.user_id = ? and b.content_id = a.content_id  and a.user_id !=? order by like_time",
						userId, userId);

		// message 保存总数
		List<TcContent> contentList = page.getList();
		List<Record> recordList = new ArrayList<Record>();
		for (TcContent content : contentList) {
			Record record = new Record()
					.set("from_user_id", content.get("from_user_id", null))
					.set("avatar", content.get("avatar", null))
					.set("nick_name", content.get("nick_name", null))
					.set("like_time", content.get("like_time", null))
					.set("content", content);
			recordList.add(record);

		}

		renderJson(new DataResponse<List<Record>>(Code.SUCCESS,
				page.getTotalRow() + "", recordList));

	}

	/**
	 * 获取用户收到的评论信息
	 * 
	 */
	public void getCommentInfo() {
		String userId = getPara("userId");
		int pageNum = getParaToInt("pageNum", 1);
		if (StringUtil.isEmpty(userId)) {
			renderParamsError("userId is null");
			return;
		}

		Page<TcContent> page = TcContent.dao
				.paginate(
						pageNum,
						20,
						"select a.comment_id,a.from_user_id,a.comment_content,a.comment_time,a.anonymous comment_anonymous,b.*,c.nick_name,c.avatar,d.target_name",
						"from t_tc_content_comment a,t_tc_content b LEFT JOIN t_tc_target d on b.target_id = d.target_id,t_user c where (b.user_id = ? or to_user_id = ?) and b.content_id = a.content_id and from_user_id !=? and c.user_id = from_user_id order by comment_time desc",
						userId, userId, userId);

		// message 保存总数
		List<TcContent> contentList = page.getList();
		List<Record> recordList = new ArrayList<Record>();
		for (TcContent content : contentList) {
			Record record = new Record()
					.set("comment_id", content.get("comment_id", null))
					.set("from_user_id", content.get("from_user_id", null))
					.set("avatar", content.get("avatar", null))
					.set("comment_content",
							content.get("comment_content", null))
					.set("comment_time", content.get("comment_time", null))
					.set("nick_name", content.get("nick_name", null))
					.set("comment_anonymous", content.get("comment_anonymous", null))
					.set("content", content);
			recordList.add(record);

		}
		renderJson(new DataResponse<List<Record>>(Code.SUCCESS,
				page.getTotalRow() + "", recordList));

	}

	/**
	 * 获取指定用户的评论列表
	 */
	public void getCommentByUserId() {
		String userId = getPara("userId");
		int pageNum = getParaToInt("pageNum", 1);
		if (StringUtil.isEmpty(userId)) {
			renderParamsError("userId is null");
			return;
		}
		Page<Comment> page = Comment.dao
				.paginate(
						pageNum,
						20,
						"select a.comment_id,a.comment_content,a.comment_time,a.to_user_id,a.to_comment_content,a.to_nick_name,c.nick_name,b.*",
						"from t_tc_content_comment a,t_tc_content b,t_user c where from_user_id = ? and a.content_id=b.content_id and b.user_id = c.user_id order by comment_time desc",
						userId);
		List<Comment> commentList = page.getList();
		List<Record> recordList = new ArrayList<Record>();
		for (Comment comment : commentList) {
			Record record = new Record()
					.set("comment_id", comment.get("comment_id", null))
					.set("comment_content",
							comment.get("comment_content", null))
					.set("comment_time", comment.get("comment_time", null))
					.set("to_user_id", comment.get("to_user_id", null))
					.set("to_comment_content",
							comment.get("to_comment_content", null))
					.set("to_nick_name", comment.get("to_nick_name", null))
					.set("content", comment);
			recordList.add(record);

		}
		renderJson(new DataResponse<List<Record>>(Code.SUCCESS,
				page.getTotalRow() + "", recordList));

	}

	/**
	 * 删除吐槽内容
	 */
	public void deleteTcContent() {
		String contentId = getPara("contentId");
		String fromUserId = getPara("fromUserId");

		if (StringUtil.isEmpty(contentId)) {
			renderParamsError("contentId is null");
			return;
		}

		String strSql = "select user_id from t_tc_content where content_id = ?";

		Record record = Db.findFirst(strSql, contentId);
		String userId = record.get("user_id") + "";
		if (!StringUtil.isEmpty(fromUserId) && !fromUserId.equals(userId)) {
			CommonUtil.sendSystemMessage(Constant.deleteTcContentText, userId);
		}
		strSql = "delete from t_tc_content where content_id = ?";
		Db.update(strSql, contentId);
		renderSuccess();
	}

	/**
	 * 删除评论
	 */
	public void deleteComment() {
		String commentId = getPara("commentId");
		String fromUserId = getPara("fromUserId");
		if (StringUtil.isEmpty(commentId)) {
			renderParamsError("commentId is null");
			return;
		}

		String strSql = "select content_id,from_user_id from t_tc_content_comment where comment_id = ?";
		Record record = Db.findFirst(strSql, commentId);
		String content_id = record.get("content_id") + "";
		minusCommentCount(content_id);

		String userId = record.get("from_user_id") + "";
		if (!StringUtil.isEmpty(fromUserId) && !fromUserId.equals(userId)) {
			CommonUtil.sendSystemMessage(Constant.deleteCommentText, userId);
		}

		strSql = "delete from t_tc_content_comment where comment_id = ?";
		Db.update(strSql, commentId);

		renderSuccess();
	}

	/**
	 * 找回密码
	 * 
	 * @throws Exception
	 */
	@Clear
	public void recoverPassword() throws Exception {
		String username = getPara("username");
		String strCode = getPara("code");
		String password = getPara("password");
		if (!renderParamsNull(new Require().put(username, "phone is null")
				.put(strCode, "code is null").put(password, "password is null")))
			return;

		String strSql = "select * from t_user where user_name=?";
		User user = User.dao.findFirst(strSql, username);
		if (user == null) {
			renderJson(new BaseResponse(Code.ACCOUNT_NOT_EXISTS,
					"the username is not exists"));
			return;
		}

		if (renderCodeIsCorrect(username, strCode)) {
			user.setPassword(password);
			user.update();

			renderSuccess();
		}

	}

}
