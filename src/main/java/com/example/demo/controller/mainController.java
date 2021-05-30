package com.example.demo.controller;

import com.example.demo.entity.Cart;
import com.example.demo.entity.Category;
import com.example.demo.entity.Image;
import com.example.demo.entity.Product;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.util.MathFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class mainController {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(){
        return "home";
    }

    @GetMapping("/products")
    public String getListProduct(Model model,
                                 @RequestParam(name = "page", defaultValue = "1")Integer page,
                                 @RequestParam(name = "categoryId", defaultValue = "-1")Integer categoryId,
                                 @RequestParam(name = "subCategoryId", defaultValue = "-1")Integer subCategoryId){
        final int PAGE_SIZE = 20;
        Page<Product> products;
        if (subCategoryId!=-1){
            products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(page-1,PAGE_SIZE));
            if (products.getContent().size()==0){
                products = productRepository.findBySubCategoryId(subCategoryId, PageRequest.of(0,PAGE_SIZE));
                page=1;
            }
        }else if (categoryId!=-1){
            products = productRepository.findByCategoryId(categoryId, PageRequest.of(page-1,PAGE_SIZE));
            if (products.getContent().size()==0){
                products = productRepository.findByCategoryId(categoryId, PageRequest.of(0,PAGE_SIZE));
                page=1;
            }
        }else {
            products = productRepository.findAll(PageRequest.of(page-1,PAGE_SIZE));
            if (products.getContent().size()==0){
                products = productRepository.findAll(PageRequest.of(page-1,PAGE_SIZE));
                page=1;
            }
        }

        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", products.getTotalPages());
        return "listProduct";
    }

    @GetMapping("/detail")
    public String detail(Model model, @RequestParam(name = "productId") Long productId){
        Product product = productRepository.findById(productId).get();
        List<Image> images = product.getImages();
        Image image = new Image();
        image.setImageUrl(product.getImageUrl());
        images.add(0,image);
        product.setImages(images);
        model.addAttribute("product",product);
        return "detail";
    }

    @GetMapping("/add-to-cart")
    public String addToCart(Model model, HttpSession session, @RequestParam("productId") Long productId) {
        Product product = productRepository.findById(productId).get();
        Cart cart = new Cart();
        cart.setProductId(product.getId());
        cart.setProductcode(product.getCode());
        cart.setProductname(product.getName());
        cart.setProductquantity(product.getQuantity());
        cart.setProductprice(product.getPrice());
        cart.setProductdescription(product.getDescription());
        cart.setProductimageUrl(product.getImageUrl());
        cart.setQuantity(1);

        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if (listCart == null) {
            listCart = new ArrayList<>();
            listCart.add(cart);
        }else{
            //có nghĩa là list cart đã tồn tại

            //2 khả năng

            //kn1: sản phẩm chuẩn bị thêm đã có trên giỏ hàng
            boolean isExist = false;
            for(Cart C :listCart){
                if(productId == C.getProductId()){
                    //tồn tại sản phẩm đấy trên giỏ hàng rồi
                    isExist = true;
                    C.setQuantity(C.getQuantity()+1);
                }
            }
            if(!isExist){
                listCart.add(cart);
            }
        }

        session.setAttribute("listCart",listCart);
        return "redirect:/carts";
    }

    @GetMapping("/carts")
    public String getListCart(Model model,HttpSession session) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if(listCart==null || listCart.size()==0){
            return "emptyCart";

        }
        //tính tổng tiền
        double totalMoney = 0;
        for(Cart c: listCart){
            totalMoney+=c.getProductprice()*c.getQuantity();
        }
        model.addAttribute("listCart",listCart);
        model.addAttribute("totalMoney", MathFunction.getMoney(totalMoney));
        return "listCart";
    }
    @GetMapping("/delete-cart")
    public String deleteCart(Model model,HttpSession session,@RequestParam(value = "productId",required = false) Long productId) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if(productId==null){
            session.setAttribute("listCart", new ArrayList<Cart>());
        }else{
            for(Cart c:listCart){
                if(c.getProductId()==productId){
                    listCart.remove(c);
                    break;
                }
            }
            session.setAttribute("listCart",listCart);
        }
        return "redirect:/carts";
    }

    @GetMapping("/update-cart")
    public String updateCart(HttpServletRequest req, HttpSession session) {
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        for (int i = 0; i < listCart.size(); i++) {
            listCart.get(i).setQuantity(Integer.parseInt(req.getParameter("quantity"+i)));
        }
        session.setAttribute("listCart", listCart);
        return "redirect:/carts";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session){
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if(listCart==null || listCart.size()==0){
            return "emptyCart";

        }
        //tính tổng tiền
        double totalMoney = 0;
        for(Cart c: listCart){
            totalMoney+=c.getProductprice()*c.getQuantity();
        }
        model.addAttribute("listCart",listCart);
        model.addAttribute("totalMoney", MathFunction.getMoney(totalMoney));
        return "checkout";
    }

    @GetMapping("/prepare-shipping")
    public String prepareshipping(Model model, HttpSession session,
                                  @RequestParam(name = "name")String name,
                                  @RequestParam(name = "phone")String phone,
                                  @RequestParam(name = "address")String address,
                                  @RequestParam(name = "note")String note){
        List<Cart> listCart = (List<Cart>) session.getAttribute("listCart");
        if(listCart==null || listCart.size()==0){
            return "emptyCart";

        }
        //tính tổng tiền
        double totalMoney = 0;
        for(Cart c: listCart){
            totalMoney+=c.getProductprice()*c.getQuantity();
        }
        model.addAttribute("listCart",listCart);
        model.addAttribute("totalMoney", MathFunction.getMoney(totalMoney));
        model.addAttribute("name",name);
        model.addAttribute("phone",phone);
        model.addAttribute("address",address);
        model.addAttribute("note",note);
        return "prepareShipping";
    }

    @GetMapping("/search")
    public String search(Model model,
                         @RequestParam(value = "page", defaultValue = "1")Integer page,
                         @RequestParam(value = "keyword", defaultValue = "")String keyword){
        final int PAGE_SIZE = 20;
        Page<Product> products = productRepository.search("%" + keyword + "%",PageRequest.of(page-1,PAGE_SIZE));
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        model.addAttribute("page", page);
        model.addAttribute("totalPage", products.getTotalPages());
        return "listProduct";
    }
}
