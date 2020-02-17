package il.co.stackMachines;

import java.util.Deque;
import java.util.LinkedList;

public class AckVSFib
{
	private enum Labels
	{
		K_INIT_ACK, K_INIT_FIB, K_ACK, K_FIB_1, K_FIB_2, T_ACK_INIT, T_FIB_INIT, T_1, T_2, T_3, T_4, T_5, T_6, T_7, T_8, T_9
	}

	/**
	 * A stack for "thread"
	 */
	private final Deque<Object>
			stackT1 = new LinkedList<>(),
			stackT2 = new LinkedList<>();

	/**
	 * A register
	 */
	private Object
			a = null,
			b = null,
			x = null,
			k = null,
			t = null;

	private void pushT1(Object a)
	{
		stackT1.push(a);
	}

	private void pushT2(Object a)
	{
		stackT2.push(a);
	}

	private Object popT1()
	{
		return stackT1.pop();
	}

	private Object popT2()
	{
		return stackT2.pop();
	}

	private void stackResetT1()
	{
		stackT1.clear();
	}

	private void stackResetT2()
	{
		stackT2.clear();
	}

	private void fib()
	{
		if ((Long) a < 2)
		{
			pushT2(a);
			pushT2(Labels.T_1);
			t = popT1();
			applyT();
		} else
		{
			pushT2(a);
			pushT2(Labels.T_2);
			t = popT1();
			applyT();
		}
	}

	private void ack()
	{
		if ((Long) a == 0)
		{
			pushT1(b);
			pushT1(Labels.T_3);
			t = popT2();
			applyT();
		} else
		{
			pushT1(b);
			pushT1(a);
			pushT1(Labels.T_4);
			t = popT2();
			applyT();
		}
	}

	private void applyK()
	{
		switch ((Labels) k)
		{
			case K_INIT_ACK:
				a = popT1();
				b = popT1();
				a = "((ack " + a + ' ' + b + ") ==> " + x + ')';
				return;
			case K_INIT_FIB:
				a = popT2();
				a = "((fib " + a + ") ==> " + x + ')';
				return;
			case K_FIB_1:
				a = popT2();
				pushT2(x);
				pushT2(a);
				pushT2(Labels.T_5);
				t = popT1();
				applyT();
				return;
			case K_FIB_2:
				a = popT2(); // fibNMinus1
				pushT2(x);
				pushT2(a);
				pushT2(Labels.T_8);
				t = popT1();
				applyT();
				return;
			case K_ACK:
				a = popT1();
				pushT1(x);
				pushT1(a);
				pushT1(Labels.T_9);
				t = popT2();
				applyT();
				return;
			default:
				throw new IllegalStateException("Not a legal label: " + k);
		}
	}

	private void applyT()
	{
		switch ((Labels) t)
		{
			case T_1:
				x = popT2();
				k = popT2();
				applyK();
				return;
			case T_2:
					a = popT2();
				pushT2(a);
				pushT2(Labels.K_FIB_1);
				a = (Long) a - 1;
				fib();
				return;
			case T_3:
				b = popT1();
				x = (Long) b + 1;
				k = popT1();
				applyK();
				return;
			case T_4:
				a = popT1();
				b = popT1();
				if ((Long) b == 0)
				{
					pushT1(a);
					pushT1(Labels.T_6);
					t = popT2();
					applyT();
					return;
				} else
				{
					pushT1(b);
					pushT1(a);
					pushT1(Labels.T_7);
					t = popT2();
					applyT();
					return;
				}
			case T_5:
				a = popT2();
				a = (Long) a - 2;
				pushT2(Labels.K_FIB_2);
				fib();
				return;
			case T_6:
				a = popT1();
				b = 1L;
				a = (Long) a - 1;
				ack();
				return;
			case T_7:
				a = popT1();
				b = popT1();
				pushT1(a);
				b = (Long) b - 1;
				pushT1(Labels.K_ACK);
				ack();
				return;
			case T_8:
				a = popT2(); // fibNMinus1
				b = popT2(); // fibNMinus2
				x = (Long) a + (Long) b;
				k = popT2();
				applyK();
				return;
			case T_9:
				a = popT1();
				b = popT1();
				a = (Long) a - 1;
				ack();
				return;
			case T_ACK_INIT:
				a = popT1();
				b = popT1();
				pushT1(b);
				pushT1(a);
				pushT1(Labels.K_INIT_ACK);
				ack();
				return;
			case T_FIB_INIT:
				a = popT2();
				pushT2(a);
				pushT2(Labels.K_INIT_FIB);
				fib();
				return;
			default:
				throw new IllegalStateException("Not a legal label: " + t);
		}
	}

	public Object ackVSFib(Object a, Object b, Object c)
	{
		stackResetT2();
		pushT2(((Number) c).longValue());
		pushT2(Labels.T_FIB_INIT);

		stackResetT1();
		pushT1(((Number) b).longValue());
		pushT1(((Number) a).longValue());
		pushT1(Labels.T_ACK_INIT);

		t = popT1();
		applyT();
		return this.a;
	}
}
