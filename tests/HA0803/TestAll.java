import org.junit.Test;
import pgdp.collections.*;
import pgdp.collections.List;
import pgdp.collections.Queue;
import pgdp.collections.Stack;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * A test class containing hopefully enough tests for HA-0803
 * <p>
 * List of tests (21):
 * - testList
 * - testLinkedStack
 * - testLinkedQueue
 * - testStackConnector
 * - testQueueConnector
 * - testDataStructureLinkStackToStack_move
 * - testDataStructureLinkStackToQueue
 * - testPenguinCustomerValidInput
 * - testPenguinCustomerInValidInput1
 * - testPenguinCustomerInValidInput2
 * - testPenguinCustomerInValidInput3
 * - testPenguinCustomerBand
 * - testCheckout
 * - testGetSmallestCheckout1
 * - testGetSmallestCheckout2
 * - testPenguinSuperMarketIllegal1
 * - testPenguinSuperMarket
 * - testPenguinSuperMarketCloseIllegal1
 * - testPenguinSuperMarketCloseIllegal2
 * - testPenguinSuperMarketCloseIllegal3
 * - testPenguinSuperMarketCloseLegal
 *
 * @author Maxi Barmetler
 * @version 1.5.1
 */
public class TestAll
{
	private final static boolean USE_RANDOM_SEED = false;
	private final static long RANDOM_SEED = new Date().getTime();
	private final static Random RANDOM = USE_RANDOM_SEED ? new Random(RANDOM_SEED) : new Random(1337);

	private static String createRandomString(int size)
	{
		String result = "";
		for (int j = 0; j < size; ++j)
			result += Math.random() < 0.5 ?
					"" + ((char) (Math.random() * 27) + 'a') :
					((char) (Math.random() * 27) + 'A');
		return result;
	}

	@Test public void testList()
	{
		String[] elements = new String[50];
		for (int i = 0; i < elements.length; ++i)
			elements[i] = createRandomString(20);

		List<String> list = new List<>(elements[0]);
		List<String> curr = list;
		for (int i = 1; i < elements.length; ++i)
		{
			curr.insert(elements[i]);
			assertNotNull(curr.getNext());
			curr = curr.getNext();
		}

		assertEquals(elements.length, list.length());

		curr = list;

		for (String element : elements)
		{
			assertNotNull(curr);
			assertEquals(element, curr.getInfo());
			curr = curr.getNext();
		}
	}

	@Test public void testLinkedStack()
	{
		String[] elements = new String[50];
		for (int i = 0; i < elements.length; ++i)
			elements[i] = createRandomString(20);

		Stack<String> stack = new LinkedStack<>();
		for (int i = elements.length - 1; i >= 0; --i)
			stack.push(elements[i]);

		assertEquals("Wrong size", elements.length, stack.size());

		for (String element : elements)
		{
			String s = stack.pop();
			assertEquals("Wrong value", element, s);
		}

		assertTrue("Stack must be empty!", stack.isEmpty());
	}

	@Test public void testLinkedQueue()
	{
		String[] elements = new String[50];
		for (int i = 0; i < elements.length; ++i)
			elements[i] = createRandomString(20);

		Queue<String> queue = new LinkedQueue<>();
		for (String element : elements)
			queue.enqueue(element);

		assertEquals("Wrong size", elements.length, queue.size());

		for (String element : elements)
		{
			String s = queue.dequeue();
			assertEquals("Wrong value", element, s);
		}

		assertTrue("Queue must be empty!", queue.isEmpty());
	}

	@Test public void testStackConnector()
	{
		DataStructureConnector<String> sc = new StackConnector<>(new LinkedStack<>());

		assertTrue("StackConnector must implement DataStructureConnector",
				sc.getClass().getInterfaces().length == 1 && sc.getClass().getInterfaces()[0]
						.equals(DataStructureConnector.class));

		int n = RANDOM.nextInt(50) + 50;

		String[] arr = new String[n];

		for (int i = 0; i < n; ++i)
		{
			arr[i] = createRandomString(20);
			sc.addElement(arr[i]);
		}

		for (int i = 0; i < n; ++i)
		{
			assertTrue(sc.hasNextElement());
			String element = sc.removeNextElement();
			assertEquals("Wrong element at position " + (n - i - 1), arr[n - i - 1], element);
		}

		assertFalse("hasNextElement() should return false", sc.hasNextElement());
	}

