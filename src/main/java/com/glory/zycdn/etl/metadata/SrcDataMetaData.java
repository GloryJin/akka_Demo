package com.glory.zycdn.etl.metadata;

import java.util.List;


public class SrcDataMetaData {

    private List<ETLTaskMetaData> etlTasks;

    private SrcCommon srcCommon;

    public SrcCommon getSrcCommon() {
        return srcCommon;
    }

    public void setSrcCommon(SrcCommon srcCommon) {
        this.srcCommon = srcCommon;
    }

    public List<ETLTaskMetaData> getEtlTasks() {
        return etlTasks;
    }

    public void setEtlTasks(List<ETLTaskMetaData> etlTasks) {
        this.etlTasks = etlTasks;
    }


    public SrcDataMetaData(String infile_splitby,String infile_suffix,String dealflag,String inpath,List<ETLTaskMetaData> etlTasks,String srcName){
        this.etlTasks = etlTasks;
        this.srcCommon = new SrcCommon(infile_splitby,infile_suffix,dealflag,inpath,srcName);
    }

    public class SrcCommon{
        private String infile_splitby;
        private String infile_suffix;
        private String dealflag;
        private String inpath;
        private String srcName;

        public String getSrcName() {
            return srcName;
        }

        public void setSrcName(String srcName) {
            this.srcName = srcName;
        }

        public SrcCommon(String infile_splitby,String infile_suffix,String dealflag,String inpath,String srcName){
            this.infile_splitby = infile_splitby;
            this.infile_suffix = infile_suffix;
            this.dealflag = dealflag;
            this.inpath = inpath;
            this.srcName = srcName;
        }

        public String getInfile_suffix() {
            return infile_suffix;
        }

        public void setInfile_suffix(String infile_suffix) {
            this.infile_suffix = infile_suffix;
        }

        public String getDealflag() {
            return dealflag;
        }

        public void setDealflag(String dealflag) {
            this.dealflag = dealflag;
        }

        public String getInfile_splitby() {
            return infile_splitby;
        }

        public void setInfile_splitby(String infile_splitby) {
            this.infile_splitby = infile_splitby;
        }

        public String getInpath() {
            return inpath;
        }

        public void setInpath(String inpath) {
            this.inpath = inpath;
        }
    }
}
