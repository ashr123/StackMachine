package il.co.stackMachines;

import java.util.Deque;
import java.util.LinkedList;

public class Fact
{
	private enum Labels
	{
		kInit, kFact
	}

	/**
	 * A stack for "thread"
	 */
	private final Deque<Object> stack = new LinkedList<>();

	/**
	 * A register
	 */
	private Object
			n = null,
			k = null,
			x = null;

	private void push(Object o)
	{
		stack.push(o);
	}

	private Object pop()
	{
		return stack.pop();
	}

	private void stackReset()
	{
		stack.clear();
	}

	protected Object applyKSM()
	{
		if (k.equals(Labels.kInit))
			return x;
		else if (k.equals(Labels.kFact))
		{
			n = pop();
			x = (Long) x * (Long) n;
			k = pop();
			return applyKSM();
		} else
			throw new IllegalStateException("Not a legal label: " + k);
	}

	private Object factSM()
	{
		if ((Long) n == 0)
		{
			x = 1L;
			k = pop();
			return applyKSM();
		} else
		{
			push(n);
			n = (Long) n - 1;
			push(Labels.kFact);
			return factSM();
		}
	}

	public Object fact(Object a)
	{
		stackReset();
		n = ((Number) a).longValue();
		push(Labels.kInit);
		return factSM();
	}
}
