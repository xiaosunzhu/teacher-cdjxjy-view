package net.sunyijun.tool.app.teacher.cdjxjy;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

public class ConstDataKeys {


    static final String EVENT_ARG_KEY = "__EVENTARGUMENT";
    static final String EVENT_ARG = ""; //TODO 是否使用返回值
    static final String ASYNC_POST_KEY = "__ASYNCPOST";
    static final String ASYNC_POST = "true";


    static final String HF_XUEXI_TIME_KEY = "hfxuexitime";
    static final String HF_XUEXI_TIME = "";
    static final String HF_IS_NOT_FLV_KEY = "hfisnotflv";
    static final String HF_LOOK_NUM_KEY = "hflooknum";//会变，网页会返回值。源于网页
    static final String HF_COMMENT_KEY = "txtComment";
    static final String HF_COMMENT = "";

    static final String HF_OPEN_TIME_KEY = "hfopenwebtime";
    static final String HFT_COURSE_ID_KEY = "hftcourseid";
    static final String ALL_TIME_KEY = "Alltime";//该值在观看中不会变。源于打开时的网页
    static final String HF_UPDATE_TIME_KEY = "hfupdatetime";//该值在观看中不会变，但网页会返回值。源于网页

    static final String SCRIPT_MANAGER_KEY = "ScriptManager1";
    static final String EVENT_TARGET_KEY = "__EVENTTARGET";
    static final String HF_SCORE_KEY = "hfpjnum";
    static final String HF_CONTENTS_KEY = "txtareainnertContents";
    static final String HF_EXPERIENCE_KEY = "txtareaExperience";
    static final String HF_ALL_TIME_KEY = "hfalltime";//该值在观看中会变。 是所有学习时间 Alltime+newTime

    static void setCourseDatas(List<NameValuePair> datas, Course course, String scriptManager, String eventTarget,
                               String hfAllTime, String lookNum, String defaultComment, String hfScore)
    {
        datas.add(new BasicNameValuePair(EVENT_ARG_KEY, EVENT_ARG));
        datas.add(new BasicNameValuePair(ASYNC_POST_KEY, ASYNC_POST));

        datas.add(new BasicNameValuePair(HF_XUEXI_TIME_KEY, HF_XUEXI_TIME));
        datas.add(new BasicNameValuePair(HF_LOOK_NUM_KEY, lookNum));
        datas.add(new BasicNameValuePair(HF_COMMENT_KEY, HF_COMMENT));

        datas.add(new BasicNameValuePair(HFT_COURSE_ID_KEY, course.cid));
        datas.add(new BasicNameValuePair(ALL_TIME_KEY, course.allTime));
        datas.add(new BasicNameValuePair(HF_UPDATE_TIME_KEY, course.updateTime));
        datas.add(new BasicNameValuePair(HF_OPEN_TIME_KEY, course.openTime));
        datas.add(new BasicNameValuePair(HF_IS_NOT_FLV_KEY, course.isNotFlv));

        datas.add(new BasicNameValuePair(SCRIPT_MANAGER_KEY, scriptManager));
        datas.add(new BasicNameValuePair(EVENT_TARGET_KEY, eventTarget));
        datas.add(new BasicNameValuePair(HF_ALL_TIME_KEY, "" + hfAllTime));
        datas.add(new BasicNameValuePair(HF_SCORE_KEY, hfScore));
        datas.add(new BasicNameValuePair(HF_CONTENTS_KEY, defaultComment));
        datas.add(new BasicNameValuePair(HF_EXPERIENCE_KEY, defaultComment));
    }
}
