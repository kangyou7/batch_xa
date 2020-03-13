/* Copyright PT. Indo Lotte Makmur, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited Proprietary and confidential copying of this file, via any medium is strictly prohibited Proprietary and confidential
 *
 * Written by ECM project team
 */
package com.glyde.mall.batch.config.entity;

import lombok.Data;

@Data
public class BatchInstance {

    private static final long serialVersionUID = 312582321885091419L;

    private String jobInstanceId;
    private int version;
    private String jobName;
    private String jobKey;
    private String executorName;

}
