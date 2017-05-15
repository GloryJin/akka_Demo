package com.glory.zycdn.etl.metadata;


public class TransMetaData {
    private int srcpos;
    private int dstpos;
    private String methodName;

    public TransMetaData(int srcpos,int dstpos,String methodName){
        this.srcpos = srcpos;
        this.dstpos = dstpos;
        this.methodName = methodName;
    }

    public int getSrcpos() {
        return srcpos;
    }

    public void setSrcpos(int srcpos) {
        this.srcpos = srcpos;
    }

    public int getDstpos() {
        return dstpos;
    }

    public void setDstpos(int dstpos) {
        this.dstpos = dstpos;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
