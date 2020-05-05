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
	private Object rdi, rsi, rax, k, t;

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
		if ((Long) rdi < 2)
		{
			pushT2(rdi);
			pushT2(Labels.T_1);
		} else
		{
			pushT2(rdi);
			pushT2(Labels.T_2);
		}
		t = popT1();
		applyT();
	}

	private void ack()
	{
		if ((Long) rdi == 0)
		{
			pushT1(rsi);
			pushT1(Labels.T_3);
		} else
		{
			pushT1(rsi);
			pushT1(rdi);
			pushT1(Labels.T_4);
		}
		t = popT2();
		applyT();
	}

	private void applyK()
	{
		switch ((Labels) k)
		{
			case K_INIT_ACK -> {
				rdi = popT1();
				rsi = popT1();
				rax = "((ack " + rdi + ' ' + rsi + ") ==> " + rax + ')';
			}
			case K_INIT_FIB -> {
				rdi = popT2();
				rax = "((fib " + rdi + ") ==> " + rax + ')';
			}
			case K_FIB_1 -> {
				rdi = popT2();
				pushT2(rax);
				pushT2(rdi);
				pushT2(Labels.T_5);
				t = popT1();
				applyT();
			}
			case K_FIB_2 -> {
				rdi = popT2(); // fibNMinus1
				pushT2(rax);
				pushT2(rdi);
				pushT2(Labels.T_8);
				t = popT1();
				applyT();
			}
			case K_ACK -> {
				rdi = popT1();
				pushT1(rax);
				pushT1(rdi);
				pushT1(Labels.T_9);
				t = popT2();
				applyT();
			}
			default -> throw new IllegalStateException("Not a legal label: " + k);
		}
	}

	private void applyT()
	{
		switch ((Labels) t)
		{
			case T_1 -> {
				rax = popT2();
				k = popT2();
				applyK();
			}
			case T_2 -> {
				rdi = popT2();
				pushT2(rdi);
				pushT2(Labels.K_FIB_1);
				rdi = (Long) rdi - 1;
				fib();
			}
			case T_3 -> {
				rsi = popT1();
				rax = (Long) rsi + 1;
				k = popT1();
				applyK();
			}
			case T_4 -> {
				rdi = popT1();
				rsi = popT1();
				if ((Long) rsi == 0)
				{
					pushT1(rdi);
					pushT1(Labels.T_6);
				} else
				{
					pushT1(rsi);
					pushT1(rdi);
					pushT1(Labels.T_7);
				}
				t = popT2();
				applyT();
			}
			case T_5 -> {
				rdi = popT2();
				rdi = (Long) rdi - 2;
				pushT2(Labels.K_FIB_2);
				fib();
			}
			case T_6 -> {
				rdi = popT1();
				rsi = 1L;
				rdi = (Long) rdi - 1;
				ack();
			}
			case T_7 -> {
				rdi = popT1();
				rsi = popT1();
				pushT1(rdi);
				rsi = (Long) rsi - 1;
				pushT1(Labels.K_ACK);
				ack();
			}
			case T_8 -> {
				rdi = popT2(); // fibNMinus1
				rsi = popT2(); // fibNMinus2
				rax = (Long) rdi + (Long) rsi;
				k = popT2();
				applyK();
			}
			case T_9 -> {
				rdi = popT1();
				rsi = popT1();
				rdi = (Long) rdi - 1;
				ack();
			}
			case T_ACK_INIT -> {
				rdi = popT1();
				rsi = popT1();
				pushT1(rsi);
				pushT1(rdi);
				pushT1(Labels.K_INIT_ACK);
				ack();
			}
			case T_FIB_INIT -> {
				rdi = popT2();
				pushT2(rdi);
				pushT2(Labels.K_INIT_FIB);
				fib();
			}
			default -> throw new IllegalStateException("Not a legal label: " + t);
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
		return rax;
	}
}
