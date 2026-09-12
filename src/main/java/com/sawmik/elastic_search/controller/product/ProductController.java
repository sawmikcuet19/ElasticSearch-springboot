package com.sawmik.elastic_search.controller.product;

import com.sawmik.elastic_search.dto.product.ProductRequest;
import com.sawmik.elastic_search.dto.product.ReviewRequest;
import com.sawmik.elastic_search.entity.Product;
import com.sawmik.elastic_search.entity.product.Review;
import com.sawmik.elastic_search.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Product> create(@RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.save(toProduct(request)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable String id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Product> update(@PathVariable String id, @RequestBody ProductRequest request) {
        Product product = toProduct(request);
        product.setId(id);
        return ResponseEntity.ok(productService.save(product));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAll() {
        productService.deleteAll();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(productService.count());
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Product>> bulkSave(@RequestBody List<ProductRequest> requests) {
        List<Product> products = requests.stream().map(this::toProduct).toList();
        return ResponseEntity.ok(productService.bulkSave(products));
    }

    @DeleteMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> bulkDelete(@RequestBody List<String> ids) {
        productService.bulkDelete(ids);
        return ResponseEntity.noContent().build();
    }

    private Product toProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setTags(request.getTags());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setAvailable(request.getAvailable());
        if (request.getReviews() != null) {
            product.setReviews(request.getReviews().stream()
                    .map(r -> new Review(r.getReviewer(), r.getComment(), r.getRating()))
                    .toList());
        }
        return product;
    }
}
