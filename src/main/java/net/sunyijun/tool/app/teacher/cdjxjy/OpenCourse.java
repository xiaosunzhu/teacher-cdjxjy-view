package net.sunyijun.tool.app.teacher.cdjxjy;


import java.io.IOException;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

public class OpenCourse {

	private static final Logger LOG = LoggerFactory.getLogger(OpenCourse.class);
	public static final String LINE_SEPARATOR = "\r\n";

	void open(Course course) throws IOException, InterruptedException {
		HttpGet httpGet = new HttpGet(
				"http://www.cdjxjy.com/Student/CoursePlay.aspx?" + "cid=" + course.cid + "&scid=" + course.scid);
		ConstHeader.setNormalHeaders(httpGet, "http://www.cdjxjy.com/IndexMain.aspx", true);
		httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		httpGet.removeHeaders("Origin");
		httpGet.removeHeaders("Content-Type");
		httpGet.removeHeaders("Cache-Control");

		HttpResponse openCoursePageResponse = Main.httpClient.execute(httpGet);
		String openCoursePage = EntityUtils.toString(openCoursePageResponse.getEntity(), "UTF-8");
		EntityUtils.consume(openCoursePageResponse.getEntity());
//        System.out.println(openCoursePage);
		parseHtmlResp(course, openCoursePage);

		String[] responseLines = openCoursePage.split(LINE_SEPARATOR);
		String isAnotherStudy = "0";
		for (String line : responseLines) {
			if (line.contains("var isstate='")) {
				String[] elements = line.split("'");
				isAnotherStudy = elements[1];
				break;
			}
		}
		LOG.info("[OpenCourse] - " + openCoursePageResponse.getStatusLine().getStatusCode() + ", openTime=" +
				course.openTime + ", isNotFlv=" + course.isNotFlv + ", isAnotherStudy=" + isAnotherStudy);

		if ("1".equals(isAnotherStudy)) {
			Thread.sleep(1000);
			updateStudyCurrent(course);
		}
	}

	private static final String STATE_SCRIPT_MANAGER = "UpdatePanel1|lbtnStudentState";
	private static final String STATE_EVENT_TARGET = "lbtnStudentState";

	private void updateStudyCurrent(Course course) throws IOException {
		HttpPost httpPost = new HttpPost(
				"http://www.cdjxjy.com/Student/CoursePlay.aspx?" + "cid=" + course.cid + "&scid=" + course.scid);
		ConstHeader.setExtendHeaders(httpPost,
				"http://www.cdjxjy.com/Student/CoursePlay.aspx?" + "cid=" + course.cid + "&scid=" + course.scid, false);

		httpPost.setHeader("Connection", "keep-alive");

		List<NameValuePair> datas = Main.generateExtendDataList();
		ConstDataKeys
				.setCourseDatas(datas, course, STATE_SCRIPT_MANAGER, STATE_EVENT_TARGET, "", "", "", "");

		String params = URLEncodedUtils.format(datas, "UTF-8");
		StringEntity stringEntity = new StringEntity(params.replaceAll("\\+", "%20") + "&",
				ContentType.create("application/x-www-form-urlencoded", "UTF-8"));
		httpPost.setEntity(stringEntity);

		HttpResponse updateStudyResponse = Main.httpClient.execute(httpPost);

		HttpEntity updateStudyResponseEntity = updateStudyResponse.getEntity();
		String updateStudyResp = EntityUtils.toString(updateStudyResponseEntity, "UTF-8");
		EntityUtils.consume(updateStudyResponseEntity);
		Header contentType = updateStudyResponseEntity.getContentType();
		LOG.info(
				"[UpdateStudyCurrent] - " + updateStudyResponse.getStatusLine().getStatusCode() + " " + contentType);
//		System.out.println(updateStudyResp);

		if (contentType.getValue().contains("html")) {
			parseHtmlResp(course, updateStudyResp);
		} else {
			parsePlainResp(course, updateStudyResp);
		}

	}

	private static final String PLAY_SCRIPT_MANAGER = "UpdatePanel1|lbtnStudentCourse";
	private static final String PLAY_EVENT_TARGET = "lbtnStudentCourse";

