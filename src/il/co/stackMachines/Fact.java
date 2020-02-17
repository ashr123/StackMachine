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

	protected void applyKSM()
	{
		switch ((Labels) k)
		{
			case kInit:
				n = pop();
				x = "((fact " + n + ") ==> " + x + ')';
				return;
			case kFact:
				n = pop();
				x = (Long) x * (Long) n;
				k = pop();
				applyKSM();
				return;
			default:
				throw new IllegalStateException("Not a legal label: " + k);
		}
	}

	private void factSM()
	{
		if ((Long) n == 0)
		{
			x = 1L;
			k = pop();
			applyKSM();
		} else
		{
			push(n);
			n = (Long) n - 1;
			push(Labels.kFact);
			factSM();
		}
	}

	public Object fact(Object a)
	{
		stackReset();
		n = ((Number) a).longValue();
		push(n);
		push(Labels.kInit);
		factSM();
		return x;
	}
}
