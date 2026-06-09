package com.quanlycafe.cafe_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ChiTietThucDonId
 * Version 1.0
 * Date: 07-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 07-06-2026 lhthoai      Create Composite Key cho ChiTietThucDon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietThucDonId implements Serializable {
    private Integer maHangHoa;
    private Integer maThucDon;
}