	@Test public void testQueueConnector()
	{
		DataStructureConnector<String> qc = new QueueConnector<>(new LinkedQueue<>());

		assertTrue("QueueConnector must implement DataStructureConnector",
				qc.getClass().getInterfaces().length == 1 && qc.getClass().getInterfaces()[0]
						.equals(DataStructureConnector.class));

		int n = RANDOM.nextInt(50) + 50;

		String[] arr = new String[n];

		for (int i = 0; i < n; ++i)
		{
			arr[i] = createRandomString(20);
			qc.addElement(arr[i]);
		}

		for (int i = 0; i < n; ++i)
		{
			assertTrue(qc.hasNextElement());
			String element = qc.removeNextElement();
			assertEquals("Wrong element at position " + i, arr[i], element);
		}

		assertFalse("hasNextElement() should return false", qc.hasNextElement());
	}

	@Test public void testDataStructureLinkStackToStack_move()
	{
		java.util.List<String> la = new ArrayList<>();
		java.util.List<String> lb = new ArrayList<>();

		for (int i = 0; i < 50; ++i)
		{
			la.add(createRandomString(20));
			if (i < 15)
				lb.add(createRandomString(20));
		}

		Stack<String> a = new LinkedStack<>();
		Stack<String> b = new LinkedStack<>();

		for (String e : la)
			a.push(e);
		for (String e : lb)
			b.push(e);

		assertEquals("(a) Wrong size", la.size(), a.size());
		assertEquals("(b) Wrong size", lb.size(), b.size());

		for (int i = la.size() - 1; i >= 0; --i)
			lb.add(la.get(i));

		DataStructureLink<String> link = new DataStructureLink<>(new StackConnector<>(a), new StackConnector<>(b));
		while (!a.isEmpty())
		{
			assertTrue(link.moveNextFromAToB());
		}

		assertFalse(link.moveNextFromAToB());
		assertEquals("(b) Wrong size", lb.size(), b.size());

		for (int i = lb.size() - 1; i >= 0; --i)
		{
			String expected = lb.get(i);
			String actual = b.pop();
			assertEquals("Wrong value", expected, actual);
		}
	}

	@Test public void testDataStructureLinkStackToQueue()
	{
		java.util.List<String> la = new ArrayList<>();
		java.util.List<String> lb = new ArrayList<>();

		for (int i = 0; i < 50; ++i)
		{
			la.add(createRandomString(20));
			if (i < 15)
				lb.add(createRandomString(20));
		}

		Stack<String> a = new LinkedStack<>();
		Queue<String> b = new LinkedQueue<>();

		for (String e : la)
			a.push(e);
		for (String e : lb)
			b.enqueue(e);

		assertEquals("(a) Wrong size", la.size(), a.size());
		assertEquals("(b) Wrong size", lb.size(), b.size());

		for (int i = la.size() - 1; i >= 0; --i)
			lb.add(la.get(i));

		DataStructureLink<String> link = new DataStructureLink<>(new StackConnector<>(a), new QueueConnector<>(b));
		link.moveAllFromAToB();

		assertEquals("(b) Wrong size", lb.size(), b.size());

		for (String expected : lb)
		{
			String actual = b.dequeue();
			assertEquals("Wrong value", expected, actual);
		}
	}

	@Test public void testPenguinCustomerValidInput()
	{
		String name = createRandomString(20);
		int money = RANDOM.nextInt(100);
		PenguinCustomer customer = new PenguinCustomer(name, money);

		assertEquals("Wrong name", name, customer.getName());
		assertEquals("Wrong money before payment", money, customer.getMoney());

		int amount = (int) (Math.random() * money) + 1;
		customer.pay(amount);

		assertEquals("Wrong money after payment", money - amount, customer.getMoney());
	}

	@Test(expected = RuntimeException.class) public void testPenguinCustomerInValidInput1()
	{
		PenguinCustomer customer = new PenguinCustomer(null, 10);
	}

	@Test(expected = RuntimeException.class) public void testPenguinCustomerInValidInput2()
	{
		PenguinCustomer customer = new PenguinCustomer("asd", -1);
	}

	@Test(expected = RuntimeException.class) public void testPenguinCustomerInValidInput3()
	{
		int money = RANDOM.nextInt(80) + 20;
		PenguinCustomer customer = new PenguinCustomer("asd", money);
		customer.pay(money + 1);
	}

