package mate.academy.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import mate.academy.dto.request.CreateCartItemRequestDto;
import mate.academy.dto.response.ShoppingCartDto;
import mate.academy.model.User;
import mate.academy.security.JwtUtil;
import mate.academy.service.ShoppingCartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ShoppingCartController.class)
@AutoConfigureMockMvc
public class ShoppingCartControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ShoppingCartService shoppingCartService;
    @MockBean
    private JwtUtil jwtUtil;

    private UsernamePasswordAuthenticationToken auth() {
        User user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        return new UsernamePasswordAuthenticationToken(
                user, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    public void getCart_AuthenticatedUser_ReturnsCart() throws Exception {
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        when(shoppingCartService.getCart(1L)).thenReturn(expected);
        mockMvc.perform(get("/api/cart").with(authentication(auth())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void addBook_ValidRequest_ReturnsCart() throws Exception {
        CreateCartItemRequestDto requestDto = new CreateCartItemRequestDto();
        requestDto.setBookId(1L);
        requestDto.setQuantity(2);
        ShoppingCartDto expected = new ShoppingCartDto();
        expected.setId(1L);
        when(shoppingCartService.addBook(eq(1L), any(CreateCartItemRequestDto.class)))
                .thenReturn(expected);
        mockMvc.perform(post("/api/cart")
                        .with(authentication(auth()))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    public void deleteBook_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/cart/cart-items/1")
                        .with(authentication(auth()))
                        .with(csrf()))
                .andExpect(status().isNoContent());
        verify(shoppingCartService).remove(1L, 1L);
    }
}
