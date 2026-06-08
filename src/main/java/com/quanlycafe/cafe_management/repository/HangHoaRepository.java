package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.HangHoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * HangHoaRepository
 * Version 1.3
 * Date: 08-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 30-05-2026 lhthoai      Apply Pagination (Pageable)
 * 08-06-2026 lhthoai      Remove hardcoded OrderBy to allow dynamic sorting
 */
@Repository
public interface HangHoaRepository extends JpaRepository<HangHoa, Integer> {

    /**
     * Tìm kiếm hàng hóa theo tên (Có phân trang và sắp xếp động)
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<HangHoa>
     */
    Page<HangHoa> findByTenHangHoaContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * Lọc theo đơn vị tính (Có phân trang và sắp xếp động)
     *
     * @param keyword     String
     * @param maDonViTinh Integer
     * @param pageable    Pageable
     * @return Page<HangHoa>
     */
    Page<HangHoa> findByTenHangHoaContainingIgnoreCaseAndDonViTinh_MaDonViTinh(String keyword, Integer maDonViTinh, Pageable pageable);
}