package com.docencia.clase.controllers;

import com.docencia.clase.models.Product;
import com.docencia.clase.models.Comment;
import com.docencia.clase.repositories.CommentRepository;
import com.docencia.clase.repositories.ProductRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/products")
    public String index(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("title", "Products - Online Store");
        model.addAttribute("subtitle", "List of products");
        model.addAttribute("products", products);
        return "product/index"; // Retorna la vista product/index.html (Thymeleaf) 
    }

    @GetMapping("/products/{id}")
    public String show(@PathVariable("id") Long id, Model model) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        model.addAttribute("title", product.getName() + " - Online Store");
        model.addAttribute("subtitle", product.getName() + " - Product information");
        model.addAttribute("product", product);
        return "product/show";
        // Retorna la vista product/show.html (Thymeleaf) 
    }

    @GetMapping("/products/create")
    public String createProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product/create";
    }

    @PostMapping("/products/save")
    public String save(@Valid Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("productForm", product);
            return "product/create";
        }
        productRepository.save(product);
        return "redirect:/products";
    }

    @PostMapping("/products")
    public String save(Product product) {
        if (product.getName() == null || product.getName().isEmpty() || product.getPrice() == null) {
            throw new RuntimeException("Name and Price are required");
        }
        productRepository.save(product);
        return "redirect:/products";
    }

    @PostMapping("/products/{id}/comments")
    public String addComment(@PathVariable("id") Long id, @RequestParam("description") String description) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Comment comment = new Comment(description, product);
        commentRepository.save(comment);
        return "redirect:/products/" + id;
    }
    
}
