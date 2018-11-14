package com.czm.zkfingerlibrary.entiy;

public class FingerInfo {
    /**
     * 最长20个字符
     */
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public byte[] getTemplateBuffer() {
        return templateBuffer;
    }

    public void setTemplateBuffer(byte[] templateBuffer) {
        this.templateBuffer = templateBuffer;
    }

    private byte[] templateBuffer;
}
