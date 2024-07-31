package com.earl.ecommerce.product;

import com.earl.ecommerce.product.exception.ProductPurchaseException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper mapper;
    public Integer createProduct(ProductRequest request) {
        Product product = mapper.toProduct(request);
        return repository.save(product).getId();
    }

    public List<ProductPurchaseResponse> purchaseProducts(List<ProductPurchaseRequest> request) {
        //extract list of products that are requested to be purchased
        var productIds = request.stream()
                .map(ProductPurchaseRequest::productId)
                .toList();
        //products from the database
        var storedProducts = repository.findAllByIdInOrderById(productIds);
        //check if the number of products in the request and the database match and can all be purchased
        if(storedProducts.size() != productIds.size()){
            throw new ProductPurchaseException("One or more products does not exist");
        }

        //purchase the requested items in sorted order based on productId
        var storedRequest = request.stream()
                .sorted(Comparator.comparing(ProductPurchaseRequest::productId))
                .toList();

        var purchasedProducts = new ArrayList<ProductPurchaseResponse>();

        //iterate through the list of stored products ensure the requested products can be purchased
        //also update the new available quantity of products after the purchase is done
        for(int i = 0; i < storedProducts.size(); i++){
            var product = storedProducts.get(i);
            var productRequest = storedRequest.get(i);
            //not enough available products for this request, throw an exception
            if(productRequest.quantity() < product.getAvailableQuantity()){
                throw new ProductPurchaseException("Insufficient stock for this product with ID::" + productRequest.productId());
            }

            //update quantity
            var newAvailableQuantity = product.getAvailableQuantity() - productRequest.quantity();
            product.setAvailableQuantity(newAvailableQuantity);
            repository.save(product);

            //build list of purchased products to sent to the client
            purchasedProducts.add(mapper.toProductPurchaseResponse(product, productRequest.quantity()));
        }

        return purchasedProducts;
    }

    public ProductResponse findById(Integer productId) {
        return repository.findById(productId)
                .map(mapper::toProductResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with this ID::" + productId));
    }

    public List<ProductResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toProductResponse)
                .collect(Collectors.toList());
    }
}
