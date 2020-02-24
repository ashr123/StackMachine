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
			rdi, // n
			k,
			rax;

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
				rdi = pop();
				rax = "((fact " + rdi + ") ==> " + rax + ')';
				return;
			case kFact:
				rdi = pop();
				rax = (Long) rax * (Long) rdi;
				k = pop();
				applyKSM();
				return;
			default:
				throw new IllegalStateException("Not a legal label: " + k);
		}
	}

	private void factSM()
	{
		if ((Long) rdi == 0)
		{
			rax = 1L;
			k = pop();
			applyKSM();
		} else
		{
			push(rdi);
			rdi = (Long) rdi - 1;
			push(Labels.kFact);
			factSM();
		}
	}

	public Object fact(Object a)
	{
		stackReset();
		rdi = ((Number) a).longValue();
		push(rdi);
		push(Labels.kInit);
		factSM();
		return rax;
	}
}