	@Test public void testPenguinCustomerBand()
	{
		PenguinCustomer customer = new PenguinCustomer("asd", 10);
		Queue<FishyProduct> a = new LinkedQueue<>();
		Queue<FishyProduct> b = new LinkedQueue<>();

		FishyProduct[] products = new FishyProduct[50];
		FishyProduct[] extraProducts = new FishyProduct[50];

		for (int i = 0; i < products.length; ++i)
		{
			products[i] = new FishyProduct(createRandomString(20), RANDOM.nextInt(9) + 2);
			extraProducts[i] = new FishyProduct(createRandomString(20), RANDOM.nextInt(9) + 2);
			a.enqueue(products[i]);
		}

		customer.takeAllProductsFromBand(a);
		for (FishyProduct fp : extraProducts)
			customer.addProductToBasket(fp);

		customer.placeAllProductsOnBand(b);

		FishyProduct[] expected = new FishyProduct[products.length + extraProducts.length];
		for (int i = 0; i < expected.length; ++i)
			expected[expected.length - i - 1] = i < products.length ? products[i] : extraProducts[i - products.length];

		assertEquals("(a) Wrong size", 0, a.size());
		assertEquals("(b) Wrong size", expected.length, b.size());

		for (FishyProduct fp : expected)
		{
			FishyProduct actual = b.dequeue();
			assertSame("Wrong element after Cashier", fp, actual);
		}
	}

	@Test public void testCheckout()
	{
		final int nCustomers = 10;

		Checkout checkout = new Checkout();

		assertNotNull("checkout.queue must not be null", checkout.getQueue());

		PenguinCustomer[] customers = new PenguinCustomer[nCustomers];
		FishyProduct[][] products = new FishyProduct[nCustomers][];
		int[] money = new int[nCustomers];
		int[] costs = new int[nCustomers];
		for (int i = 0; i < nCustomers; ++i)
		{
			money[i] = RANDOM.nextInt(1000) + 500;
			costs[i] = 0;
			customers[i] = new PenguinCustomer(createRandomString(20), money[i]);
			products[i] = new FishyProduct[RANDOM.nextInt(10) + 10];
			for (int j = 0; j < products[i].length; ++j)
			{
				products[i][j] = new FishyProduct(createRandomString(20), RANDOM.nextInt(20) + 10);
				customers[i].addProductToBasket(products[i][j]);
				costs[i] += products[i][j].getPrice();
			}
			assertEquals("(products) Wrong size before payment", products[i].length, customers[i].getProducts().size());
			checkout.getQueue().enqueue(customers[i]);
		}

		assertEquals("(customers) Wrong size", nCustomers, checkout.queueLength());

		for (int i = 0; i < nCustomers; ++i)
		{
			checkout.serveNextCustomer();

			assertEquals("Wrong money after payment, cost: " + costs[i], money[i] - costs[i], customers[i].getMoney());

			assertEquals("(products) Wrong size after payment", products[i].length, customers[i].getProducts().size());

			for (int j = 0; j < products[i].length; ++j)
			{
				FishyProduct actual = customers[i].getProducts().pop();
				assertSame("Wrong element after Cashier", products[i][j], actual);
			}
		}
	}

	@Test public void testGetSmallestCheckout1()
	{
		final int nCheckouts = 50;

		int smallestCheckout = RANDOM.nextInt(nCheckouts);

		PenguinSupermarket psm = new PenguinSupermarket(nCheckouts);

		int[] sizes = new int[nCheckouts];
		for (int i = 0; i < nCheckouts; ++i)
		{
			sizes[i] = RANDOM.nextInt(20) + (i == smallestCheckout ? 20 : 50);
			Checkout c = psm.getCheckouts()[i];
			for (int j = 0; j < sizes[i]; ++j)
				c.getQueue().enqueue(new PenguinCustomer("asd", 10));
		}

		assertEquals("Wrong checkout!", psm.getCheckouts()[smallestCheckout], psm.getCheckoutWithSmallestQueue());
	}

	@Test public void testGetSmallestCheckout2()
	{
		final int nCheckouts = 50;

		PenguinSupermarket psm = new PenguinSupermarket(nCheckouts);

		int[] sizes = new int[nCheckouts];
		for (int i = 0; i < nCheckouts; ++i)
		{
			sizes[i] = RANDOM.nextInt(20) + 20;
			Checkout c = psm.getCheckouts()[i];
			for (int j = 0; j < sizes[i]; ++j)
				c.getQueue().enqueue(new PenguinCustomer("asd", 10));
		}

		int smallestCheckout = 0;
		for (int i = 1; i < nCheckouts; ++i)
			if (sizes[i] < sizes[smallestCheckout])
				smallestCheckout = i;

		assertEquals("Wrong checkout!", psm.getCheckouts()[smallestCheckout], psm.getCheckoutWithSmallestQueue());
	}

	@Test(expected = RuntimeException.class) public void testPenguinSuperMarketIllegal1()
	{
		new PenguinSupermarket(0);
	}

