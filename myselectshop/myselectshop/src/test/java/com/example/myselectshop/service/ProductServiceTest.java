package com.example.myselectshop.service;


import com.example.myselectshop.dto.ProductMypriceRequestDto;
import com.example.myselectshop.dto.ProductRequestDto;
import com.example.myselectshop.entity.Product;
import com.example.myselectshop.entity.User;
import com.example.myselectshop.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static com.example.myselectshop.service.ProductService.MIN_MY_PRICE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock //  (1) @Mock 어노테이션으로 모킹할 객체 표기
    ProductRepository productRepository;

    @InjectMocks //  (2) @InjectMock 어노테이션으로 모킹한 객체를 주입해주는 코드. 서비스에 있는 레포에는 가짜 객체가 들어감
    ProductService productService;

    @Mock
    User user;
    // @InjectMocks = 서비스부분 / @Mock = Repository 부분

    @Test
    @DisplayName("관심 상품 희망가 - 최저가 이상으로 변경") // 정상케이스
    void updateProduct_Success() {
        // given
        Long productId = 100L;
        int myprice = MIN_MY_PRICE + 100;
        Long userId = 777L;

        ProductMypriceRequestDto requestMyPriceDto = new ProductMypriceRequestDto(
                myprice
        );


        ProductRequestDto requestProductDto = new ProductRequestDto(
                "오리온 꼬북칩 초코츄러스맛 160g",
                "https://shopping-phinf.pstatic.net/main_2416122/24161228524.20200915151118.jpg",
                "https://search.shopping.naver.com/gate.nhn?id=24161228524",
                2350
        );

        Product product = new Product(requestProductDto, userId);

        //  (3) when() 메서드를 통해 모킹한 객체들이 특정 조건으로 특정 메서드를 호출 시 일괄적으로 다음과 같이 동작하도록 지정.
        when(user.getId())
                .thenReturn(userId);
        when(productRepository.findByIdAndUserId(productId, userId)) // 가짜객체로 우리가 지정해놓은 객체 반환
                .thenReturn(Optional.of(product));


        // when, then
        assertDoesNotThrow( () -> { // 실제로 우리가 테스트하고싶은 부분
            productService.updateProduct(productId, requestMyPriceDto, user);
        });
    }

    @Test
    @DisplayName("관심 상품 희망가 - 최저가 미만으로 변경")
    void updateProduct_Failed() {
        // given
        Long productId = 100L;
        int myprice = MIN_MY_PRICE - 50;

        ProductMypriceRequestDto requestMyPriceDto = new ProductMypriceRequestDto(
                myprice
        );

        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            Long result = productService.updateProduct(productId, requestMyPriceDto, user);
        });

        // then
        assertEquals(
                "유효하지 않은 관심 가격입니다. 최소 " + MIN_MY_PRICE + " 원 이상으로 설정해 주세요.",
                exception.getMessage()
        );
    }
}