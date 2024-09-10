package hello.gccoffee.service;



import hello.gccoffee.entity.Order;
import hello.gccoffee.entity.Product;
import hello.gccoffee.repository.ProductRepository;

import hello.gccoffee.dto.OrderItemDTO;
import hello.gccoffee.entity.OrderItem;
import hello.gccoffee.exception.OrderException;
import hello.gccoffee.repository.OrderItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository; //문제점1

    // orderId(혹은 order)에 해당하는 상품들 조회
    public List<OrderItemDTO> getAllItems(int orderId) {
        log.info("OrderItemService ===> getAllItems() ");

        List<OrderItem> orderItemList = orderItemRepository.findByOrderId(orderId).orElseThrow(OrderException.NOT_FOUND_ORDERID::get);
        log.info("orderItemList: " + orderItemList);


        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        if (orderItemList == null) {
            return orderItemDTOS;
        }

        orderItemList.forEach(orderItem -> {
            orderItemDTOS.add(OrderItemDTO.builder()
                    .email(orderItem.getOrder().getEmail())
                    .address(orderItem.getOrder().getAddress())
                    .postcode(orderItem.getOrder().getPostcode())
                    .productName(orderItem.getProduct().getProductName())
                    .price(orderItem.getPrice())
                    .quantity(orderItem.getQuantity())
                    .category(orderItem.getCategory())
                    .build());
        });
        return orderItemDTOS;
    }
    public List<OrderItem> addItems(Order order, List<OrderItemDTO> items) {

            try {
                List<OrderItem> orderItemList = new ArrayList<>();
                for (OrderItemDTO item : items) {
                //문제1 productName을 productId로 변환하기 위해 productRepository에 의존하는 게 맞는가?
                String productName = item.getProductName();
                Product product = productRepository.findByProductName(productName);
                int productId = product.getProductId();

                OrderItem orderItem = item.toEntity(productId, order.getOrderId());

                if(orderItem.getOrderItemId()!=order.getOrderId()){
                        return null;
                }
                orderItemRepository.save(orderItem);
                order.addOrderItems(orderItem);
                }
                    return orderItemList;
            } catch (Exception e) {
                throw OrderException.ORDER_ITEM_NOT_REGISTERED.get();
            }

    }
}
