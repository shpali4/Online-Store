package com.zufar.onlinestore.cart.api;

import com.zufar.onlinestore.cart.converter.ShoppingSessionDtoConverter;
import com.zufar.onlinestore.cart.dto.AddNewItemToShoppingSessionRequest;
import com.zufar.onlinestore.cart.dto.GetShoppingSessionRequest;
import com.zufar.onlinestore.cart.dto.RemoveItemFromShoppingSessionRequest;
import com.zufar.onlinestore.cart.dto.ShoppingSessionDto;
import com.zufar.onlinestore.cart.dto.UpdateProductsQuantityInShoppingSessionItemRequest;
import com.zufar.onlinestore.cart.entity.ShoppingSession;
import com.zufar.onlinestore.cart.entity.ShoppingSessionItem;
import com.zufar.onlinestore.cart.exception.InvalidShoppingSessionIdInUpdateProductsQuantityRequestException;
import com.zufar.onlinestore.cart.exception.ShoppingSessionItemNotFoundException;
import com.zufar.onlinestore.cart.exception.ShoppingSessionNotFoundException;
import com.zufar.onlinestore.cart.repository.ShoppingSessionItemRepository;
import com.zufar.onlinestore.cart.repository.ShoppingSessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartApiImpl implements CartApi {

    private final ShoppingSessionRepository shoppingSessionRepository;
    private final ShoppingSessionItemRepository shoppingSessionItemRepository;
    private final ShoppingSessionDtoConverter shoppingSessionDtoConverter;

    @Override
    public ShoppingSessionDto getShoppingSession(final GetShoppingSessionRequest getShoppingSessionRequest) {
        return null;
    }

    @Override
    public ShoppingSessionDto addNewItemToShoppingSession(final AddNewItemToShoppingSessionRequest addNewItemToShoppingSessionRequest) {
        return null;
    }

    @Override
    public ShoppingSessionDto removeItemFromShoppingSession(final RemoveItemFromShoppingSessionRequest removeItemFromShoppingSessionRequest) {
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    public ShoppingSessionDto updateProductsQuantityInShoppingSessionItem(final UpdateProductsQuantityInShoppingSessionItemRequest request) throws ShoppingSessionNotFoundException, ShoppingSessionItemNotFoundException, InvalidShoppingSessionIdInUpdateProductsQuantityRequestException {
        ShoppingSessionItem updatedItem =
                shoppingSessionItemRepository.updateProductsQuantityInShoppingSessionItem(request.shoppingSessionItemId(), request.productsQuantityChange());

        if (updatedItem == null) {
            log.warn("Failed to update the productsQuantity with the change = {} in the shoppingSessionItem with id: {} of the shoppingSession with the id = {}.",
                    request.productsQuantityChange(), request.shoppingSessionItemId(), request.shoppingSessionId());

            throw new ShoppingSessionItemNotFoundException(request.shoppingSessionId(), request.shoppingSessionItemId());
        }

        Optional<ShoppingSession> shoppingSession = shoppingSessionRepository.findById(request.shoppingSessionId());
        if (shoppingSession.isEmpty()) {
            log.warn("Failed to update the productsQuantity with the change = {} in the shoppingSessionItem with id: {} of the shoppingSession with the id = {}.",
                    request.productsQuantityChange(), request.shoppingSessionItemId(), request.shoppingSessionId());

            throw new ShoppingSessionNotFoundException(request.shoppingSessionId(), request.shoppingSessionItemId());
        }

        if (request.shoppingSessionId() != updatedItem.getShoppingSession().getId()) {
            log.warn("Failed to update the productsQuantity with the change = {} in the shoppingSessionItem with id: {} of the shoppingSession with the id = {}.",
                    request.productsQuantityChange(), request.shoppingSessionItemId(), request.shoppingSessionId());

            throw new InvalidShoppingSessionIdInUpdateProductsQuantityRequestException(request.shoppingSessionId());
        }

        return shoppingSessionDtoConverter.toDto(shoppingSession.get());
    }


}
