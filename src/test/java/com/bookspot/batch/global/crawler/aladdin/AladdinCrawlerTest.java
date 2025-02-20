package com.bookspot.batch.global.crawler.aladdin;

import com.bookspot.batch.global.crawler.aladdin.bookid.AladdinBookIdFinder;
import com.bookspot.batch.global.crawler.common.JsoupCrawler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AladdinCrawlerTest {

    @Mock AladdinBookIdFinder aladdinBookIdFinder;
    JsoupCrawler jsoupCrawler = new JsoupCrawler();
    AladdinCrawler aladdinCrawler;

    @BeforeEach
    void beforeEach() {
        aladdinCrawler = new AladdinCrawler(jsoupCrawler, aladdinBookIdFinder);
    }

    @Test
    void 책의_디테일_정보_파싱가능() {
        String isbn = "9788998139766";
        when(aladdinBookIdFinder.findBookDetail(anyString()))
                .thenReturn("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=60550259");

        AladdinBookDetail result = aladdinCrawler.findBookDetail(isbn);

        assertEquals("https://image.aladin.co.kr/product/6055/2/cover500/8998139766_1.jpg", result.getImage());
        assertEquals(isbn, result.getIsbn());
        assertEquals("객체지향의 사실과 오해", result.getTitle());
        assertEquals("- 역할, 책임, 협력 관점에서 본 객체지향", result.getSubTitle());
        assertEquals("조영호", result.getAuthor());
        assertEquals("위키북스", result.getPublisher());
//        assertEquals(description, result.getDescription());
//        assertEquals(tableContent, result.getTableOfContents());
        assertEquals(LocalDate.of(2015, 6, 17), result.getPublishedDate());
        assertEquals(260, result.getPageCount());
    }

    final String description = """
            <a href="javascript:fn_hide_introduce('PublisherDesc')"><b>객체지향에 대한 선입견을 버려랴!</b><br>
            <br>
            『객체지향의 사실과 오해』는 객체지향이란 무엇인가라는 원론적면서도 다소 위험한 질문에 답하기 위해 쓰여진 책이다. 안타깝게도 많은 사람들이 객체지향의 본질을 오해하고 있다. 가장 널리 퍼져있는 오해는 클래스가 객체지향 프로그래밍의 중심이라는 것이다. 객체지향으로 향하는 첫 걸음은 클래스가 아니라 객체를 바라보는 것에서부터 시작한다. 객체지향으로 향하는 두 번째 걸음은 객체를 독립적인 존재가 아니라 기능을 구현하기 위해 협력하는 공동체의 일원으로 바라보는 것이다. 세 번째 걸음을 내디딜 수 있는지 여부는 협력에 참여하는 객체들에게 얼마나 적절한 역할과 책임을 부여할 수 있느냐에 달려 있다. 객체지향의 마지막 걸음은 앞에서 설명한 개념들을 프로그래밍 언어라는 틀에 흐트러짐 없이 담아낼 수 있는 기술을 익히는 것이다. <br>
            객체지향이란 무엇인가? 이 책은 이 질문에 대한 답을 찾기 위해 노력하고 있는 모든 개발자를 위한 책이다.<br>
            <br>
            현재의 소프트웨어 개발에 있어 가장 중요한 패러다임이 객체지향이라는 사실에 대부분 이견이 없을 것이다. 절차형 패러다임을 접목한 C++나 함수형 패러다임을 접목한 Scala나 Java 8과 같은 멀티패러다임 언어들이 인기를 끌고 있지만 여전히 그 중심에는 객체지향 패러다임이 위치한다. 그러나 객체지향이 소프트웨어 개발 패러다임에 대한 패권을 쥔 이후로 많은 시간이 흘렀음에도 소프트웨어 개발 커뮤니티는 여전히 객체지향이란 무엇인가라는 질문에 정확하게 대답하지 못하고 있다. <br>
            『객체지향의 사실과 오해』는 객체지향이란 무엇인가라는 원론적면서도 다소 위험한 질문에 답하기 위해 쓰여진 책이다. 안타깝게도 많은 사람들이 객체지향의 본질을 오해하고 있다. 가장 널리 퍼져있는 오해는 클래스가 객체지향 프로그래밍의 중심이라는 것이다. 객체지향으로 향하는 첫 걸음은 클래스가 아니라 객체를 바라보는 것에서부터 시작한다. 객체지향으로 향하는 두 번째 걸음은 객체를 독립적인 존재가 아니라 기능을 구현하기 위해 협력하는 공동체의 일원으로 바라보는 것이다. 세 번째 걸음을 내디딜 수 있는지 여부는 협력에 참여하는 객체들에게 얼마나 적절한 역할과 책임을 부여할 수 있느냐에 달려 있다. 객체지향의 마지막 걸음은 앞에서 설명한 개념들을 프로그래밍 언어라는 틀에 흐트러짐 없이 담아낼 수 있는 기술을 익히는 것이다. <br>
            이 책의 목적은 특정한 기술이나 언어를 설명하는 데 있지 않다. 대신 객체지향적으로 세상을 바라본다는 것이 무엇을 의미하는지를 설명하는 데 있다. 이를 위해 많은 사람들이 가지고 있는 객체지향에 관한 잘못된 편견과 선입견의 벽을 하나씩 무너트려가면서 객체지향이 추구하는 가치를 전달한다. <br>
            이 책을 읽고 나면 기존의 선입견에서 벗어나 다음과 같은 객체지향의 진실과 마주하게 될 것이다.<br>
            <br>
            ◎ 객체지향의 핵심은 역할, 책임, 협력이다.<br>
            ◎ 객체지향 설계의 목표는 자율적인 객체들의 협력 공동체를 만드는 것이다.<br>
            ◎ 객체지향은 클래스를 지향하는 것이 아니라 객체를 지향하는 것이다. 클래스는 단지 구현 메커니즘일 뿐이다.<br>
            ◎ 자율적인 책임이 자율적인 객체와 유연한 설계를 낳는다.<br>
            ◎ 객체지향은 안정적인 도메인 구조에 불안정한 기능을 통합한 것이다.<br>
            ◎ 객체가 메시지를 선택하는 것이 아니라 메시지가 객체를 선택하게 해야 한다.</a>
            """;
    final String tableContent = """
            <p><b>▣ 01장: 협력하는 객체들의 공동체</b><br>
            __협력하는 사람들 <br>
            ____커피 공화국의 아침 <br>
            ____요청과 응답으로 구성된 협력 <br>
            ____역할과 책임<br>
            __역할, 책임, 협력 <br>
            ____기능을 구현하기 위해 협력하는 객체들 <br>
            ____역할과 책임을 수행하며 협력하는 객체들 <br>
            __협력 속에 사는 객체 <br>
            ____상태와 행동을 함께 지닌 자율적인 객체 <br>
            ____협력과 메시지 <br>
            ____메서드와 자율성 <br>
            __객체지향의 본질<br>
            ____객체를 지향하라 <br>
            <br>
            <b>▣ 02장: 이상한 나라의 객체</b><br>
            __객체지향과 인지 능력 <br>
            __객체, 그리고 이상한 나라<br>
            ____이상한 나라의 앨리스 <br>
            ____앨리스 객체 <br>
            __객체, 그리고 소프트웨어 나라<br>
            ____상태 <br>
            ____행동 <br>
            ____식별자<br>
            __기계로서의 객체<br>
            __행동이 상태를 결정한다<br>
            __은유와 객체<br>
            ____두 번째 도시전설 <br>
            ____의인화 <br>
            ____은유  <br>
            ____이상한 나라를 창조하라 <br>
            <br>
            <b>▣ 03장: 타입과 추상화 </b><br>
            __추상화를 통한 복잡성 극복 <br>
            __객체지향과 추상화 <br>
            ____모두 트럼프일 뿐 <br>
            ____그룹으로 나누어 단순화하기 <br>
            ____개념 <br>
            ____개념의 세 가지 관점 <br>
            ____객체를 분류하기 위한 틀 <br>
            ____분류는 추상화를 위한 도구다 <br>
            __타입<br>
            ____타입은 개념이다 <br>
            ____데이터 타입 <br>
            ____객체와 타입 <br>
            ____행동이 우선이다 <br>
            __타입의 계층<br>
            ____트럼프 계층 <br>
            ____일반화/특수화 관계 <br>
            ____슈퍼타입과 서브타입 <br>
            ____일반화는 추상화를 위한 도구다 <br>
            __정적 모델<br>
            ____타입의 목적 <br>
            ____그래서 결국 타입은 추상화다<br>
            ____동적 모델과 정적 모델<br>
            ____클래스<br>
            <br>
            <b>▣ 04장: 역할, 책임, 협력 </b><br>
            __협력<br>
            ____요청하고 응답하며 협력하는 사람들 <br>
            ____누가 파이를 훔쳤지? <br>
            ____재판 속의 협력<br>
            __책임 <br>
            ____책임의 분류<br>
            ____책임과 메시지<br>
            __역할 <br>
            ____책임의 집합이 의미하는 것<br>
            ____판사와 증인<br>
            ____역할이 답이다<br>
            ____협력의 추상화<br>
            ____대체 가능성 <br>
            __객체의 모양을 결정하는 협력 <br>
            ____흔한 오류<br>
            ____협력을 따라 흐르는 객체의 책임<br>
            __객체지향 설계 기법 <br>
            ____책임-주도 설계<br>
            ____디자인 패턴<br>
            ____테스트-주도 개발<br>
            <br>
            <b>▣ 05장: 책임과 메시지 </b><br>
            __자율적인 책임 <br>
            ____설계의 품질을 좌우하는 책임<br>
            ____자신의 의지에 따라 증언할 수 있는 자유<br>
            ____너무 추상적인 책임<br>
            ____'어떻게'가 아니라 '무엇'을<br>
            ____책임을 자극하는 메시지<br>
            __메시지와 메서드 <br>
            ____메시지<br>
            ____메서드<br>
            ____다형성<br>
            ____유연하고 확장 가능하고 재사용성이 높은 협력의 의미<br>
            ____송신자와 수신자를 약하게 연결하는 메시지<br>
            __메시지를 따라라 <br>
            ____객체지향의 핵심, 메시지<br>
            ____책임-주도 설계 다시 살펴보기 <br>
            ____What/Who 사이클<br>
            ____묻지 말고 시켜라<br>
            ____메시지를 믿어라<br>
            __객체 인터페이스 <br>
            ____인터페이스<br>
            ____메시지가 인터페이스를 결정한다<br>
            ____공용 인터페이스<br>
            ____책임, 메시지, 그리고 인터페이스<br>
            __인터페이스와 구현의 분리<br>
            ____객체 관점에서 생각하는 방법 <br>
            ____구현<br>
            ____인터페이스와 구현의 분리 원칙<br>
            ____캡슐화<br>
            __책임의 자율성이 협력의 품질을 결정한다<br>
            <br>
            <b>▣ 06장: 객체 지도</b><br>
            __기능 설계 대 구조 설계 <br>
            __두 가지 재료: 기능과 구조 <br>
            __안정적인 재료: 구조 <br>
            ____도메인 모델<br>
            ____도메인의 모습을 담을 수 있는 객체지향 <br>
            ____표현적 차이<br>
            ____불안정한 기능을 담는 안정적인 도메인 모델<br>
            __불안정한 재료: 기능 <br>
            ____유스케이스<br>
            ____유스케이스의 특성<br>
            ____유스케이스는 설계 기법도, 객체지향 기법도 아니다<br>
            __재료 합치기: 기능과 구조의 통합<br>
            ____도메인 모델, 유스케이스, 그리고 책임-주도 설계 <br>
            ____기능 변경을 흡수하는 안정적인 구조<br>
            <br>
            <b>▣ 07장: 함께 모으기 </b><br>
            __커피 전문점 도메인<br>
            ____커피 주문<br>
            ____커피 전문점이라는 세상<br>
            __설계하고 구현하기<br>
            ____커피를 주문하기 위한 협력 찾기<br>
            ____인터페이스 정리하기<br>
            ____구현하기 <br>
            __코드와 세 가지 관점 <br>
            ____코드는 세 가지 관점을 모두 제공해야 한다 <br>
            ____도메인 개념을 참조하는 이유 <br>
            ____인터페이스와 구현을 분리하라 <br>
            __추상화 기법<br>
            </p>
            """;
}