package com.acuity.visualisations.web.service.uploadreports.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder
public class UploadReportSummary {

    /**
     * The date the data started uploading
     */
    private Date date;

    /**
     * Total size of all uploaded files in bytes
     */
    private long filesSize;

    /**
     * Total number of files uploaded
     */
    private int filesCount;

}