	void play(Course course) throws IOException, InterruptedException {
		HttpPost httpPost = new HttpPost(
				"http://www.cdjxjy.com/Student/CoursePlay.aspx?" + "cid=" + course.cid + "&scid=" + course.scid);
		ConstHeader.setExtendHeaders(httpPost,
				"http://www.cdjxjy.com/Student/CoursePlay.aspx?" + "cid=" + course.cid + "&scid=" + course.scid, false);

		httpPost.setHeader("Connection", "keep-alive");

		int hfAllTime = Integer.parseInt(course.allTime);
		String lookNum = "";
		int needLookNum = (3000 - hfAllTime) / 60 / 5;

		LOG.info("[StartPlayCourse] - courseId=" + course.cid + ", lastTime=" + course.allTime);

		while (Integer.parseInt((lookNum.isEmpty() ? "0" : lookNum)) < needLookNum) {
			Thread.sleep(330000);
			hfAllTime += 156;

			List<NameValuePair> datas = Main.generateExtendDataList();
			ConstDataKeys
					.setCourseDatas(datas, course, PLAY_SCRIPT_MANAGER, PLAY_EVENT_TARGET, "" + hfAllTime, lookNum, "",
							"");

			String params = URLEncodedUtils.format(datas, "UTF-8");
			StringEntity stringEntity = new StringEntity(params.replaceAll("\\+", "%20") + "&",
					ContentType.create("application/x-www-form-urlencoded", "UTF-8"));
//            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(datas, "UTF-8");
			httpPost.setEntity(stringEntity);
			HttpResponse playCourseResponse = Main.httpClient.execute(httpPost);

			HttpEntity playCourseResponseEntity = playCourseResponse.getEntity();
			String playCourseResp = EntityUtils.toString(playCourseResponseEntity, "UTF-8");
			EntityUtils.consume(playCourseResponseEntity);
			Header contentType = playCourseResponseEntity.getContentType();
//			System.out.println(playCourseResp);
			if (contentType.getValue().contains("html")) {
				parseHtmlResp(course, playCourseResp);
				continue;
			}

			lookNum = parsePlainResp(course, playCourseResp);
			int looNumCount = Integer.parseInt((lookNum.isEmpty() ? "1" : lookNum));
			LOG.info(
					"[PlayCourse] - " + playCourseResponse.getStatusLine().getStatusCode() + " " + contentType
							+ LINE_SEPARATOR + "    courseId=" + course.cid + ", isNotFlv=" + course.isNotFlv
							+ ", allTime=" + course.allTime + ", hfAllTime=" + hfAllTime + ", lookNum=" + lookNum
							+ ", studyMinutes=" + ((Integer.parseInt(course.allTime) + (looNumCount - 1) * 300) / 60));
		}

	}

	private void parseHtmlResp(Course course, String page) {
		Document doc = Jsoup.parse(page);
		Main.setExtendDatas(doc);

		course.allTime = doc.getElementById(ConstDataKeys.ALL_TIME_KEY).val();
		course.isNotFlv = doc.getElementById(ConstDataKeys.HF_IS_NOT_FLV_KEY).val();
		course.updateTime = doc.getElementById(ConstDataKeys.HF_UPDATE_TIME_KEY).val();
		course.openTime = doc.getElementById(ConstDataKeys.HF_OPEN_TIME_KEY).val();
		Elements commentPanels = doc.getElementById("UpdatePanel3").getElementsMatchingOwnText("体会：");
		if (!commentPanels.isEmpty()) {
			course.lastComment = commentPanels.get(0).parent().nextElementSibling().text();
		}
	}

	void updateStudy(Course course) throws IOException {
		HttpPost httpPost = new HttpPost(
				"http://www.cdjxjy.com/Student/CoursePlay.aspx/UpdateIsStudyState");
		ConstHeader.setJsonHeaders(httpPost, "http://www.cdjxjy.com/student/SelectCourseRecord.aspx", false);

		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");
		httpPost.removeHeaders("X-MicrosoftAjax");
		httpPost.removeHeaders("Cache-Control");

		JsonObject jsonParam = new JsonObject();
		jsonParam.addProperty("SelectClassGuid", course.scid);
		StringEntity entity = new StringEntity(jsonParam.toString().replaceAll("\"", "'"), "utf-8");
//		entity.setContentEncoding("UTF-8");
		entity.setContentType("application/json");
		httpPost.setEntity(entity);

		HttpResponse closeCourseResponse = Main.httpClient.execute(httpPost);
		EntityUtils.consume(closeCourseResponse.getEntity());
		LOG.info("[CloseCourse] - " + closeCourseResponse.getStatusLine().getStatusCode() + ", courseId=" + course.cid);
	}

	private String parsePlainResp(Course course, String playCourseResp) {
		String lookNum = "";
		String[] lines = playCourseResp.split(LINE_SEPARATOR);
		for (String line : lines) {
			if (line.contains("name=\"" + ConstDataKeys.HF_UPDATE_TIME_KEY)) {
				String[] elements = line.split("\"");
				if (elements.length == 9) {
					course.updateTime = elements[7];
				}
			} else if (line.contains("name=\"" + ConstDataKeys.HF_LOOK_NUM_KEY)) {
				String[] elements = line.split("\"");
				if (elements.length == 9) {
					lookNum = elements[7];
				}
			} else if (line.contains("hiddenField")) {
				String[] elements = line.split("\\|");
				parseHidden(elements);
			}
		}
		return lookNum;
	}

	private void parseHidden(String[] elements) {
		for (int i = 0; i < elements.length; i++) {
			switch (elements[i]) {
				case Main.VIEWSTATE: {
					Main.viewState = parseHiddenValue(elements, i);
					i += 3;
					break;
				}
				case Main.EVENTVALIDATION: {
					Main.eventValidation = parseHiddenValue(elements, i);
					i += 3;
					break;
				}
				case Main.VIEWSTATEGENERATOR: {
					Main.viewStateGen = parseHiddenValue(elements, i);
					i += 3;
					break;
				}
			}
		}
	}

	private String parseHiddenValue(String[] elements, int i) {
		return elements[i + 1].substring(0, Integer.parseInt(elements[i - 2]));
	}
}
