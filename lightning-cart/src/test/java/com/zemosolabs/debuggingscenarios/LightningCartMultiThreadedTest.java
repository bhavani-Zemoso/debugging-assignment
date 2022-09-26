package com.zemosolabs.debuggingscenarios;

import com.google.code.tempusfugit.concurrency.ConcurrentRule;
import com.google.code.tempusfugit.concurrency.ConcurrentTestRunner;
import com.google.code.tempusfugit.concurrency.RepeatingRule;
import com.google.code.tempusfugit.concurrency.annotations.Concurrent;
import com.google.code.tempusfugit.concurrency.annotations.Repeating;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.UUID;
@RunWith(ConcurrentTestRunner.class)
@Slf4j
public class LightningCartMultiThreadedTest {

  private ICartService fCartService;
  private IBalanceService fBalanceService;
  private IItemCatalogue fItemCatalogue;
  private ICheckoutService fCheckoutService;

  @Rule
  public ConcurrentRule concurrently = new ConcurrentRule();
  @Rule
  public RepeatingRule rule = new RepeatingRule();
  private static final UUID CUSTOMER_1 = UUID.randomUUID();

  @BeforeTest
  public void init(){
    fCartService = new CartService();
    fBalanceService = new BalanceService();
    fItemCatalogue = new ItemCatalogue();
    fCheckoutService = new CheckoutService(fCartService, fBalanceService, fItemCatalogue);
    //Create items
    var item1 = createItem("Book 1", 30);
    var item2 = createItem("Book 2", 30);

    fItemCatalogue.addItemToCatalogue(item1);
    fItemCatalogue.addItemToCatalogue(item2);

    //Add Balance to customer
    fBalanceService.addBalance(CUSTOMER_1, 100);

    //Add items to customer cart
    fCartService.addItemToCart(CUSTOMER_1, fItemCatalogue.getItem("Book 1").get());
    fCartService.addItemToCart(CUSTOMER_1, fItemCatalogue.getItem("Book 2").get());
    fCartService.addItemToCart(CUSTOMER_1, fItemCatalogue.getItem("Book 2").get());
  }

  @Test(threadPoolSize = 2, invocationCount = 2)
  @Concurrent(count = 2)
  @Repeating(repetition = 2)
  public void testMultiThreadedCheckout() {
    try {
      fCheckoutService.checkout(CUSTOMER_1);
    }
    catch (IllegalStateException e)
    {
      log.error(e.getMessage());
    }

    Assertions.assertEquals(10, fBalanceService.getBalance(CUSTOMER_1));
  }

  private static Item createItem(
          final String name, final double cost){
    return new Item(name, cost);
  }
}
