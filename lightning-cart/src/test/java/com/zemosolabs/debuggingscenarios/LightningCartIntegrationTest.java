package com.zemosolabs.debuggingscenarios;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LightningCartIntegrationTest {
  private ICartService fCartService;
  private IBalanceService fBalanceService;
  private ICheckoutService fCheckoutService;
  private IItemCatalogue fItemCatalogue;
  private static final UUID CUSTOMER_1 = UUID.randomUUID();

  @BeforeEach
  public void initEach(){
    fCartService = new CartService();
    fBalanceService = new BalanceService();
    fItemCatalogue = new ItemCatalogue();
    fCheckoutService = new CheckoutService(fCartService, fBalanceService, fItemCatalogue);
  }

  @Test
  public void testSmoothCheckout(){
    //Add Balance to customer
    System.out.println(fBalanceService.getBalance(CUSTOMER_1));
    fBalanceService.addBalance(CUSTOMER_1, 100);
    System.out.println(fBalanceService.getBalance(CUSTOMER_1));

    //Create items
    var item1 = createItem("Book 1", 30, 1);
    var item2 = createItem("Book 2", 30, 2);

    fItemCatalogue.addItemToCatalogue(item1);
    fItemCatalogue.addItemToCatalogue(item2);

    //Add items to customer cart
    fCartService.addItemToCart(CUSTOMER_1, item1);
    fCartService.addItemToCart(CUSTOMER_1, item2);

    Map<String, Integer> cartMap = fCartService.getCart(CUSTOMER_1);
    for (Map.Entry<String, Integer> entry : cartMap.entrySet())
      System.out.println(entry.getKey() + " : " + entry.getValue());

    //Checkout
    Assertions.assertDoesNotThrow(() -> fCheckoutService.checkout(CUSTOMER_1));

    System.out.println(fBalanceService.getBalance(CUSTOMER_1));
  }

  @Test
  public void testLowBalanceCheckout(){
    //Add Balance to customer
    fBalanceService.addBalance(CUSTOMER_1, 100);

    //Create items
    var item1 = createItem("Book 1", 30, 2);
    var item2 = createItem("Book 2", 30, 2);

    //Add items to customer cart
    fCartService.addItemToCart(CUSTOMER_1, item1);
    fCartService.addItemToCart(CUSTOMER_1, item2);

    //Checkout
    Assertions.assertThrows(
            IllegalStateException.class, () -> fCheckoutService.checkout(CUSTOMER_1));
  }

  private static Item createItem(
          final String name, final double cost, final int quantity){
    return new Item(name, cost, quantity);
  }
}
