package io.github.taichi0373.kumamoto_henno_map.dto.admin;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * AdminPagedResponseDto の単体テスト
 */
class AdminPagedResponseDtoTest {

    /**
     * of() にアイテムリスト・合計数・ページ番号・サイズを渡すと正しいフィールド値を持つDTOが生成される
     */
    @Test
    void of_アイテムと合計とページとサイズを指定すると正しいDTOが生成される() {
        List<String> items = List.of("item1", "item2");

        AdminPagedResponseDto<String> dto = AdminPagedResponseDto.of(items, 100L, 2, 20);

        assertEquals(2, dto.getItems().size());
        assertEquals("item1", dto.getItems().get(0));
        assertEquals(100L, dto.getTotal());
        assertEquals(2, dto.getPage());
        assertEquals(20, dto.getSize());
    }

    /**
     * of() に空リストを渡すと items が空の DTO が生成される
     */
    @Test
    void of_空リストを渡すと空のDTOが生成される() {
        AdminPagedResponseDto<Object> dto = AdminPagedResponseDto.of(List.of(), 0L, 0, 20);

        assertTrue(dto.getItems().isEmpty());
        assertEquals(0L, dto.getTotal());
        assertEquals(0, dto.getPage());
        assertEquals(20, dto.getSize());
    }
}
