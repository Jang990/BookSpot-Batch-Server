package com.bookspot.batch.global.crawler.kdc;

import com.bookspot.batch.global.crawler.common.CrawlingResult;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import com.bookspot.batch.global.crawler.kdc.css.KdcCssTarget;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class KdcCodeParserTest {
    KdcCodeParser parser = new KdcCodeParser(new KdcTextParser(new KdcParentBookCodeResolver()));
    CrawlingResult crawlingResult = new JsoupCrawler().get(KdcCrawler.KDC_WIKI_URL);

    @Test
    void Top코드를_파싱해온다() {
        List<KdcCode> result = parser.parseTopCodes(crawlingResult, new KdcCssTarget());

        assertThat(result).isEqualTo(
                List.of(
                        new KdcCode(0, "총류", null),
                        new KdcCode(100, "철학", null),
                        new KdcCode(200, "종교", null),
                        new KdcCode(300, "사회과학", null),
                        new KdcCode(400, "자연과학", null),
                        new KdcCode(500, "기술과학", null),
                        new KdcCode(600, "예술", null),
                        new KdcCode(700, "언어", null),
                        new KdcCode(800, "문학", null),
                        new KdcCode(900, "역사", null)
                )
        );
    }

    @Test
    void mid코드를_파싱해온다() {
        KdcCssTarget cssTarget = new KdcCssTarget();
        List<KdcCode> result = parser.parseMidCodes(crawlingResult, cssTarget);

        assertThat(result).isEqualTo(
                List.of(
                        // new KdcCode(0, "총류", 0), // topCode에서 파싱됨
                        new KdcCode(10, "도서학, 서지학", 0),
                        new KdcCode(20, "문헌정보학", 0),
                        new KdcCode(30, "백과사전", 0),
                        new KdcCode(40, "강연집, 수필집, 연설문집", 0),
                        new KdcCode(50, "일반연속간행물", 0),
                        new KdcCode(60, "일반 학회, 단체, 협회, 기관, 연구기관", 0),
                        new KdcCode(70, "신문, 저널리즘", 0),
                        new KdcCode(80, "일반 전집, 총서", 0),
                        new KdcCode(90, "향토자료", 0)
                )
        );
    }

    @Test
    void cssTarget에따라_mid코드값이_바뀐다() {
        KdcCssTarget cssTarget = new KdcCssTarget();
        cssTarget.nextTop();
        cssTarget.nextTop();

        List<KdcCode> result = parser.parseMidCodes(crawlingResult, cssTarget);
        assertThat(result).isEqualTo(
                List.of(
                        // new KdcCode(0, "총류", 0), // topCode에서 파싱됨
                        new KdcCode(210, "비교종교", 200),
                        new KdcCode(220, "불교", 200),
                        new KdcCode(230, "기독교", 200),
                        new KdcCode(240, "도교", 200),
                        new KdcCode(250, "천도교", 200),
                        new KdcCode(270, "힌두교, 브라만교", 200),
                        new KdcCode(280, "이슬람교(회교)", 200),
                        new KdcCode(290, "기타 제종교", 200)
                )
        );
    }

    @Test
    void leaf코드를_파싱해온다() {
        KdcCssTarget cssTarget = new KdcCssTarget();
        List<KdcCode> result = parser.parseLeafCodes(crawlingResult, cssTarget);

        assertThat(result).isEqualTo(
                List.of(
                        new KdcCode(1, "지식 및 학문 일반", 0),
                        new KdcCode(3, "이론 체계 및 시스템", 0),
                        new KdcCode(4, "컴퓨터과학", 0),
                        new KdcCode(5, "프로그래밍, 프로그램, 데이터", 0)
                )
        );
    }

    @Test
    void cssTarget에따라_leaf코드값이_바뀐다() {
        KdcCssTarget cssTarget = new KdcCssTarget();
        cssTarget.nextTop();
        cssTarget.nextMid();
        List<KdcCode> result = parser.parseLeafCodes(crawlingResult, cssTarget);

        assertThat(result).isEqualTo(
                List.of(
                        new KdcCode(111, "방법론", 110),
                        new KdcCode(112, "존재론", 110),
                        new KdcCode(113, "우주론 및 자연철학", 110),
                        new KdcCode(114, "공간", 110),
                        new KdcCode(115, "시간", 110),
                        new KdcCode(116, "운동과 변화", 110),
                        new KdcCode(117, "구조", 110),
                        new KdcCode(118, "힘과 에너지", 110),
                        new KdcCode(119, "물질과 질량", 110)
                )
        );
    }
}