package com.camunda.quick.camunda.ext.cmd;

/**
 * 〈〉
 *
 * @author fengtao
 * @create 2021/5/13
 */
public class BaseIllegalException extends IllegalStateException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static BaseIllegalException tooManyBackActivities = new BaseIllegalException(10001, "current task has too many back activities，please user backSpecial method。");

    public static BaseIllegalException backActivitiesWrongType = new BaseIllegalException(10002, "back activities is " +
            "wrong type，back activities must be ActivityImpl and Userbehavior");

    public static final BaseIllegalException NO_BACK_ACTIVITIES = new BaseIllegalException(10003, "current task has none back activities");

    private int code;

    private String msg;

    public BaseIllegalException(int code, String msg) {
        this.code = code;
        this.msg = msg;

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
