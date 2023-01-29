package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {
    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        Member member = createMember("회원1");

        Book book = createBook("JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        Order getOrder = orderRepository.findOne(orderId);

        Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
        Assertions.assertEquals(1, getOrder.getOrderitems().size(), "주문한 상품 종류 수가 정확해야 한다");
        Assertions.assertEquals(10000 * orderCount, getOrder.getTotalPrice(),"주문 가격은 가격 * 수량이다");
        Assertions.assertEquals(8, book.getStockQuantity(),"주문 수량만큼 재고가 줄어야 한다");
    }

    @Test
    public void 상품주문_재고수량초과() throws Exception{
        Member member = createMember("회원1");
        Book book = createBook("JPA", 10000, 10);

        int orderCount = 11;

        Assertions.assertThrows(NotEnoughStockException.class, ()->orderService.order(member.getId(), book.getId(), orderCount));
    }

    @Test
    public void 주문취소() throws Exception{
        Member member = createMember("회원1");
        Book book = createBook("JPA", 10000, 10);
        int orderCount = 2;

        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        orderService.cancelOrder(orderId);

        Order order = orderRepository.findOne(orderId);
        
        Assertions.assertEquals(order.getStatus(), OrderStatus.CANCEL, "취소된 주문의 상태는 CANCEL 이다");
        Assertions.assertEquals(10, book.getStockQuantity(), "주문이 취소되면 재고가 원복되어야 한다");

    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember(String name) {
        Member member = new Member();
        member.setName(name);
        member.setAddress(new Address("서울", "연희로", "111-222"));
        em.persist(member);
        return member;
    }
}

