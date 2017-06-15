package net.sunyijun.tool.app.teacher.cdjxjy;


import java.io.IOException;
import java.util.List;

import net.sunyijun.resource.config.Configs;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Login {

	private static final Logger LOG = LoggerFactory.getLogger(Login.class);

	private static final String USERNAME_KEY = "myusername";
	private static final String PASSWORD_KEY = "mypassword";
	private static final String USERNAME = Configs.getSystemConfig(IConfigs.USER_NAME);
	private static final String PASSWORD = Configs.getSystemConfig(IConfigs.PASSWORD);
//	private static final String USERNAME = "a11039026";
//	private static final String PASSWORD = "19821210";
	private static final String BTN_KEY = "btnlogin";

	void login() throws IOException {
		LOG.info("Username=[" + USERNAME + "], Password=[" + PASSWORD + "]");
		reqeustLoginPage();
		executeLogin();
	}

	private void executeLogin() throws IOException {
		HttpPost httpPost = new HttpPost("http://www.cdjxjy.com/teacherlogin.aspx");
		ConstHeader.setNormalHeaders(httpPost, "http://www.cdjxjy.com/teacherlogin.aspx", true);

		httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpPost.setHeader("Cache-Control", "max-age=0");
		httpPost.setHeader("Connection", "keep-alive");
		httpPost.setHeader("Accept-Encoding", "gzip, deflate");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");

		List<NameValuePair> datas = Main.generateExtendDataList();
		datas.add(new BasicNameValuePair(USERNAME_KEY, USERNAME));
		datas.add(new BasicNameValuePair(PASSWORD_KEY, PASSWORD));
		datas.add(new BasicNameValuePair(BTN_KEY, ""));

		UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(datas, "UTF-8");
		httpPost.setEntity(uefEntity);
		HttpResponse loginResponse = Main.httpClient.execute(httpPost);
		EntityUtils.consume(loginResponse.getEntity());

		LOG.info("[Login] - " + loginResponse.getStatusLine().getStatusCode() + "\r\n    cookies=" + Main.cookieStore.getCookies());
	}

	private void reqeustLoginPage() throws IOException {
		HttpGet httpGet = new HttpGet("http://www.cdjxjy.com/teacherlogin.aspx");
		ConstHeader.setNormalHeaders(httpGet, null, true);

		httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		httpGet.setHeader("Connection", "keep-alive");
		httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
		httpGet.removeHeaders("Origin");
		httpGet.removeHeaders("Content-Type");
		httpGet.removeHeaders("Cache-Control");

		HttpResponse loginPageResponse = Main.httpClient.execute(httpGet);
		String loginPage = EntityUtils.toString(loginPageResponse.getEntity(), "UTF-8");
		Document loginDoc = Jsoup.parse(loginPage);

		Main.setExtendDatas(loginDoc);
		LOG.info("[GetLoginPage] - " + loginPageResponse.getStatusLine().getStatusCode());
	}

}
