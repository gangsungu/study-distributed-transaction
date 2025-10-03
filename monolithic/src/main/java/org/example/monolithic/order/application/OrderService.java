package org.example.monolithic.order.application;

import jakarta.transaction.Transactional;
import java.util.List;
import org.example.monolithic.order.application.dto.CreateOrderCommand;
import org.example.monolithic.order.application.dto.CreateOrderResult;
import org.example.monolithic.order.application.dto.PlaceOrderCommand;
import org.example.monolithic.order.domain.Order;
import org.example.monolithic.order.domain.OrderItem;
import org.example.monolithic.order.infrastructure.OrderItemRepository;
import org.example.monolithic.order.infrastructure.OrderRepository;
import org.example.monolithic.point.application.PointService;
import org.example.monolithic.product.application.ProductService;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final PointService pointService;
    private final ProductService productService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, PointService pointService, ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.pointService = pointService;
        this.productService = productService;
    }

    @Transactional
    public CreateOrderResult createOrder(CreateOrderCommand command) {
        Order order = orderRepository.save(new Order());
        List<OrderItem> orderItems = command.orderItems()
            .stream()
            .map(item -> new OrderItem(order.getId(), item.productId(), item.quantity()))
            .toList();

        orderItemRepository.saveAll(orderItems);
        return new CreateOrderResult(order.getId());
    }

    @Transactional
    public void placeOrder(PlaceOrderCommand command) throws InterruptedException {
        Order order = orderRepository.findById(command.orderId())
            .orElseThrow(() -> new RuntimeException("주문정보가 존재하지 않습니다."));

        if(order.getStatus() == Order.OrderStatus.COMPLETED) {
            return;
        }

        Long totalPrice = 0L;
        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());

        for(OrderItem item : orderItems) {
            Long price = productService.buy(item.getProductId(), item.getQuantity());
            totalPrice += price;
        }

        pointService.use(1L, totalPrice);

        order.complete();
        orderRepository.save(order);

        System.out.println("결제 완료!");
        Thread.sleep(3000);
    }
}
