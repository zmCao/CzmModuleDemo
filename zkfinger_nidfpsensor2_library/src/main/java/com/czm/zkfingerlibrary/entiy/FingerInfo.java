package com.czm.zkfingerlibrary.entiy;

public class FingerInfo {
    /**
     * 最长20个字符
     */
    private Long fingerId;
    private String prisonerId;
    /**
     * byte[] hex字符串
     */
    private String templateBuffer;
    public FingerInfo(Long fingerId, String prisonerId, String templateBuffer) {
        this.fingerId = fingerId;
        this.prisonerId = prisonerId;
        this.templateBuffer = templateBuffer;
    }
    public FingerInfo() {
    }
    public Long getFingerId() {
        return this.fingerId;
    }
    public void setFingerId(Long fingerId) {
        this.fingerId = fingerId;
    }
    public String getPrisonerId() {
        return this.prisonerId;
    }
    public void setPrisonerId(String prisonerId) {
        this.prisonerId = prisonerId;
    }
    public String getTemplateBuffer() {
        return this.templateBuffer;
    }
    public void setTemplateBuffer(String templateBuffer) {
        this.templateBuffer = templateBuffer;
    }
}
