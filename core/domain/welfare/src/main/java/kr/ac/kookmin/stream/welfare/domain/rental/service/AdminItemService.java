package kr.ac.kookmin.stream.welfare.domain.rental.service;

import kr.ac.kookmin.stream.common.PageOffset;
import kr.ac.kookmin.stream.common.PageResult;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.Item;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCategory;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemCreateCommand;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemType;
import kr.ac.kookmin.stream.welfare.domain.rental.domain.ItemUpdateCommand;

/** 운영진 전용 물품 관리. 학생 앱이 쓰는 {@link ItemService}와 분리한다(coding-style.md 2-7절). */
public interface AdminItemService {

    /**
     * 물품을 이름 오름차순(같으면 식별자 오름차순)으로 페이지 번호 단위로 조회한다.
     *
     * @param category 카테고리. null이면 전체
     * @param type     물품 구분. null이면 전체
     * @param keyword  이름 검색어. null이거나 공백이면 검색하지 않는다
     */
    PageResult<Item> search(ItemCategory category, ItemType type, String keyword, PageOffset pageOffset);

    /** 물품을 등록한다. 이름이 중복되면 {@code ITEM_NAME_DUPLICATED}. */
    Item create(ItemCreateCommand command);

    /**
     * 물품 정보를 통째로 수정한다. {@code command.imageKey()}가 null이면 기존 이미지를 유지한다.
     * 존재하지 않으면 {@code ITEM_NOT_FOUND}, 다른 물품과 이름이 겹치면 {@code ITEM_NAME_DUPLICATED}.
     */
    Item update(Long itemId, ItemUpdateCommand command);

    /**
     * 물품을 하드 삭제한다. 존재하지 않으면 {@code ITEM_NOT_FOUND}, 진행 중인 대여 기록이 있으면
     * {@code ITEM_HAS_ACTIVE_RENTAL}.
     */
    void delete(Long itemId);
}
