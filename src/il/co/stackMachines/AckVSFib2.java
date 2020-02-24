package il.co.stackMachines;

import java.util.Deque;
import java.util.LinkedList;

public class AckVSFib2
{
	private enum Labels
	{
		K_INIT_ACK, K_INIT_FIB, K_ACK, K_FIB_1, K_FIB_2, T_ACK_INIT, T_FIB_INIT, T_1, T_2, T_3, T_4, T_5, T_6, T_7, T_8, T_9
	}


	private enum Registers
	{
		/**
		 * A register
		 */
		rdi, rsi, rax, k, t;

		private Object value;

		public void setValue(Object value)
		{
			this.value = value;
		}

		public Object getValue()
		{
			return value;
		}

		@Override
		public String toString()
		{
			return String.valueOf(value);
		}
	}

	/**
	 * A stack for "thread"
	 */
	private final Deque<Object>
			stackT1 = new LinkedList<>(),
			stackT2 = new LinkedList<>();

	private void pushT1(Object a)
	{
		stackT1.push(a instanceof Registers ? ((Registers) a).getValue() : a);
	}

	private void pushT2(Object a)
	{
		stackT2.push(a instanceof Registers ? ((Registers) a).getValue() : a);
	}

	private void mov(Registers register, Object value)
	{
		register.setValue(value);
	}

	private void popT1(Registers register)
	{
		mov(register, stackT1.pop());
	}

	private void popT2(Registers register)
	{
		mov(register, stackT2.pop());
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
		if ((Long) Registers.rdi.getValue() < 2)
		{
			pushT2(Registers.rdi);
			pushT2(Labels.T_1);
			popT1(Registers.t);
			applyT();
		} else
		{
			pushT2(Registers.rdi);
			pushT2(Labels.T_2);
			popT1(Registers.t);
			applyT();
		}
	}

	private void ack()
	{
		if ((Long) Registers.rdi.getValue() == 0)
		{
			pushT1(Registers.rsi);
			pushT1(Labels.T_3);
			popT2(Registers.t);
			applyT();
		} else
		{
			pushT1(Registers.rsi);
			pushT1(Registers.rdi);
			pushT1(Labels.T_4);
			popT2(Registers.t);
			applyT();
		}
	}

	private void applyK()
	{
		switch ((Labels) Registers.k.getValue())
		{
			case K_INIT_ACK:
				popT1(Registers.rdi);
				popT1(Registers.rsi);
				mov(Registers.rax, "((ack " + Registers.rdi + ' ' + Registers.rsi + ") ==> " + Registers.rax + ')');
				return;
			case K_INIT_FIB:
				popT2(Registers.rdi);
				mov(Registers.rax, "((fib " + Registers.rdi + ") ==> " + Registers.rax + ')');
				return;
			case K_FIB_1:
				popT2(Registers.rdi);
				pushT2(Registers.rax);
				pushT2(Registers.rdi);
				pushT2(Labels.T_5);
				popT1(Registers.t);
				applyT();
				return;
			case K_FIB_2:
				popT2(Registers.rdi); // fibNMinus1
				pushT2(Registers.rax);
				pushT2(Registers.rdi);
				pushT2(Labels.T_8);
				popT1(Registers.t);
				applyT();
				return;
			case K_ACK:
				popT1(Registers.rdi);
				pushT1(Registers.rax);
				pushT1(Registers.rdi);
				pushT1(Labels.T_9);
				popT2(Registers.t);
				applyT();
				return;
			default:
				throw new IllegalStateException("Not a legal label: " + Registers.k);
		}
	}

	private void applyT()
	{
		switch ((Labels) Registers.t.getValue())
		{
			case T_1:
				popT2(Registers.rax);
				popT2(Registers.k);
				applyK();
				return;
			case T_2:
				popT2(Registers.rdi);
				pushT2(Registers.rdi);
				pushT2(Labels.K_FIB_1);
				mov(Registers.rdi, (Long) Registers.rdi.getValue());
				fib();
				return;
			case T_3:
				popT1(Registers.rsi);
				mov(Registers.rax, (Long) Registers.rsi.getValue() + 1);
				popT1(Registers.k);
				applyK();
				return;
			case T_4:
				popT1(Registers.rdi);
				popT1(Registers.rsi);
				if ((Long) Registers.rsi.getValue() == 0)
				{
					pushT1(Registers.rdi);
					pushT1(Labels.T_6);
					popT2(Registers.t);
					applyT();
					return;
				} else
				{
					pushT1(Registers.rsi);
					pushT1(Registers.rdi);
					pushT1(Labels.T_7);
					popT2(Registers.t);
					applyT();
					return;
				}
			case T_5:
				popT2(Registers.rdi);
				mov(Registers.rdi, (Long) Registers.rdi.getValue() - 2);
				pushT2(Labels.K_FIB_2);
				fib();
				return;
			case T_6:
				popT1(Registers.rdi);
				mov(Registers.rsi, 1L);
				mov(Registers.rdi, (Long) Registers.rdi.getValue() - 1);
				ack();
				return;
			case T_7:
				popT1(Registers.rdi);
				popT1(Registers.rsi);
				pushT1(Registers.rdi);
				mov(Registers.rsi, (Long) Registers.rsi.getValue() - 1);
				pushT1(Labels.K_ACK);
				ack();
				return;
			case T_8:
				popT2(Registers.rdi); // fibNMinus1
				popT2(Registers.rsi); // fibNMinus2
				mov(Registers.rax, (Long) Registers.rdi.getValue() + (Long) Registers.rsi.getValue());
				popT2(Registers.k);
				applyK();
				return;
			case T_9:
				popT1(Registers.rdi);
				popT1(Registers.rsi);
				mov(Registers.rdi, (Long) Registers.rdi.getValue() - 1);
				ack();
				return;
			case T_ACK_INIT:
				popT1(Registers.rdi);
				popT1(Registers.rsi);
				pushT1(Registers.rsi);
				pushT1(Registers.rdi);
				pushT1(Labels.K_INIT_ACK);
				ack();
				return;
			case T_FIB_INIT:
				popT2(Registers.rdi);
				pushT2(Registers.rdi);
				pushT2(Labels.K_INIT_FIB);
				fib();
				return;
			default:
				throw new IllegalStateException("Not a legal label: " + Registers.t);
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

		popT1(Registers.t);
		applyT();
		return Registers.rax.getValue();
	}
}
