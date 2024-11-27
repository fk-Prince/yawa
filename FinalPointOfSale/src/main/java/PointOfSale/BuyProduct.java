package PointOfSale;

import Exceptions.IdNotFoundException;
import Exceptions.InvalidInputException;
import Exceptions.NoAvailableProduct;
import Exceptions.OutOfStockException;
import Product.Entity.Payment;
import Product.Entity.Product;
import Product.PaymentRepository;
import Product.ProductRepository;

import java.io.IOException;
import java.util.List;

import static Login.Login.sc;

public class BuyProduct {
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;


    public BuyProduct(ProductRepository productRepository, PaymentRepository paymentRepository) {
        this.productRepository = productRepository;
        this.paymentRepository = paymentRepository;
    }

    public void runService() {
        while (true) {
            try {
                System.out.println("--------BUY PRODUCT--------");
                System.out.println("Enter Product Choice");
                System.out.println("[1] - Ballpen ");
                System.out.println("[2] - Notebook ");
                System.out.println("[3] - Crayons ");
                System.out.println("[4] - Bondpaper ");
                System.out.println("[5] - Back");
                System.out.print("Choice: ");
                int choice = Integer.parseInt(sc.nextLine());
                switch (choice) {
                    case 1 -> buyProduct("Ballpen");
                    case 2 -> buyProduct("Notebook");
                    case 3 -> buyProduct("Crayons");
                    case 4 -> buyProduct("Bondpaper");
                    case 5 -> {
                        return;
                    }
                    default -> throw new InvalidInputException("Invalid Choice");
                }
            } catch (InvalidInputException | NoAvailableProduct e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void buyProduct(String productType) throws IOException, NoAvailableProduct {
        List<Product> productList = productRepository.getProducts(productType);
        if (productList.isEmpty()) {
            throw new NoAvailableProduct("There is no available products for " + productType);
        }
        productList.forEach(Product::displayDetails);
        buy(productList, productType);
    }

    private void buy(List<Product> productList, String productType) {
        try {
            System.out.println("--------BUY PRODUCT--------");
            System.out.print("Enter Product Id to buy / X to Back: ");
            String choice = sc.nextLine();
            if (choice.equalsIgnoreCase("X")) return;

            int idToBuy = Integer.parseInt(choice);

            Product productSelected = productList.stream()
                    .filter(product -> idToBuy == product.getId())
                    .findFirst()
                    .orElseThrow(() -> new IdNotFoundException("Product Id doesn't exist."));

            System.out.print("Enter how many qty/s you want to buy: ");
            int qtyToBuy = Integer.parseInt(sc.nextLine());

            if (qtyToBuy > productSelected.getQty()) {
                throw new OutOfStockException("Out of Stock!");
            }

            Payment payment = new Payment(productSelected.getId(), productSelected.getBrandName(), qtyToBuy, productSelected.getPrice());
            paymentRepository.doPayment(payment, productType);
            System.out.println("Receipt");
            payment.displayDetails();
            double subtotal = qtyToBuy * productSelected.getPrice();
            System.out.println("Subtotal  \t\t= " + subtotal);
            System.out.println("VAT  \t\t\t= " + (qtyToBuy * productSelected.getPrice()) * 0.12);
            System.out.println("Total (TAX 0.12) = " + payment.compute());
            System.out.println("Purchase Successful");
        } catch (InvalidInputException | OutOfStockException | NoAvailableProduct | IdNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Input.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
