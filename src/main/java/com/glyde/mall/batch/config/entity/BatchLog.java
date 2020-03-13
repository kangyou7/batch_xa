/* Copyright PT. Indo Lotte Makmur, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential copying of this file, via any medium is strictly prohibited Proprietary and confidential
 * 
 * Written by ECM project team
 */

package com.glyde.mall.batch.config.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class BatchLog  {

    private static final long serialVersionUID = -1206887308238596776L;

    private String batchExeLogNo;
    private String batchNo;
    private String batchNm;
    private String batchProgStCd;
    private String exeRstMsgCntt;
    private Timestamp batchStrtDtm;
    private Timestamp batchEndDtm;
    private Long wrkCnt;
    private Long succCnt;
    private Long failCnt;
    private String fstRegpId;
    private Timestamp fstRegDtm;
    private String lstModpId;
    private Timestamp lstModDtm;
}