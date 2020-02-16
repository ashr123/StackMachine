package il.co.stackMachines;

public class Main
{
	public static void main(String[] args)
	{
		final AckVSFib ackVSFib = new AckVSFib();
		System.out.println(ackVSFib.ackVSFib(2, 2, 10));
		System.out.println(ackVSFib.ackVSFib(3, 3, 10));
		System.out.println(ackVSFib.ackVSFib(3, 3, 13));
		try
		{
			System.out.println(ackVSFib.ackVSFib(3, 3, 20));
		} catch (Throwable e)
		{
			e.printStackTrace();
		}


		final Fact fact = new Fact();
		System.out.println(fact.fact(4));
		System.out.println(fact.fact(5));
		System.out.println(fact.fact(10));
		System.out.println(fact.fact(19));
		System.out.println(fact.fact(20));
		System.out.println(fact.fact(25));
		System.out.println(fact.fact(26));
//		System.out.println(fact.fact(30L));
	}
}
