package com.zte.zycdn.etl.metadata;

import java.util.List;

/**
 * Created by 10190203 on 2017/5/9.
 */
public class ETLTaskMetaData {


    private List<TransMetaData> transFields;
    private ETLTaskCommon etlTaskCommon;

    public ETLTaskCommon getEtlTaskCommon() {
        return etlTaskCommon;
    }

    public void setEtlTaskCommon(ETLTaskCommon etlTaskCommon) {
        this.etlTaskCommon = etlTaskCommon;
    }

    public ETLTaskMetaData(String storetype, String outfile_separator, String outpath,
                           String exporttype, String outfilename, String outsplitnum,
                           List<TransMetaData> transFields, String outfile_suffix, String outfilename_method,
                           String taskName){
        this.transFields = transFields;
        this.etlTaskCommon = new ETLTaskCommon(storetype,outfile_separator,
                outpath,exporttype,outfilename,outsplitnum,outfile_suffix,outfilename_method,taskName);
    }



    public List<TransMetaData> getTransFields() {
        return transFields;
    }

    public void setTransFields(List<TransMetaData> transFields) {
        transFields = transFields;
    }

    public class ETLTaskCommon{
        private String storetype;
        private String outfile_separator;
        private String outpath;
        private String exporttype;
        private String outfilename;
        private String outfilename_method;
        private String outfile_suffix;
        private String outsplitnum;
        private String taskName;

        public ETLTaskCommon(String storetype, String outfile_separator, String outpath,
                             String exporttype, String outfilename, String outsplitnum,
                              String outfile_suffix, String outfilename_method,
                             String taskName){
            this.storetype = storetype;
            this.outfile_separator = outfile_separator;
            this.outpath = outpath;
            this.exporttype = exporttype;
            this.outfilename = outfilename;
            this.outsplitnum = outsplitnum;
            this.outfile_suffix = outfile_suffix;
            this.outfilename_method = outfilename_method;
            this.taskName = taskName;
        }

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getOutfilename_method() {
            return outfilename_method;
        }

        public void setOutfilename_method(String outfilename_method) {
            this.outfilename_method = outfilename_method;
        }

        public String getOutfile_suffix() {
            return outfile_suffix;
        }

        public void setOutfile_suffix(String outfile_suffix) {
            this.outfile_suffix = outfile_suffix;
        }

        public String getStoretype() {
            return storetype;
        }

        public void setStoretype(String storetype) {
            this.storetype = storetype;
        }

        public String getOutfile_separator() {
            return outfile_separator;
        }

        public void setOutfile_separator(String outfile_separator) {
            this.outfile_separator = outfile_separator;
        }

        public String getOutpath() {
            return outpath;
        }

        public void setOutpath(String outpath) {
            this.outpath = outpath;
        }

        public String getExporttype() {
            return exporttype;
        }

        public void setExporttype(String exporttype) {
            this.exporttype = exporttype;
        }

        public String getOutfilename() {
            return outfilename;
        }

        public void setOutfilename(String outfilename) {
            this.outfilename = outfilename;
        }

        public String getOutsplitnum() {
            return outsplitnum;
        }

        public void setOutsplitnum(String outsplitnum) {
            this.outsplitnum = outsplitnum;
        }
    }
}
