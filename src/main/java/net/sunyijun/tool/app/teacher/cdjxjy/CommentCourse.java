package net.sunyijun.tool.app.teacher.cdjxjy;


import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommentCourse {

	private static final Logger LOG = LoggerFactory.getLogger(CommentCourse.class);

	private static final String HF_STU_COURSE_GUID_KEY = "hfStudentCoursesGuid";
	private static final String HF_CREATE_DATE_KEY = "hfCreateDate";
	private static final String HF_SAVE_GUID_KEY = "hdSaveGuid";
	private static final String HF_COURSE_ID_KEY = "hfcourseid";
	private static final String HF_COURSE_ID = "";
	private static final String HF_STU_COURSE_GUID = "";
	private static final String HF_CREATE_DATE = "";
	private static final String HF_SAVE_GUID = "";

	private static final String SCRIPT_MANAGER = "UpdatePanel3|btnaddRecord";
	private static final String EVENT_TARGET = "";
	private static final String HF_LOOK_NUM = "";

	private static final String HF_SCORE = "5";

	private static final String BTN_ADD_KEY = "btnaddRecord";
	private static final String BTN_ADD = "提交记录";

	void comment(Course course) throws IOException {
		HttpPost httpPost = new HttpPost(
				"http://www.cdjxjy.com/Student/CoursePlay.aspx?" + "cid=" + course.cid + "&scid=" + course.scid);
		ConstHeader.setExtendHeaders(httpPost,
				"http://www.cdjxjy.com/Student/CoursePlay.aspx?" + "cid=" + course.cid + "&scid=" + course.scid, true);

		List<NameValuePair> datas = Main.generateExtendDataList();
		setDatas(datas, course);

		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(datas, "UTF-8");
		httpPost.setEntity(uefEntity);
		HttpResponse commentCourseResponse = Main.httpClient.execute(httpPost);
		EntityUtils.consume(commentCourseResponse.getEntity());
		LOG.info("[Comment] - " + commentCourseResponse.getStatusLine().getStatusCode() + ", courseId=" + course.cid);
//		System.out.println(EntityUtils.toString(commentCourseResponse.getEntity(), "UTF-8"));
	}

	private static void setDatas(List<NameValuePair> datas, Course course) {
		String defaultComment =
				"目标明确，思路清晰重点突出，教师的引导很有效，课堂活跃度高，学生对文章的理解也较为深入。";
		if (course.lastComment != null && course.lastComment.length() > 25) {
			defaultComment = course.lastComment;
		}

		ConstDataKeys
				.setCourseDatas(datas, course, SCRIPT_MANAGER, EVENT_TARGET, course.allTime, HF_LOOK_NUM,
						defaultComment,
						HF_SCORE);

		datas.add(new BasicNameValuePair(HF_COURSE_ID_KEY, HF_COURSE_ID));
		datas.add(new BasicNameValuePair(HF_STU_COURSE_GUID_KEY, HF_STU_COURSE_GUID));
		datas.add(new BasicNameValuePair(HF_CREATE_DATE_KEY, HF_CREATE_DATE));
		datas.add(new BasicNameValuePair(HF_SAVE_GUID_KEY, HF_SAVE_GUID));
		datas.add(new BasicNameValuePair(BTN_ADD_KEY, BTN_ADD));
	}

}
