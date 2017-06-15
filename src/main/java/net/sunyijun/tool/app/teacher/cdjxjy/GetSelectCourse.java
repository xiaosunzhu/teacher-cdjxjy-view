package net.sunyijun.tool.app.teacher.cdjxjy;


import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetSelectCourse {

	private static final Logger LOG = LoggerFactory.getLogger(GetSelectCourse.class);

	private static final String SCRIPT_MANAGER_KEY = "ScriptManager1";
	private static final String SCRIPT_MANAGER = "UpdatePanel1|AspNetPager1";
	private static final String EVENT_TARGET_KEY = "__EVENTTARGET";
	private static final String EVENT_TARGET = "AspNetPager1";
	private static final String EVENT_ARG_KEY = "__EVENTARGUMENT";
	private static final String EVENT_ARG = "undefined";
	private static final String ASYNC_POST_KEY = "__ASYNCPOST";
	private static final String ASYNC_POST = "true";

	private static final String HF_COURSE_ID_KEY = "hfcourseid";
	private static final String HF_COURSE_ID = "";
	private static final String HF_STU_COURSE_GUID_KEY = "hfStudentCoursesGuid";
	private static final String HF_STU_COURSE_GUID = "";
	private static final String HF_CREATE_DATE_KEY = "hfCreateDate";
	private static final String HF_CREATE_DATE = "";
	private static final String HF_SAVE_GUID_KEY = "hdSaveGuid";
	private static final String HF_SAVE_GUID = "";

	Course getSelectCourse() throws IOException {
		HttpGet httpGet = new HttpGet("http://www.cdjxjy.com/student/SelectCourseRecord.aspx");
		ConstHeader.setNormalHeaders(httpGet, "http://www.cdjxjy.com/IndexMain.aspx", true);

		httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		httpGet.removeHeaders("Origin");
		httpGet.removeHeaders("Content-Type");
		httpGet.removeHeaders("Cache-Control");

		HttpResponse selectedCoursePageResponse = Main.httpClient.execute(httpGet);
		String selectedCoursePage = EntityUtils.toString(selectedCoursePageResponse.getEntity(), "UTF-8");
		EntityUtils.consume(selectedCoursePageResponse.getEntity());
		Document selectedCourseDoc = Jsoup.parse(selectedCoursePage);
		Main.setExtendDatas(selectedCourseDoc);

		Element panelDiv = selectedCourseDoc.getElementById("UpdatePanel1");
		Elements allCoursesCommentTd = panelDiv.getElementsMatchingOwnText("学习记录：");
		if (allCoursesCommentTd == null || allCoursesCommentTd.isEmpty()) {
			return null;
		}

		Element courseCommentTd = allCoursesCommentTd.get(0);
		Course course = new Course();
		String text = courseCommentTd.child(0).text();
		if (text.contains("已填写")) {
			course.commentOK = true;
		}

		Element operateTr = courseCommentTd.parent().previousElementSibling();
		String allCoursesStudyTimeText = operateTr.getElementsMatchingOwnText("学习累计时间：").get(0).child(0).text();
		String studyTimeStr = allCoursesStudyTimeText.replace("分钟", "");
		try {
			course.studyMinutes = Integer.parseInt(studyTimeStr);
		} catch (Exception e) {
			course.studyMinutes = Main.NEED_STUDY_MINUTES;
		}
		String[] elements = operateTr.getElementsMatchingOwnText("课程学习").attr("onclick").split("'");
		course.cid = elements[1];
		course.scid = elements[3];

		LOG.info("[ChooseCourse] - " + selectedCoursePageResponse.getStatusLine().getStatusCode()
				+ ", courseId=" + course.cid + ", studyMinutes=" + course.studyMinutes + ", haveComment=" + course.commentOK);
		return course;
	}

	private void setDatas(List<NameValuePair> datas) {
		datas.add(new BasicNameValuePair(SCRIPT_MANAGER_KEY, SCRIPT_MANAGER));
		datas.add(new BasicNameValuePair(EVENT_TARGET_KEY, EVENT_TARGET));
		datas.add(new BasicNameValuePair(EVENT_ARG_KEY, EVENT_ARG));
		datas.add(new BasicNameValuePair(ASYNC_POST_KEY, ASYNC_POST));
		datas.add(new BasicNameValuePair(HF_COURSE_ID_KEY, HF_COURSE_ID));
		datas.add(new BasicNameValuePair(HF_STU_COURSE_GUID_KEY, HF_STU_COURSE_GUID));
		datas.add(new BasicNameValuePair(HF_CREATE_DATE_KEY, HF_CREATE_DATE));
		datas.add(new BasicNameValuePair(HF_SAVE_GUID_KEY, HF_SAVE_GUID));
		datas.add(new BasicNameValuePair(EVENT_ARG_KEY, EVENT_ARG));
		datas.add(new BasicNameValuePair(EVENT_ARG_KEY, EVENT_ARG));
	}

}