	@Test public void testPenguinSuperMarket()
	{
		final int nCustomers = 123;

		int nCheckouts = 7;
		PenguinSupermarket sm = new PenguinSupermarket(nCheckouts);

		PenguinCustomer[] customers = new PenguinCustomer[nCustomers];
		FishyProduct[][] products = new FishyProduct[nCustomers][];
		int[] money = new int[nCustomers];
		int[] costs = new int[nCustomers];
		for (int i = 0; i < nCustomers; ++i)
		{
			money[i] = RANDOM.nextInt(1000) + 500;
			costs[i] = 0;
			customers[i] = new PenguinCustomer(createRandomString(20), money[i]);
			products[i] = new FishyProduct[RANDOM.nextInt(10) + 10];
			for (int j = 0; j < products[i].length; ++j)
			{
				products[i][j] = new FishyProduct(createRandomString(20), RANDOM.nextInt(20) + 10);
				customers[i].addProductToBasket(products[i][j]);
				costs[i] += products[i][j].getPrice();
			}
			assertEquals("(products) Wrong size", products[i].length, customers[i].getProducts().size());
		}

		for (int i = 0; i < nCustomers; ++i)
		{
			customers[i].goToCheckout(sm);
			if (i % Math.max((int) (nCheckouts * 0.8), 1) != 0 && i != nCustomers - 1)
				continue;

			int[] customerAmounts = new int[nCheckouts];
			for (int j = 0; j < nCheckouts; ++j)
				customerAmounts[j] = sm.getCheckouts()[j].queueLength();

			sm.serveCustomers();

			for (int j = 0; j < nCheckouts; ++j)
				assertEquals("(customers) Wrong size", Math.max(customerAmounts[j] - 1, 0),
						sm.getCheckouts()[j].queueLength());
		}

		for (int i = 0; i < nCustomers; ++i)
		{
			assertEquals("Wrong money after payment, cost: " + costs[i], money[i] - costs[i], customers[i].getMoney());

			assertEquals("(products) Wrong size", products[i].length, customers[i].getProducts().size());

			for (int j = 0; j < products[i].length; ++j)
			{
				FishyProduct actual = customers[i].getProducts().pop();
				assertSame("Wrong element after Cashier", products[i][j], actual);
			}
		}
	}

	@Test(expected = RuntimeException.class) public void testPenguinSuperMarketCloseIllegal1()
	{
		PenguinSupermarket sm = new PenguinSupermarket(1);
		sm.closeCheckout(0);
	}

	@Test(expected = RuntimeException.class) public void testPenguinSuperMarketCloseIllegal2()
	{
		PenguinSupermarket sm = new PenguinSupermarket(10);
		sm.closeCheckout(10);
	}

	@Test(expected = RuntimeException.class) public void testPenguinSuperMarketCloseIllegal3()
	{
		PenguinSupermarket sm = new PenguinSupermarket(10);
		sm.closeCheckout(-1);
	}

	@Test public void testPenguinSuperMarketCloseLegal()
	{
		final int nCustomers = 20;
		final int nCheckouts = 5;
		final int close = 1;

		PenguinSupermarket sm = new PenguinSupermarket(nCheckouts);
		PenguinCustomer[] customers = new PenguinCustomer[nCustomers];
		for (int i = 0; i < nCustomers; ++i)
		{
			customers[i] = new PenguinCustomer(createRandomString(20), 10);
			customers[i].goToCheckout(sm);
		}

		PenguinCustomer[][] queues = new PenguinCustomer[nCheckouts][nCustomers];

		int[] len = new int[nCheckouts];

		for (int cus = 0, che = 0; cus < nCustomers; ++cus)
		{
			queues[che][len[che]] = customers[cus];
			++len[che];
			che = (che + 1) % nCheckouts;
		}

		for (int i = 0; i < nCheckouts; ++i)
			assertEquals("(customers) Wrong size", len[i], sm.getCheckouts()[i].queueLength());

		for (int i = len[close] - 1; i >= 0; --i)
		{
			int minLen = Integer.MAX_VALUE;
			int minInd = 0;
			for (int j = 0; j < nCheckouts; ++j)
			{
				if (j != close && len[j] < minLen)
				{
					minLen = len[j];
					minInd = j;
				}
			}

			queues[minInd][len[minInd]++] = queues[close][--len[close]];
			queues[close][len[close]] = null;
		}

		sm.closeCheckout(close);

		for (int i = 0, j = 0; i < nCheckouts - 1; ++i, ++j)
		{
			if (j == close)
				++j;
			assertEquals("(checkout[" + i + "]) Wrong size", len[j], sm.getCheckouts()[i].queueLength());
			for (int p = 0; p < len[j]; ++p)
				assertSame("Wrong penguin", queues[j][p], sm.getCheckouts()[i].getQueue().dequeue());
		}
	}
}
