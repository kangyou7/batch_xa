/* Copyright PT. Indo Lotte Makmur, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential copying of this file, via any medium is strictly prohibited Proprietary and confidential
 * 
 * Written by ECM project team
 */
package com.glyde.mall.batch.config.entity;

import lombok.Data;

@Data
public class BatchJob  {

    private static final long serialVersionUID = -4174478941545344617L;

    private String batchId;
    private String batchNm;
    private String batchSvrNm;
    private String srcSysNm;
    private String tgtSysNm;
    private String usePsblStrtDtm;
    private String usePsblEndDtm;
    private String batchExeYn;
    private String batchExeStrtDtm;
    private String batchExeEndDtm;
    private String batchExeCycleMinuteVal;
    private String batchExeCycleHhVal;
    private String batchExeCycleDdVal;
    private String batchExeCycleMmVal;
    private String batchExeCycleDaywVal;
    private String batchExeCycleDscr;
    private String useYn;
    private String lstModpId;

}
