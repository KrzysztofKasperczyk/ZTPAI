package com.example.demo;
import com.example.demo.dto.ProductResponse;
import com.example.demo.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.dto.ProductRequest;

import java.util.Map;
import java.util.List;

@RestController
public class Controller {

    private final ProductRepository productRepository;

    public Controller(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    //1. Endpoint GET /hello zwracający tekst z pozdrowieniem (np. "Hello, World!")
    @GetMapping("/hello")
    public String hello(){
        return "Hello, World!";
    }

    //2. Endpoint GET /hello/{name} przyjmujący parametr ścieżki i zwracający
    //spersonalizowaną odpowiedź (np. "Hello, Anna!")
    @GetMapping("/hello/{name}")
    public String helloName(@PathVariable String name){
        return "Hello, " + name + "!";
    }

    //3. Endpoint GET /greet?name=X przyjmujący parametr jako query string i zwracający
    //odpowiedź
    @GetMapping("/greet")
    public String greet(@RequestParam String name){
        return "Hello, " + name + "!";
    }

    //4. Endpoint GET /info zwracający obiekt JSON z polami: autor, framework, wersja
    //aplikacji
    @GetMapping("/info")
    public Map<String, Object> info(){
        return Map.of(
                "autor", "Krzysztof Kasperczyk",
                "framework", "Spring Boot",
                "wersjaAplikacji", "1.2.3"
        );
    }

    //5. Endpoint GET /api/products
    @GetMapping("/api/products")
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    //6. Endpoint GET /api/{encja}/{id} zwracający pojedynczy rekord lub błąd 404 jeśli nie
    // istnieje
    @GetMapping("/api/products/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        return mapToResponse(product);
    }

    //7. Endpoint POST /api/{encja} tworzący nowy rekord w bazie i zwracający go z
    //nadanym ID
    @PostMapping("/api/products")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());

        Product saved = productRepository.save(product);

        return mapToResponse(saved);
    }

    //8. Endpoint DELETE /api/{encja}/{id} usuwający rekord z bazy
    @DeleteMapping("/api/products/{id}")
    public void deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }

        productRepository.deleteById(id);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice()
        );
    }
}
