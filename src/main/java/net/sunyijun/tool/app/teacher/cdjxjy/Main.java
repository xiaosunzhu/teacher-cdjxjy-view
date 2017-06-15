package net.sunyijun.tool.app.teacher.cdjxjy;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger LOG = LoggerFactory.getLogger(OpenCourse.class);

	static CookieStore cookieStore = new BasicCookieStore();
	static PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
	static RequestConfig requestConfig;
	static HttpClient httpClient;

	static final String VIEWSTATE = "__VIEWSTATE";
	static final String VIEWSTATEGENERATOR = "__VIEWSTATEGENERATOR";
	static final String EVENTVALIDATION = "__EVENTVALIDATION";

	static final int NEED_STUDY_MINUTES = 50;

	static String viewState;
	static String viewStateGen;
	static String eventValidation;

	private static Login login = new Login();
	private static GetSelectCourse getSelectCourse = new GetSelectCourse();
	private static OpenCourse openCourse = new OpenCourse();
	private static CommentCourse commentCourse = new CommentCourse();

	public static void main(String[] args) throws IOException, InterruptedException {
		setupClient();

		login.login();

		while (true) {
			Thread.sleep(1000);
			Course course;
			try {
				course = getSelectCourse.getSelectCourse();
				if (course == null) {
					break;
				}
			} catch (Throwable e) {
				LOG.warn("getSelectCourse error in main.", e);
				Thread.sleep(5000);
				continue;
			}

//			course.cid="3ecee30e-fa81-4f11-af0b-3f6fcc7b70ac";
//			course.scid="369f25a3-c2e4-4c84-992f-8856b25c2633";
//			course.commentOK=false;
//			course.studyMinutes=0;

			Thread.sleep(1000);
			try {
				openCourse.open(course);
				if (course.studyMinutes < NEED_STUDY_MINUTES) {
					openCourse.play(course);
				}
				Thread.sleep(2000);
				if (!course.commentOK) {
					commentCourse.comment(course);
				}
			} catch (Throwable e) {
				LOG.warn("catch error in main.", e);
			} finally {
				try {
					openCourse.updateStudy(course);
					LOG.info("updated study.");
				} catch (Throwable e) {
					LOG.warn("updateStudy error in main.", e);
				}
			}
		}
		LOG.info("All course have been studied.");
	}

	private static void setupClient() {
//		connectionManager.setDefaultMaxPerRoute(20);
		connectionManager.setValidateAfterInactivity(10000);
		requestConfig = RequestConfig.custom().setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000).build();
		httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore)
				.setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).build();
	}

	static void setExtendDatas(Document document) {
		Element form = document.body().getElementById("form1");
		Element viewStateElement = form.getElementById(Main.VIEWSTATE);
		Element viewStateGenElement = form.getElementById(Main.VIEWSTATEGENERATOR);
		Element eventValidationElement = form.getElementById(Main.EVENTVALIDATION);
		viewState = viewStateElement.val();
		viewStateGen = viewStateGenElement.val();
		eventValidation = eventValidationElement.val();
	}

	static List<NameValuePair> generateExtendDataList() {
		List<NameValuePair> datas = new ArrayList<NameValuePair>();
		datas.add(new BasicNameValuePair(VIEWSTATE, viewState));
		datas.add(new BasicNameValuePair(VIEWSTATEGENERATOR, viewStateGen));
		datas.add(new BasicNameValuePair(EVENTVALIDATION, eventValidation));
		return datas;
	}
}
